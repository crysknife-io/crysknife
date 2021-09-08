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

package io.crysknife.generator.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import io.crysknife.nextstep.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/3/19
 */
public class ClassBuilder {

  public final BeanDefinition beanDefinition;
  private Set<Expression> statementToConstructor = new HashSet<>();
  private CompilationUnit clazz = new CompilationUnit();
  private ClassOrInterfaceDeclaration classDeclaration;
  private MethodDeclaration getGetMethodDeclaration;
  private ConstructorDeclaration constructorDeclaration;
  private HashMap<String, FieldDeclaration> fields = new HashMap<>();

  public ClassBuilder(BeanDefinition beanDefinition) {
    this.beanDefinition = beanDefinition;
  }

  public void build() {
    beanDefinition.getIocGenerator()
        .ifPresent(iocGenerator -> iocGenerator.generate(this, beanDefinition));
  }

  public String toSourceCode() {
    return clazz.toString();
  }

  public CompilationUnit getClassCompilationUnit() {
    return clazz;
  }

  public void setClassName(String className) {
    this.classDeclaration = clazz.addClass(className);
  }

  public MethodDeclaration getGetMethodDeclaration() {
    return getGetMethodDeclaration;
  }

  public void setGetMethodDeclaration(MethodDeclaration getMethodDeclaration) {
    this.getGetMethodDeclaration = getMethodDeclaration;
  }

  public FieldDeclaration addField(Type type, String name, Modifier.Keyword... modifiers) {
    if (fields.containsKey(name)) {
      return fields.get(name);
    }
    FieldDeclaration fieldDeclaration = null;
    if (getClassDeclaration() != null) {
      fieldDeclaration = getClassDeclaration().addField(type, name, modifiers);
      fields.put(name, fieldDeclaration);
    }
    return fieldDeclaration;
  }

  public ClassOrInterfaceDeclaration getClassDeclaration() {
    return classDeclaration;
  }

  public FieldDeclaration addField(String type, String name, Modifier.Keyword... modifiers) {
    if (fields.containsKey(name)) {
      return fields.get(name);
    }
    FieldDeclaration fieldDeclaration = getClassDeclaration().addField(type, name, modifiers);
    fields.put(name, fieldDeclaration);
    return fieldDeclaration;
  }

  public NodeList<ClassOrInterfaceType> getExtendedTypes() {
    return getClassDeclaration().getExtendedTypes();
  }

  public ConstructorDeclaration addConstructorDeclaration(Modifier.Keyword... modifiers) {
    if (constructorDeclaration == null) {
      this.constructorDeclaration = classDeclaration.addConstructor(modifiers);
    }
    return constructorDeclaration;
  }

  public void addParametersToConstructor(Parameter p) {
    getConstructorDeclaration().getParameters().add(p);
  }

  private ConstructorDeclaration getConstructorDeclaration() {
    return constructorDeclaration;
  }

  public void addStatementToConstructor(Expression expr) {
    if (constructorDeclaration == null) {
      this.constructorDeclaration = classDeclaration.addConstructor(Modifier.Keyword.PUBLIC);
    }

    if (!statementToConstructor.contains(expr)) {
      getConstructorDeclaration().getBody().addStatement(expr);
      statementToConstructor.add(expr);
    }
  }

  public MethodDeclaration addMethod(String methodName, Modifier.Keyword... modifiers) {
    return getClassDeclaration().addMethod(methodName, modifiers);
  }

  public NodeList<ClassOrInterfaceType> getImplementedTypes() {
    return getClassDeclaration().getImplementedTypes();
  }

  public FieldDeclaration addFieldWithInitializer(Type type, String name, Expression initializer,
      Modifier.Keyword... modifiers) {
    FieldDeclaration fieldDeclaration =
        getClassDeclaration().addFieldWithInitializer(type, name, initializer, modifiers);
    fields.put(name, fieldDeclaration);
    return fieldDeclaration;
  }

  public FieldDeclaration addFieldWithInitializer(String type, String name, Expression initializer,
      Modifier.Keyword... modifiers) {
    FieldDeclaration fieldDeclaration =
        getClassDeclaration().addFieldWithInitializer(type, name, initializer, modifiers);
    fields.put(name, fieldDeclaration);
    return fieldDeclaration;
  }
}
