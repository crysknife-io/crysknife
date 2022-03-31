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

package io.crysknife.ui.navigation.generator;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.FilerException;
import javax.enterprise.event.Event;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import io.crysknife.client.IsElement;
import io.crysknife.client.utils.CreationalCallback;
import io.crysknife.client.BeanManager;
import io.crysknife.client.internal.collections.BiMap;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.context.GenerationContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.ui.navigation.client.local.DefaultPage;
import io.crysknife.ui.navigation.client.local.HistoryToken;
import io.crysknife.ui.navigation.client.local.Page;
import io.crysknife.ui.navigation.client.local.PageHidden;
import io.crysknife.ui.navigation.client.local.PageHiding;
import io.crysknife.ui.navigation.client.local.PageShowing;
import io.crysknife.ui.navigation.client.local.PageShown;
import io.crysknife.ui.navigation.client.local.URLPattern;
import io.crysknife.ui.navigation.client.local.URLPatternMatcher;
import io.crysknife.ui.navigation.client.local.api.NavigationControl;
import io.crysknife.ui.navigation.client.local.api.PageRequest;
import io.crysknife.ui.navigation.client.local.spi.NavigationGraph;
import io.crysknife.ui.navigation.client.local.spi.PageNode;
import io.crysknife.ui.navigation.client.shared.NavigationEvent;

/**
 * Generates the GeneratedNavigationGraph class based on {@code @Page} annotations.
 * 
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
public class NavigationGraphGenerator {

  public static final String PACKAGE_NAME = NavigationGraph.class.getPackage().getName();
  public static final String CLASS_NAME = "GeneratedNavigationGraph";
  private final Set<TypeElement> pages;
  private BiMap<String, TypeElement> pageNames = new BiMap<>();
  private GenerationContext context;
  private CompilationUnit compilationUnit;

  NavigationGraphGenerator(Set<TypeElement> pages) {
    this.pages = pages;
  }

  protected String generate(TreeLogger logger, GenerationContext context) {

    final String fqcn = PACKAGE_NAME + "." + CLASS_NAME;

    return generateSource(context, fqcn);
  }

  public String generateSource(final GenerationContext context, final String fqcn) {
    this.context = context;

    compilationUnit = new CompilationUnit();
    compilationUnit.setPackageDeclaration(PACKAGE_NAME);
    ClassOrInterfaceDeclaration classDeclaration =
        compilationUnit.addClass(CLASS_NAME).setPublic(true);
    compilationUnit.addImport(NavigationGraph.class);
    compilationUnit.addImport(CreationalCallback.class);
    compilationUnit.addImport(Map.class);
    compilationUnit.addImport(HashMap.class);
    compilationUnit.addImport(HistoryToken.class);
    compilationUnit.addImport(PageRequest.class);
    compilationUnit.addImport(PageNode.class);
    compilationUnit.addImport(DefaultPage.class);
    compilationUnit.addImport(CreationalCallback.class);
    compilationUnit.addImport(NavigationEvent.class);
    compilationUnit.addImport(NavigationControl.class);
    compilationUnit.addImport(BeanManager.class);
    compilationUnit.addImport(Event.class);
    classDeclaration.getExtendedTypes().add(new ClassOrInterfaceType().setName("NavigationGraph"));

    ConstructorDeclaration constructorDeclaration =
        classDeclaration.addConstructor(Modifier.Keyword.PUBLIC);
    constructorDeclaration.addParameter(BeanManager.class.getSimpleName(), "beanManager");
    constructorDeclaration.addParameter("Event<NavigationEvent>", "event");

    MethodCallExpr superExpr =
        new MethodCallExpr("super").addArgument("beanManager").addArgument("event");
    constructorDeclaration.getBody().addAndGetStatement(superExpr);
    generatePages(constructorDeclaration);

    try {
      String fileName = fqcn;
      String source = compilationUnit.toString();
      build(fileName, source, context);
    } catch (javax.annotation.processing.FilerException e1) {
      // just ignore it
    } catch (IOException e1) {
      throw new Error(e1);
    }

    return classDeclaration.toString();
  }

  private void generatePages(ConstructorDeclaration constructorDeclaration) {
    pages.forEach(page -> {
      generatePage(page, constructorDeclaration);
    });
  }

  protected void build(String fileName, String source, GenerationContext context)
      throws IOException {
    JavaFileObject builderFile =
        context.getProcessingEnvironment().getFiler().createSourceFile(fileName);

    try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
      out.append(source);
    } catch (FilerException e) {
      throw new Error(e);
    }
  }

  private void generatePage(TypeElement page, ConstructorDeclaration ctor) {
    if (!(isAssignable(page.asType(), IsElement.class))) {
      throw new GenerationException("Class " + page
          + " is annotated with @Page, so it must implement org.jboss.elemento.IsElement or io.crysknife.client.IsElement");
    }
    compilationUnit.addImport(page.getQualifiedName().toString());
    Page annotation = page.getAnnotation(Page.class);
    String pageName = getPageName(page);

    List<? extends TypeMirror> annotatedPageRoles = getRoles(annotation);

    TypeElement prevPageWithThisName = pageNames.put(pageName, page);
    if (prevPageWithThisName != null) {
      throw new GenerationException("Page names must be unique, but " + prevPageWithThisName
          + " and " + page + " are both named [" + pageName + "]");
    }

    String pageImplStmt = generateNewInstanceOfPageImpl(page, pageName, ctor);

    TypeMirror defaultPage =
        context.getElements().getTypeElement(DefaultPage.class.getCanonicalName()).asType();
    if (annotatedPageRoles.contains(defaultPage)) {
      // need to assign the page impl to a variable and add it to the map twice
      URLPattern pattern = URLPatternMatcher.generatePattern(annotation.path());
      if (pattern.getParamList().size() > 0) {
        throw new GenerationException("Default Page must not contain any path parameters.");
      }

      ctor.getBody()
          .addStatement(
              new AssignExpr()
                  .setTarget(new VariableDeclarationExpr(
                      new ClassOrInterfaceType().setName(PageNode.class.getSimpleName()),
                      "defaultPage").setModifiers(Modifier.Keyword.FINAL))
                  .setValue(new NameExpr(pageImplStmt)));

      ctor.getBody().addStatement(new MethodCallExpr(new NameExpr("pagesByName"), "put")
          .addArgument(new StringLiteralExpr("")).addArgument(toLowerCase(page)));

      ctor.getBody()
          .addStatement(new MethodCallExpr(new NameExpr("pagesByRole"), "put")
              .addArgument(new NameExpr(DefaultPage.class.getSimpleName()) + ".class")
              .addArgument(toLowerCase(page)));
    } else if (pageName.equals("")) {
      throw new GenerationException("Page " + page + " has an empty path. Only the"
          + " page with the role DefaultPage is permitted to have an empty path.");
    }

    ctor.getBody().addStatement(new MethodCallExpr(new NameExpr("pagesByName"), "put")
        .addArgument(new StringLiteralExpr(pageName)).addArgument(toLowerCase(page)));
  }

  private boolean isAssignable(TypeMirror subType, Class<?> baseType) {
    return isAssignable(subType, getTypeMirror(baseType));
  }

  private String getPageName(TypeElement pageClass) {
    return pageClass.getSimpleName().toString();
  }

  private List<? extends TypeMirror> getRoles(Page annotation) {
    try {
      annotation.role();
    } catch (MirroredTypesException e) {
      return e.getTypeMirrors();
    }

    return null;
  }

  private String generateNewInstanceOfPageImpl(TypeElement page, String pageName,
      ConstructorDeclaration constructorDeclaration) {
    NodeList<BodyDeclaration<?>> anonymousClassBody = new NodeList<>();
    String instanceName = toLowerCase(page);

    ClassOrInterfaceType type =
        new ClassOrInterfaceType().setName("PageNode<" + page.getSimpleName().toString() + ">");
    ObjectCreationExpr expr = new ObjectCreationExpr().setType(type);

    generateName(pageName, anonymousClassBody);
    generateToString(pageName, anonymousClassBody);
    generateGetURL(page, pageName, anonymousClassBody);
    generateContentType(page, pageName, anonymousClassBody);
    generateProduceContent(page, pageName, anonymousClassBody);
    generatePageHiding(page, pageName, anonymousClassBody);
    generatePageHidden(page, pageName, anonymousClassBody);

    appendPageShowingMethod(page, pageName, anonymousClassBody);
    appendPageShownMethod(page, pageName, anonymousClassBody);
    appendPageUpdateMethod(page, pageName, anonymousClassBody);
    appendDestroyMethod(page, pageName, anonymousClassBody);

    expr.setAnonymousClassBody(anonymousClassBody);

    constructorDeclaration.getBody()
        .addAndGetStatement(new AssignExpr()
            .setTarget(new VariableDeclarationExpr(
                new ClassOrInterfaceType().setName(PageNode.class.getSimpleName()), instanceName))
            .setValue(expr));
    return instanceName;
  }

  private boolean isAssignable(TypeMirror subType, TypeMirror baseType) {
    return context.getTypes().isAssignable(context.getTypes().erasure(subType),
        context.getTypes().erasure(baseType));
  }

  private TypeMirror getTypeMirror(Class<?> c) {
    return context.getElements().getTypeElement(c.getName()).asType();
  }

  private String toLowerCase(TypeElement page) {
    return Character.toLowerCase(page.getSimpleName().toString().charAt(0))
        + page.getSimpleName().toString().substring(1);
  }

  private void generateName(String pageName, NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.setName("name");
    method.setType(new ClassOrInterfaceType().setName("String"));
    method.getBody().get().addAndGetStatement(new ReturnStmt(new StringLiteralExpr(pageName)));
    anonymousClassBody.add(method);
  }

  private void generateToString(String pageName, NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.setName("toString");
    method.setType(new ClassOrInterfaceType().setName("String"));
    method.getBody().get().addAndGetStatement(new ReturnStmt(new StringLiteralExpr(pageName)));
    anonymousClassBody.add(method);
  }

  private void generateGetURL(TypeElement page, String pageName,
      NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.setName("getURL");
    method.setType(new ClassOrInterfaceType().setName("String"));
    method.getBody().get()
        .addAndGetStatement(new ReturnStmt(new StringLiteralExpr(getPageURL(page, pageName))));
    anonymousClassBody.add(method);
  }

  private void generateContentType(TypeElement page, String pageName,
      NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.setName("contentType");
    method.setType(new ClassOrInterfaceType().setName("Class"));
    method.getBody().get()
        .addAndGetStatement(new ReturnStmt(new NameExpr(page.getQualifiedName() + ".class")));
    anonymousClassBody.add(method);
  }

  private void generateProduceContent(TypeElement page, String pageName,
      NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.addParameter(CreationalCallback.class.getSimpleName(), "callback");
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.setName("produceContent");
    method.setType("void");

    method.getBody().get().addAndGetStatement(

        new MethodCallExpr(new NameExpr("callback"), "callback").addArgument(
            new MethodCallExpr(new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
                .addArgument(page.getQualifiedName() + ".class"), "getInstance")));

    anonymousClassBody.add(method);
  }

  private void generatePageHiding(TypeElement page, String pageName,
      NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.addParameter(page.getSimpleName().toString(), "widget");
    method.addParameter(NavigationControl.class.getSimpleName(), "control");
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.setName("pageHiding");
    method.setType("void");

    ExecutableElement executableElement = checkMethod(page, PageHiding.class);
    if (executableElement != null) {
      method.getBody().get().addAndGetStatement(
          new MethodCallExpr(new NameExpr("widget"), executableElement.getSimpleName().toString()));
    }
    method.getBody().get()
        .addAndGetStatement(new MethodCallExpr(new NameExpr("control"), "proceed"));
    anonymousClassBody.add(method);
  }

  private void generatePageHidden(TypeElement page, String pageName,
      NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.addParameter(page.getSimpleName().toString(), "widget");
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.setType("void");
    method.setName("pageHidden");
    ExecutableElement executableElement = checkMethod(page, PageHidden.class);
    if (executableElement != null) {
      method.getBody().get().addAndGetStatement(
          new MethodCallExpr(new NameExpr("widget"), executableElement.getSimpleName().toString()));
    }
    anonymousClassBody.add(method);
  }

  private void appendPageShowingMethod(TypeElement page, String pageName,
      NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.setName("pageShowing");
    method.setType("void");

    method.addParameter(page.getSimpleName().toString(), "widget");
    method.addParameter(HistoryToken.class.getSimpleName(), "state");
    method.addParameter(NavigationControl.class.getSimpleName(), "control");

    ObjectCreationExpr mapCreationExpr = new ObjectCreationExpr()
        .setType(new ClassOrInterfaceType().setName(HashMap.class.getSimpleName()));

    method.getBody().get()
        .addAndGetStatement(new AssignExpr()
            .setTarget(new VariableDeclarationExpr(
                new ClassOrInterfaceType().setName(Map.class.getSimpleName()), "pageState"))
            .setValue(mapCreationExpr));
    ObjectCreationExpr pageRequest = new ObjectCreationExpr()
        .setType(new ClassOrInterfaceType().setName(PageRequest.class.getSimpleName()))
        .addArgument(new StringLiteralExpr(pageName)).addArgument("pageState");

    ObjectCreationExpr navigationEvent = new ObjectCreationExpr()
        .setType(new ClassOrInterfaceType().setName(NavigationEvent.class.getSimpleName()))
        .addArgument(pageRequest);

    method.getBody().get().addAndGetStatement(
        new MethodCallExpr(new NameExpr("event"), "fire").addArgument(navigationEvent));

    ExecutableElement executableElement = checkMethod(page, PageShowing.class);
    if (executableElement != null) {
      method.getBody().get().addAndGetStatement(
          new MethodCallExpr(new NameExpr("widget"), executableElement.getSimpleName().toString()));
    }
    method.getBody().get()
        .addAndGetStatement(new MethodCallExpr(new NameExpr("control"), "proceed"));
    anonymousClassBody.add(method);
  }

  private void appendPageShownMethod(TypeElement page, String pageName,
      NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.setName("pageShown");
    method.setType("void");

    method.addParameter(page.getSimpleName().toString(), "widget");
    method.addParameter(HistoryToken.class.getSimpleName(), "state");

    ObjectCreationExpr mapCreationExpr = new ObjectCreationExpr()
        .setType(new ClassOrInterfaceType().setName(HashMap.class.getSimpleName()));

    method.getBody().get()
        .addAndGetStatement(new AssignExpr()
            .setTarget(new VariableDeclarationExpr(
                new ClassOrInterfaceType().setName(Map.class.getSimpleName()), "pageState"))
            .setValue(mapCreationExpr));

    ExecutableElement executableElement = checkMethod(page, PageShown.class);
    if (executableElement != null) {
      method.getBody().get().addAndGetStatement(
          new MethodCallExpr(new NameExpr("widget"), executableElement.getSimpleName().toString()));
    }
    anonymousClassBody.add(method);
  }

  private void appendPageUpdateMethod(TypeElement page, String pageName,
      NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.setName("pageUpdate");
    method.setType("void");

    method.addParameter(page.getSimpleName().toString(), "widget");
    method.addParameter(HistoryToken.class.getSimpleName(), "state");

    ObjectCreationExpr mapCreationExpr = new ObjectCreationExpr()
        .setType(new ClassOrInterfaceType().setName(HashMap.class.getSimpleName()));

    method.getBody().get()
        .addAndGetStatement(new AssignExpr()
            .setTarget(new VariableDeclarationExpr(
                new ClassOrInterfaceType().setName(Map.class.getSimpleName()), "pageState"))
            .setValue(mapCreationExpr));
    anonymousClassBody.add(method);
  }

  private void appendDestroyMethod(TypeElement page, String pageName,
      NodeList<BodyDeclaration<?>> anonymousClassBody) {
    MethodDeclaration method = new MethodDeclaration();
    method.addParameter(page.getSimpleName().toString(), "widget");
    method.setModifiers(Modifier.Keyword.PUBLIC);
    method.setName("destroy");
    method.setType("void");

    method.getBody().get().addAndGetStatement(
        new MethodCallExpr(new NameExpr("beanManager"), "destroyBean").addArgument("widget"));
    anonymousClassBody.add(method);
  }

  private String getPageURL(TypeElement pageClass, String pageName) {
    Page pageAnnotation = pageClass.getAnnotation(Page.class);
    String path = pageAnnotation.path();
    if (path.equals("")) {
      return pageName;
    }

    return path;
  }

  private ExecutableElement checkMethod(TypeElement pageClass,
      Class<? extends Annotation> annotation) {
    List<ExecutableElement> annotatedMethods = pageClass.getEnclosedElements().stream()
        .filter(elm -> elm.getKind().equals(ElementKind.METHOD))
        .map(elm -> MoreElements.asExecutable(elm))
        .filter(elm -> elm.getAnnotation(annotation) != null).collect(Collectors.toList());

    if (annotatedMethods.size() > 1) {
      throw new UnsupportedOperationException(
          "A @Page can have at most 1 " + createAnnotationName(annotation) + " method, but "
              + pageClass + " has " + annotatedMethods.size());
    }

    if (annotatedMethods.size() == 1) {
      ExecutableElement method = annotatedMethods.get(0);

      if (!method.getReturnType().getKind().equals(TypeKind.VOID)) {
        throw new UnsupportedOperationException(createAnnotationName(annotation)
            + " methods must have a void return type, but " + method.getEnclosingElement() + "."
            + method.getSimpleName() + " returns " + method.getReturnType());
      }

      if (!method.getModifiers().contains(javax.lang.model.element.Modifier.PUBLIC)) {
        throw new UnsupportedOperationException(
            "Method " + method.getEnclosingElement() + "." + method.getSimpleName()
                + " annotated with " + createAnnotationName(annotation) + " must be public ");
      }

      return method;
    }
    return null;
  }

  private String createAnnotationName(Class<? extends Annotation> annotation) {
    return "@" + annotation.getSimpleName();
  }

  protected boolean isRelevantClass(TypeElement clazz) {
    return null != clazz.getAnnotation(Page.class);
  }
}
