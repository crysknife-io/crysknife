<#if isDependent>
beanManager.lookupBean(EventManager.class).getInstance().getRegistry().subscribe(${target}.class, instance, _event_ -> {
    ${call?no_esc}    });
<#else>
beanManager.lookupBean(EventManager.class).getInstance().getRegistry().subscribe(${target}.class, this, ${consumer});
</#if>
