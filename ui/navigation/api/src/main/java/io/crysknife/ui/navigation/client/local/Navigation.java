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
import java.util.Queue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.gwtproject.event.shared.HandlerRegistration;
import org.jboss.elemento.By;
import org.jboss.elemento.Elements;
import org.jboss.elemento.ElementsBag;
import org.jboss.elemento.IsElement;
import io.crysknife.client.internal.collections.Multimap;
import io.crysknife.ui.navigation.client.local.api.DelegationControl;
import io.crysknife.ui.navigation.client.local.api.NavigationControl;
import io.crysknife.ui.navigation.client.local.api.PageNavigationErrorHandler;
import io.crysknife.ui.navigation.client.local.api.PageNotFoundException;
import io.crysknife.ui.navigation.client.local.api.RedirectLoopException;
import io.crysknife.ui.navigation.client.local.pushstate.PushStateUtil;
import io.crysknife.ui.navigation.client.local.spi.NavigationGraph;
import io.crysknife.ui.navigation.client.local.spi.PageNode;

import static elemental2.dom.DomGlobal.console;
import static elemental2.dom.DomGlobal.document;
import static org.jboss.elemento.Elements.removeChildrenFrom;

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
    if (PushStateUtil.isPushStateActivated()) {
      return getAppContextFromHostPage();
    } else {
      return "";
    }
  }

  /**
   * Sets the application context used in pushstate URL paths. This application context should match
   * the deployed application context in your web.xml
   *
   * @param path The context path. Never null.
   */
  public static void setAppContext(String path) {
    if (path == null) {
      Js.asPropertyMap(DomGlobal.window).set("erraiApplicationWebContext", Js.undefined());
    } else {
      Js.asPropertyMap(DomGlobal.window).set("erraiApplicationWebContext", path);
    }
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
  private By navigationContainerSelector;
  private HTMLElement navigationContainer;

  private PageNode<Object> currentNode;
  private Object currentPage;
  private ElementsBag currentElements;
  private HistoryToken currentToken;

  private ContentDelegation contentDelegation = new DefaultContentDelegation();
  private PageNavigationErrorHandler navigationErrorHandler;
  private HandlerRegistration historyHandlerRegistration;

  @Inject
  private NavigationGraph navGraph;
  @Inject
  private HistoryTokenFactory historyTokenFactory;

  // ------------------------------------------------------ setup & tear down

  @PostConstruct
  void init() {
    if (navGraph.isEmpty()) {
      return;
    }

    String hash = DomGlobal.window.location.hash;
    navigationErrorHandler = new DefaultNavigationErrorHandler(this);
    historyHandlerRegistration = HistoryWrapper.addValueChangeHandler(event -> {
      HistoryToken token = null;
      try {
        console.debug("URL value changed to " + event.getValue());
        if (needsApplicationContext()) {
          String context = inferAppContext(event.getValue());
          console.info("No application context defined. Inferring application context as " + context
              + ". Change this value by setting the variable \"erraiApplicationWebContext\" "
              + "in your GWT host page, or calling Navigation.setAppContext(String).");
          setAppContext(context);
        }
        token = historyTokenFactory.parseURL(event.getValue());
        if (currentNode == null || !token.equals(currentToken)) {
          PageNode<Object> toPage = navGraph.getPage(token.getPageName());
          navigate(new Request<>(toPage, token), false);
        }
      } catch (final Exception e) {
        if (token == null) {
          navigationErrorHandler.handleInvalidURLError(e, event.getValue());
        } else {
          navigationErrorHandler.handleInvalidPageNameError(e, token.getPageName());
        }
      }
    });

    maybeConvertHistoryToken(hash);
    if (navigationContainer() != null) {
      HistoryWrapper.fireCurrentHistoryState();
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

  private void maybeConvertHistoryToken(String token) {
    if (PushStateUtil.isPushStateActivated()) {
      if (token == null || token.isEmpty()) {
        return;
      }
      if (token.startsWith("#")) {
        token = token.substring(1);
      }
      HistoryWrapper.newItem(DomGlobal.window.location.pathname + token, false);
    }
  }

  @PreDestroy
  public void cleanUp() {
    historyHandlerRegistration.removeHandler();
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
      HistoryWrapper.newItem(currentToken.toString(), false);
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
      HistoryWrapper.newItem(request.state.toString(), fireEvent);
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
        removeChildrenFrom(navigationContainer);
        hideControl.proceed();
      }
    }
  }

  private <P> void pageHiding(P page, ElementsBag pageElements, Request<P> request,
      boolean fireEvent) {
    HTMLElement navigationContainer = navigationContainer();
    if (navigationContainer != null) {

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
      if (currentNode != null && currentPage != null && pageElements != null
          && sameElements(navigationContainer, pageElements)) {
        currentNode.pageHiding(currentPage, control);
      } else {
        control.proceed();
      }
    }
  }

  private boolean sameElements(HTMLElement navigationContainer, ElementsBag elements) {
    int currentElementCount = (int) navigationContainer.childElementCount;
    int newElementsCount = 0;
    for (HTMLElement ignored : elements.elements()) {
      newElementsCount++;
    }
    if (currentElementCount != newElementsCount) {
      return false;
    }
    Iterator<HTMLElement> currentIterator = Elements.iterator(navigationContainer);
    Iterator<HTMLElement> newIterator = elements.elements().iterator();
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

  public void setNavigationContainerSelector(By selector) {
    this.navigationContainerSelector = selector;
    if (selector != null) {
      this.navigationContainer = Elements.find(document.body, navigationContainerSelector);
    }
  }

  public void setNavigationContainer(HTMLElement navigationContainer) {
    this.navigationContainer = navigationContainer;
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
    return (currentNode == null) && (PushStateUtil.isPushStateActivated())
        && (getAppContextFromHostPage().isEmpty());
  }

  private HTMLElement navigationContainer() {
    if (navigationContainer != null) {
      return navigationContainer;
    }
    if (navigationContainerSelector != null) {
      navigationContainer = Elements.find(document.body, navigationContainerSelector);
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
  private ElementsBag pageElements(Object page) {
    ElementsBag elements = new ElementsBag();
    if (page != null) {
      if (page instanceof IsElement) {
        elements.add(((IsElement) page).element());
      } else if (page instanceof io.crysknife.client.IsElement) {
        elements.add(((io.crysknife.client.IsElement) page).getElement());
      } else if (page instanceof Iterable) {
        for (Object o : ((Iterable) page)) {
          if (o instanceof IsElement) {
            elements.add(((IsElement) o).element());
          } else if (page instanceof io.crysknife.client.IsElement) {
            elements.add(((io.crysknife.client.IsElement) o).getElement());
          } else if (o instanceof HTMLElement) {
            elements.add(((HTMLElement) o));
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
}
