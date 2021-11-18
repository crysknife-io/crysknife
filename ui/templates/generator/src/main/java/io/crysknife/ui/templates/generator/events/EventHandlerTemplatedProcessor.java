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

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import org.jboss.gwt.elemento.processor.AbortProcessingException;
import org.jboss.gwt.elemento.processor.context.DataElementInfo;
import org.jboss.gwt.elemento.processor.context.EventHandlerInfo;
import org.jboss.gwt.elemento.processor.context.TemplateContext;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.crysknife.ui.templates.generator.events.Elemental2EventsMapping.EVENTS;

public class EventHandlerTemplatedProcessor {

  private final IOCContext iocContext;
  private EventHandlerValidator eventHandlerValidator;

  public EventHandlerTemplatedProcessor(IOCContext context) {
    this.iocContext = context;
    this.eventHandlerValidator = new EventHandlerValidator(iocContext);
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

  private String[] getEvents(VariableElement parameter) {
    if (parameter.getAnnotation(ForEvent.class) != null) {
      return parameter.getAnnotation(ForEvent.class).value();
    }
    String[] result = new String[1];
    result[0] = EVENTS.get(MoreTypes.asDeclared(parameter.asType()).toString());
    return result;
  }

  private void abortWithError(Element element, String msg, Object... args) {
    error(element, msg, args);
    throw new AbortProcessingException();
  }

  public void error(Element element, String msg, Object... args) {
    this.iocContext.getGenerationContext().getProcessingEnvironment().getMessager()
        .printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);
  }
}
