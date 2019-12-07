package org.treblereel.gwt.crysknife.generator;

import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/2/19
 */
@Generator
public class BasicIOCGenerator extends DependentGenerator {

    @Override
    public void register(IOCContext iocContext) {
        this.iocContext = iocContext;
    }
}
