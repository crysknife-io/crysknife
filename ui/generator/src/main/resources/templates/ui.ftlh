<#if initRootElement>
@jsinterop.annotations.JsFunction
    private interface HTMLElementAccessor {
        elemental2.dom.HTMLElement getElement();
    }
</#if>
    private final EventHandlerRegistration eventHandlerRegistration = new EventHandlerRegistration();

    private static boolean isInjected = false;

    private void setAndInitTemplate(${bean} instance, Interceptor interceptor) {
<#if css??>
        if (!isInjected) {
          StyleInjector.fromString("${css?no_esc}").inject();
          isInjected = true;
        }
</#if>
<#if initRootElement>
        elemental2.core.Reflect.set(instance, "__root_element__", elemental2.dom.DomGlobal.document.createElement("div"));
        interceptor.addGetMethodInterceptor(Reflect.objectProperty("${rootElementPropertyName}", instance), new java.util.function.BiFunction<Object, String, Object>() {
                @Override
                public Object apply(Object o, String s) {
                    return new HTMLElementAccessor() {
                    @Override
                    public elemental2.dom.HTMLElement getElement() {
                        return (elemental2.dom.HTMLElement) elemental2.core.Reflect.get(instance, "__root_element__");
                        }
                    };
                }
        });
</#if>
<#list attributes as attribute>
       instance.getElement().setAttribute("${attribute.name}", "${attribute.value?no_esc}");
</#list>
       instance.getElement().innerHTML = "${html?no_esc}";
<#list elements as element>
       if (Js.asPropertyMap(instance).get(Reflect.objectProperty("${element.mangledName}", instance)) == null) {
    <#if element.needCast>
           Js.asPropertyMap(instance).set(Reflect.objectProperty("${element.mangledName}", instance), TemplateUtil.<${element.element}>resolveElementAs(instance.getElement(), "${element.name}"));
    <#else>
           Js.asPropertyMap(instance).set(Reflect.objectProperty("${element.mangledName}", instance), TemplateUtil.resolveElement(instance.getElement(), "${element.name}"));
    </#if>
       } else {
           TemplateUtil.replaceElement(instance.getElement(), "${element.name}", ((${element.element}) Js.asPropertyMap(instance).get(Reflect.objectProperty("${element.mangledName}", instance))));
       }
</#list>

<#list events as event>
    <#list event.eventTypes as type>
       eventHandlerRegistration.add(instance, new EventHandlerHolder("${type}", ((elemental2.dom.HTMLElement) Js.asPropertyMap(instance).get(Reflect.objectProperty("${event.mangleName}", instance))), (e) -> {
           ${event.call?no_esc}
       }));
    </#list>
</#list>
    }
