package org.treblereel.gwt.crysknife.processor;

import java.util.HashMap;

import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/4/19
 */
public abstract class TypeProcessor {

    public abstract void process(IOCContext context, IOCGenerator generator, Element element);

    protected BeanDefinition getBeanDefinitionOrCreateAndGet(IOCContext iocContext, TypeElement typeElement) {
        BeanDefinition beanDefinition;
        if (iocContext.getBeans().containsKey(typeElement)) {
            beanDefinition = iocContext.getBeans().get(typeElement);
        } else {
            beanDefinition = BeanDefinition.of(typeElement, iocContext);
            iocContext.getBeans().put(typeElement, beanDefinition);
        }
        checkNamedAndAdd(iocContext, typeElement, beanDefinition);
        return beanDefinition;
    }

    protected void checkNamedAndAdd(IOCContext iocContext, TypeElement typeElement, BeanDefinition beanDefinition) {
        if (typeElement.getAnnotation(Named.class) != null) {
            String named = typeElement.getAnnotation(Named.class).value();
            typeElement.getInterfaces().stream().forEach(i -> {
                Element asElement = MoreTypes.asElement(i);
                TypeElement iface = MoreElements.asType(asElement);
                if (!iocContext.getQualifiers().containsKey(iface)) {
                    iocContext.getQualifiers().put(iface, new HashMap<>());
                }
                iocContext.getQualifiers().get(iface).put(named, beanDefinition);
            });
        }
    }
}
