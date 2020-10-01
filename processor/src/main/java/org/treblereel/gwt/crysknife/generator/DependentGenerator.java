package org.treblereel.gwt.crysknife.generator;

import javax.enterprise.context.Dependent;

import com.github.javaparser.ast.Modifier;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/2/19
 */
@Generator(priority = 2)
public class DependentGenerator extends ScopedBeanGenerator {

  public DependentGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Dependent.class, WiringElementType.BEAN, this);
  }

  @Override
  public void generateInstanceGetMethodBuilder(ClassBuilder builder,
      BeanDefinition beanDefinition) {
    super.generateInstanceGetMethodBuilder(builder, beanDefinition);
    builder.addField(beanDefinition.getClassName(), "instance", Modifier.Keyword.PRIVATE);

    builder.getGetMethodDeclaration().getBody().get()
        .addAndGetStatement(generateInstanceInitializer(builder, beanDefinition));
  }
}
