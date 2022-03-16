/*
 * Copyright © 2022 Treblereel
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

package io.crysknife.util;

import jsinterop.annotations.JsProperty;
import org.junit.Test;
import org.mockito.Mockito;

import javax.lang.model.element.*;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class TestJ2clVariableNameGenerator {


  @Test
  public void testFieldWithJsProperty() {
    VariableElement field = getField("field", "MyClass");

    JsProperty jsProperty = new JsProperty() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return null;
      }

      @Override
      public String name() {
        return null;
      }

      @Override
      public String namespace() {
        return null;
      }
    };

    Mockito.when(field.getAnnotation(any())).thenReturn(jsProperty);
    assertEquals("field", Utils.getJsFieldName(field));
  }

  @Test
  public void testFieldPrivate() {
    VariableElement field = getField("field", "MyClass", Modifier.PRIVATE);
    assertEquals("f_field__MyClass_", Utils.getJsFieldName(field));
  }

  @Test
  public void testFieldPublic() {
    VariableElement field = getField("field", "MyClass", Modifier.PUBLIC);
    assertEquals("f_field__MyClass", Utils.getJsFieldName(field));
  }

  @Test
  public void testFieldProtected() {
    VariableElement field = getField("field", "MyClass", Modifier.PROTECTED);
    assertEquals("f_field__MyClass", Utils.getJsFieldName(field));
  }

  @Test
  public void testFieldDefault() {
    VariableElement field = getField("field", "MyClass");
    assertEquals("f_field__MyClass", Utils.getJsFieldName(field));
  }

  private VariableElement getField(String name, String clazz) {
    VariableElement field = Mockito.mock(VariableElement.class);
    TypeElement enclosingElement = Mockito.mock(TypeElement.class);
    Name fieldName = Mockito.mock(Name.class);
    Name qualifiedNameEnclosingElement = Mockito.mock(Name.class);

    Mockito.when(enclosingElement.getQualifiedName()).thenReturn(qualifiedNameEnclosingElement);
    Mockito.when(field.getEnclosingElement()).thenReturn(enclosingElement);
    Mockito.when(field.getSimpleName()).thenReturn(fieldName);
    Mockito.when(fieldName.toString()).thenReturn(name);
    Mockito.when(qualifiedNameEnclosingElement.toString()).thenReturn(clazz);
    return field;
  }

  private VariableElement getField(String name, String clazz, Modifier modifier) {
    VariableElement field = getField(name, clazz);
    Set<Modifier> modifiers = new HashSet<>();
    modifiers.add(modifier);
    Mockito.when(field.getModifiers()).thenReturn(modifiers);
    return field;
  }

}
