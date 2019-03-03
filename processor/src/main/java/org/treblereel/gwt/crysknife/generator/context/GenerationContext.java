package org.treblereel.gwt.crysknife.generator.context;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/21/19
 */
public class GenerationContext {

    private final RoundEnvironment roundEnvironment;

    private final ProcessingEnvironment processingEnvironment;

    public GenerationContext(RoundEnvironment roundEnvironment,
                             ProcessingEnvironment processingEnvironment) {
        this.roundEnvironment = roundEnvironment;
        this.processingEnvironment = processingEnvironment;
    }

    public Elements getElements() {
        return processingEnvironment.getElementUtils();
    }

    public Types getTypes() {
        return processingEnvironment.getTypeUtils();
    }

    public RoundEnvironment getRoundEnvironment() {
        return roundEnvironment;
    }

    public ProcessingEnvironment getProcessingEnvironment() {
        return processingEnvironment;
    }
}
