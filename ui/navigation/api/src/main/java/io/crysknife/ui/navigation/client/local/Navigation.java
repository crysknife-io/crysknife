/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.crysknife.ui.navigation.client.local;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import elemental2.core.JsArray;
import elemental2.core.JsString;
import elemental2.dom.*;
import jsinterop.base.Js;
import io.crysknife.client.internal.collections.Multimap;
import io.crysknife.ui.navigation.client.local.api.DelegationControl;
import io.crysknife.ui.navigation.client.local.api.NavigationControl;
import io.crysknife.ui.navigation.client.local.api.PageNavigationErrorHandler;
import io.crysknife.ui.navigation.client.local.api.PageNotFoundException;
import io.crysknife.ui.navigation.client.local.api.RedirectLoopException;
import io.crysknife.ui.navigation.client.local.spi.NavigationGraph;
import io.crysknife.ui.navigation.client.local.spi.PageNode;

import static elemental2.dom.DomGlobal.console;
import static elemental2.dom.DomGlobal.document;

/**
 * Central control point for navigating between pages of the application.
 * <p>
 * Configuration is decentralized: it is based on fields and annotations present in other
 * application classes. This configuration is gathered at compile time.
 *
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 *
 * @see Page
 * @see PageState
 * @see PageShowing
 * @see PageShown
 * @see PageHiding
 * @see PageHidden
 */
@ApplicationScoped
public class Navigation {

  // ------------------------------------------------------ static

  /** Maximum number of successive redirects until Errai suspects an endless loop. */
  private final static int MAXIMUM_REDIRECTS = 99;

  /**
   * Gets the application context used in pushstate URL paths. This application context should match
   * the deployed application context in your web.xml
   *
   * @return The application context. This may return the empty string (but never null). If
   *         non-empty, the return value always starts with a slash and never ends with one.
   */
  public static String getAppContext() {
    return "";
  }

  private static String getAppContextFromHostPage() {
    String context =
        Js.uncheckedCast(Js.asPropertyMap(DomGlobal.window).get("erraiApplicationWebContext"));
    context = context == null ? "" : context;
    if (!context.startsWith("/")) {
      context = "/" + context;
    }
    if (context.endsWith("/")) {
      context = context.substring(0, context.length() - 1);
    }
    return context;
  }

  // ------------------------------------------------------ instance

  // Queued navigation requests which could not handled immediately.
  private final Queue<Request<?>> queuedRequests = new LinkedList<>();
  private boolean locked = false;
  private int redirectDepth = 0;

  // @Inject
  // @NavigationSelector
  private String navigationContainerSelector;
  private HTMLElement navigationContainer;

  private PageNode<Object> currentNode;
  private Object currentPage;
  private JsArray<HTMLElement> currentElements;
  private HistoryToken currentToken;

  private ContentDelegation contentDelegation = new DefaultContentDelegation();
  private PageNavigationErrorHandler navigationErrorHandler;

  @Inject
  private NavigationGraph navGraph;
  @Inject
  private HistoryTokenFactory historyTokenFactory;

  // ------------------------------------------------------ setup & tear down

  private Window window = DomGlobal.window;

  void init() {
    if (navGraph.isEmpty()) {
      return;
    }

    String raw = window.location.hash;
    navigationErrorHandler = new DefaultNavigationErrorHandler(this);
    navigationErrorHandler = new DefaultNavigationErrorHandler(this);
    processToken(raw);

    window.addEventListener("hashchange", evt -> {
      String raw1 = window.location.hash;
      processToken(raw1);
    });

    window.addEventListener("locationchange", evt -> {
      HashChangeEvent.HashChangeEventEventInitDictType eventEventInitDictType =
          HashChangeEvent.HashChangeEventEventInitDictType.create();
      eventEventInitDictType.setOldURL(hash());
      eventEventInitDictType.setNewURL(window.location.href);
      window.dispatchEvent(new HashChangeEvent("hashchange", eventEventInitDictType));
    });
  }

  private void processToken(String raw) {
    HistoryToken token = historyTokenFactory.parseURL(raw);
    if (currentNode == null || !token.equals(currentToken)) {
      PageNode<Object> toPage = navGraph.getPage(token.getPageName());
      navigate(new Request<>(toPage, token), false);
    }
  }

  private String inferAppContext(String url) {
    if (!(url.startsWith("/"))) {
      url = "/" + url;
    }
    int indexOfNextSlash = url.indexOf("/", 1);
    if (indexOfNextSlash < 0) {
      return "";
    } else {
      return url.substring(0, indexOfNextSlash);
    }
  }

  @PreDestroy
  public void cleanUp() {
    setErrorHandler(null);
  }

  // ------------------------------------------------------ public API

  /**
   * Looks up the PageNode instance that provides content for the given widget type, sets the state
   * on that page, then makes the widget visible in the content area.
   *
   * @param toPage The content type of the page node to look up and display. Normally, this is a
   *        Widget subclass that has been annotated with {@code @Page}.
   * @param state The state information to set on the page node before showing it. Normally the map
   *        keys correspond with the names of fields annotated with {@code @PageState} in the widget
   *        class, but this is not required.
   */
  public <P> void goTo(Class<P> toPage, Multimap<String, String> state) {
    PageNode<P> toPageInstance = null;

    try {
      toPageInstance = navGraph.getPage(toPage);
      navigate(toPageInstance, state);
    } catch (final RedirectLoopException e) {
      throw e;
    } catch (final RuntimeException e) {
      if (toPageInstance == null) {
        // This is an extremely unlikely case, so throwing an exception is preferable
        // to going through the navigation error handler.
        throw new PageNotFoundException(
            "There is no page of type " + toPage.getName() + " in the navigation graph.");
      } else {
        navigationErrorHandler.handleInvalidPageNameError(e, toPageInstance.name());
      }
    }
  }

  /**
   * Same as {@link #goTo(Class, Multimap)} but then with the page name.
   *
   * @param toPage the name of the page node to lookup and display.
   */
  public void goTo(String toPage) {
    PageNode<?> toPageInstance;
    try {
      toPageInstance = navGraph.getPage(toPage);
      navigate(toPageInstance);
    } catch (final RedirectLoopException e) {
      throw e;
    } catch (final RuntimeException e) {
      navigationErrorHandler.handleInvalidPageNameError(e, toPage);
    }
  }

  /**
   * Looks up the PageNode instance of the page that has the unique role set and makes the widget
   * visible in the content area.
   *
   * @param role The unique role of the page that needs to be displayed.
   */
  public void goToWithRole(Class<? extends UniquePageRole> role) {
    PageNode<?> toPageInstance;
    try {
      toPageInstance = navGraph.getPageByRole(role);
      navigate(toPageInstance);
    } catch (final RedirectLoopException e) {
      throw e;
    } catch (final RuntimeException e) {
      navigationErrorHandler.handleError(e, role);
    }
  }

  /**
   * Update the state of your existing page without performing a full navigation. <br/>
   * This will perform a pseudo navigation updating the history token with the new states.
   */
  public void updateState(Multimap<String, String> state) {
    if (currentNode != null) {
      currentToken = historyTokenFactory.createHistoryToken(currentNode.name(), state);
      currentNode.pageUpdate(currentPage, currentToken);
    } else {
      console.error("Cannot update the state before a page has loaded.");
    }
  }

  // ------------------------------------------------------ internal navigation

  private <C> void navigate(PageNode<C> toPageInstance) {
    navigate(toPageInstance, new Multimap<>());
  }

  private <C> void navigate(PageNode<C> toPageInstance, Multimap<String, String> state) {
    HistoryToken token = historyTokenFactory.createHistoryToken(toPageInstance.name(), state);
    navigate(new Request<>(toPageInstance, token), true);
  }

  /**
   * Captures a backup of the current page state in history, sets the state on the given PageNode
   * from the given state token, then makes its widget visible in the content area.
   */
  private <P> void navigate(Request<P> request, boolean fireEvent) {
    if (locked) {
      queuedRequests.add(request);
      return;
    }

    redirectDepth++;
    if (redirectDepth >= MAXIMUM_REDIRECTS) {
      throw new RedirectLoopException("Maximum redirect limit of " + MAXIMUM_REDIRECTS
          + " reached. " + "Do you have a redirect loop?");
    }
    maybeShowPage(request, fireEvent);
  }

  private <P> void handleQueuedRequests(Request<P> request, boolean fireEvent) {
    if (queuedRequests.isEmpty()) {
      // No new navigation requests were recorded in the lifecycle methods.
      // This is the page which has to be displayed and the browser's history
      // can be updated.
      redirectDepth = 0;
      if (!hash().equals(request.state.toString())
          && currentToken.equals(request.state.toString())) {
        HashChangeEvent.HashChangeEventEventInitDictType eventEventInitDictType =
            HashChangeEvent.HashChangeEventEventInitDictType.create();
        eventEventInitDictType.setNewURL(request.state.toString());
        eventEventInitDictType.setOldURL(hash());
        window.dispatchEvent(new HashChangeEvent("hashchange", eventEventInitDictType));
      }
    } else {
      // Process all navigation requests captured in the lifecycle methods.
      while (queuedRequests.size() != 0) {
        navigate(queuedRequests.poll(), fireEvent);
      }
    }
  }

  // ------------------------------------------------------ hide page lifecycle

  /**
   * Hide the page currently displayed and call the associated lifecycle methods.
   *
   * @param requestPage the next requested page, this can be null if there is none.
   */
  private <P> void hideCurrentPage(P requestPage, NavigationControl control) {
    HTMLElement navigationContainer = navigationContainer();
    if (navigationContainer != null) {
      if (currentNode != null && currentElements == null) {
        // This could happen if someone was manipulating the DOM behind our backs
        console.log("Current content vanished or changed. " + "Not delivering pageHiding event to "
            + currentNode + ".");
      }

      DelegationControl hideControl = new DelegationControl(() -> {
        if (currentNode != null && currentPage != null) {
          currentNode.pageHidden(currentPage);
          currentNode.destroy(currentPage);
        }
        control.proceed();
      });
      if (currentPage != null) {
        contentDelegation.hideContent(currentPage, navigationContainer, pageElements(currentPage),
            requestPage, hideControl);
      } else {
        // Cannot call content delegation. The contract requests that currentPage != null!
        if (navigationContainer != null) {
          while (navigationContainer.firstChild != null) {
            navigationContainer.removeChild(navigationContainer.firstChild);
          }
        }

        hideControl.proceed();
      }
    }
  }

  private <P> void pageHiding(P page, JsArray<HTMLElement> pageElements, Request<P> request,
      boolean fireEvent) {
    HTMLElement navigationContainer = navigationContainer();
    if (navigationContainer != null) {

      String currentHash = hash();
      if (!request.state.toString().equals(currentHash)) {
        DomGlobal.location.hash = request.state.toString();
      }
      Runnable runnable = () -> {
        NavigationControl showControl = new NavigationControl(Navigation.this, () -> {
          try {
            Object previousPage = currentPage;
            setCurrentNode(request.pageNode);
            currentElements = pageElements;
            currentPage = page;
            contentDelegation.showContent(page, navigationContainer, pageElements, previousPage,
                new DelegationControl(() -> request.pageNode.pageShown(page, request.state)));
          } finally {
            locked = false;
          }
          handleQueuedRequests(request, fireEvent);
        }, () -> locked = false);

        try {
          locked = true;
          hideCurrentPage(page, new NavigationControl(Navigation.this,
              () -> request.pageNode.pageShowing(page, request.state, showControl)));
        } finally {
          locked = false;
        }
      };
      Runnable interrupt = () -> hideCurrentPage(null,
          new NavigationControl(Navigation.this, () -> setCurrentNode(null)));

      NavigationControl control = new NavigationControl(Navigation.this, runnable, interrupt);
      if (currentNode != null && currentPage != null) {
        currentNode.pageHiding(currentPage, control);
      } else {
        control.proceed();
      }
    }
  }

  private boolean sameElements(HTMLElement navigationContainer, JsArray<HTMLElement> elements) {
    int currentElementCount = (int) navigationContainer.childElementCount;
    int newElementsCount = 0;
    for (HTMLElement ignored : elements.asList()) {
      newElementsCount++;
    }
    if (currentElementCount != newElementsCount) {
      return false;
    }
    Iterator<HTMLElement> currentIterator = new JsArrayElementIterator(navigationContainer);
    Iterator<HTMLElement> newIterator = elements.asList().iterator();
    while (currentIterator.hasNext() && newIterator.hasNext()) {
      HTMLElement currentElement = currentIterator.next();
      HTMLElement newElement = newIterator.next();
      if (currentElement != newElement) {
        return false;
      }
    }
    return true;
  }

  // ------------------------------------------------------ show page lifecycle

  /** Call navigation and page related lifecycle methods. */
  private <P> void maybeShowPage(Request<P> request, boolean fireEvent) {
    request.pageNode.produceContent(page -> {
      if (page == null) {
        throw new NullPointerException("Target page " + request.pageNode + " is null");
      }
      currentToken = request.state;
      pageHiding(page, pageElements(page), request, fireEvent);
    });
  }

  // ------------------------------------------------------ properties

  /**
   * Return all PageNode instances that have specified pageRole.
   *
   * @param pageRole the role to find PageNodes by
   *
   * @return All the pageNodes of the pages that have the specific pageRole.
   */
  public Collection<PageNode<?>> getPagesByRole(Class<? extends PageRole> pageRole) {
    return navGraph.getPagesByRole(pageRole);
  }

  /**
   * @return The state multimap used to show the currently displayed page. If a navigation request
   *         has been submitted this may return the state of page being navigated to before that
   *         page has actually been displayed.
   */
  public Multimap<String, String> getCurrentState() {
    return (currentToken != null) ? currentToken.getState() : new Multimap<>();
  }

  /** Are we in the navigation process right now. */
  public boolean isNavigating() {
    return locked;
  }

  /**
   * Set an error handler that is called in case of a {@link PageNotFoundException} error during
   * page navigation.
   *
   * @param handler An error handler for navigation. Setting this to null assigns the
   *        {@link DefaultNavigationErrorHandler}
   */
  public void setErrorHandler(final PageNavigationErrorHandler handler) {
    if (handler == null) {
      navigationErrorHandler = new DefaultNavigationErrorHandler(this);
    } else {
      navigationErrorHandler = handler;
    }
  }

  public void setContentDelegation(ContentDelegation contentDelegation) {
    this.contentDelegation = contentDelegation;
  }

  public void setNavigationContainerSelector(String selector) {
    this.navigationContainerSelector = selector;
    if (selector != null) {
      document.body.querySelector(navigationContainerSelector);
    }
    init();
  }

  public void setWindowObject(Window window) {
    this.window = window;
    init();
  }

  public void setNavigationContainer(HTMLElement navigationContainer) {
    this.navigationContainer = navigationContainer;
    init();
  }

  /** Returns the navigation graph that provides PageNode instances to this Navigation instance. */
  // should this method be public? should we expose a way to set the nav graph?
  NavigationGraph getNavGraph() {
    return navGraph;
  }

  /**
   * Just sets the currentNode field. This method exists primarily to get around a generics
   * Catch-22.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void setCurrentNode(PageNode currentNode) {
    this.currentNode = currentNode;
  }

  private boolean needsApplicationContext() {
    return currentNode == null && getAppContextFromHostPage().isEmpty();
  }

  private HTMLElement navigationContainer() {
    if (navigationContainer != null) {
      return navigationContainer;
    }
    if (navigationContainerSelector != null) {
      navigationContainer = (HTMLElement) document.body.querySelector(navigationContainerSelector);
    }
    if (navigationContainer == null) {
      console.warn(
          "Navigation container is null. " + "Please make sure to set the container using either\n"
              + "Navigation.setNavigationContainer(HTMLHTMLElement element) or\n"
              + "Navigation.setNavigationContainerSelector(By selector)"
              + " Ignore this message if container is lazy-initializable.");

    }
    return navigationContainer;
  }

  @SuppressWarnings("rawtypes")
  private JsArray<HTMLElement> pageElements(Object page) {
    JsArray<HTMLElement> elements = new JsArray<>();
    if (page != null) {
      if (page instanceof io.crysknife.client.IsElement) {
        elements.push(((io.crysknife.client.IsElement) page).getElement());
      } else if (page instanceof Iterable) {
        for (Object o : ((Iterable) page)) {
          if (page instanceof io.crysknife.client.IsElement) {
            elements.push(((io.crysknife.client.IsElement) o).getElement());
          } else if (o instanceof HTMLElement) {
            elements.push(((HTMLElement) o));
          }
        }
      }
    }
    return elements;
  }

  // ------------------------------------------------------ inner classes

  /** Encapsulates a navigation request to another page. */
  private static class Request<P> {

    PageNode<P> pageNode;
    HistoryToken state;

    private Request(PageNode<P> pageNode, HistoryToken state) {
      this.pageNode = pageNode;
      this.state = state;
    }
  }

  private static class JsArrayElementIterator implements Iterator<HTMLElement> {

    private HTMLElement parent, last, next;

    public JsArrayElementIterator(HTMLElement parent) {
      this.parent = parent;
      next = (HTMLElement) parent.firstElementChild;
    }

    @Override
    public boolean hasNext() {
      return next != null;
    }

    @Override
    public HTMLElement next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      last = next;
      next = (HTMLElement) last.nextElementSibling;
      return last;
    }

    @Override
    public void remove() {
      if (last == null) {
        throw new IllegalStateException();
      }
      parent.removeChild(last);
      last = null;
    }
  }

  private String hash() {
    String raw = window.location.hash;
    if (!raw.isEmpty()) {
      if (raw.indexOf("?") > 0) {
        raw = new JsString(raw).split("?").getAt(0);
      }
      raw = raw.replaceFirst("#", "");
    }
    return raw;

  }
}
