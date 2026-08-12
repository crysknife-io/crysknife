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

package io.crysknife.ui.translation.generator;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.javascript.jscomp.GoogleJsMessageIdGenerator;
import com.google.javascript.jscomp.JsMessage;
import com.google.javascript.jscomp.JsMessageVisitor;
import io.crysknife.client.InstanceFactory;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.api.Generator;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.ui.translation.client.annotation.TranslationBundle;
import io.crysknife.ui.translation.client.annotation.TranslationKey;
import org.treblereel.j2cl.processors.utils.J2CLUtils;

/**
 * Derived from GWT3-processor TranslationBundleGenerator, adapted for Crysknife.
 */
@Generator(priority = 100002)
public class TranslationBundleGenerator extends IOCGenerator<BeanDefinition> {

  private static final String XTB_PACKAGE = "io.crysknife.ui.translation";

  private final J2CLUtils j2clUtils;
  private final GoogleJsMessageIdGenerator idGenerator = new GoogleJsMessageIdGenerator(null);

  // Global across all bundles: key -> msgId
  private final Map<String, String> defaultMessageMapping = new LinkedHashMap<>();
  // Global across all bundles: locale -> (key -> translatedValue)
  private final Map<String, Map<String, String>> localeMapping = new LinkedHashMap<>();
  private boolean afterExecuted = false;

  public TranslationBundleGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
    this.j2clUtils =
        new J2CLUtils(iocContext.getGenerationContext().getProcessingEnvironment());
  }

  @Override
  public void register() {
    Set<TypeElement> bundles = iocContext.getTypeElementsByAnnotation(
        TranslationBundle.class.getCanonicalName());

    for (TypeElement bundle : bundles) {
      iocContext.register(Inject.class, bundle, WiringElementType.BEAN, this);
    }
  }

  @Override
  public void generate(ClassMetaInfo classMetaInfo, BeanDefinition beanDefinition) {
    TypeElement bundleInterface = MoreTypes.asTypeElement(beanDefinition.getType());
    List<ExecutableElement> methods = collectAndValidateMethods(bundleInterface);

    generateImpl(bundleInterface, methods);
    generateNative(bundleInterface, methods);
    processBundles(bundleInterface, methods);
  }

  @Override
  public void after() {
    if (afterExecuted) {
      return;
    }
    afterExecuted = true;
    processMapping();
    for (Map.Entry<String, Map<String, String>> entry : localeMapping.entrySet()) {
      String locale = entry.getKey();
      if (!locale.isEmpty()) {
        generateXTB(locale, entry.getValue());
      }
    }
  }

  @Override
  public String generateBeanLookupCall(InjectableVariableDefinition fieldPoint) {
    String interfaceName = generationUtils.getActualQualifiedBeanName(fieldPoint);
    String implName = interfaceName + "Impl";

    ClassOrInterfaceType type = new ClassOrInterfaceType();
    type.setName(InstanceFactory.class.getCanonicalName());
    type.setTypeArguments(new ClassOrInterfaceType().setName(interfaceName));

    NodeList<BodyDeclaration<?>> body = new NodeList<>();

    MethodDeclaration getInstance = new MethodDeclaration();
    getInstance.setModifiers(com.github.javaparser.ast.Modifier.Keyword.PUBLIC);
    getInstance.setName("getInstance");
    getInstance.addAnnotation(Override.class);
    getInstance.setType(new ClassOrInterfaceType().setName(interfaceName));
    getInstance.getBody().get()
        .addAndGetStatement(new ReturnStmt(
            new ObjectCreationExpr()
                .setType(new ClassOrInterfaceType().setName(implName))));
    body.add(getInstance);

    return new ObjectCreationExpr().setType(type).setAnonymousClassBody(body).toString();
  }

  // --- Validation ---

  private List<ExecutableElement> collectAndValidateMethods(TypeElement bundleInterface) {
    checkBean(bundleInterface);
    List<ExecutableElement> methods = new ArrayList<>();
    for (javax.lang.model.element.Element enclosed : bundleInterface.getEnclosedElements()) {
      if (enclosed.getKind() == ElementKind.METHOD) {
        ExecutableElement method = (ExecutableElement) enclosed;
        if (method.getAnnotation(TranslationKey.class) != null) {
          checkMethod(method);
          methods.add(method);
        }
      }
    }
    return methods;
  }

  private void checkBean(TypeElement element) {
    if (element.getKind() != ElementKind.INTERFACE) {
      throw new GenerationException(
          "@TranslationBundle target " + element.getQualifiedName()
              + " must be a public interface");
    }
    if (!element.getModifiers().contains(Modifier.PUBLIC)) {
      throw new GenerationException(
          "@TranslationBundle target " + element.getQualifiedName()
              + " must be a public interface");
    }
  }

  private void checkMethod(ExecutableElement method) {
    if (!method.getModifiers().contains(Modifier.PUBLIC)
        || !method.getModifiers().contains(Modifier.ABSTRACT)) {
      throw new GenerationException(
          "@TranslationKey method " + method.getSimpleName()
              + " must be public and abstract");
    }
    if (method.isDefault()) {
      throw new GenerationException(
          "@TranslationKey method " + method.getSimpleName()
              + " must be public and abstract");
    }
    String stringType = String.class.getCanonicalName();
    for (javax.lang.model.element.VariableElement param : method.getParameters()) {
      if (!param.asType().toString().equals(stringType)) {
        throw new GenerationException(
            "@TranslationKey method " + method.getSimpleName()
                + ": all parameters must be String, but found " + param.asType());
      }
    }
    TranslationKey annotation = method.getAnnotation(TranslationKey.class);
    validatePlaceholders(method, annotation);
  }

  private void validatePlaceholders(ExecutableElement method, TranslationKey annotation) {
    String defaultValue = annotation.defaultValue();
    List<JsMessage.Part> parts = parseMessageParts(defaultValue);
    List<String> placeholders = parts.stream()
        .filter(JsMessage.Part::isPlaceholder)
        .map(JsMessage.Part::getCanonicalPlaceholderName)
        .collect(Collectors.toList());
    List<String> paramNames = method.getParameters().stream()
        .map(p -> p.getSimpleName().toString())
        .collect(Collectors.toList());

    if (placeholders.size() != paramNames.size()) {
      throw new GenerationException(
          "@TranslationKey method " + method.getSimpleName()
              + ": placeholders must match method parameters. Found " + placeholders.size()
              + " placeholders but " + paramNames.size() + " parameters");
    }
    for (String ph : placeholders) {
      if (!paramNames.contains(ph.toLowerCase())) {
        throw new GenerationException(
            "@TranslationKey method " + method.getSimpleName()
                + ": placeholder {$" + ph + "} has no matching parameter");
      }
    }
  }

  // --- Java Stub Generation ---

  private void generateImpl(TypeElement bundleInterface, List<ExecutableElement> methods) {
    String pkg = elements.getPackageOf(bundleInterface).getQualifiedName().toString();
    String simpleName = bundleInterface.getSimpleName().toString();
    String implName = simpleName + "Impl";
    String qualifiedImpl = pkg + "." + implName;

    StringBuilder sb = new StringBuilder();
    sb.append("package ").append(pkg).append(";\n\n");
    sb.append("public class ").append(implName).append(" implements ").append(simpleName)
        .append(" {\n\n");
    sb.append("  private final UnsupportedOperationException exception =\n");
    sb.append("      new UnsupportedOperationException(")
        .append("\"must be implemented by crysknife\");\n\n");

    for (ExecutableElement method : methods) {
      sb.append("  public String ").append(method.getSimpleName()).append("(");
      List<String> params = method.getParameters().stream()
          .map(p -> "String " + p.getSimpleName())
          .collect(Collectors.toList());
      sb.append(String.join(", ", params));
      sb.append(") {\n");
      sb.append("    throw exception;\n");
      sb.append("  }\n\n");
    }

    sb.append("}\n");

    writeJavaFile(qualifiedImpl, sb.toString());
  }

  // --- Native JS Generation ---

  private void generateNative(TypeElement bundleInterface, List<ExecutableElement> methods) {
    String pkg = elements.getPackageOf(bundleInterface).getQualifiedName().toString();
    String simpleName = bundleInterface.getSimpleName().toString();
    String implName = simpleName + "Impl";

    StringBuilder sb = new StringBuilder();
    sb.append("\n\n\n");

    List<ExecutableElement> sorted = methods.stream()
        .sorted((a, b) -> a.getSimpleName().toString().compareTo(b.getSimpleName().toString()))
        .collect(Collectors.toList());

    for (ExecutableElement method : sorted) {
      writeMethod(sb, implName, simpleName, method);
    }

    writeResource(pkg, implName + ".native.js", sb.toString());
  }

  private void writeMethod(StringBuilder sb, String implName, String bundleSimpleName, ExecutableElement method) {
    String mangledName = j2clUtils.getMethodMangledName(method);
    TranslationKey annotation = method.getAnnotation(TranslationKey.class);
    String key = annotation.key().equals("<auto>")
        ? method.getSimpleName().toString()
        : annotation.key();
    String qualifiedKey = bundleSimpleName + "." + key;

    List<String> paramNames = method.getParameters().stream()
        .map(p -> "_" + p.getSimpleName())
        .collect(Collectors.toList());

    sb.append(implName).append(".prototype.").append(mangledName)
        .append(" = function(").append(String.join(", ", paramNames)).append(") {\n");

    writeMsg(sb, qualifiedKey, annotation, method);

    sb.append("  return MSG_").append(qualifiedKey.replace('.', '_')).append(";\n");
    sb.append("}\n\n");

    // Record for XTB generation
    String msgId = computeMessageId(qualifiedKey, annotation.defaultValue());
    defaultMessageMapping.put(qualifiedKey, msgId);
  }

  private void writeMsg(StringBuilder sb, String key, TranslationKey annotation,
      ExecutableElement method) {
    String defaultValue = annotation.defaultValue();
    String jsVarName = key.replace('.', '_');
    List<JsMessage.Part> parts = parseMessageParts(defaultValue);
    List<String> placeholders = parts.stream()
        .filter(JsMessage.Part::isPlaceholder)
        .map(JsMessage.Part::getCanonicalPlaceholderName)
        .collect(Collectors.toList());

    sb.append("/** @desc ").append(key).append(" */\n");
    sb.append("   var MSG_").append(jsVarName).append(" = goog.getMsg('")
        .append(escapeJs(defaultValue)).append("'");

    boolean hasPlaceholders = !placeholders.isEmpty();
    boolean hasOptions = annotation.html() || annotation.unescapeHtmlEntities();

    if (hasPlaceholders) {
      sb.append(", { ");
      List<String> entries = new ArrayList<>();
      for (String ph : placeholders) {
        entries.add(ph.toLowerCase() + ": _" + ph.toLowerCase());
      }
      sb.append(String.join(", ", entries));
      sb.append(" }");
    } else if (hasOptions) {
      sb.append(", {}");
    }

    if (hasOptions) {
      sb.append(", { ");
      List<String> opts = new ArrayList<>();
      if (annotation.html()) {
        opts.add("html: true");
      }
      if (annotation.unescapeHtmlEntities()) {
        opts.add("unescapeHtmlEntities: true");
      }
      sb.append(String.join(", ", opts));
      sb.append(" }");
    }

    sb.append(");\n");
  }

  private String escapeJs(String value) {
    return value.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n");
  }

  private String computeMessageId(String key, String defaultValue) {
    List<JsMessage.Part> parts = parseMessageParts(defaultValue);
    String jsKey = "MSG_" + key.replace('.', '_');
    JsMessage.Builder builder = new JsMessage.Builder();
    builder.setKey(jsKey);
    builder.setId(jsKey);
    builder.appendParts(parts);
    JsMessage jsMessage = builder.build();
    return idGenerator.generateId(jsMessage.getKey(), jsMessage.getParts());
  }

  // --- Resource Bundle Discovery ---

  private void processBundles(TypeElement bundleInterface, List<ExecutableElement> methods) {
    TranslationBundle bundleAnnotation = bundleInterface.getAnnotation(TranslationBundle.class);
    String bundleName = bundleAnnotation.value();
    if (bundleName.equals("<auto>") || bundleName.isEmpty()) {
      bundleName = bundleInterface.getSimpleName().toString();
    }
    String bundleSimpleName = bundleInterface.getSimpleName().toString();

    try {
      URI sourceUri = getSourceFileUri(bundleInterface);
      if (sourceUri == null) {
        return;
      }
      Path sourceDir = Paths.get(sourceUri).getParent();
      if (sourceDir == null || !Files.isDirectory(sourceDir)) {
        return;
      }

      try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
        for (Path file : stream) {
          String fileName = file.getFileName().toString();
          if (fileName.startsWith(bundleName) && !fileName.endsWith(".java")) {
            String locale = extractLocale(fileName, bundleName);
            if (fileName.endsWith(".properties")) {
              loadPropertiesBundle(file, locale, bundleSimpleName);
            } else if (fileName.endsWith(".json")) {
              loadJsonBundle(file, locale, bundleSimpleName);
            }
          }
        }
      }
    } catch (IOException e) {
      throw new GenerationException("Failed to process bundles for " + bundleInterface, e);
    }
  }

  private URI getSourceFileUri(TypeElement element) {
    String pkg = elements.getPackageOf(element).getQualifiedName().toString();
    String simpleName = element.getSimpleName().toString() + ".java";
    try {
      FileObject resource = iocContext.getGenerationContext().getProcessingEnvironment()
          .getFiler().getResource(StandardLocation.SOURCE_PATH, pkg, simpleName);
      return resource.toUri();
    } catch (IOException e) {
      return null;
    }
  }

  private String extractLocale(String fileName, String bundleName) {
    String withoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
    if (withoutExt.equals(bundleName)) {
      return "";
    }
    String locale = withoutExt.substring(bundleName.length());
    if (locale.startsWith("_")) {
      locale = locale.substring(1);
    }
    return locale;
  }

  private void loadPropertiesBundle(Path file, String locale, String bundleSimpleName) throws IOException {
    Properties props = new Properties();
    try (java.io.BufferedReader reader = Files.newBufferedReader(file)) {
      props.load(reader);
    }
    Map<String, String> translations = localeMapping
        .computeIfAbsent(locale, k -> new LinkedHashMap<>());
    for (String key : props.stringPropertyNames()) {
      String qualifiedKey = bundleSimpleName + "." + key;
      translations.put(qualifiedKey, props.getProperty(key));
    }
  }

  private void loadJsonBundle(Path file, String locale, String bundleSimpleName) throws IOException {
    String content = new String(Files.readAllBytes(file), "UTF-8");
    JsonObject json = JsonParser.parseString(content).getAsJsonObject();
    Map<String, String> translations = localeMapping
        .computeIfAbsent(locale, k -> new LinkedHashMap<>());
    for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
      String qualifiedKey = bundleSimpleName + "." + entry.getKey();
      translations.put(qualifiedKey, entry.getValue().getAsString());
    }
  }

  // --- Locale Fallback ---

  private void processMapping() {
    for (Map.Entry<String, Map<String, String>> entry : localeMapping.entrySet()) {
      String locale = entry.getKey();
      Map<String, String> translations = entry.getValue();
      for (String key : defaultMessageMapping.keySet()) {
        if (!translations.containsKey(key)) {
          String parentLocale = getParentLocale(locale);
          if (parentLocale != null && localeMapping.containsKey(parentLocale)
              && localeMapping.get(parentLocale).containsKey(key)) {
            translations.put(key, localeMapping.get(parentLocale).get(key));
          }
        }
      }
    }
  }

  private String getParentLocale(String locale) {
    int idx = locale.lastIndexOf('_');
    if (idx > 0) {
      return locale.substring(0, idx);
    }
    return null;
  }

  // --- XTB Generation ---

  private void generateXTB(String locale, Map<String, String> translations) {
    StringBuilder sb = new StringBuilder();
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    sb.append("<!DOCTYPE translationbundle SYSTEM \"translationbundle.dtd\">\n");
    sb.append("<translationbundle lang=\"")
        .append(locale.replace("_", "-")).append("\">\n");

    for (Map.Entry<String, String> entry : translations.entrySet()) {
      String key = entry.getKey();
      String msgId = defaultMessageMapping.get(key);
      if (msgId == null) {
        continue;
      }
      String value = entry.getValue();
      String xtbValue = convertToXtbValue(value);
      sb.append("<translation id=\"").append(msgId)
          .append("\" key=\"MSG_").append(key).append("\">")
          .append(xtbValue)
          .append("</translation>\n");
    }

    sb.append("</translationbundle>\n");

    String filename = "gwt3_message_bundle_" + locale + ".xtb";
    writeResource(XTB_PACKAGE, filename, sb.toString());
  }

  private String convertToXtbValue(String value) {
    List<JsMessage.Part> parts = parseMessageParts(value);
    StringBuilder sb = new StringBuilder();
    for (JsMessage.Part part : parts) {
      if (part.isPlaceholder()) {
        sb.append("<ph name=\"").append(part.getCanonicalPlaceholderName().toUpperCase())
            .append("\" />");
      } else {
        sb.append(escapeXml(part.getString()));
      }
    }
    return sb.toString();
  }

  private List<JsMessage.Part> parseMessageParts(String text) {
    try {
      return JsMessageVisitor.parseJsMessageTextIntoParts(text);
    } catch (JsMessage.PlaceholderFormatException e) {
      throw new GenerationException("Invalid placeholder format in message: " + text, e);
    }
  }

  private String escapeXml(String s) {
    return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
        .replace("\"", "&quot;");
  }

  // --- File Writing ---

  private void writeResource(String pkg, String filename, String content) {
    iocContext.addTask(() -> {
      try {
        FileObject file = iocContext.getGenerationContext().getProcessingEnvironment()
            .getFiler()
            .createResource(StandardLocation.SOURCE_OUTPUT, pkg, filename);
        try (PrintWriter pw =
            new PrintWriter(new OutputStreamWriter(file.openOutputStream(), "UTF-8"))) {
          pw.print(content);
        }
      } catch (IOException e) {
        throw new GenerationException(
            "Failed to write resource: " + pkg + "/" + filename, e);
      }
    });
  }
}
