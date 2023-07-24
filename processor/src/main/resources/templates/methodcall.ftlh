<#if private>
<#if jre>
        try {
            java.lang.reflect.Method method = org.apache.commons.lang3.reflect.MethodUtils.getMatchingMethod(${parent}.class, "${name}");
            method.setAccessible(true);
            method.invoke(instance, <#if args??>, ${args}</#if>);
        } catch (Exception e) {
            throw new Error(e);
        };
<#else>
    ((elemental2.core.Function) elemental2.core.Reflect.get(instance, io.crysknife.client.Reflect.objectProperty("${name?no_esc}", instance))).bind(instance<#if args??>, ${args}</#if>).call();
</#if>
<#else>
    instance.${name}(<#if args??>${args}</#if>);
</#if>
