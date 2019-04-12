package org.treblereel.gwt.crysknife.processor;

import javax.lang.model.element.Element;

import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/4/19
 */
public abstract class TypeProcessor {

    protected IOCGenerator generator;

    protected TypeProcessor(IOCGenerator generator) {
        this.generator = generator;
    }

    public abstract void process(IOCContext context, Element element);
}
