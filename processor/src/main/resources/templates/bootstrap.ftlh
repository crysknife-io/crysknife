package ${package};

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import jakarta.inject.Provider;

import io.crysknife.client.Reflect;
import io.crysknife.client.SyncBeanDef;
import io.crysknife.client.internal.BeanFactory;
import io.crysknife.client.BeanManager;
import io.crysknife.client.InstanceFactory;

<#if !jre>
import java.util.function.BiFunction;
import io.crysknife.client.internal.proxy.Interceptor;
import io.crysknife.client.internal.proxy.OnFieldAccessed;
import io.crysknife.client.Reflect;
</#if>

<#list imports as import>
import ${import};
</#list>

public class ${bean}Bootstrap {

<#if !jre>
    private Interceptor interceptor;
</#if>

    private final BeanManager beanManager = io.crysknife.client.BeanManagerImpl.get();

<#if fields?? && fields?size != 0>
    <#list fields as call>
    ${call?no_esc}
    </#list>
</#if>

<#if deps?? && deps?size != 0>
    <#list deps as dep>
    private Supplier<InstanceFactory<${dep.fqdn}>> ${dep.fieldName} = ${dep.call?no_esc};
    </#list>
</#if>

    private ${package}.${bean} instance;

    public void doInitInstance(${bean} instance) {
<#list doInitInstance as call>
    ${call?no_esc}
</#list>
<#if postConstruct?? && postConstruct?size != 0>
    <#list postConstruct as call>
        ${call?no_esc};
    </#list>
</#if>
    }

    ${bean}Bootstrap(${bean} application) {
        this.instance = application;
    }

    void initialize() {
<#if !jre>
        initInterceptors();
</#if>
        runOnStartup();
        doInitInstance(instance);
    }

    private void runOnStartup() {
<#if onStartup?? && onStartup?size != 0>
    <#list onStartup as bean>
        beanManager.lookupBean(${bean}.class).getInstance();
    </#list>
</#if>
    }

<#if !jre>
    private void initInterceptors() {
        this.interceptor = new Interceptor(instance);
        instance = interceptor.getProxy();
<#list fieldInterceptors as call>
        ${call?no_esc};
</#list>
    }
</#if>

<#if !jre>
    public final class OnFieldAccessed implements BiFunction<Object, String, Object> {

        private final Supplier<InstanceFactory> supplier;

        public OnFieldAccessed(${bean}Bootstrap appbootstrap, Supplier<InstanceFactory> supplier) {
            this.supplier = supplier;
        }

        @Override
        public Object apply(Object o, String propertyKey) {
            if (elemental2.core.Reflect.get(o, propertyKey) == null) {
              elemental2.core.Reflect.set(o, propertyKey, supplier.get().getInstance());
            }
            return elemental2.core.Reflect.get(o, propertyKey);
       }
    }
</#if>
}
