package org.treblereel.gwt.crysknife.generator.api;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
public class InitBuilder extends Builder {

    public InitBuilder(ClassBuilder classBuilder) {
        super(classBuilder);
    }

    @Override
    public void build() {

        MethodDeclaration getMethodDeclaration = classBuilder.getClassDeclaration()
                .addMethod("get", Modifier.Keyword.PUBLIC);

        getMethodDeclaration.addAnnotation(Override.class);
        getMethodDeclaration.setType(classBuilder.beanDefinition.getClassName());
        classBuilder.setGetMethodDeclaration(getMethodDeclaration);
    }
}
