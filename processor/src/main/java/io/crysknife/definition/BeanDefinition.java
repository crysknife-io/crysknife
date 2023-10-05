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

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Singleton;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.CircularDependency;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.util.TypeUtils;


/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/3/21
 */
public class BeanDefinition implements Definition {

  private final TypeMirror type;
  private final Set<InjectableVariableDefinition> fields = new LinkedHashSet<>();
  private final Set<InjectionParameterDefinition> constructorParams = new LinkedHashSet<>();
  private final Set<MethodDefinition> methods = new LinkedHashSet<>();
  private final Set<BeanDefinition> dependencies = new LinkedHashSet<>();
  private final Set<IOCGenerator<BeanDefinition>> decorators = new LinkedHashSet<>();
  private Optional<IOCGenerator<BeanDefinition>> iocGenerator = Optional.empty();
  private final Set<BeanDefinition> subclasses = new LinkedHashSet<>();
  private boolean hasFactory = true;

  private boolean factoryGenerationFinished = false;


  public BeanDefinition(TypeMirror type) {
    this.type = type;
  }

  public TypeMirror getType() {
    return type;
  }

  public Set<InjectableVariableDefinition> getFields() {
    return fields;
  }

  public Set<InjectionParameterDefinition> getConstructorParams() {
    return constructorParams;
  }

  public List<? extends AnnotationMirror> getAnnotationMirrors() {
    return MoreTypes.asTypeElement(type).getAnnotationMirrors();
  }

  public Set<BeanDefinition> getSubclasses() {
    return subclasses;
  }

  public Set<MethodDefinition> getMethods() {
    return methods;
  }

  public Set<BeanDefinition> getDependencies() {
    return dependencies;
  }

  public Optional<IOCGenerator<BeanDefinition>> getIocGenerator() {
    return iocGenerator;
  }

  public void setIocGenerator(IOCGenerator<BeanDefinition> iocGenerator) {
    this.iocGenerator = Optional.of(iocGenerator);
  }

  public boolean isProxy() {
    return MoreTypes.asTypeElement(type).getAnnotation(CircularDependency.class) != null;
  }

  public boolean isAlternative() {
    return MoreElements.isAnnotationPresent(MoreTypes.asTypeElement(type), Alternative.class);
  }

  public String getPackageName() {
    return TypeUtils.getPackageName(MoreTypes.asTypeElement(type));
  }

  public String getQualifiedName() {
    return MoreTypes.asTypeElement(type).getQualifiedName().toString();
  }

  public String getSimpleClassName() {
    return TypeUtils.getSimpleClassName(type);
  }

  public Set<IOCGenerator<BeanDefinition>> getDecorators() {
    return decorators;
  }

  public Annotation getScope() {
    if (MoreTypes.asTypeElement(type).getAnnotation(Singleton.class) != null) {
      return MoreTypes.asTypeElement(type).getAnnotation(Singleton.class);
    }

    if (MoreTypes.asTypeElement(type).getAnnotation(jakarta.ejb.Singleton.class) != null) {
      return MoreTypes.asTypeElement(type).getAnnotation(jakarta.ejb.Singleton.class);
    }

    if (MoreTypes.asTypeElement(type).getAnnotation(ApplicationScoped.class) != null) {
      return MoreTypes.asTypeElement(type).getAnnotation(ApplicationScoped.class);
    }

    return new Dependent() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Dependent.class;
      }
    };
  }

  public boolean hasFactory() {
    return hasFactory;
  }

  public void setHasFactory(boolean hasFactory) {
    this.hasFactory = hasFactory;
  }


  public boolean isFactoryGenerationFinished() {
    return factoryGenerationFinished;
  }

  public void setFactoryGenerationFinished(boolean factoryGenerationFinished) {
    this.factoryGenerationFinished = factoryGenerationFinished;
  }

  @Override
  public int hashCode() {
    return Objects.hash(MoreTypes.asTypeElement(type).getQualifiedName().toString());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    BeanDefinition that = (BeanDefinition) o;
    return MoreTypes.asTypeElement(type).getQualifiedName().toString()
        .equals(MoreTypes.asTypeElement(that.type).getQualifiedName().toString());
  }
}
