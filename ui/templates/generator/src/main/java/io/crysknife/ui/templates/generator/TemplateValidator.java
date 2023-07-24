package io.crysknife.ui.templates.generator;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import io.crysknife.client.IsElement;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.templates.client.annotation.Templated;
import io.crysknife.validation.Check;

public class TemplateValidator extends io.crysknife.validation.Validator<TypeElement> {

  private final TypeMirror isElement;
  private final Types types;
  private final Elements elements;


  public TemplateValidator(IOCContext context) {
    super(context);

    types = context.getGenerationContext().getTypes();
    elements = context.getGenerationContext().getElements();

    isElement = types.erasure(elements.getTypeElement(IsElement.class.getCanonicalName()).asType());

    addCheck(new Check<>() {
      @Override
      public void check(TypeElement element) throws UnableToCompleteException {
        if (element.getAnnotation(Templated.class) == null) {
          log(element, "Templated class must be annotated with @Templated");
        }
      }
    });

    addCheck(new Check<>() {
      @Override
      public void check(TypeElement element) throws UnableToCompleteException {
        if (!types.isSubtype(element.asType(), isElement)) {
          log(element, "Templated class must implement IsElement");
        }
      }
    });
  }
}
