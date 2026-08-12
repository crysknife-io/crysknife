<#if isDependent>
beanManager.lookupBean(EventManager.class).getInstance().getRegistry().unsubscribe(${target}.class, instance);
<#else>
beanManager.lookupBean(EventManager.class).getInstance().getRegistry().unsubscribe(${target}.class, this);
</#if>
