package org.treblereel.gwt.crysknife.generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Qualifier;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/12/19
 */
public class QualifiersScan {

    private final IOCContext iocContext;

    public QualifiersScan(IOCContext iocContext) {
        this.iocContext = iocContext;
    }

    public void process() {
        Set<VariableElement> points = new HashSet<>();

        iocContext.getGenerationContext()
                .getRoundEnvironment()
                .getElementsAnnotatedWith(Qualifier.class)
                .forEach(qualified -> iocContext.getGenerationContext()
                        .getRoundEnvironment()
                        .getElementsAnnotatedWith(MoreElements.asType(qualified)).forEach(element -> {
                            if (element.getKind().isField()) {
                                points.add(MoreElements.asVariable(element));
                            } else if (element.getKind().isClass()) {
                                processQualifier(MoreElements.asType(element), qualified);
                            }
                        }));

    }

    private void processQualifier(TypeElement qualifier, Element annotation) {
        qualifier.getInterfaces().forEach(parent -> {
            BeanDefinition iface = iocContext.getBeanDefinitionOrCreateAndReturn(MoreTypes.asTypeElement(parent));
            if (!iocContext.getQualifiers().containsKey(iface.getType())) {
                iocContext.getQualifiers().put(iface.getType(), new HashMap<>());
            }
            iocContext.getQualifiers()
                    .get(iface.getType())
                    .put(annotation.toString(),
                         iocContext.getBeanDefinitionOrCreateAndReturn(qualifier));
        });
    }
}
