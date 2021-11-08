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

package io.crysknife.generator.context;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import io.crysknife.generator.context.oracle.ResourceOracle;
import io.crysknife.generator.context.oracle.ResourceOracleImpl;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 2/21/19
 */
public class GenerationContext {

  private final RoundEnvironment roundEnvironment;
  private final ProcessingEnvironment processingEnvironment;
  private final ScanResult scanResult = new ClassGraph().enableAllInfo().scan();
  private final ResourceOracle resourceOracle = new ResourceOracleImpl(this);
  private ExecutionEnv executionEnv = ExecutionEnv.J2CL;


  public GenerationContext(RoundEnvironment roundEnvironment,
      ProcessingEnvironment processingEnvironment) {
    this.roundEnvironment = roundEnvironment;
    this.processingEnvironment = processingEnvironment;

    try {
      Class.forName("com.google.gwt.core.client.GWT");
      executionEnv = ExecutionEnv.GWT2;
      processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING,
          "GWT2 generation mode.");
    } catch (ClassNotFoundException e) {

    }

    try {
      Class.forName("org.aspectj.lang.ProceedingJoinPoint");
      executionEnv = ExecutionEnv.JRE;
      processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING,
          "JRE generation mode.");
    } catch (ClassNotFoundException e) {

    }
    System.out.println("Current generation mode: " + executionEnv);
  }

  public ExecutionEnv getExecutionEnv() {
    return executionEnv;
  }

  public Elements getElements() {
    return processingEnvironment.getElementUtils();
  }

  public Types getTypes() {
    return processingEnvironment.getTypeUtils();
  }

  public RoundEnvironment getRoundEnvironment() {
    return roundEnvironment;
  }

  public ProcessingEnvironment getProcessingEnvironment() {
    return processingEnvironment;
  }

  public ResourceOracle getResourceOracle() {
    return resourceOracle;
  }

  public ScanResult getScanResult() {
    return scanResult;
  }
}
