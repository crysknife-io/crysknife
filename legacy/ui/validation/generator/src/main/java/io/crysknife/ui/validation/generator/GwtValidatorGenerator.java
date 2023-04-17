/*
 * Copyright (C) 2013 Red Hat, Inc. and/or its affiliates.
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

package io.crysknife.ui.validation.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.TypeUtils;
import org.gwtproject.validation.client.GwtValidation;

import javax.lang.model.element.*;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;
import javax.validation.Constraint;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * Generates the GWT {@link Validator} interface based on validation annotations.
 *
 * @author Johannes Barop <jb@barop.de>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
class GwtValidatorGenerator {

  private static final String DENYLIST_PROPERTY = "errai.validation.denylist";
  public static final String FILE_NAME =
      "io.crysknife.ui.validation.client." + "Gwt" + Validator.class.getSimpleName();

  private static final Set<Class> GLOBAL_CONSTRAINTS = Stream
      .of(javax.validation.constraints.AssertFalse.class,
          javax.validation.constraints.AssertTrue.class,
          javax.validation.constraints.DecimalMax.class,
          javax.validation.constraints.DecimalMin.class, javax.validation.constraints.Digits.class,
          // javax.validation.constraints.Email.class,
          javax.validation.constraints.Future.class,
          // javax.validation.constraints.FutureOrPresent.class,
          javax.validation.constraints.Max.class, javax.validation.constraints.Min.class,
          // javax.validation.constraints.Negative.class,
          // javax.validation.constraints.NegativeOrZero.class,
          // javax.validation.constraints.NotBlank.class,
          // javax.validation.constraints.NotEmpty.class,
          javax.validation.constraints.NotNull.class, javax.validation.constraints.Null.class,
          javax.validation.constraints.Past.class,
          // javax.validation.constraints.PastOrPresent.class,
          javax.validation.constraints.Pattern.class,
          // javax.validation.constraints.Positive.class,
          // javax.validation.constraints.PositiveOrZero.class,
          javax.validation.constraints.Size.class,
          org.hibernate.validator.constraints.CreditCardNumber.class,
          org.hibernate.validator.constraints.Email.class,
          org.hibernate.validator.constraints.Length.class,
          org.hibernate.validator.constraints.NotBlank.class,
          org.hibernate.validator.constraints.NotEmpty.class,
          org.hibernate.validator.constraints.Range.class)
      // org.hibernate.validator.constraints.URL.class)
      // .map(MetaClassFactory::get)
      .collect(toSet());


  public CompilationUnit generate(TreeLogger logger, final IOCContext context) {

    final Collection<TypeElement> constraintAnnotations = context
        .getTypeElementsByAnnotation(Constraint.class.getCanonicalName()).stream()
        .filter(m -> m.getKind().equals(ElementKind.ANNOTATION_TYPE)).collect(Collectors.toSet());

    final Set<TypeElement> allConstraintAnnotations = new HashSet<>();
    GLOBAL_CONSTRAINTS.stream()
        .map(constrain -> context.getTypeElementsByAnnotation(constrain.getCanonicalName()));

    allConstraintAnnotations.addAll(constraintAnnotations);

    final SetMultimap<TypeElement, TypeElement> constraintAnnotationsByBeans =
        getConstraintAnnotationsByBeans(allConstraintAnnotations, context);

    final Set<TypeElement> beans =
        extractValidatableBeans(constraintAnnotationsByBeans.keySet(), context);
    final Set<Class<?>> groups =
        extractValidationGroups(constraintAnnotationsByBeans.values(), context);

    final Set<TypeElement> filteredBeans = new HashSet<>();

    filteredBeans.addAll(beans);

    /*        final SimplePackageFilter filter = new SimplePackageFilter(PropertiesUtil.getPropertyValues(DENYLIST_PROPERTY, " "));
        for (final Class<?> bean : beans) {
            if (!filter.apply(bean.getName())) {
                filteredBeans.add(bean);
            }
        }
    */

    if (filteredBeans.isEmpty() || groups.isEmpty()) {
      // Nothing to validate
      return null;
    }

    CompilationUnit compilationUnit = new CompilationUnit();
    compilationUnit.setPackageDeclaration("io.crysknife.ui.validation.client");
    ClassOrInterfaceDeclaration classDeclaration =
        compilationUnit.addInterface("Gwt" + Validator.class.getSimpleName()).setPublic(true);
    classDeclaration.getExtendedTypes()
        .add(new ClassOrInterfaceType().setName(Validator.class.getCanonicalName()));

    NodeList<MemberValuePair> values = new NodeList<>();
    MemberValuePair groupsMemberValuePair = new MemberValuePair();
    MemberValuePair valuesMemberValuePair = new MemberValuePair();
    groupsMemberValuePair.setName("groups");
    valuesMemberValuePair.setName("value");

    String groupsValue = Arrays.stream(groups.toArray(new Class<?>[groups.size()]))
        .map(clazz -> clazz.getCanonicalName() + ".class").collect(Collectors.joining(","));
    groupsMemberValuePair.setValue(new NameExpr("{" + groupsValue + "}"));

    String valueValues =
        filteredBeans.stream().map(bean -> bean.getQualifiedName().toString() + ".class")
            .collect(Collectors.joining(","));
    valuesMemberValuePair.setValue(new NameExpr("{" + valueValues + "}"));

    values.add(groupsMemberValuePair);
    values.add(valuesMemberValuePair);


    NormalAnnotationExpr GwtValidation =
        new NormalAnnotationExpr(new Name(GwtValidation.class.getCanonicalName()), values);
    classDeclaration.addAnnotation(GwtValidation);



    /*        final ClassStructureBuilder<?> builder =
                ClassBuilder.define("Gwt" + Validator.class.getSimpleName()).publicScope()
                        .interfaceDefinition().implementsInterface(Validator.class).body();

        builder.getClassDefinition().addAnnotation(new GwtValidation() {
            @Override
            public Class<?>[] value() {
                return filteredBeans.toArray(new Class<?>[0]);
            }

            @Override
            public Class<?>[] groups() {
                return groups.toArray(new Class<?>[0]);
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return GwtValidation.class;
            }
        });*/

    return compilationUnit;

  }


  @SuppressWarnings("unchecked")
  private SetMultimap<TypeElement, TypeElement> getConstraintAnnotationsByBeans(
      Collection<TypeElement> constraintAnnotations, IOCContext context) {
    final SetMultimap<TypeElement, TypeElement> beans = HashMultimap.create();

    for (final TypeElement annotation : constraintAnnotations) {
      for (VariableElement variableElement : context
          .getFieldsByAnnotation(annotation.getQualifiedName().toString())) {
        beans.put(MoreElements.asType(variableElement.getEnclosingElement()), annotation);
      }

      for (ExecutableElement executableElement : context
          .getMethodsByAnnotation(annotation.getQualifiedName().toString())) {
        beans.put(MoreElements.asType(executableElement.getEnclosingElement()), annotation);
      }

      for (TypeElement typeElement : context
          .getTypeElementsByAnnotation(annotation.getQualifiedName().toString())) {
        beans.put(typeElement, annotation);
      }
    }

    return beans;
  }

  private Set<TypeElement> extractValidatableBeans(final Set<TypeElement> beans,
      final IOCContext context) {
    final Set<TypeElement> allBeans = new HashSet<>();

    for (TypeElement bean : beans) {
      if (!bean.getKind().equals(ElementKind.ANNOTATION_TYPE)) {
        allBeans.add(bean);
      }
    }

    // allBeans.addAll(beans);

    for (VariableElement variableElement : context
        .getFieldsByAnnotation(Valid.class.getCanonicalName())) {
      allBeans.add(MoreElements.asType(variableElement.getEnclosingElement()));
      allBeans.add(MoreTypes.asTypeElement(variableElement.asType()));
    }

    for (ExecutableElement executableElement : context
        .getMethodsByAnnotation(Valid.class.getCanonicalName())) {
      allBeans.add(MoreElements.asType(executableElement.getEnclosingElement()));
      allBeans.add(MoreTypes.asTypeElement(executableElement.asType()));
    }

    return allBeans;
  }

  private Set<Class<?>> extractValidationGroups(
      final Collection<TypeElement> constraintAnnotationInstances, final IOCContext context) {
    final Set<Class<?>> groups = new HashSet<>();

    for (final TypeElement instance : constraintAnnotationInstances) {
      TypeUtils.getAllMethodsIn(context.getGenerationContext().getElements(), instance).stream()
          .filter(e -> e.getSimpleName().toString().equals("groups")).forEach(e -> {
            e.getDefaultValue().accept(new SimpleAnnotationValueVisitor8<Boolean, Void>() {

              @Override
              public Boolean visitArray(List<? extends AnnotationValue> vals, Void p) {
                if (vals.isEmpty()) {
                  groups.add(Default.class);
                } else {
                  throw new GenerationException("@Constraint.groups is not implemented for now.");
                }
                return false;
              }
            }, null);
          });
    }
    return groups;
  }
}
