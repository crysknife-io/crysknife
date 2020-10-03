/*
 * Copyright 2012 Johannes Barop
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

package io.crysknife.ui.navigation.client.local.pushstate;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.gwtproject.event.logical.shared.HasValueChangeHandlers;
import org.gwtproject.event.logical.shared.ValueChangeEvent;
import org.gwtproject.event.logical.shared.ValueChangeHandler;
import org.gwtproject.event.shared.Event;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.history.client.History;
import io.crysknife.ui.navigation.client.local.HandlerManager;

import static elemental2.dom.DomGlobal.window;

/**
 * Enhances GWT's History implementation to add HTML5 pushState support.
 *
 * <p>
 * This class no longer inherits from HistoryImpl to allow for compatibility with both GWT 2.6 and
 * GWT 2.7+. HistoryImpl was moved in GWT 2.7 and is no longer accessible. The previously inherited
 * methods are now part of this class.
 * </p>
 *
 * <p>
 * The complete path is treated as history token.
 * </p>
 *
 * <p>
 * The leading '/' is hidden from GWTs History API, so that the path '/' is returned as an empty
 * history token ('').
 * </p>
 * 
 * @author <a href="mailto:jb@barop.de">Johannes Barop</a>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class HistoryImplPushState implements HasValueChangeHandlers<String> {

  private HandlerManager handlers = new HandlerManager(null);
  private String token = "";

  public boolean init() {
    // initialize HistoryImpl with the current path
    updateHistoryToken(window.location.pathname + window.location.search);
    // initialize the empty state with the current history token
    nativeUpdate(token, true);
    // initialize the popState handler
    initPopStateHandler();

    return true;
  }

  /**
   * Set the current path as GWT History token which can later retrieved with
   * {@link History#getToken()}.
   */
  private void updateHistoryToken(String path) {
    String[] split = path.split("\\?");
    String token = split[0];
    token = (token.length() > 0) ? decodeFragment(token) : "";
    token = (token.startsWith("/")) ? token.substring(1) : token;

    String queryString = (split.length == 2) ? split[1] : "";
    queryString = CodeServerParameterHelper.remove(queryString);
    if (queryString != null && !queryString.trim().isEmpty()) {
      token += "?" + queryString;
    }

    DomGlobal.console.info("Set token to '" + token + "'");
    this.token = token;
  }

  private void nativeUpdate(final String historyToken, boolean replace) {
    String newPushStateToken = CodeServerParameterHelper.append(encodeFragment(historyToken));
    if (!newPushStateToken.startsWith("/")) {
      newPushStateToken = "/" + newPushStateToken;
    }

    if (replace) {
      replaceState(newPushStateToken);
      DomGlobal.console.info("Replaced '" + newPushStateToken + "' (" + historyToken + ")");
    } else {
      pushState(newPushStateToken);
      DomGlobal.console.info("Pushed '" + newPushStateToken + "' (" + historyToken + ")");
    }
  }

  /**
   * Initialize an event handler that gets executed when the token changes.
   */
  private void initPopStateHandler() {
    elemental2.dom.Window.OnpopstateFn oldHandler = DomGlobal.window.onpopstate;

    DomGlobal.window.onpopstate = new elemental2.dom.Window.OnpopstateFn() {
      @Override
      public Object onInvoke(elemental2.dom.Event e) {
        if (Js.asPropertyMap(e).has("state")) {
          JsPropertyMap state = Js.uncheckedCast(Js.asPropertyMap(e).get("state"));
          if (state.has("historyToken")) {
            onPopState(Js.uncheckedCast(Js.asPropertyMap(state).get("historyToken")));
          }
        }
        if (oldHandler != null) {
          oldHandler.onInvoke(null);
        }
        return null;
      }
    };
  }

  private String decodeFragment(String encodedFragment) {
    return Global.decodeURI(encodedFragment.replace("#", "%23"));
  }

  private String encodeFragment(String fragment) {
    return Global.encodeURI(fragment).replace("#", "%23");
  }

  /**
   * Replace the given token in the history using replaceState.
   */
  private static void replaceState(final String token) {
    DomGlobal.window.history.replaceState(Js.uncheckedCast(JsPropertyMap.of("historyToken", token)),
        DomGlobal.document.title, token);
  }

  /**
   * Add the given token to the history using pushState.
   */
  private static void pushState(final String token) {
    DomGlobal.window.history.pushState(Js.uncheckedCast(JsPropertyMap.of("historyToken", token)),
        DomGlobal.document.title, token);
  }

  /**
   * Called from native JavaScript when an old history state was popped.
   */
  private void onPopState(final String historyToken) {
    DomGlobal.console.info("Popped '" + historyToken + "'");
    updateHistoryToken(historyToken);
    fireHistoryChangedImpl(token);
  }

  /**
   * Fires the {@link ValueChangeEvent} to all handlers with the given tokens.
   */
  public void fireHistoryChangedImpl(String newToken) {
    ValueChangeEvent.fire(this, newToken);
  }

  /**
   * Fires the {@link ValueChangeEvent} to all handlers with the current token.
   */
  public void fireCurrentHistoryState() {
    ValueChangeEvent.fire(this, token);
  }

  @Override
  public void fireEvent(Event<?> event) {
    handlers.fireEvent(event);
  }

  /**
   * Adds a {@link ValueChangeEvent} handler to be informed of changes to the browser's history
   * stack.
   * 
   * @param handler the handler
   */
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
    return handlers.addHandler(ValueChangeEvent.getType(), handler);
  }

  /**
   * Adds a new browser history entry. Calling this method will cause
   * {@link ValueChangeHandler#onValueChange(ValueChangeEvent)} to be called as well if and only if
   * issueEvent is true.
   * 
   * @param historyToken the token to associate with the new history item
   * @param issueEvent true if a {@link ValueChangeHandler#onValueChange(ValueChangeEvent)} event
   *        should be issued
   */
  public final void newItem(String historyToken, boolean issueEvent) {
    historyToken = (historyToken == null) ? "" : historyToken;
    if (!historyToken.equals(this.token)) {
      this.token = historyToken;
      nativeUpdate(historyToken);
      if (issueEvent) {
        fireHistoryChangedImpl(historyToken);
      }
    }
  }

  private void nativeUpdate(final String historyToken) {
    nativeUpdate(historyToken, false);
  }
}
