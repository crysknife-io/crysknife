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

package io.crysknife.ui.navigation.client.local.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import io.crysknife.client.IsElement;
import io.crysknife.client.utils.CreationalCallback;
import io.crysknife.client.BeanManager;
import io.crysknife.client.internal.collections.Multimap;
import io.crysknife.ui.navigation.client.local.PageRole;
import io.crysknife.ui.navigation.client.local.UniquePageRole;
import io.crysknife.ui.navigation.client.local.api.MissingPageRoleException;
import io.crysknife.ui.navigation.client.local.api.PageNotFoundException;
import io.crysknife.ui.navigation.client.local.api.TransitionTo;
import io.crysknife.ui.navigation.client.shared.NavigationEvent;

/**
 * The NavigationGraph is responsible for creating or retrieving instances of Page and
 * PageTransition objects. It is also the central repository for structural information about the
 * interpage navigation in the app (this information is defined in a decentralized way, by classes
 * that implement {@link PageNode} and contain injected {@link TransitionTo} fields.
 * <p>
 * The concrete implementation of this class is usually generated at compile-time by scanning for
 * page classes. It is expected to fill in the {@link #pagesByName} map in its constructor.
 *
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
@ApplicationScoped
public class NavigationGraph {

  @Inject
  protected BeanManager beanManager;
  @Inject
  protected Event<NavigationEvent> event;


  public NavigationGraph() {}

  public NavigationGraph(BeanManager beanManager, Event<NavigationEvent> event) {
    this.beanManager = beanManager;
    this.event = event;
  }

  /**
   * Maps page names to the classes that implement them. The subclass's constructor is responsible
   * for populating this map.
   */
  protected final Map<String, PageNode<?>> pagesByName = new HashMap<>();
  protected final Multimap<Class<? extends PageRole>, PageNode<?>> pagesByRole = new Multimap<>();

  /**
   * Returns an instance of the given page type. If the page is an ApplicationScoped bean, the
   * singleton instance of the page will be returned; otherwise (for Dependent-scoped beans) a new
   * instance will be returned.
   *
   * @param name The page name, as defined by the implementation of page.
   *
   * @return The appropriate instance of the page.
   */
  public <C> PageNode<C> getPage(String name) {
    @SuppressWarnings("unchecked")
    PageNode<C> page = (PageNode<C>) pagesByName.get(name);
    if (page == null) {
      throw new PageNotFoundException("Page not found: \"" + name + "\"");
    }
    return page;
  }

  /**
   * Returns an instance of the given page type. If the page is an ApplicationScoped bean, the
   * singleton instance of the page will be returned; otherwise (for Dependent-scoped beans) a new
   * instance will be returned.
   *
   * @param type The Class object for the bean that implements the page.
   *
   * @return The appropriate instance of the page.
   */
  public <C> PageNode<C> getPage(Class<C> type) {
    // TODO this could be made more efficient if we had a pagesByWidgetType map
    for (Entry<String, PageNode<?>> e : pagesByName.entrySet()) {
      if (e.getValue().contentType().equals(type)) {
        @SuppressWarnings({"unchecked"})
        PageNode<C> page = (PageNode<C>) e.getValue();
        return page;
      }
    }
    throw new PageNotFoundException("No page with a widget type of " + type.getName() + " exists");
  }

  /**
   * Returns all pages that have the specified role. In the add page annotation one can specify
   * multiple roles for a page. {@link #getPage(Class)} {@link PageRole}
   *
   * @param role the role used to lookup the pages
   *
   * @return all pages that have the role set.
   */
  public Collection<PageNode<?>> getPagesByRole(Class<? extends PageRole> role) {
    return pagesByRole.get(role);
  }

  public PageNode getPageByRole(Class<? extends UniquePageRole> role) {
    final Collection<PageNode<?>> pageNodes = pagesByRole.get(role);
    if (pageNodes.size() == 1) {
      return pageNodes.iterator().next();
    } else if (pageNodes.size() < 1) {
      throw new MissingPageRoleException(role);
    } else {
      throw new IllegalStateException(
          "Role '" + role + "' is not unique multiple pages: " + pageNodes + " found");
    }
  }

  /**
   * Returns true if and only if there are no pages in this nagivation graph.
   */
  public boolean isEmpty() {
    return pagesByName.isEmpty();
  }

  protected static final class PageNodeCreationalCallback<W extends IsElement>
      implements CreationalCallback<PageNode<W>> {

    @Override
    public void callback(PageNode<W> beanInstance) {

    }

  }

  /**
   * @return Returns a collection of all {@link PageNode PageNodes} in the navigation graph.
   */
  public Collection<PageNode<?>> getAllPages() {
    Collection<PageNode<?>> values = pagesByName.values();
    return Collections.unmodifiableCollection(new HashSet<PageNode<?>>(values));
  }

}
