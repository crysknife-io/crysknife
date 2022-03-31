/*
 * Copyright © 2020 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.crysknife.ui.navigation.generator;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.google.auto.common.MoreElements;
import io.crysknife.annotation.Generator;
import io.crysknife.client.BeanManager;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.SingletonGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.PrintWriterTreeLogger;
import io.crysknife.logger.TreeLogger;
import io.crysknife.ui.navigation.client.local.Page;
import io.crysknife.ui.navigation.client.local.spi.NavigationGraph;
import io.crysknife.ui.navigation.client.shared.NavigationEvent;

import javax.inject.Inject;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/1/20
 */
@Generator
public class NavigationGenerator extends SingletonGenerator {

  public NavigationGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
  }

  @Override
  public void register() {

    iocContext.register(Inject.class, NavigationGraph.class, WiringElementType.FIELD_TYPE, this);
    iocContext.getOrderedBeans().add(iocContext.getTypeMirror(NavigationGraph.class));
  }

  @Override
  public void before() {

    Set<TypeElement> pages =
        iocContext.getGenerationContext().getRoundEnvironment().getElementsAnnotatedWith(Page.class)
            .stream().filter(elm -> elm.getKind().equals(ElementKind.CLASS))
            .map(elm -> MoreElements.asType(elm)).collect(Collectors.toSet());

    new NavigationGraphGenerator(pages).generate(new PrintWriterTreeLogger(),
        iocContext.getGenerationContext());
  }

  @Override
  protected ObjectCreationExpr generateNewInstanceCreationExpr(BeanDefinition definition) {
    ObjectCreationExpr newInstance = new ObjectCreationExpr();
    newInstance.setType(NavigationGraph.class.getPackage().getName() + ".GeneratedNavigationGraph");
    newInstance.addArgument("beanManager");
    newInstance.addArgument(
        new MethodCallExpr(new MethodCallExpr(new NameExpr("_field_event"), "get"), "getInstance"));
    return newInstance;
  }

  @Override
  public Expression generateBeanLookupCall(ClassBuilder classBuilder,
      InjectableVariableDefinition fieldPoint) {
    ObjectCreationExpr newInstance = new ObjectCreationExpr();


    return generationUtils.wrapCallInstanceImpl(classBuilder, newInstance
        .setType(NavigationGraph.class.getPackage().getName() + ".GeneratedNavigationGraph")
        .addArgument(new MethodCallExpr(
            new NameExpr(BeanManager.class.getPackage().getName() + ".BeanManagerImpl"), "get"))
        .addArgument(new MethodCallExpr(
            new MethodCallExpr(new NameExpr("javax.enterprise.event.Event_Factory"), "get"), "get")
                .addArgument(NavigationEvent.class.getCanonicalName() + ".class")));
  }
}
