/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.treblereel.gwt.crysknife.navigation.client.local;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.gwtproject.event.logical.shared.ValueChangeEvent;
import org.gwtproject.event.logical.shared.ValueChangeHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.window.client.Window;
import org.jboss.elemento.IsElement;
import org.treblereel.gwt.crysknife.client.internal.collections.ImmutableMultimap;
import org.treblereel.gwt.crysknife.client.internal.collections.Multimap;
import org.treblereel.gwt.crysknife.navigation.client.local.api.DelegationControl;
import org.treblereel.gwt.crysknife.navigation.client.local.api.NavigationControl;
import org.treblereel.gwt.crysknife.navigation.client.local.api.PageNavigationErrorHandler;
import org.treblereel.gwt.crysknife.navigation.client.local.api.PageNotFoundException;
import org.treblereel.gwt.crysknife.navigation.client.local.api.RedirectLoopException;
import org.treblereel.gwt.crysknife.navigation.client.local.pushstate.PushStateUtil;
import org.treblereel.gwt.crysknife.navigation.client.local.spi.NavigationGraph;
import org.treblereel.gwt.crysknife.navigation.client.local.spi.PageNode;

/**
 * Central control point for navigating between pages of the application.
 * <p>
 * Configuration is decentralized: it is based on fields and annotations present in other application classes. This
 * configuration is gathered at compile time.
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 * @see Page
 * @see PageState
 * @see PageShowing
 * @see PageShown
 * @see PageHiding
 * @see PageHidden
 */
@ApplicationScoped
public class Navigation {

    /**
     * Maximum number of successive redirects until Errai suspects an endless loop.
     */
    final static int MAXIMUM_REDIRECTS = 99;
    private final NavigatingContainer navigatingContainer = new DefaultNavigatingContainer();
    private final Map<IsWidget, HandlerRegistration> attachHandlerRegistrations = new HashMap<>();
    /**
     * Queued navigation requests which could not handled immediately.
     */
    private final Queue<Request> queuedRequests = new LinkedList<>();
    protected PageNode<Object> currentPage;
    protected Object currentComponent;
    protected HTMLElement currentWidget;
    protected HistoryToken currentPageToken;
    protected ContentDelegation contentDelegation = new DefaultContentDelegation();
    private PageNavigationErrorHandler navigationErrorHandler;
    private HandlerRegistration historyHandlerRegistration;
    /**
     * Indicates that a navigation request is currently processed.
     */
    private boolean locked = false;
    private int redirectDepth = 0;
    @Inject
    private NavigationGraph navGraph;
    @Inject
    private HistoryTokenFactory historyTokenFactory;

    /**
     * Gets the application context used in pushstate URL paths. This application context should match the deployed
     * application context in your web.xml
     * @return The application context. This may return the empty String (but never null). If non-empty, the return value
     * always starts with a slash and never ends with one.
     */
    public static String getAppContext() {
      if (PushStateUtil.isPushStateActivated()) {
        return getAppContextFromHostPage();
      } else {
        return "";
      }
    }

    /**
     * Sets the application context used in pushstate URL paths. This application context should match the deployed
     * application context in your web.xml
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
        String context = getRawAppContextFromHostPage();
        if (!context.isEmpty() && !context.startsWith("/")) {
            context = "/" + context;
        }
        if (context.endsWith("/")) {
            context = context.substring(0, context.length() - 1);
        }

        return context;
    }

    private static String getRawAppContextFromHostPage() {
        String erraiApplicationWebContext = Js.uncheckedCast(Js.asPropertyMap(DomGlobal.window).get("erraiApplicationWebContext"));
        if (erraiApplicationWebContext != null || erraiApplicationWebContext.isEmpty()) {
            return "";
        } else {
            return erraiApplicationWebContext;
        }
    }

    @PostConstruct
    private void init() {
      if (navGraph.isEmpty()) {
        return;
      }

        final String hash = Window.Location.getHash();

        navigationErrorHandler = new DefaultNavigationErrorHandler(this);

        historyHandlerRegistration = HistoryWrapper.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(final ValueChangeEvent<String> event) {
                HistoryToken token = null;
                try {
                    DomGlobal.console.debug("URL value changed to " + event.getValue());
                    if (needsApplicationContext()) {
                        final String context = inferAppContext(event.getValue());
                        DomGlobal.console.info("No application context defined. Inferring application context as "
                                                       + context
                                                       + ". Change this value by setting the variable \"erraiApplicationWebContext\" in your GWT host page"
                                                       + ", or calling Navigation.setAppContext.");
                        setAppContext(context);
                    }
                    token = historyTokenFactory.parseURL(event.getValue());

                    if (currentPage == null || !token.equals(currentPageToken)) {
                        final PageNode<IsWidget> toPage = navGraph.getPage(token.getPageName());
                        navigate(new Request<>(toPage, token), false);
                    }
                } catch (final Exception e) {
                  if (token == null) {
                    navigationErrorHandler.handleInvalidURLError(e, event.getValue());
                  } else {
                    navigationErrorHandler.handleInvalidPageNameError(e, token.getPageName());
                  }
                }
            }
        });

        maybeConvertHistoryToken(hash);
    }

    protected String inferAppContext(String url) {
      if (!(url.startsWith("/"))) {
        url = "/" + url;
      }

        final int indexOfNextSlash = url.indexOf("/", 1);

      if (indexOfNextSlash < 0) {
        return "";
      } else {
        return url.substring(0, indexOfNextSlash);
      }
    }

    /**
     * Public for testability.
     */
    @PreDestroy
    public void cleanUp() {
        historyHandlerRegistration.removeHandler();
        setErrorHandler(null);
    }

    /**
     * Set an error handler that is called in case of a {@link PageNotFoundException} error during page navigation.
     * @param handler An error handler for navigation. Setting this to null assigns the {@link DefaultNavigationErrorHandler}
     */
    public void setErrorHandler(final PageNavigationErrorHandler handler) {
      if (handler == null) {
        navigationErrorHandler = new DefaultNavigationErrorHandler(this);
      } else {
        navigationErrorHandler = handler;
      }
    }

    /**
     * Looks up the PageNode instance that provides content for the given widget type, sets the state on that page, then
     * makes the widget visible in the content area.
     * @param toPage The content type of the page node to look up and display. Normally, this is a Widget subclass that has
     * been annotated with {@code @Page}.
     * @param state The state information to set on the page node before showing it. Normally the map keys correspond with the
     * names of fields annotated with {@code @PageState} in the widget class, but this is not required.
     */
    public <C> void goTo(final Class<C> toPage, final Multimap<String, String> state) {
        PageNode<C> toPageInstance = null;

        try {
            toPageInstance = navGraph.getPage(toPage);
            navigate(toPageInstance, state);
        } catch (final RedirectLoopException e) {
            throw e;
        } catch (final RuntimeException e) {
          if (toPageInstance == null)
          // This is an extremely unlikely case, so throwing an exception is preferable to going through the navigation error handler.
          {
            throw new PageNotFoundException("There is no page of type " + toPage.getName() + " in the navigation graph.");
          } else {
            navigationErrorHandler.handleInvalidPageNameError(e, toPageInstance.name());
          }
        }
    }

    /**
     * Same as {@link #goTo(Class, Multimap)} but then with the page name.
     * @param toPage the name of the page node to lookup and display.
     */
    public void goTo(final String toPage) {
        PageNode<?> toPageInstance = null;
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
     * Looks up the PageNode instance of the page that has the unique role set and makes the widget visible in the content
     * area.
     * @param role The unique role of the page that needs to be displayed.
     */
    public void goToWithRole(final Class<? extends UniquePageRole> role) {
        PageNode<?> toPageInstance = null;
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
     * Return all PageNode instances that have specified pageRole.
     * @param pageRole the role to find PageNodes by
     * @return All the pageNodes of the pages that have the specific pageRole.
     */
    public Collection<PageNode<?>> getPagesByRole(final Class<? extends PageRole> pageRole) {
        return navGraph.getPagesByRole(pageRole);
    }

    private <C> void navigate(final PageNode<C> toPageInstance) {
        navigate(toPageInstance, ImmutableMultimap.of());
    }

    private <C> void navigate(final PageNode<C> toPageInstance, final Multimap<String, String> state) {
        final HistoryToken token = historyTokenFactory.createHistoryToken(toPageInstance.name(), state);
        DomGlobal.console.debug("Navigating to " + toPageInstance.name() + " at url: " + token.toString());
        navigate(new Request<>(toPageInstance, token), true);
    }

    /**
     * Captures a backup of the current page state in history, sets the state on the given PageNode from the given state
     * token, then makes its widget visible in the content area.
     */
    private <C> void navigate(final Request<C> request, final boolean fireEvent) {
        if (locked) {
            queuedRequests.add(request);
            return;
        }

        redirectDepth++;
        if (redirectDepth >= MAXIMUM_REDIRECTS) {
            throw new RedirectLoopException("Maximum redirect limit of " + MAXIMUM_REDIRECTS + " reached. "
                                                    + "Do you have a redirect loop?");
        }

        maybeShowPage(request, fireEvent);
    }

    private <C> void handleQueuedRequests(final Request<C> request, final boolean fireEvent) {
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

    /**
     * Attach the content panel to the RootPanel if does not already have a parent.
     */
    private void maybeAttachContentPanel() {
        if (getContentPanel().getWidget().parentElement == null) {
            DomGlobal.document.body.appendChild(getContentPanel().getWidget());
        }
    }

    /**
     * Hide the page currently displayed and call the associated lifecycle methods.
     * @param requestPage the next requested page, this can be null if there is none.
     */
    private void hideCurrentPage(Object requestPage, NavigationControl control) {
        final NavigationPanel currentContent = navigatingContainer.getWidget();

        // Note: Optimized out in production mode
        if (currentPage != null && (currentContent == null || currentWidget != currentContent.getWidget())) {
            // This could happen if someone was manipulating the DOM behind our backs
            DomGlobal.console.log("Current content widget vanished or changed. " + "Not delivering pageHiding event to " + currentPage
                                          + ".");
        }

        DelegationControl hideControl = new DelegationControl(() -> {
            if (currentPage != null && currentComponent != null) {
                currentPage.pageHidden(currentComponent);
                currentPage.destroy(currentComponent);
            }

            control.proceed();
        });

        if (currentComponent != null) {
            contentDelegation.hideContent(currentComponent, navigatingContainer, currentWidget, requestPage, hideControl);
        } else {
            navigatingContainer.clear();
            hideControl.proceed();
        }
    }

    /**
     * Call navigation and page related lifecycle methods.
     * If the {@link Access} is fired successfully, load the new page.
     */
    private <C> void maybeShowPage(final Request<C> request, final boolean fireEvent) {
        request.pageNode.produceContent(component -> {
            if (component == null) {
                throw new NullPointerException("Target page " + request.pageNode + " returned a null content widget");
            }

            final C unwrappedComponent = component;
            HTMLElement widget = null;
            if (unwrappedComponent instanceof IsWidget) {
                widget = Js.uncheckedCast(((IsWidget) unwrappedComponent).asWidget().getElement());
            } else if (unwrappedComponent instanceof IsElement) {
                widget = ((IsElement) unwrappedComponent).element();
            }
            maybeAttachContentPanel();
            currentPageToken = request.state;

            DomGlobal.console.log("maybeShowPage " + component.getClass().getCanonicalName() + " " + widget.tagName);

            pageHiding(unwrappedComponent, widget, request, fireEvent);

/*      if ((unwrappedComponent instanceof Composite) && (getCompositeWidget((Composite) unwrappedComponent) == null)) {
        final HandlerRegistration reg = widget.addAttachHandler(event -> {

          if (event.isAttached() && currentWidget != unwrappedComponent) {
            pageHiding(unwrappedComponent, widget, request, fireEvent);
          }


        });
        attachHandlerRegistrations.put(widget, reg);
      }
      else {
        pageHiding(unwrappedComponent, widget, request, fireEvent);
      }*/
        });
    }

    private <C, W extends HTMLElement> void pageHiding(final C component, final W componentWidget, final Request<C> request, final boolean fireEvent) {
        DomGlobal.console.log("pageHiding " + component.getClass().getCanonicalName() + " " + componentWidget.tagName);

        final HandlerRegistration reg = attachHandlerRegistrations.remove(component);
        if (reg != null) {
            reg.removeHandler();
        }

        final NavigationControl control = new NavigationControl(Navigation.this, new Runnable() {
            @Override
            public void run() {
                NavigationControl showControl = new NavigationControl(Navigation.this, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Object previousPage = currentComponent;
                            setCurrentPage(request.pageNode);
                            currentWidget = componentWidget;
                            currentComponent = component;
                            contentDelegation.showContent(component, navigatingContainer, currentWidget, previousPage, new DelegationControl(() -> {
                                request.pageNode.pageShown(component, request.state);
                            }));
                        } finally {
                            locked = false;
                        }

                        handleQueuedRequests(request, fireEvent);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        locked = false;
                    }
                });

                try {
                    locked = true;
                    hideCurrentPage(component, new NavigationControl(Navigation.this, () -> {
                        request.pageNode.pageShowing(component, request.state, showControl);
                    }));
                } catch (Exception ex) {
                    locked = false;
                    throw ex;
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                hideCurrentPage(null, new NavigationControl(Navigation.this, () -> {
                    setCurrentPage(null);
                }));
            }
        });

        if (currentPage != null && currentWidget != null && currentComponent != null && currentWidget == Js.uncheckedCast(navigatingContainer.getWidget())) {
            currentPage.pageHiding(currentComponent, control);
        } else {
            control.proceed();
        }
    }

    /**
     * Return the current page that is being displayed.
     * @return the current page
     */
    public PageNode<?> getCurrentPage() {
        return currentPage;
    }

    /**
     * Just sets the currentPage field. This method exists primarily to get around a generics Catch-22.
     * @param currentPage the new value for currentPage.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setCurrentPage(final PageNode currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * @return The state multimap used to show the currently displayed page. If a navigation request has been submitted
     * this may return the state of page being navigated to before that page has actually been displayed.
     */
    public Multimap<String, String> getCurrentState() {
        return (currentPageToken != null) ? currentPageToken.getState() : ImmutableMultimap.of();
    }

    /**
     * Returns the panel that this Navigation object manages. The contents of this panel will be updated by the navigation
     * system in response to PageTransition requests, as well as changes to the GWT navigation system.
     * @return The content panel of this Navigation instance. It is not recommended that client code modifies the contents
     * of this panel, because this Navigation instance may replace its contents at any time.
     */
    public NavigationPanel getContentPanel() {
        return navigatingContainer.getWidget();
    }

    /**
     * Returns the navigation graph that provides PageNode instances to this Navigation instance.
     */
    // should this method be public? should we expose a way to set the nav graph?
    NavigationGraph getNavGraph() {
        return navGraph;
    }

    private boolean needsApplicationContext() {
        return (currentPage == null) && (PushStateUtil.isPushStateActivated()) && (getAppContextFromHostPage() == null);
    }

    private void maybeConvertHistoryToken(String token) {
        if (PushStateUtil.isPushStateActivated()) {
            if (token == null || token.isEmpty()) {
                return;
            }

            if (token.startsWith("#")) {
                token = token.substring(1);
            }

            HistoryWrapper.newItem(Window.Location.getPath() + token, false);
        }
    }

    /**
     * Update the state of your existing page without performing a full navigation.
     * <br/>
     * This will perform a pseudo navigation updating the history token with the new states.
     */
    public void updateState(Multimap<String, String> state) {
        if (currentPage != null) {
            currentPageToken = historyTokenFactory.createHistoryToken(currentPage.name(), state);
            HistoryWrapper.newItem(currentPageToken.toString(), false);
            currentPage.pageUpdate(currentComponent, currentPageToken);
        } else {
            DomGlobal.console.error("Cannot update the state before a page has loaded.");
        }
    }

    /**
     * Are we in the navigation process right now.
     */
    public boolean isNavigating() {
        return locked;
    }

    public void setContentDelegation(ContentDelegation contentDelegation) {
        this.contentDelegation = contentDelegation;
    }

    /**
     * Encapsulates a navigation request to another page.
     */
    private static class Request<C> {

        PageNode<C> pageNode;

        HistoryToken state;

        /**
         * Construct a new {@link Request}.
         * @param pageNode The page node to display. Normally, the implementation of PageNode is generated at compile time based on
         * a Widget subclass that has been annotated with {@code @Page}. Anything calling this method must ensure
         * that the given PageNode has been entered into the navigation graph, or later navigation back to
         * {@code toPage} will fail.
         * @param state The state information to pass to the page node before showing it.
         */
        private Request(final PageNode<C> pageNode, final HistoryToken state) {
            this.pageNode = pageNode;
            this.state = state;
        }
    }
}
