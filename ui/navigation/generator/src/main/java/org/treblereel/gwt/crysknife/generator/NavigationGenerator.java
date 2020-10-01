package org.treblereel.gwt.crysknife.generator;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.google.auto.common.MoreElements;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.client.BeanManager;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.logger.PrintWriterTreeLogger;
import org.treblereel.gwt.crysknife.navigation.client.local.Navigation;
import org.treblereel.gwt.crysknife.navigation.client.local.Page;
import org.treblereel.gwt.crysknife.navigation.client.local.spi.NavigationGraph;
import org.treblereel.gwt.crysknife.navigation.client.shared.NavigationEvent;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/1/20
 */
@Generator
public class NavigationGenerator extends SingletonGenerator {

  public NavigationGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Inject.class, NavigationGraph.class, WiringElementType.BEAN, this);
  }

  @Override
  public void before() {

    Set<TypeElement> pages =
        iocContext.getGenerationContext().getRoundEnvironment().getElementsAnnotatedWith(Page.class)
            .stream().filter(elm -> elm.getKind().equals(ElementKind.CLASS))
            .map(elm -> MoreElements.asType(elm)).collect(Collectors.toSet());

    TypeElement type = iocContext.getGenerationContext().getElements()
        .getTypeElement(Navigation.class.getCanonicalName());
    BeanDefinition navigation = iocContext.getBeanDefinitionOrCreateAndReturn(type);

    pages.forEach(elm -> {
      BeanDefinition page = iocContext.getBeanDefinitionOrCreateAndReturn(elm);
      navigation.getDependsOn().add(page);
    });

    new NavigationGraphGenerator(pages).generate(new PrintWriterTreeLogger(),
        iocContext.getGenerationContext());
  }

  @Override
  protected ObjectCreationExpr generateNewInstanceCreationExpr(BeanDefinition definition) {
    ObjectCreationExpr newInstance = new ObjectCreationExpr();
    return newInstance
        .setType(NavigationGraph.class.getPackage().getName() + ".GeneratedNavigationGraph")
        .addArgument(new MethodCallExpr(
            new MethodCallExpr(
                new NameExpr(Utils.toVariableName(BeanManager.class.getCanonicalName())), "get"),
            "get"))
        .addArgument(
            new MethodCallExpr(new MethodCallExpr(new NameExpr("Event_Factory"), "get"), "get")
                .addArgument(NavigationEvent.class.getCanonicalName() + ".class"));
  }
}
