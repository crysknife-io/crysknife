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

package io.crysknife.generator.definition;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.crysknife.annotation.Generator;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.api.ClassBuilder;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/3/19
 */
public abstract class Definition {

  public static final Comparator<IOCGenerator> iocGeneratorComparator =
      Comparator.comparing(h -> h.getClass().getAnnotation(Generator.class).priority());

  protected final Map<IOCGenerator, LinkedList<ExecutableDefinition>> executableDefinitions =
      new HashMap<>();

  protected Set<BeanDefinition> dependsOn = new LinkedHashSet<>();

  protected Optional<IOCGenerator> generator = Optional.empty();

  protected Map<IOCGenerator, Definition> decorators = new HashMap<>();

  public void setGenerator(IOCGenerator generator) {
    this.generator = Optional.of(generator);
  }

  public void generate(ClassBuilder classBuilder) {
    // if (generator.isPresent()) {
    // generator.get().generate(classBuilder, this);
    // }
  }

  public void generateDecorators(ClassBuilder builder) {
    // decorators.keySet().stream().sorted(iocGeneratorComparator)
    // .forEach(decorator -> (decorator).generate(builder, this));
  }

  public <T extends Definition> T addDecorator(IOCGenerator generator, Definition definition) {
    decorators.put(generator, definition);
    return (T) this;
  }
}
