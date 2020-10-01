package org.treblereel.gwt.crysknife.generator;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.processing.FilerException;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.github.javaparser.ast.expr.Expression;
import org.treblereel.gwt.crysknife.exception.GenerationException;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.GenerationContext;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/4/19
 */
public abstract class BeanIOCGenerator extends IOCGenerator {

  public BeanIOCGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  /**
   * @param clazz
   * @param fieldPoint
   * @param beanDefinition
   * 
   * @return Expression, how to call instance of this bean ?
   */
  public abstract Expression generateBeanCall(ClassBuilder clazz, FieldPoint fieldPoint,
      BeanDefinition beanDefinition);

  public void write(ClassBuilder clazz, BeanDefinition beanDefinition, GenerationContext context) {
    try {
      String fileName = Utils.getQualifiedFactoryName(beanDefinition.getType());
      String source = clazz.toSourceCode();
      build(fileName, source, context);
    } catch (javax.annotation.processing.FilerException e1) {
      context.getProcessingEnvironment().getMessager().printMessage(Diagnostic.Kind.NOTE,
          e1.getMessage());
    } catch (IOException e1) {
      throw new GenerationException(e1);
    }
  }

  protected void build(String fileName, String source, GenerationContext context)
      throws IOException {
    JavaFileObject builderFile =
        context.getProcessingEnvironment().getFiler().createSourceFile(fileName);

    try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
      out.append(source);
    } catch (FilerException e) {
      throw new GenerationException(e);
    }
  }
}
