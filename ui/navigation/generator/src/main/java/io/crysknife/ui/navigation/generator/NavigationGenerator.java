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

import io.crysknife.generator.SingletonGenerator;
import io.crysknife.generator.api.Generator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.ui.navigation.client.local.Page;
import io.crysknife.ui.navigation.client.local.spi.NavigationGraph;
import jakarta.enterprise.context.ApplicationScoped;

import javax.lang.model.element.TypeElement;
import java.util.Set;

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
    iocContext.register(ApplicationScoped.class, NavigationGraph.class, WiringElementType.BEAN,
        this);
  }

  @Override
  public void before() {
    Set<TypeElement> pages = iocContext.getTypeElementsByAnnotation(Page.class.getCanonicalName());
    if (!pages.isEmpty()) {
      new NavigationGraphGenerator(iocContext, pages)
          .generate(logger.branch(TreeLogger.DEBUG, " starting generating navigation"));
    }
  }

}
