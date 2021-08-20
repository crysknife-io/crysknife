/*
 * Copyright © 2021 Treblereel
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

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import javax.inject.Named;
import javax.lang.model.element.TypeElement;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 8/19/21
 */
public class GenerationUtils {

  public static void maybeAddQualifiers(MethodCallExpr call, TypeElement field,
      String annotationName) {
    if (annotationName != null) {
      boolean isNamed = field.getAnnotation(Named.class) != null;
      annotationName = isNamed ? Named.class.getCanonicalName() : annotationName;
      ObjectCreationExpr annotation = new ObjectCreationExpr();
      annotation.setType(new ClassOrInterfaceType()
          .setName(isNamed ? Named.class.getCanonicalName() : annotationName));
      NodeList<BodyDeclaration<?>> anonymousClassBody = new NodeList<>();

      MethodDeclaration annotationType = new MethodDeclaration();
      annotationType.setModifiers(Modifier.Keyword.PUBLIC);
      annotationType.setName("annotationType");
      annotationType.setType(new ClassOrInterfaceType().setName("Class<? extends Annotation>"));
      annotationType.getBody().get()
          .addAndGetStatement(new ReturnStmt(new NameExpr(annotationName + ".class")));
      anonymousClassBody.add(annotationType);

      if (isNamed) {
        MethodDeclaration value = new MethodDeclaration();
        value.setModifiers(Modifier.Keyword.PUBLIC);
        value.setName("value");
        value.setType(new ClassOrInterfaceType().setName("String"));
        value.getBody().get().addAndGetStatement(
            new ReturnStmt(new StringLiteralExpr(field.getAnnotation(Named.class).value())));
        anonymousClassBody.add(value);
      }

      annotation.setAnonymousClassBody(anonymousClassBody);

      call.addArgument(annotation);
    }
  }
}
