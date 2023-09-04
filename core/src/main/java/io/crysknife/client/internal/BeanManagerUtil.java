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

package io.crysknife.client.internal;

import io.crysknife.client.IOCBeanDef;
import jakarta.inject.Named;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class BeanManagerUtil {

  public static <T> IOCResolutionException ambiguousResolutionException(Class<T> type,
      final Collection<IOCBeanDef<T>> resolved, Annotation... qualifiers) {
    final StringBuilder builder = new StringBuilder();
    builder.append("Multiple beans matched ").append(type.getName()).append(" with qualifiers ")
        .append(qualifiersToString(qualifiers)).append("\n").append("Found:\n");
    for (final IOCBeanDef<T> beanDef : resolved) {
      builder.append("  ").append(beanDef.toString()).append("\n");
    }
    builder.append("}");
    return new IOCResolutionException(builder.toString());
  }

  public static <T> IOCResolutionException unsatisfiedResolutionException(Class<T> type,
      Annotation... qualifiers) {
    return new IOCResolutionException("No beans matched " + type.getName() + " with qualifiers "
        + qualifiersToString(qualifiers));
  }

  public static <T> IOCResolutionException noFactoryResolutionException(Class<T> type,
      Annotation... qualifiers) {
    return new IOCResolutionException("No factory registered for " + type.getName()
        + " with qualifiers " + qualifiersToString(qualifiers));
  }

  public static String qualifiersToString(Collection<Annotation> qualifiers) {
    return qualifiersToString(qualifiers.toArray(new Annotation[qualifiers.size()]));
  }

  public static String qualifiersToString(final Annotation[] qualifiers) {
    final StringBuilder builder = new StringBuilder().append("{ ");

    String strings = Arrays.stream(qualifiers).map(BeanManagerUtil::qualifierToString)
        .collect(Collectors.joining(", "));

    builder.append(strings);
    builder.append(" }");
    return builder.toString();
  }

  public static String qualifierToString(final Annotation qualifier) {
    final StringBuilder builder = new StringBuilder();
    builder.append(qualifier.annotationType().getName());

    if (qualifier instanceof Named) {
      Named named = (Named) qualifier;
      builder.append("(\"").append(named.value()).append("\")");
    }

    return builder.toString();
  }

}
