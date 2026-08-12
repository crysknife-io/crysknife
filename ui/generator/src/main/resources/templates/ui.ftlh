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
       io.crysknife.ui.common.client.SafeHtmlUtils.setInnerHTML(instance.getElement(), "${html?no_esc}");
<#list elements as element>
       if (Js.asPropertyMap(instance).get(Reflect.objectProperty("${element.mangledName}", instance)) == null) {
    <#if element.needCast>
           Js.asPropertyMap(instance).set(Reflect.objectProperty("${element.mangledName}", instance), TemplateUtil.<${element.element}>resolveElementAs(instance.getElement(), "${element.name}"));
    <#else>
           Js.asPropertyMap(instance).set(Reflect.objectProperty("${element.mangledName}", instance), TemplateUtil.resolveElement(instance.getElement(), "${element.name}"));
    </#if>
       } else {
<#if element.useBean>
    <#if element.elementoIsElement>
           TemplateUtil.replaceElementUseBean(instance.getElement(), "${element.name}", (elemental2.dom.HTMLElement) ((${element.element?no_esc}) Js.asPropertyMap(instance).get(Reflect.objectProperty("${element.mangledName}", instance))).element());
    <#elseif element.isElement>
           TemplateUtil.replaceElementUseBean(instance.getElement(), "${element.name}", ((io.crysknife.client.IsElement) Js.asPropertyMap(instance).get(Reflect.objectProperty("${element.mangledName}", instance))).getElement());
    <#else>
           TemplateUtil.replaceElementUseBean(instance.getElement(), "${element.name}", ((${element.element?no_esc}) Js.asPropertyMap(instance).get(Reflect.objectProperty("${element.mangledName}", instance))));
    </#if>
<#else>
    <#if element.elementoIsElement>
           TemplateUtil.replaceElement(instance.getElement(), "${element.name}", (elemental2.dom.HTMLElement) ((${element.element?no_esc}) Js.asPropertyMap(instance).get(Reflect.objectProperty("${element.mangledName}", instance))).element());
    <#elseif element.isElement>
           TemplateUtil.replaceElement(instance.getElement(), "${element.name}", ((io.crysknife.client.IsElement) Js.asPropertyMap(instance).get(Reflect.objectProperty("${element.mangledName}", instance))).getElement());
    <#else>
           TemplateUtil.replaceElement(instance.getElement(), "${element.name}", ((${element.element?no_esc}) Js.asPropertyMap(instance).get(Reflect.objectProperty("${element.mangledName}", instance))));
    </#if>
</#if>
       }
<#if element.roles?has_content>
       {
           io.crysknife.ui.security.SecurityContext _secCtx = (io.crysknife.ui.security.SecurityContext) beanManager.lookupBean(io.crysknife.ui.security.SecurityContext.class).getInstance();
           elemental2.dom.HTMLElement _el = (elemental2.dom.HTMLElement) Js.asPropertyMap(instance).get(Reflect.objectProperty("${element.mangledName}", instance));
           if (_el != null) {
               Runnable _guard = () -> {
                   _el.style.display = _secCtx.isUserInAllRoles(<#list element.roles as role>"${role}"<#if role?has_next>, </#if></#list>) ? "" : "none";
               };
               _guard.run();
               _secCtx.addChangeListener(_guard);
           }
       }
</#if>
</#list>

<#list events as event>
    <#list event.eventTypes as type>
    <#if event.rootBound>
       eventHandlerRegistration.add(instance, new EventHandlerHolder("${type}", instance.getElement(), (e) -> {
    <#elseif event.elementoIsElement>
       eventHandlerRegistration.add(instance, new EventHandlerHolder("${type}", (elemental2.dom.HTMLElement) ((org.jboss.elemento.IsElement) Js.asPropertyMap(instance).get(Reflect.objectProperty("${event.mangleName}", instance))).element(), (e) -> {
    <#elseif event.isElement>
       eventHandlerRegistration.add(instance, new EventHandlerHolder("${type}", ((io.crysknife.client.IsElement) Js.asPropertyMap(instance).get(Reflect.objectProperty("${event.mangleName}", instance))).getElement(), (e) -> {
    <#else>
       eventHandlerRegistration.add(instance, new EventHandlerHolder("${type}", ((elemental2.dom.HTMLElement) Js.asPropertyMap(instance).get(Reflect.objectProperty("${event.mangleName}", instance))), (e) -> {
    </#if>
           ${event.call?no_esc}
       }));
    </#list>
</#list>
    }
