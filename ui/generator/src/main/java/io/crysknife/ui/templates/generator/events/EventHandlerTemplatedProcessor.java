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

package io.crysknife.ui.templates.generator.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.helpers.MethodCallGenerator;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.generator.dto.Event;
import io.crysknife.ui.templates.generator.dto.TemplateDefinition;
import org.jboss.gwt.elemento.processor.AbortProcessingException;
import org.jboss.gwt.elemento.processor.context.DataElementInfo;
import org.jboss.gwt.elemento.processor.context.DataElementInfo.Kind;
import org.jboss.gwt.elemento.processor.context.EventHandlerInfo;
import org.jboss.gwt.elemento.processor.context.TemplateContext;
import org.treblereel.j2cl.processors.utils.J2CLUtils;

public class EventHandlerTemplatedProcessor {

  private final IOCContext iocContext;
  private final EventHandlerValidator eventHandlerValidator;
  private final J2CLUtils j2CLUtils;
  private final MethodCallGenerator methodCallGenerator;

  public EventHandlerTemplatedProcessor(IOCContext context) {
    this.iocContext = context;
    this.eventHandlerValidator = new EventHandlerValidator(iocContext);
    this.j2CLUtils = new J2CLUtils(context.getGenerationContext().getProcessingEnvironment());
    this.methodCallGenerator = new MethodCallGenerator(context);
  }

  public List<EventHandlerInfo> processEventHandlers(TypeElement type,
      TemplateContext templateContext) {
    List<EventHandlerInfo> eventHandlerElements = new ArrayList<>();
    Set<UnableToCompleteException> errors = new HashSet<>();

    for (ExecutableElement method : ElementFilter.methodsIn(type.getEnclosedElements())) {
      if (MoreElements.isAnnotationPresent(method, EventHandler.class)) {
        try {
          eventHandlerValidator.validate(method);
        } catch (UnableToCompleteException e) {
          errors.add(e);
        }

        VariableElement parameter = method.getParameters().get(0);
        DeclaredType declaredType = MoreTypes.asDeclared(parameter.asType());

        String[] events = getEvents(parameter);
        String[] dataElements = method.getAnnotation(EventHandler.class).value();

        if (dataElements.length > 0) {
          Arrays.stream(dataElements).forEach(data -> {
            java.util.Optional<DataElementInfo> result = templateContext.getDataElements().stream()
                .filter(elm -> elm.getSelector().equals(data)).findFirst();
            if (result.isPresent()) {
              DataElementInfo info = result.get();
              eventHandlerElements
                  .add(new EventHandlerInfo(info, events, method, declaredType.toString()));
            } else {
              abortWithError(method,
                  "Unable to find DataField element with name or alias " + data + " from ");
            }
          });
          // Handle events, that binds to the root of the template
        } else {
          eventHandlerElements
              .add(new EventHandlerInfo(null, events, method, declaredType.toString()));
        }
      }
    }

    if (!errors.isEmpty()) {
      throw new GenerationException(new UnableToCompleteException(errors));
    }
    return eventHandlerElements;
  }

  public void generateEventCode(BeanDefinition beanDefinition, TemplateContext templateContext,
                                TemplateDefinition templateDefinition) {
    for (EventHandlerInfo eventHandlerInfo : templateContext.getEvents()) {
      try {
        eventHandlerValidator.validate(eventHandlerInfo.getMethod());
        String[] eventTypes = eventHandlerInfo.getMethod().getParameters().get(0)
                .getAnnotation(ForEvent.class).value();
        String clazz = iocContext.getGenerationContext().getTypes()
                .erasure(eventHandlerInfo.getMethod().getParameters().get(0).asType()).toString();
        String call = methodCallGenerator.generate(beanDefinition.getType(),
                eventHandlerInfo.getMethod(), List.of("e"));
        DataElementInfo info = eventHandlerInfo.getInfo();
        boolean rootBound = info == null;
        String mangleName = rootBound ? null
                : j2CLUtils.getVariableMangledName(info.getField());
        boolean isElement = !rootBound && info.getKind() == Kind.IsElement;
        boolean elementoIsElement = !rootBound && info.getKind() == Kind.ElementoIsElement;
        Event event = new Event(eventTypes, mangleName, clazz, call,
                isElement, elementoIsElement, rootBound);
        templateDefinition.getEvents().add(event);
      } catch (UnableToCompleteException e) {
        throw new GenerationException(e);
      }
    }
  }

  private String[] getEvents(VariableElement parameter) {
    if (parameter.getAnnotation(ForEvent.class) != null) {
      return parameter.getAnnotation(ForEvent.class).value();
    }
    throw new GenerationException("Parameter " + parameter
        + " must be annotated with @ForEvent annotation at " + parameter.getEnclosingElement() + "."
        + parameter.getEnclosingElement().getEnclosingElement() + "." + parameter);
  }

  private void abortWithError(Element element, String msg, Object... args) {
    error(element, msg, args);
    throw new AbortProcessingException();
  }


  // todo
  public void error(Element element, String msg, Object... args) {
    System.out.println(
        "ERROR: " + String.format(msg, args) + " " + element.getEnclosingElement() + "." + element);
    // this.iocContext.getGenerationContext().getProcessingEnvironment().getMessager()
    // .printMessage(Kind.ERROR, String.format(msg, args), element);
  }
}
