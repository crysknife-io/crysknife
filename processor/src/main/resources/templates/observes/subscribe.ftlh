((AbstractEventHandler) beanManager.lookupBean(EventManager.class).getInstance().get(${target}.class)).addSubscriber(<#if isDependent>instance<#else>this</#if>,  ${consumer});
