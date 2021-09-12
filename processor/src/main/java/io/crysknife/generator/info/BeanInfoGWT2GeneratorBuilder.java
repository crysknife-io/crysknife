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

package io.crysknife.generator.info;

import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.util.Utils;

import java.io.IOException;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class BeanInfoGWT2GeneratorBuilder extends AbstractBeanInfoGenerator {

  private final String newLine = System.lineSeparator();
  private BeanDefinition bean;
  private StringBuilder clazz;

  BeanInfoGWT2GeneratorBuilder(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  protected String build(BeanDefinition bean) throws IOException {
    this.bean = bean;
    this.clazz = new StringBuilder();
    initClass();
    addFields();
    return clazz.append(newLine).append("}").toString();
  }

  private void initClass() {
    String clazzName = MoreTypes.asTypeElement(bean.getType()).getSimpleName().toString();
    String pkg = Utils.getPackageName(MoreTypes.asTypeElement(bean.getType()));


    clazz.append("package ").append(clazzName).append(";");
    clazz.append(newLine);
    clazz.append("class ").append(pkg).append("Info").append(" {");
  }


  private void addFields() {
    for (InjectableVariableDefinition fieldPoint : bean.getFields()) {
      clazz.append(newLine);
      makeSetter(fieldPoint);
      clazz.append(newLine);
      makeGetter(fieldPoint);
    }
  }

  private void makeSetter(InjectableVariableDefinition fieldPoint) {
    clazz.append("static native void ").append(fieldPoint.getVariableElement().getSimpleName())
        .append("(");
    clazz.append(MoreTypes.asTypeElement(bean.getType()).getQualifiedName()).append(" ")
        .append(" instance").append(",");
    clazz.append("Object").append(" ").append(" value").append(")/*-{");
    clazz.append(newLine);

    clazz.append(" ").append("instance.@").append(bean.getQualifiedName()).append("::")
        .append(fieldPoint.getVariableElement().getSimpleName()).append("=").append("value;");

    clazz.append(newLine).append("}-*/;");
  }

  private void makeGetter(InjectableVariableDefinition fieldPoint) {
    clazz.append("static native ")
        .append(
            MoreTypes.asTypeElement(fieldPoint.getVariableElement().asType()).getQualifiedName())
        .append(" ").append(fieldPoint.getVariableElement().getSimpleName()).append("(");
    clazz.append(MoreTypes.asTypeElement(bean.getType()).getQualifiedName().toString()).append(" ")
        .append(" instance");
    clazz.append(")/*-{");
    clazz.append(newLine);

    clazz.append(" ").append("return instance.@").append(bean.getQualifiedName()).append("::")
        .append(fieldPoint.getVariableElement().getSimpleName()).append(";");

    clazz.append(newLine).append("}-*/;");
  }


}
