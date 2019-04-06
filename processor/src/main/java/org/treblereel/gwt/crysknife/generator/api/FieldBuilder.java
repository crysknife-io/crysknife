package org.treblereel.gwt.crysknife.generator.api;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
public class FieldBuilder extends Builder {

    public FieldBuilder(ClassBuilder classBuilder) {
        super(classBuilder);
    }

    @Override
    public void build(BeanDefinition argument) {
        String varName = Utils.toVariableName(argument.getQualifiedName());
        ClassOrInterfaceType type = new ClassOrInterfaceType();
        type.setName("org.treblereel.gwt.crysknife.client.Instance");
        type.setTypeArguments(new ClassOrInterfaceType().setName(argument.getQualifiedName()));

        Parameter param = new Parameter();
        param.setName(varName);
        param.setType(type);

        classBuilder.getClassDeclaration().addField(type, varName, Modifier.Keyword.FINAL, Modifier.Keyword.PRIVATE);
    }
}
