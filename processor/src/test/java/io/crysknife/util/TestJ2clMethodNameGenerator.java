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

import jsinterop.annotations.JsMethod;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class TestJ2clMethodNameGenerator {

  @Test
  public void testMethodDefault() {
    ExecutableElement method =
        getMethod("run_default", "org.treblereel.test", "org.treblereel.test.Runner");
    assertEquals("m_run_default___$pp_org_treblereel_test", Utils.getJsMethodName(method));
  }

  @Test
  public void testMethodDefaultWithArg() {
    VariableElement arg = getParam("java.lang.String");
    ExecutableElement method =
        getMethod("run_default", "org.treblereel.test", "org.treblereel.test.Runner", arg);
    assertEquals("m_run_default__java_lang_String_$pp_org_treblereel_test",
        Utils.getJsMethodName(method));
  }

  @Test
  public void testMethodJsMethod() {
    ExecutableElement method = getMethod("method", "org.treblereel.test", "MyClass");
    JsMethod jsMethod = new JsMethod() {

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

    Mockito.when(method.getAnnotation(any())).thenReturn(jsMethod);
    assertEquals("method", Utils.getJsMethodName(method));
  }

  @Test
  public void testMethodPublic() {
    ExecutableElement method =
        getMethod("run_public", "org.treblereel.test", "org.treblereel.test.Runner");
    addModifier(method, Modifier.PUBLIC);
    assertEquals("m_run_public__", Utils.getJsMethodName(method));
  }

  @Test
  public void testMethodPublicWithArg() {
    VariableElement arg = getParam("java.lang.String");
    ExecutableElement method =
        getMethod("run_public", "org.treblereel.test", "org.treblereel.test.Runner", arg);
    addModifier(method, Modifier.PUBLIC);
    assertEquals("m_run_public__java_lang_String", Utils.getJsMethodName(method));
  }

  @Test
  public void testMethodProtected() {
    ExecutableElement method =
        getMethod("run", "org.treblereel.test", "org.treblereel.test.Runner");
    addModifier(method, Modifier.PROTECTED);
    assertEquals("m_run__", Utils.getJsMethodName(method));
  }

  @Test
  public void testMethodProtectedWithArg() {
    VariableElement arg = getParam("java.lang.String");
    ExecutableElement method =
        getMethod("run", "org.treblereel.test", "org.treblereel.test.Runner", arg);
    addModifier(method, Modifier.PROTECTED);
    assertEquals("m_run__java_lang_String", Utils.getJsMethodName(method));
  }

  @Test
  public void testMethodPrivate() {
    ExecutableElement method =
        getMethod("run_private", "org.treblereel.test", "org.treblereel.test.Runner");
    addModifier(method, Modifier.PRIVATE);
    assertEquals("m_run_private___$p_org_treblereel_test_Runner", Utils.getJsMethodName(method));
  }

  @Test
  public void testMethodPrivateWithArg() {
    VariableElement arg = getParam("java.lang.String");
    ExecutableElement method =
        getMethod("run_private", "org.treblereel.test", "org.treblereel.test.Runner", arg);
    addModifier(method, Modifier.PRIVATE);
    assertEquals("m_run_private__java_lang_String_$p_org_treblereel_test_Runner",
        Utils.getJsMethodName(method));
  }

  private ExecutableElement getMethod(String name, String pkg, String clazz) {
    ExecutableElement method = mock(ExecutableElement.class);
    PackageElement packageElement = mock(PackageElement.class);

    TypeElement enclosingElement = mock(TypeElement.class);
    Name methodName = mock(Name.class);
    Name packageName = mock(Name.class);
    Name qualifiedNameEnclosingElement = mock(Name.class);

    Mockito.when(enclosingElement.getQualifiedName()).thenReturn(qualifiedNameEnclosingElement);
    Mockito.when(method.getEnclosingElement()).thenReturn(enclosingElement);
    Mockito.when(enclosingElement.getEnclosingElement()).thenReturn(packageElement);
    Mockito.when(method.getSimpleName()).thenReturn(methodName);
    Mockito.when(packageElement.getQualifiedName()).thenReturn(packageName);
    Mockito.when(packageElement.toString()).thenReturn(pkg);
    Mockito.when(methodName.toString()).thenReturn(name);
    Mockito.when(packageName.toString()).thenReturn(pkg);
    Mockito.when(qualifiedNameEnclosingElement.toString()).thenReturn(clazz);

    return method;
  }

  private ExecutableElement getMethod(String name, String pkg, String clazz,
      VariableElement... arg) {
    ExecutableElement method = getMethod(name, pkg, clazz);
    List<VariableElement> args = new ArrayList<>();
    for (VariableElement variableElement : arg) {
      args.add(variableElement);
    }
    Mockito.when((Object) method.getParameters()).thenReturn(args);

    return method;
  }

  private ExecutableElement addModifier(ExecutableElement method, Modifier modifier) {
    Set<Modifier> modifiers = new HashSet<>();
    modifiers.add(modifier);
    Mockito.when(method.getModifiers()).thenReturn(modifiers);
    return method;
  }

  private VariableElement getParam(String type) {
    VariableElement param = Mockito.mock(VariableElement.class);
    TypeMirror typeMirror = Mockito.mock(TypeMirror.class);

    Mockito.when(param.asType()).thenReturn(typeMirror);
    Mockito.when(typeMirror.toString()).thenReturn(type);

    return param;
  }
}
