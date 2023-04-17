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

package io.crysknife.generator;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.crysknife.annotation.Application;
import io.crysknife.generator.api.Generator;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.ExecutionEnv;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.StringOutputStream;
import jakarta.ejb.Startup;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.crysknife.util.TypeUtils.isDependent;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/5/19
 */
@Generator(priority = 100000)
public class BootstrapperGenerator extends SingletonGenerator {

  private final String BOOTSTRAP_EXTENSION = "Bootstrap";


  public BootstrapperGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Application.class, WiringElementType.BEAN, this);
  }

  @Override
  public void generate(ClassMetaInfo classMetaInfo, BeanDefinition beanDefinition) {
    Map<String, Object> root = new HashMap<>();
    List<Dep> fields = new ArrayList<>();

    root.put("jre", iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.JRE));
    root.put("package", beanDefinition.getPackageName());
    root.put("bean", beanDefinition.getSimpleClassName());
    root.put("imports", classMetaInfo.getImports());
    root.put("deps", fields);


    deps(beanDefinition, fields);
    fieldDecorators(beanDefinition, classMetaInfo);
    methodDecorators(beanDefinition, classMetaInfo);
    postConstruct(beanDefinition, root);
    runOnStartup(root);


    root.put("fields", classMetaInfo.getBodyStatements());
    root.put("preDestroy", classMetaInfo.getOnDestroy());
    root.put("doInitInstance", classMetaInfo.getDoInitInstance());

    StringOutputStream os = new StringOutputStream();
    try (Writer out = new OutputStreamWriter(os, "UTF-8")) {
      Template temp = cfg.getTemplate("bootstrap.ftlh");
      temp.process(root, out);
      String fileName = beanDefinition.getPackageName() + "." + beanDefinition.getSimpleClassName()
          + BOOTSTRAP_EXTENSION;
      write(iocContext, fileName, os.toString());
    } catch (UnsupportedEncodingException | TemplateException e) {
      throw new GenerationException(e);
    } catch (IOException e) {
      throw new GenerationException(e);
    }

  }

  private void runOnStartup(Map<String, Object> root) {
    Set<String> onStartup = iocContext.getTypeElementsByAnnotation(Startup.class.getCanonicalName())
        .stream().map(type -> {
          if (isDependent(iocContext.getBean(type.asType()))) {
            throw new GenerationException(
                "Bean, annotated with @Startup, must be @Singleton or @ApplicationScoped : "
                    + type);
          }
          return type.asType().toString();
        }).collect(Collectors.toSet());
    root.put("onStartup", onStartup);
  }

}
