package org.treblereel.gwt.crysknife.generator.api;

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
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/3/19
 */
public class ClassBuilder {

  public final BeanDefinition beanDefinition;
  Set<Expression> statementToConstructor = new HashSet<>();
  private CompilationUnit clazz = new CompilationUnit();
  private ClassOrInterfaceDeclaration classDeclaration;
  private MethodDeclaration getGetMethodDeclaration;
  private ConstructorDeclaration constructorDeclaration;
  private HashMap<String, FieldDeclaration> fields = new HashMap<>();

  public ClassBuilder(BeanDefinition beanDefinition) {
    this.beanDefinition = beanDefinition;
  }

  public void build() {
    beanDefinition.generate(this);
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

  public void addConstructorDeclaration(Modifier.Keyword... modifiers) {
    if (constructorDeclaration == null) {
      this.constructorDeclaration = classDeclaration.addConstructor(modifiers);
    }
  }

  public void addParametersToConstructor(Parameter p) {
    getConstructorDeclaration().getParameters().add(p);
  }

  private ConstructorDeclaration getConstructorDeclaration() {
    return constructorDeclaration;
  }

  public void addStatementToConstructor(Expression expr) {
    if (!statementToConstructor.contains(expr) && getConstructorDeclaration() != null) {
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
