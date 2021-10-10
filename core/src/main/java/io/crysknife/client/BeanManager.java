/*
 * Copyright © 2020 Treblereel
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
package io.crysknife.client;

import io.crysknife.client.internal.BeanManagerUtil;
import io.crysknife.client.internal.QualifierUtil;
import io.crysknife.client.internal.SyncBeanDefImpl;

import javax.enterprise.inject.Typed;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/28/19
 */
public interface BeanManager {

  void register(SyncBeanDefImpl beanDefinition);


  <T> Collection<SyncBeanDef<T>> lookupBeans(final Class<T> type, Annotation... qualifiers);

  <T> SyncBeanDef<T> lookupBean(final Class<T> type);

  <T> SyncBeanDef<T> lookupBean(final Class<T> type, Annotation... qualifiers);

  void destroyBean(Object ref);
}

