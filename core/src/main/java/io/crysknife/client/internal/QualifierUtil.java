/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Specializes;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Named;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A utility class for testing the equality of qualifiers at runtime.
 *
 * @author Mike Brock
 */
public class QualifierUtil {

  public static final Annotation DEFAULT_ANNOTATION = new Default() {
    @Override
    public Class<? extends Annotation> annotationType() {
      return Default.class;
    }

    @Override
    public String toString() {
      return "@Default";
    };
  };

  public static final Annotation SPECIALIZES_ANNOTATION = new Specializes() {
    @Override
    public Class<? extends Annotation> annotationType() {
      return Specializes.class;
    }

    @Override
    public String toString() {
      return "@Specializes";
    };
  };

  public static final Annotation ANY_ANNOTATION = new Any() {
    @Override
    public Class<? extends Annotation> annotationType() {
      return Any.class;
    }

    @Override
    public String toString() {
      return "@Any";
    };
  };

  public static final Annotation[] DEFAULT_QUALIFIERS =
      new Annotation[] {DEFAULT_ANNOTATION, ANY_ANNOTATION};

  private static final Map<String, Annotation> DEFAULT_MATCHING_MAP =
      new HashMap<String, Annotation>() {
        {
          for (final Annotation a : DEFAULT_QUALIFIERS) {
            put(a.annotationType().getName(), a);
          }
        }
      };

  public static boolean isSameType(final Annotation a1, final Annotation a2) {
    return !(a1 == null || a2 == null) && a1.annotationType().equals(a2.annotationType());
  }

  public static boolean matches(final Annotation[] allOf, final Annotation[] in) {
    if (in.length == 0) {
      return true;
    } else {
      return contains(Arrays.asList(allOf), Arrays.asList(in));
    }
  }

  /**
   * @param allOf A collection of qualifiers that must be satisfied.
   * @param in A collection of qualifiers for potentially satisfying {@code allOf}. If this
   *        collection is empty, then it represents the universal qualifier that satisfies all other
   *        qualifiers. This is unambiguous since it is otherwise impossible to have no qualifiers
   *        (everything has {@link Any}).
   * @return If {@code in} is non-empty then this returns true iff every annotation in
   *         {@code allOff} contains an equal annotation in {@code in}. If {@code in} is empty, then
   *         this returns true.
   */
  public static boolean matches(final Collection<Annotation> allOf,
      final Collection<Annotation> in) {
    if (in.isEmpty()) {
      return true;
    } else {
      return contains(allOf, in);
    }
  }

  public static boolean contains(final Collection<Annotation> allOf,
      final Collection<Annotation> in) {
    if (allOf.isEmpty())
      return true;

    final Map<String, Annotation> allOfMap = new HashMap<>();
    final Map<String, Annotation> inMap = new HashMap<>();

    for (final Annotation a : in) {
      inMap.put(BeanManagerUtil.qualifierToString(a), a);
    }

    for (final Annotation a : allOf) {
      allOfMap.put(BeanManagerUtil.qualifierToString(a), a);
    }

    if (!inMap.keySet().containsAll(allOfMap.keySet())) {
      return false;
    }

    return true;
  }

  public static boolean isDefaultAnnotations(final Annotation[] annotations) {
    return annotations == null || isDefaultAnnotations(Arrays.asList(annotations));
  }


  public static boolean isDefaultAnnotations(final Collection<Annotation> annotations) {
    return annotations == null
        || (annotations.size() == 2 && contains(DEFAULT_MATCHING_MAP.values(), annotations));
  }

  public static Named createNamed(final String name) {
    return new Named() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Named.class;
      }

      @Override
      public String value() {
        return name;
      }
    };
  }

  public static Typed createTyped(Class... classes) {
    return new Typed() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Typed.class;
      }

      @Override
      public Class[] value() {
        return classes;
      }
    };
  }

  public static String print(Annotation[] annotations) {
    return Arrays.stream(annotations).map(e -> e.annotationType().getSimpleName())
        .collect(Collectors.joining(","));
  }

  public static String print(Collection<Annotation> annotations) {
    return annotations.stream().map(e -> e.annotationType().getSimpleName())
        .collect(Collectors.joining(","));
  }

}
