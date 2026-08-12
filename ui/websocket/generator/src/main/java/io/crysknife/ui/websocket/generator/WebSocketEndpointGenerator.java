/*
 * Copyright © 2026 Treblereel
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

package io.crysknife.ui.websocket.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import jakarta.inject.Inject;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.api.Generator;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.ui.websocket.client.BrowserWebSocketConnector;
import io.crysknife.ui.websocket.client.WebSocketConnector;

@Generator(priority = 100003)
public class WebSocketEndpointGenerator extends IOCGenerator<BeanDefinition> {

    private TypeMirror sessionType;
    private TypeMirror closeReasonType;
    private TypeMirror throwableType;
    private TypeMirror stringType;

    public WebSocketEndpointGenerator(TreeLogger treeLogger, IOCContext iocContext) {
        super(treeLogger, iocContext);
    }

    @Override
    public void register() {
        iocContext.register(ClientEndpoint.class, WiringElementType.CLASS_DECORATOR, this);

        iocContext.register(Inject.class, WebSocketConnector.class,
                WiringElementType.FIELD_TYPE, this);
        iocContext.getBuildIn().add(WebSocketConnector.class.getCanonicalName());

        sessionType = asType("jakarta.websocket.Session");
        closeReasonType = asType("jakarta.websocket.CloseReason");
        throwableType = asType("java.lang.Throwable");
        stringType = asType("java.lang.String");
    }

    @Override
    public void generate(ClassMetaInfo classMetaInfo, BeanDefinition beanDefinition) {
        TypeElement typeElement = MoreTypes.asTypeElement(beanDefinition.getType());
        if (typeElement.getAnnotation(ClientEndpoint.class) == null) {
            return;
        }
        String packageName = elements.getPackageOf(typeElement).getQualifiedName().toString();
        String simpleName = typeElement.getSimpleName().toString();
        String generatedName = simpleName + "_WebSocketEndpoint";
        String qualifiedName = packageName + "." + generatedName;

        ExecutableElement onOpenMethod = null;
        ExecutableElement onTextMessageMethod = null;
        ExecutableElement onBinaryMessageMethod = null;
        ExecutableElement onCloseMethod = null;
        ExecutableElement onErrorMethod = null;

        for (ExecutableElement method :
                ElementFilter.methodsIn(elements.getAllMembers(typeElement))) {
            if (method.getAnnotation(OnOpen.class) != null) {
                if (onOpenMethod != null) {
                    throw new GenerationException(
                            "Multiple @OnOpen methods in " + simpleName);
                }
                validateOnOpen(method);
                onOpenMethod = method;
            }
            if (method.getAnnotation(OnMessage.class) != null) {
                validateOnMessage(method);
                if (hasByteArrayParam(method)) {
                    if (onBinaryMessageMethod != null) {
                        throw new GenerationException(
                                "Multiple binary @OnMessage methods in "
                                        + simpleName);
                    }
                    onBinaryMessageMethod = method;
                } else {
                    if (onTextMessageMethod != null) {
                        throw new GenerationException(
                                "Multiple text @OnMessage methods in "
                                        + simpleName);
                    }
                    onTextMessageMethod = method;
                }
            }
            if (method.getAnnotation(OnClose.class) != null) {
                if (onCloseMethod != null) {
                    throw new GenerationException(
                            "Multiple @OnClose methods in " + simpleName);
                }
                validateOnClose(method);
                onCloseMethod = method;
            }
            if (method.getAnnotation(OnError.class) != null) {
                if (onErrorMethod != null) {
                    throw new GenerationException(
                            "Multiple @OnError methods in " + simpleName);
                }
                validateOnError(method);
                onErrorMethod = method;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("import jakarta.websocket.CloseReason;\n");
        sb.append("import jakarta.websocket.Session;\n");
        sb.append("import org.treblereel.gwt.websocket.client.WebSocketConfig;\n");
        sb.append("import org.treblereel.gwt.websocket.client.proxy.")
                .append("AbstractWebSocketEndpoint;\n\n");

        sb.append("public class ").append(generatedName)
                .append(" extends AbstractWebSocketEndpoint {\n\n");
        sb.append("    private final ").append(simpleName).append(" delegate;\n\n");

        sb.append("    public ").append(generatedName).append("(")
                .append(simpleName).append(" delegate, WebSocketConfig config) {\n");
        sb.append("        super(config);\n");
        sb.append("        this.delegate = delegate;\n");
        sb.append("    }\n");

        if (onOpenMethod != null) {
            sb.append("\n    @Override\n");
            sb.append("    protected void handleOpen(")
                    .append("Session session) {\n");
            sb.append("        delegate.").append(onOpenMethod.getSimpleName())
                    .append("(")
                    .append(buildDelegateArgs(onOpenMethod,
                            this::isSession, "session"))
                    .append(");\n");
            sb.append("    }\n");
        }

        if (onTextMessageMethod != null) {
            sb.append("\n    @Override\n");
            sb.append("    protected void handleTextMessage(")
                    .append("String message, ")
                    .append("Session session) {\n");
            sb.append("        delegate.")
                    .append(onTextMessageMethod.getSimpleName())
                    .append("(")
                    .append(buildDelegateArgs(onTextMessageMethod,
                            this::isSession, "session",
                            this::isString, "message"))
                    .append(");\n");
            sb.append("    }\n");
        }

        if (onBinaryMessageMethod != null) {
            sb.append("\n    @Override\n");
            sb.append("    protected void handleBinaryMessage(")
                    .append("byte[] data, Session session) {\n");
            sb.append("        delegate.")
                    .append(onBinaryMessageMethod.getSimpleName())
                    .append("(")
                    .append(buildDelegateArgs(onBinaryMessageMethod,
                            this::isSession, "session",
                            this::isByteArray, "data"))
                    .append(");\n");
            sb.append("    }\n");
        }

        if (onCloseMethod != null) {
            sb.append("\n    @Override\n");
            sb.append("    protected void handleClose(")
                    .append("CloseReason closeReason, ")
                    .append("Session session) {\n");
            sb.append("        delegate.").append(onCloseMethod.getSimpleName())
                    .append("(")
                    .append(buildDelegateArgs(onCloseMethod,
                            this::isSession, "session",
                            this::isCloseReason, "closeReason"))
                    .append(");\n");
            sb.append("    }\n");
        }

        if (onErrorMethod != null) {
            sb.append("\n    @Override\n");
            sb.append("    protected void handleError(")
                    .append("Throwable error, ")
                    .append("Session session) {\n");
            sb.append("        delegate.").append(onErrorMethod.getSimpleName())
                    .append("(")
                    .append(buildDelegateArgs(onErrorMethod,
                            this::isSession, "session",
                            this::isThrowable, "error"))
                    .append(");\n");
            sb.append("    }\n");
        }

        sb.append("}\n");

        writeJavaFile(qualifiedName, sb.toString());
    }

    @Override
    public String generateBeanLookupCall(InjectableVariableDefinition fieldPoint) {
        DeclaredType declaredType = (DeclaredType) fieldPoint.getVariableElement().asType();
        List<? extends TypeMirror> typeArgs = declaredType.getTypeArguments();
        if (typeArgs.isEmpty()) {
            throw new GenerationException(
                    "WebSocketConnector must have a type parameter, "
                            + "e.g. WebSocketConnector<MyEndpoint>");
        }
        String endpointQualified = typeArgs.get(0).toString();
        String endpointImplQualified = endpointQualified + "_WebSocketEndpoint";

        ObjectCreationExpr newConnector = new ObjectCreationExpr()
                .setType(new ClassOrInterfaceType(null,
                        BrowserWebSocketConnector.class.getCanonicalName()))
                .addArgument("() -> (" + endpointQualified
                        + ") beanManager.lookupBean("
                        + endpointQualified + ".class).getInstance()")
                .addArgument("(java.util.function.BiFunction<" + endpointQualified
                        + ", org.treblereel.gwt.websocket.client.WebSocketConfig, "
                        + "org.treblereel.gwt.websocket.client.proxy.AbstractWebSocketEndpoint>) "
                        + "(delegate, config) -> new "
                        + endpointImplQualified + "(((" + endpointQualified
                        + ") delegate), config)");

        return generationUtils.wrapCallInstanceImpl(newConnector).toString();
    }

    private void validateOnOpen(ExecutableElement method) {
        for (VariableElement param : method.getParameters()) {
            if (!isSession(param.asType())) {
                throw paramError(param, method, "OnOpen", "Session");
            }
        }
    }

    private void validateOnMessage(ExecutableElement method) {
        boolean hasPayload = false;
        for (VariableElement param : method.getParameters()) {
            TypeMirror paramType = param.asType();
            if (!isSession(paramType) && !isString(paramType)
                    && !isByteArray(paramType)) {
                throw paramError(param, method, "OnMessage",
                        "Session, String, byte[]");
            }
            if (isString(paramType) || isByteArray(paramType)) {
                hasPayload = true;
            }
        }
        if (!hasPayload) {
            throw new GenerationException(
                    "@OnMessage method " + method.getSimpleName()
                            + " must have at least one payload parameter"
                            + " (String or byte[])");
        }
    }

    private void validateOnClose(ExecutableElement method) {
        for (VariableElement param : method.getParameters()) {
            TypeMirror paramType = param.asType();
            if (!isSession(paramType) && !isCloseReason(paramType)) {
                throw paramError(param, method, "OnClose",
                        "Session, CloseReason");
            }
        }
    }

    private void validateOnError(ExecutableElement method) {
        for (VariableElement param : method.getParameters()) {
            TypeMirror paramType = param.asType();
            if (!isSession(paramType) && !isThrowable(paramType)) {
                throw paramError(param, method, "OnError",
                        "Session, Throwable");
            }
        }
    }

    private GenerationException paramError(VariableElement param,
            ExecutableElement method, String annotation, String allowed) {
        return new GenerationException(
                "Invalid parameter type '" + param.asType()
                        + "' in @" + annotation + " method "
                        + method.getSimpleName()
                        + ". Allowed: " + allowed);
    }

    private boolean hasByteArrayParam(ExecutableElement method) {
        for (VariableElement param : method.getParameters()) {
            if (isByteArray(param.asType())) {
                return true;
            }
        }
        return false;
    }

    private String buildDelegateArgs(ExecutableElement method,
            Predicate<TypeMirror> test1, String var1) {
        return buildDelegateArgs(method, test1, var1, p -> false, null);
    }

    private String buildDelegateArgs(ExecutableElement method,
            Predicate<TypeMirror> test1, String var1,
            Predicate<TypeMirror> test2, String var2) {
        List<? extends VariableElement> params = method.getParameters();
        if (params.isEmpty()) {
            return "";
        }
        List<String> args = new ArrayList<>();
        for (VariableElement param : params) {
            TypeMirror paramType = param.asType();
            if (test1.test(paramType)) {
                args.add(var1);
            } else if (test2.test(paramType)) {
                args.add(var2);
            } else {
                args.add("null");
            }
        }
        return String.join(", ", args);
    }

    private boolean isSession(TypeMirror type) {
        return types.isSameType(type, sessionType);
    }

    private boolean isCloseReason(TypeMirror type) {
        return types.isSameType(type, closeReasonType);
    }

    private boolean isThrowable(TypeMirror type) {
        return types.isSubtype(type, throwableType);
    }

    private boolean isString(TypeMirror type) {
        return types.isSameType(type, stringType);
    }

    private boolean isByteArray(TypeMirror type) {
        return type.getKind() == TypeKind.ARRAY
                && ((ArrayType) type).getComponentType().getKind() == TypeKind.BYTE;
    }

    private TypeMirror asType(String fqcn) {
        TypeElement element = elements.getTypeElement(fqcn);
        if (element == null) {
            throw new GenerationException("Cannot resolve type: " + fqcn);
        }
        return element.asType();
    }
}
