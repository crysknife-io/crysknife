package org.treblereel.gwt.crysknife.generator;

import javax.enterprise.inject.Produces;
import javax.lang.model.element.TypeElement;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.ProducerDefinition;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/4/19
 */
@Generator(priority = 500)
public class ProducesGenerator extends ScopedBeanGenerator {

    @Override
    public void register(IOCContext iocContext) {
        iocContext.register(Produces.class, WiringElementType.PRODUCER_ELEMENT, this);
        this.iocContext = iocContext;
    }

    @Override
    public void generateDependantFieldDeclaration(ClassBuilder builder, BeanDefinition definition) {
        if (definition instanceof ProducerDefinition) {
            ProducerDefinition producesDefinition = (ProducerDefinition) definition;
            TypeElement instance = producesDefinition.getInstance();

            ObjectCreationExpr newInstance = new ObjectCreationExpr();
            newInstance.setType(new ClassOrInterfaceType()
                                        .setName(Utils.getQualifiedName(instance)));

            builder.addFieldWithInitializer(Utils.getQualifiedName(instance),
                                            "producer",
                                            newInstance,
                                            Modifier.Keyword.PRIVATE, Modifier.Keyword.FINAL);
        }
    }

    @Override
    public void generateInstanceGetMethodReturn(ClassBuilder builder, BeanDefinition definition) {
        if (definition instanceof ProducerDefinition) {
            FieldAccessExpr fieldAccess = new FieldAccessExpr(new ThisExpr(), "producer");
            MethodCallExpr call = new MethodCallExpr(fieldAccess, ((ProducerDefinition) definition).getMethod().getSimpleName().toString());
            builder.getGetMethodDeclaration().getBody().get().addAndGetStatement(new ReturnStmt(call));
        }
    }
}
