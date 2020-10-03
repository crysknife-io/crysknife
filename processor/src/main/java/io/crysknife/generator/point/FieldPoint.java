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

package io.crysknife.generator.point;

import java.util.Objects;

import javax.inject.Named;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/3/19
 */
public class FieldPoint extends Point {

  private VariableElement field;

  private FieldPoint(String name, TypeElement injection, VariableElement field) {
    super(injection, name);
    this.field = field;
  }

  public static FieldPoint of(VariableElement injection) {
    TypeElement type = MoreElements.asType(MoreTypes.asElement(injection.asType()));
    return new FieldPoint(injection.getSimpleName().toString(), type, injection);
  }

  public VariableElement getField() {
    return field;
  }

  public TypeElement getEnclosingElement() {
    if (field.getEnclosingElement().getKind().isClass()) {
      return MoreElements.asType(field.getEnclosingElement());
    } else {
      return MoreElements.asType(field.getEnclosingElement().getEnclosingElement());
    }
  }

  public boolean isQualified() {
    throw new UnsupportedOperationException();
  }

  public String getNamed() {
    return field.getAnnotation(Named.class).value();
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FieldPoint that = (FieldPoint) o;
    return Objects.equals(type, that.type);
  }

  @Override
  public String toString() {
    return "FieldPoint{" + "injection=" + type + " name=" + name + (isNamed() ? getNamed() : "")
        + '}';
  }

  public boolean isNamed() {
    return field.getAnnotation(Named.class) != null;
  }
}
