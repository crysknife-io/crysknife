package org.treblereel.gwt.crysknife.generator.context;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/21/19
 */
public class GenerationContext {

    private final RoundEnvironment roundEnvironment;
    private final ProcessingEnvironment processingEnvironment;
    private boolean isGwt2 = false;
    private boolean isJre = false;

    public GenerationContext(RoundEnvironment roundEnvironment,
                             ProcessingEnvironment processingEnvironment) {
        this.roundEnvironment = roundEnvironment;
        this.processingEnvironment = processingEnvironment;

        try {
            Class.forName("com.google.gwt.core.client.GWT");
            isGwt2 = true;
            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING,"GWT2 generation mode.");
        } catch (ClassNotFoundException e) {

        }

        try {
            Class.forName("org.aspectj.lang.ProceedingJoinPoint");
            isJre = true;
            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING,"JRE generation mode.");
        } catch (ClassNotFoundException e) {

        }
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

    public boolean isGwt2() {
        return isGwt2;
    }

    public boolean isJre() {
        return isJre;
    }
}
