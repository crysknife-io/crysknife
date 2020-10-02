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

package org.treblereel.gwt.crysknife.generator.info;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.treblereel.gwt.crysknife.client.Reflect;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class BeanInfoJ2CLGeneratorBuilder extends AbstractBeanInfoGenerator {

  private BeanDefinition bean;
  private CompilationUnit clazz = new CompilationUnit();
  private ClassOrInterfaceDeclaration classDeclaration;

  BeanInfoJ2CLGeneratorBuilder(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  protected String build(BeanDefinition bean) {
    this.bean = bean;
    this.clazz = new CompilationUnit();
    initClass();
    addFields();
    return clazz.toString();
  }

  private void initClass() {
    clazz.setPackageDeclaration(bean.getPackageName());
    classDeclaration = clazz.addClass(bean.getClassName() + "Info");
    clazz.addImport(Reflect.class);
  }

  private void addFields() {
    for (FieldPoint fieldPoint : bean.getFieldInjectionPoints()) {
      classDeclaration.addFieldWithInitializer(String.class, fieldPoint.getName(),
          new StringLiteralExpr(Utils.getJsFieldName(fieldPoint.getField())),
          Modifier.Keyword.PUBLIC, Modifier.Keyword.FINAL, Modifier.Keyword.STATIC);
    }
  }
}
