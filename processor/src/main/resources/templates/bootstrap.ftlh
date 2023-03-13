package ${package};

import java.util.function.Supplier;
import jakarta.inject.Provider;

import io.crysknife.client.internal.proxy.OnFieldAccessed;
import io.crysknife.client.Reflect;
import io.crysknife.client.SyncBeanDef;
import io.crysknife.client.internal.BeanFactory;
import io.crysknife.client.BeanManager;
import io.crysknife.client.InstanceFactory;

public class ${bean}Bootstrap {

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

}
