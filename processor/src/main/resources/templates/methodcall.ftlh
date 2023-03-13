<#if private>



<#if jre>
        try {
            java.lang.reflect.Method method = org.apache.commons.lang3.reflect.MethodUtils.getMatchingMethod(${parent}.class, "${name}");
            method.setAccessible(true);
            method.invoke(instance);
        } catch (Exception e) {
            throw new Error(e);
        };
<#else>
    OLOLO
</#if>

<#else>
    instance.${name}();
</#if>
