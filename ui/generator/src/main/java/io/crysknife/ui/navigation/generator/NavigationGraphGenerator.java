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

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Specializes;
import jakarta.inject.Inject;
import javax.annotation.processing.FilerException;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
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
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import io.crysknife.client.BeanManager;
import io.crysknife.client.IsElement;
import io.crysknife.client.internal.collections.BiMap;
import io.crysknife.client.utils.CreationalCallback;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.context.GenerationContext;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.ui.navigation.client.DefaultPage;
import io.crysknife.ui.navigation.client.HistoryToken;
import io.crysknife.ui.navigation.client.LoginPage;
import io.crysknife.ui.navigation.client.NavigationEvent;
import io.crysknife.ui.navigation.client.PageRequest;
import io.crysknife.ui.navigation.client.SecurityError;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.navigation.client.annotation.PageHidden;
import io.crysknife.ui.navigation.client.annotation.PageHiding;
import io.crysknife.ui.navigation.client.annotation.PageShowing;
import io.crysknife.ui.navigation.client.annotation.PageShown;
import io.crysknife.ui.navigation.client.annotation.PageState;
import io.crysknife.ui.navigation.client.internal.NavigationControl;
import io.crysknife.ui.navigation.client.internal.NavigationGraph;
import io.crysknife.ui.navigation.client.internal.PageNode;
import io.crysknife.ui.security.SecurityContext;
import io.crysknife.util.GenerationUtils;

/**
 * Generates the GeneratedNavigationGraph class based on {@code @Page} annotations.
 *
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
// TODO this class must be refactored
public class NavigationGraphGenerator {

    public static final String PACKAGE_NAME = NavigationGraph.class.getPackage().getName();
    public static final String CLASS_NAME = "GeneratedNavigationGraph";
    private final Set<TypeElement> pages;
    private BiMap<String, TypeElement> pageNames = new BiMap<>();
    private IOCContext context;
    private CompilationUnit compilationUnit;

    private GenerationUtils generationUtils;

    private TypeMirror navigationGraph;

    NavigationGraphGenerator(IOCContext context, Set<TypeElement> pages) {
        this.context = context;
        this.generationUtils = new GenerationUtils(context);
        this.navigationGraph = context.getTypeMirror(NavigationGraph.class.getCanonicalName());
        this.pages = pages;
    }

    protected String generate(TreeLogger logger) {
        final String fqcn = PACKAGE_NAME + "." + CLASS_NAME;
        return generateSource(fqcn, logger);
    }

    public String generateSource(final String fqcn, TreeLogger logger) {
        logger.log(TreeLogger.Type.DEBUG, "generating " + fqcn);
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
        compilationUnit.addImport(ApplicationScoped.class);
        compilationUnit.addImport(Specializes.class);
        if (hasSecurityAnnotatedPages()) {
            compilationUnit.addImport(SecurityContext.class);
            compilationUnit.addImport(LoginPage.class);
            compilationUnit.addImport(SecurityError.class);
        }


        classDeclaration.getExtendedTypes().add(new ClassOrInterfaceType().setName("NavigationGraph"));
        classDeclaration.addAnnotation(ApplicationScoped.class);
        classDeclaration.addAnnotation(Specializes.class);


        ConstructorDeclaration constructorDeclaration =
                classDeclaration.addConstructor(Modifier.Keyword.PUBLIC);
        generatePages(constructorDeclaration);

        constructorDeclaration.addAnnotation(Inject.class);
        constructorDeclaration.addParameter(BeanManager.class, "beanManager");
        constructorDeclaration.addParameter("Event<NavigationEvent>", "event");

        classDeclaration.addField(BeanManager.class, "beanManager", Modifier.Keyword.PRIVATE)
                .addAnnotation(Inject.class);
        classDeclaration.addField("Event<NavigationEvent>", "event", Modifier.Keyword.PRIVATE)
                .addAnnotation(Inject.class);

        try {
            String fileName = fqcn;
            String source = compilationUnit.toString();
            build(fileName, source, context.getGenerationContext());
        } catch (javax.annotation.processing.FilerException e1) {
            // just ignore it
        } catch (IOException e1) {
            throw new GenerationException(e1);
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
            throw new GenerationException(e);
        }
    }

    private void generatePage(TypeElement page, ConstructorDeclaration ctor) {
        if (!(isAssignable(page.asType(), IsElement.class))) {
            throw new GenerationException("Class " + page
                    + " is annotated with @Page, so it must implement io.crysknife.client.IsElement");
        }
        compilationUnit.addImport(page.getQualifiedName().toString());
        generatePageStateHelperClass(page);
        Page annotation = page.getAnnotation(Page.class);
        String pageName = getPageName(page);

        Set<String> annotatedPageRoles = getRoles(annotation);

        TypeElement prevPageWithThisName = pageNames.put(pageName, page);
        if (prevPageWithThisName != null) {
            throw new GenerationException("Page names must be unique, but " + prevPageWithThisName
                    + " and " + page + " are both named [" + pageName + "]");
        }

        String pageImplStmt = generateNewInstanceOfPageImpl(page, pageName, ctor);
        if (annotatedPageRoles.contains(DefaultPage.class.getCanonicalName())) {
            // need to assign the page impl to a variable and add it to the map twice
            if (!"".equals(annotation.path())) {
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

        for (String role : annotatedPageRoles) {
            if (!role.equals(DefaultPage.class.getCanonicalName())) {
                String simpleRoleName = role.substring(role.lastIndexOf('.') + 1);
                compilationUnit.addImport(role);
                ctor.getBody()
                        .addStatement(new MethodCallExpr(new NameExpr("pagesByRole"), "put")
                                .addArgument(new NameExpr(simpleRoleName + ".class"))
                                .addArgument(toLowerCase(page)));
            }
        }
    }

    private boolean isAssignable(TypeMirror subType, Class<?> baseType) {
        return isAssignable(subType, getTypeMirror(baseType));
    }

    private String getPageName(TypeElement pageClass) {
        return pageClass.getSimpleName().toString();
    }

    private Set<String> getRoles(Page annotation) {
        try {
            annotation.role();
        } catch (MirroredTypesException e) {
            return e.getTypeMirrors().stream().map(elm -> elm.toString()).collect(Collectors.toSet());
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
        return context.getGenerationContext().getTypes().isAssignable(
                context.getGenerationContext().getTypes().erasure(subType),
                context.getGenerationContext().getTypes().erasure(baseType));
    }

    private TypeMirror getTypeMirror(Class<?> c) {
        return context.getGenerationContext().getElements().getTypeElement(c.getName()).asType();
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
        method.getBody().ifPresent(
                body -> body.addAndGetStatement(new ReturnStmt(new StringLiteralExpr(pageName))));
        anonymousClassBody.add(method);
    }

    private void generateToString(String pageName, NodeList<BodyDeclaration<?>> anonymousClassBody) {
        MethodDeclaration method = new MethodDeclaration();
        method.setModifiers(Modifier.Keyword.PUBLIC);
        method.setName("toString");
        method.setType(new ClassOrInterfaceType().setName("String"));
        method.getBody().ifPresent(
                body -> body.addAndGetStatement(new ReturnStmt(new StringLiteralExpr(pageName))));
        anonymousClassBody.add(method);
    }

    private void generateGetURL(TypeElement page, String pageName,
                                NodeList<BodyDeclaration<?>> anonymousClassBody) {
        MethodDeclaration method = new MethodDeclaration();
        method.setModifiers(Modifier.Keyword.PUBLIC);
        method.setName("getURL");
        method.setType(new ClassOrInterfaceType().setName("String"));
        method.getBody().ifPresent(body -> body
                .addAndGetStatement(new ReturnStmt(new StringLiteralExpr(getPageURL(page, pageName)))));
        anonymousClassBody.add(method);
    }

    private void generateContentType(TypeElement page, String pageName,
                                     NodeList<BodyDeclaration<?>> anonymousClassBody) {
        MethodDeclaration method = new MethodDeclaration();
        method.setModifiers(Modifier.Keyword.PUBLIC);
        method.setName("contentType");
        method.setType(new ClassOrInterfaceType().setName("Class"));
        method.getBody().ifPresent(body -> body
                .addAndGetStatement(new ReturnStmt(new NameExpr(page.getQualifiedName() + ".class"))));
        anonymousClassBody.add(method);
    }

    private void generateProduceContent(TypeElement page, String pageName,
                                        NodeList<BodyDeclaration<?>> anonymousClassBody) {
        MethodDeclaration method = new MethodDeclaration();
        method.addParameter(CreationalCallback.class.getSimpleName(), "callback");
        method.setModifiers(Modifier.Keyword.PUBLIC);
        method.setName("produceContent");
        method.setType("void");
        method.getBody()
                .ifPresent(body -> body.addAndGetStatement(
                        new MethodCallExpr(new NameExpr("callback"), "callback").addArgument(
                                new MethodCallExpr(new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
                                        .addArgument(page.getQualifiedName() + ".class"), "getInstance"))));
        anonymousClassBody.add(method);
    }

    private void generatePageHiding(TypeElement page, String pageName,
                                    NodeList<BodyDeclaration<?>> anonymousClassBody) {
        MethodDeclaration method = new MethodDeclaration();
        method.addParameter(page.getSimpleName().toString(), "instance");
        method.addParameter(NavigationControl.class.getSimpleName(), "control");
        method.setModifiers(Modifier.Keyword.PUBLIC);
        method.setName("pageHiding");
        method.setType("void");

        ExecutableElement executableElement = checkMethod(page, PageHiding.class);
        if (executableElement != null) {
            method.getBody().ifPresent(body -> body.addAndGetStatement(
                    generationUtils.generateMethodCall(navigationGraph, executableElement)));
        }
        method.getBody().ifPresent(
                body -> body.addAndGetStatement(new MethodCallExpr(new NameExpr("control"), "proceed")));
        anonymousClassBody.add(method);
    }

    private void generatePageHidden(TypeElement page, String pageName,
                                    NodeList<BodyDeclaration<?>> anonymousClassBody) {
        MethodDeclaration method = new MethodDeclaration();
        method.addParameter(page.getSimpleName().toString(), "instance");
        method.setModifiers(Modifier.Keyword.PUBLIC);
        method.setType("void");
        method.setName("pageHidden");
        ExecutableElement executableElement = checkMethod(page, PageHidden.class);
        if (executableElement != null) {
            method.getBody().ifPresent(body -> body.addAndGetStatement(
                    generationUtils.generateMethodCall(navigationGraph, executableElement)));
        }
        anonymousClassBody.add(method);
    }

    private void appendPageShowingMethod(TypeElement page, String pageName,
                                         NodeList<BodyDeclaration<?>> anonymousClassBody) {
        MethodDeclaration method = new MethodDeclaration();
        method.setModifiers(Modifier.Keyword.PUBLIC);
        method.setName("pageShowing");
        method.setType("void");

        method.addParameter(page.getSimpleName().toString(), "instance");
        method.addParameter(HistoryToken.class.getSimpleName(), "state");
        method.addParameter(NavigationControl.class.getSimpleName(), "control");

        generateSecurityGuard(page, method);

        ObjectCreationExpr mapCreationExpr = new ObjectCreationExpr()
                .setType(new ClassOrInterfaceType().setName(HashMap.class.getSimpleName()));


        method.getBody()
                .ifPresent(body -> body.addAndGetStatement(new AssignExpr()
                        .setTarget(new VariableDeclarationExpr(
                                new ClassOrInterfaceType().setName(Map.class.getSimpleName()), "pageState"))
                        .setValue(mapCreationExpr)));
        ObjectCreationExpr pageRequest = new ObjectCreationExpr()
                .setType(new ClassOrInterfaceType().setName(PageRequest.class.getSimpleName()))
                .addArgument(new StringLiteralExpr(pageName)).addArgument("pageState");

        ObjectCreationExpr navigationEvent = new ObjectCreationExpr()
                .setType(new ClassOrInterfaceType().setName(NavigationEvent.class.getSimpleName()))
                .addArgument(pageRequest);

        method.getBody().get().addAndGetStatement(
                new MethodCallExpr(new NameExpr("event"), "fire").addArgument(navigationEvent));

        generatePageStateInjection(page, method);

        ExecutableElement executableElement = checkMethod(page, PageShowing.class);
        if (executableElement != null) {
            method.getBody().ifPresent(body -> body.addAndGetStatement(
                    generationUtils.generateMethodCall(navigationGraph, executableElement)));
        }
        method.getBody().ifPresent(
                body -> body.addAndGetStatement(new MethodCallExpr(new NameExpr("control"), "proceed")));
        anonymousClassBody.add(method);
    }

    private void generateSecurityGuard(TypeElement page, MethodDeclaration method) {
        DenyAll denyAll = page.getAnnotation(DenyAll.class);
        PermitAll permitAll = page.getAnnotation(PermitAll.class);
        RolesAllowed rolesAllowed = page.getAnnotation(RolesAllowed.class);

        if (denyAll == null && rolesAllowed == null) {
            return;
        }

        if (permitAll != null) {
            return;
        }

        method.getBody().ifPresent(body -> {
            // SecurityContext _secCtx = (SecurityContext) beanManager.lookupBean(SecurityContext.class).getInstance();
            CastExpr castExpr = new CastExpr(
                    new ClassOrInterfaceType().setName("SecurityContext"),
                    new MethodCallExpr(
                            new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
                                    .addArgument("SecurityContext.class"),
                            "getInstance"));
            body.addAndGetStatement(new AssignExpr()
                    .setTarget(new VariableDeclarationExpr(
                            new ClassOrInterfaceType().setName("SecurityContext"), "_secCtx"))
                    .setValue(castExpr));

            if (denyAll != null) {
                // @DenyAll: always redirect to SecurityError
                body.addAndGetStatement(
                        new MethodCallExpr(new NameExpr("control"), "redirectToRole")
                                .addArgument("SecurityError.class"));
                body.addAndGetStatement(new ReturnStmt());
            } else {
                // @RolesAllowed: check login, then check roles
                String[] roles = rolesAllowed.value();

                // if (!_secCtx.isLoggedIn()) { control.redirectToRole(LoginPage.class); return; }
                BlockStmt loginBlock = new BlockStmt();
                loginBlock.addStatement(
                        new MethodCallExpr(new NameExpr("control"), "redirectToRole")
                                .addArgument("LoginPage.class"));
                loginBlock.addStatement(new ReturnStmt());

                body.addAndGetStatement(new IfStmt(
                        new UnaryExpr(
                                new MethodCallExpr(new NameExpr("_secCtx"), "isLoggedIn"),
                                UnaryExpr.Operator.LOGICAL_COMPLEMENT),
                        loginBlock, null));

                // if (!_secCtx.isUserInAllRoles("role1", "role2")) { control.redirectToRole(SecurityError.class); return; }
                MethodCallExpr rolesCheck = new MethodCallExpr(new NameExpr("_secCtx"), "isUserInAllRoles");
                for (String role : roles) {
                    rolesCheck.addArgument(new StringLiteralExpr(role));
                }

                BlockStmt rolesBlock = new BlockStmt();
                rolesBlock.addStatement(
                        new MethodCallExpr(new NameExpr("control"), "redirectToRole")
                                .addArgument("SecurityError.class"));
                rolesBlock.addStatement(new ReturnStmt());

                body.addAndGetStatement(new IfStmt(
                        new UnaryExpr(rolesCheck, UnaryExpr.Operator.LOGICAL_COMPLEMENT),
                        rolesBlock, null));
            }
        });
    }

    private void appendPageShownMethod(TypeElement page, String pageName,
                                       NodeList<BodyDeclaration<?>> anonymousClassBody) {
        MethodDeclaration method = new MethodDeclaration();
        method.setModifiers(Modifier.Keyword.PUBLIC);
        method.setName("pageShown");
        method.setType("void");

        method.addParameter(page.getSimpleName().toString(), "instance");
        method.addParameter(HistoryToken.class.getSimpleName(), "state");

        ObjectCreationExpr mapCreationExpr = new ObjectCreationExpr()
                .setType(new ClassOrInterfaceType().setName(HashMap.class.getSimpleName()));
        method.getBody()
                .ifPresent(body -> body.addAndGetStatement(new AssignExpr()
                        .setTarget(new VariableDeclarationExpr(
                                new ClassOrInterfaceType().setName(Map.class.getSimpleName()), "pageState"))
                        .setValue(mapCreationExpr)));

        generatePageStateInjection(page, method);

        ExecutableElement executableElement = checkMethod(page, PageShown.class);
        if (executableElement != null) {
            method.getBody().ifPresent(body -> body.addAndGetStatement(
                    generationUtils.generateMethodCall(navigationGraph, executableElement)));
        }
        anonymousClassBody.add(method);
    }

    private void appendPageUpdateMethod(TypeElement page, String pageName,
                                        NodeList<BodyDeclaration<?>> anonymousClassBody) {
        MethodDeclaration method = new MethodDeclaration();
        method.setModifiers(Modifier.Keyword.PUBLIC);
        method.setName("pageUpdate");
        method.setType("void");

        method.addParameter(page.getSimpleName().toString(), "instance");
        method.addParameter(HistoryToken.class.getSimpleName(), "state");

        ObjectCreationExpr mapCreationExpr = new ObjectCreationExpr()
                .setType(new ClassOrInterfaceType().setName(HashMap.class.getSimpleName()));
        method.getBody()
                .ifPresent(body -> body.addAndGetStatement(new AssignExpr()
                        .setTarget(new VariableDeclarationExpr(
                                new ClassOrInterfaceType().setName(Map.class.getSimpleName()), "pageState"))
                        .setValue(mapCreationExpr)));

        generatePageStateInjection(page, method);

        anonymousClassBody.add(method);
    }

    private void appendDestroyMethod(TypeElement page, String pageName,
                                     NodeList<BodyDeclaration<?>> anonymousClassBody) {
        MethodDeclaration method = new MethodDeclaration();
        method.addParameter(page.getSimpleName().toString(), "instance");
        method.setModifiers(Modifier.Keyword.PUBLIC);
        method.setName("destroy");
        method.setType("void");
        boolean isSingleton =
                page.getAnnotation(jakarta.inject.Singleton.class) != null
                        || page.getAnnotation(ApplicationScoped.class) != null;
        if (!isSingleton) {
            method.getBody().ifPresent(body -> body.addAndGetStatement(
                    new MethodCallExpr(new NameExpr("beanManager"), "destroyBean")
                            .addArgument("instance")));
        }
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

    private static final Map<String, String> SUPPORTED_TYPES = new HashMap<>();

    static {
        SUPPORTED_TYPES.put("java.lang.String", null);
        SUPPORTED_TYPES.put("int", "Integer.parseInt");
        SUPPORTED_TYPES.put("java.lang.Integer", "Integer.parseInt");
        SUPPORTED_TYPES.put("long", "Long.parseLong");
        SUPPORTED_TYPES.put("java.lang.Long", "Long.parseLong");
        SUPPORTED_TYPES.put("boolean", "Boolean.parseBoolean");
        SUPPORTED_TYPES.put("java.lang.Boolean", "Boolean.parseBoolean");
        SUPPORTED_TYPES.put("double", "Double.parseDouble");
        SUPPORTED_TYPES.put("java.lang.Double", "Double.parseDouble");
        SUPPORTED_TYPES.put("float", "Float.parseFloat");
        SUPPORTED_TYPES.put("java.lang.Float", "Float.parseFloat");
        SUPPORTED_TYPES.put("short", "Short.parseShort");
        SUPPORTED_TYPES.put("java.lang.Short", "Short.parseShort");
        SUPPORTED_TYPES.put("byte", "Byte.parseByte");
        SUPPORTED_TYPES.put("java.lang.Byte", "Byte.parseByte");
    }

    private static final Map<String, String> SUPPORTED_LIST_ELEMENT_TYPES = new HashMap<>();

    static {
        SUPPORTED_LIST_ELEMENT_TYPES.put("java.lang.String", null);
        SUPPORTED_LIST_ELEMENT_TYPES.put("java.lang.Integer", "Integer::parseInt");
        SUPPORTED_LIST_ELEMENT_TYPES.put("java.lang.Long", "Long::parseLong");
        SUPPORTED_LIST_ELEMENT_TYPES.put("java.lang.Boolean", "Boolean::parseBoolean");
        SUPPORTED_LIST_ELEMENT_TYPES.put("java.lang.Double", "Double::parseDouble");
        SUPPORTED_LIST_ELEMENT_TYPES.put("java.lang.Float", "Float::parseFloat");
        SUPPORTED_LIST_ELEMENT_TYPES.put("java.lang.Short", "Short::parseShort");
        SUPPORTED_LIST_ELEMENT_TYPES.put("java.lang.Byte", "Byte::parseByte");
    }

    private List<VariableElement> findPageStateFields(TypeElement pageClass) {
        List<VariableElement> fields = pageClass.getEnclosedElements().stream()
                .filter(elm -> elm.getKind().equals(ElementKind.FIELD))
                .map(elm -> MoreElements.asVariable(elm))
                .filter(elm -> elm.getAnnotation(PageState.class) != null)
                .collect(Collectors.toList());

        for (VariableElement field : fields) {
            if (field.getModifiers().contains(javax.lang.model.element.Modifier.PRIVATE)) {
                throw new GenerationException("@PageState field [" + field.getSimpleName()
                        + "] in " + pageClass.getQualifiedName() + " must not be private");
            }
            if (field.getModifiers().contains(javax.lang.model.element.Modifier.FINAL)) {
                throw new GenerationException("@PageState field [" + field.getSimpleName()
                        + "] in " + pageClass.getQualifiedName() + " must not be final");
            }
            String typeName = field.asType().toString();
            if (isListType(field)) {
                String elementType = getListElementType(field);
                if (!SUPPORTED_LIST_ELEMENT_TYPES.containsKey(elementType)) {
                    throw new GenerationException("@PageState field [" + field.getSimpleName()
                            + "] in " + pageClass.getQualifiedName()
                            + " has unsupported List element type " + elementType
                            + ". Supported: List<String>, List<Integer>, List<Long>,"
                            + " List<Boolean>, List<Double>, List<Float>, List<Short>,"
                            + " List<Byte>");
                }
                String defaultValue = field.getAnnotation(PageState.class).defaultValue();
                if (!PageState.DEFAULT_VALUE_UNSET.equals(defaultValue)) {
                    throw new GenerationException("@PageState field [" + field.getSimpleName()
                            + "] in " + pageClass.getQualifiedName()
                            + ": defaultValue is not supported for List fields");
                }
            } else if (!SUPPORTED_TYPES.containsKey(typeName)) {
                throw new GenerationException("@PageState field [" + field.getSimpleName()
                        + "] in " + pageClass.getQualifiedName()
                        + " has unsupported type " + typeName
                        + ". Supported: String, int, long, boolean, double, float, short, byte,"
                        + " their boxed equivalents, and List<BoxedType>");
            }
        }
        return fields;
    }

    private String wrapWithConversion(String rawExpr, String typeName) {
        String parseMethod = SUPPORTED_TYPES.get(typeName);
        if (parseMethod == null) {
            return rawExpr;
        }
        return parseMethod + "(" + rawExpr + ")";
    }

    private boolean isListType(VariableElement field) {
        TypeMirror type = field.asType();
        if (!(type instanceof DeclaredType)) {
            return false;
        }
        DeclaredType dt = (DeclaredType) type;
        TypeMirror erasure = context.getGenerationContext().getTypes().erasure(type);
        TypeElement listElement =
                context.getGenerationContext().getElements().getTypeElement("java.util.List");
        TypeMirror listErasure =
                context.getGenerationContext().getTypes().erasure(listElement.asType());
        return context.getGenerationContext().getTypes().isSameType(erasure, listErasure)
                && dt.getTypeArguments().size() == 1;
    }

    private String getListElementType(VariableElement field) {
        DeclaredType dt = (DeclaredType) field.asType();
        return dt.getTypeArguments().get(0).toString();
    }

    private String getHelperClassName(TypeElement page) {
        return page.getSimpleName().toString() + "_PageStateHelper";
    }

    private String getHelperFqcn(TypeElement page) {
        String pkg = MoreElements.getPackage(page).getQualifiedName().toString();
        return pkg + "." + getHelperClassName(page);
    }

    private String getSimpleFieldTypeName(String typeName) {
        int dot = typeName.lastIndexOf('.');
        return dot >= 0 ? typeName.substring(dot + 1) : typeName;
    }

    private void generatePageStateHelperClass(TypeElement page) {
        List<VariableElement> fields = findPageStateFields(page);
        if (fields.isEmpty()) {
            return;
        }

        String pkg = MoreElements.getPackage(page).getQualifiedName().toString();
        String helperClassName = getHelperClassName(page);
        String pageSimpleName = page.getSimpleName().toString();

        CompilationUnit helperCu = new CompilationUnit();
        helperCu.setPackageDeclaration(pkg);

        ClassOrInterfaceDeclaration helperClass =
                helperCu.addClass(helperClassName).setPublic(true);

        boolean needsListImport = false;

        for (VariableElement field : fields) {
            String fieldName = field.getSimpleName().toString();
            String simpleType;

            if (isListType(field)) {
                String elementFqcn = getListElementType(field);
                String simpleElement = getSimpleFieldTypeName(elementFqcn);
                simpleType = "List<" + simpleElement + ">";
                needsListImport = true;
            } else {
                simpleType = getSimpleFieldTypeName(field.asType().toString());
            }

            MethodDeclaration setter = helperClass.addMethod(
                    "set_" + fieldName,
                    Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
            setter.setType("void");
            setter.addParameter(pageSimpleName, "instance");
            setter.addParameter(simpleType, "value");
            setter.getBody().ifPresent(body ->
                    body.addStatement(new AssignExpr()
                            .setTarget(new NameExpr("instance." + fieldName))
                            .setValue(new NameExpr("value"))));
        }

        if (needsListImport) {
            helperCu.addImport("java.util.List");
        }

        try {
            String fqcn = pkg + "." + helperClassName;
            build(fqcn, helperCu.toString(), context.getGenerationContext());
        } catch (FilerException e) {
            // ignore if already generated
        } catch (IOException e) {
            throw new GenerationException(e);
        }
    }

    private void generatePageStateInjection(TypeElement page, MethodDeclaration method) {
        List<VariableElement> fields = findPageStateFields(page);
        if (fields.isEmpty()) {
            return;
        }

        String helperFqcn = getHelperFqcn(page);
        String helperSimple = getHelperClassName(page);
        compilationUnit.addImport(helperFqcn);
        compilationUnit.addImport(java.util.List.class);

        for (VariableElement field : fields) {
            PageState ann = field.getAnnotation(PageState.class);
            String paramName = ann.value().isEmpty()
                    ? field.getSimpleName().toString() : ann.value();
            String defaultValue = ann.defaultValue();
            boolean hasDefault = !PageState.DEFAULT_VALUE_UNSET.equals(defaultValue);
            String typeName = field.asType().toString();
            String fieldName = field.getSimpleName().toString();

            String valuesVar = "_" + fieldName + "_values";

            method.getBody().ifPresent(body -> body.addAndGetStatement(
                    new AssignExpr()
                            .setTarget(new VariableDeclarationExpr(
                                    new ClassOrInterfaceType().setName("List<String>"), valuesVar))
                            .setValue(new MethodCallExpr(
                                    new MethodCallExpr(new NameExpr("state"), "getState"),
                                    "get").addArgument(new StringLiteralExpr(paramName)))));

            if (isListType(field)) {
                String elementType = getListElementType(field);
                String mapRef = SUPPORTED_LIST_ELEMENT_TYPES.get(elementType);
                MethodCallExpr setterCall = new MethodCallExpr(
                        new NameExpr(helperSimple), "set_" + fieldName)
                        .addArgument("instance");
                if (mapRef == null) {
                    setterCall.addArgument(new NameExpr(valuesVar));
                } else {
                    compilationUnit.addImport("java.util.stream.Collectors");
                    String streamExpr = valuesVar + ".stream().map(" + mapRef
                            + ").collect(Collectors.toList())";
                    setterCall.addArgument(new NameExpr(streamExpr));
                }
                method.getBody().ifPresent(body -> body.addAndGetStatement(setterCall));
            } else {
                String convertedGet = wrapWithConversion(valuesVar + ".get(0)", typeName);

                MethodCallExpr setterCall = new MethodCallExpr(
                        new NameExpr(helperSimple), "set_" + fieldName)
                        .addArgument("instance");

                if (hasDefault) {
                    String convertedDefault = wrapWithConversion(
                            "\"" + defaultValue.replace("\"", "\\\"") + "\"", typeName);
                    String valueExpr = valuesVar + ".isEmpty() ? " + convertedDefault
                            + " : " + convertedGet;
                    setterCall.addArgument(new NameExpr(valueExpr));
                    method.getBody().ifPresent(body -> body.addAndGetStatement(setterCall));
                } else {
                    setterCall.addArgument(new NameExpr(convertedGet));
                    method.getBody().ifPresent(body -> {
                        com.github.javaparser.ast.stmt.IfStmt ifStmt =
                                new com.github.javaparser.ast.stmt.IfStmt();
                        ifStmt.setCondition(new NameExpr("!" + valuesVar + ".isEmpty()"));
                        ifStmt.setThenStmt(
                                new com.github.javaparser.ast.stmt.BlockStmt()
                                        .addAndGetStatement(setterCall));
                        body.addAndGetStatement(ifStmt);
                    });
                }
            }
        }
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

            if (!method.getParameters().isEmpty()) {
                throw new UnsupportedOperationException(
                        "Method " + method.getEnclosingElement() + "." + method.getSimpleName()
                                + " annotated with " + createAnnotationName(annotation) + " must have no args");
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

    private boolean hasSecurityAnnotatedPages() {
        return pages.stream().anyMatch(page ->
                page.getAnnotation(DenyAll.class) != null
                        || page.getAnnotation(RolesAllowed.class) != null);
    }
}
