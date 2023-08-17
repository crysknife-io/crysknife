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

package io.crysknife.definition;

import com.google.auto.common.MoreElements;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Singleton;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/6/21
 */
public class ProducesBeanDefinition extends BeanDefinition {

  private ExecutableElement method;

  private List<AnnotationMirror> qualifiers;

  private Set<ProducesBeanDefinition> subtypes = new HashSet<>();


  public ProducesBeanDefinition(ExecutableElement method, List<AnnotationMirror> qualifiers) {
    super(method.getReturnType());
    super.setHasFactory(false);
    this.method = method;
    this.qualifiers = qualifiers;
  }

  public void addSubtype(ProducesBeanDefinition subtype) {
    subtypes.add(subtype);
  }

  public Set<ProducesBeanDefinition> getSubtypes() {
    return new HashSet<>(subtypes);
  }

  public ExecutableElement getMethod() {
    return method;
  }

  public List<AnnotationMirror> getQualifier() {
    return qualifiers;
  }

  public TypeElement getProducer() {
    return MoreElements.asType(method.getEnclosingElement());
  }

  public boolean isSingleton() {
    return method.getAnnotation(Singleton.class) != null
        || method.getAnnotation(ApplicationScoped.class) != null;
  }

  @Override
  public Annotation getScope() {
    if (method.getAnnotation(Singleton.class) != null) {
      return method.getAnnotation(Singleton.class);
    }

    if (method.getAnnotation(ApplicationScoped.class) != null) {
      return method.getAnnotation(ApplicationScoped.class);
    }

    return new Dependent() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Dependent.class;
      }
    };
  }

}
