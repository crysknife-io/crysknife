/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.crysknife.ui.databinding.client.components;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import io.crysknife.client.BeanManager;
import io.crysknife.client.IsElement;
import io.crysknife.client.SyncBeanDef;
import io.crysknife.client.ioc.ContextualTypeProvider;
import io.crysknife.client.ioc.IOCProvider;
import jsinterop.base.Js;
import org.gwtproject.user.client.ui.IsWidget;

import javax.enterprise.inject.Instance;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Provides {@link ListComponent} instances that lookup displayed components through Errai IoC. Any
 * qualifiers on the injection site are used when looking up displayed components.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@SuppressWarnings("rawtypes")
@IOCProvider
public class ListComponentProvider implements ContextualTypeProvider<ListComponent> {

  public final BeanManager beanManager;

  public ListComponentProvider(BeanManager beanManager) {
    this.beanManager = beanManager;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ListComponent provide(final Class<?>[] typeargs, final Annotation[] qualifiers) {
    final Annotation[] filteredQualifiers = filterQualifiers(qualifiers);
    final Optional<ListContainer> listContainer = getListContainer(qualifiers);
    final HTMLElement root = (HTMLElement) DomGlobal.document
        .createElement(listContainer.map(anno -> anno.value()).orElse("div"));
    final SyncBeanDef<?> beanDef =
        (SyncBeanDef<?>) beanManager.lookupBean(typeargs[1], filteredQualifiers);
    final Supplier<?> supplier = () -> beanDef.getInstance();
    // final Consumer<?> destroyer = (!Dependent.class.equals(beanDef.getScope()) ? c -> {} : c ->
    // IOC.getBeanManager().destroyBean(c));
    final Consumer<?> destroyer = (Consumer<Object>) o -> {
      DomGlobal.console.log("destroyer is not supported yet");
    };

    final Function<?, HTMLElement> elementAccessor;
    if (beanDef.getInstance() instanceof IsElement) {
      elementAccessor = (Function<Object, HTMLElement>) c -> ((IsElement) c).getElement();
    } else if (beanDef.getInstance() instanceof IsWidget) {
      elementAccessor =
          c -> Js.uncheckedCast(((IsWidget) beanDef.getInstance()).asWidget().getElement());
    } else {
      throw new RuntimeException("Cannot create element accessor for "
          + beanDef.getInstance().getClass().getCanonicalName()
          + ". Must implement IsElement or IsWidget.");
    }

    return new DefaultListComponent(root, supplier, destroyer, elementAccessor);
  }

  private Optional<ListContainer> getListContainer(final Annotation[] qualifiers) {
    for (final Annotation qual : qualifiers) {
      if (qual.annotationType().equals(ListContainer.class)) {
        return Optional.ofNullable((ListContainer) qual);
      }
    }

    return Optional.empty();
  }

  private Annotation[] filterQualifiers(final Annotation[] qualifiers) {
    final List<Annotation> filtered = new ArrayList<>(qualifiers.length);
    for (final Annotation qual : qualifiers) {
      if (!qual.annotationType().equals(ListContainer.class)) {
        filtered.add(qual);
      }
    }

    return filtered.toArray(new Annotation[filtered.size()]);
  }

}
