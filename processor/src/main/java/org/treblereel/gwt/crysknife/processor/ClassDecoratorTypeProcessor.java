package org.treblereel.gwt.crysknife.processor;

import javax.lang.model.element.Element;

import com.google.auto.common.MoreElements;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/7/19
 */
public class ClassDecoratorTypeProcessor extends TypeProcessor {

    @Override
    public void process(IOCContext context, IOCGenerator generator, Element element) {
        if (MoreElements.isType(element)) {
            context.getBeans().get(MoreElements.asType(element)).addDecorator(generator, null);
        }
    }
}