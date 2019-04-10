package org.treblereel.gwt.crysknife.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.point.ConstructorPoint;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/11/19
 */
public class FieldTypeProcessor extends TypeProcessor {

    protected FieldTypeProcessor(IOCGenerator generator) {
        super(generator);
    }

    @Override
    public void process(IOCContext context, Element element) {
        BeanDefinition beanDefinition = getBeanDefinition(element, context);

        if (element.getKind().equals(ElementKind.FIELD)) {
            VariableElement field = MoreElements.asVariable(element);
            TypeElement parent = MoreElements.asType(field.getEnclosingElement());
            BeanDefinition parentDefinition = context.getBeanDefinitionOrCreateAndReturn(parent);

            TypeMirror mirror = MoreElements.asVariable(element).asType();
            TypeElement typeElement = MoreTypes.asTypeElement(mirror);

            if (typeElement.getTypeParameters().size() > 0) {
                TypeMirror type = element.asType();
                beanDefinition.getDeclaredTypes().add(MoreTypes.asDeclared(type));
            }
            FieldPoint fieldPoint = FieldPoint.of(field);
            parentDefinition.getFieldInjectionPoints().add(fieldPoint);
            parentDefinition.getDependsOn().add(beanDefinition);
        } else if (element.getKind().equals(ElementKind.CONSTRUCTOR)) {
            ExecutableElement constructor = MoreElements.asExecutable(element);
            TypeElement parent = MoreElements.asType(constructor.getEnclosingElement());
            BeanDefinition parentDefinition = context.getBeanDefinitionOrCreateAndReturn(parent);

            beanDefinition.setConstructorInjectionPoint(new ConstructorPoint(Utils.getQualifiedName(parent), parent));

            for (int i = 0; i < constructor.getParameters().size(); i++) {
                FieldPoint field = parseField(parentDefinition, constructor.getParameters().get(i), context);
                beanDefinition.getConstructorInjectionPoint().addArgument(field);
            }
        }
    }

    private FieldPoint parseField(BeanDefinition beanDefinition, Element type, IOCContext context) {
        FieldPoint field = FieldPoint.of(MoreElements.asVariable(type));
        if (!field.isNamed()) {
            TypeElement typeElement = MoreElements.asType(MoreTypes.asElement(MoreElements.asVariable(type).asType()));
            beanDefinition.getDependsOn().add(context.getBeanDefinitionOrCreateAndReturn(typeElement));
        }
        return field;
    }

    private BeanDefinition getBeanDefinition(Element element, IOCContext context) {
        BeanDefinition definition;
        if (element.getKind().equals(ElementKind.FIELD)) {
            TypeMirror mirror = MoreElements.asVariable(element).asType();
            TypeElement typeElement = MoreTypes.asTypeElement(mirror);
            definition = context.getBeanDefinitionOrCreateAndReturn(typeElement);
        } else {
            ExecutableElement constructor = MoreElements.asExecutable(element);
            TypeElement parent = MoreElements.asType(constructor.getEnclosingElement());
            definition = context.getBeanDefinitionOrCreateAndReturn(parent);
        }
        return definition;
    }
}
