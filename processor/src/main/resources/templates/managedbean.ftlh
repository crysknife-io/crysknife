package ${package};

import java.lang.annotation.Annotation;
import java.util.function.Supplier;
import java.util.Set;
import java.util.HashSet;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Provider;

import io.crysknife.client.BeanManager;
import io.crysknife.client.Reflect;
import io.crysknife.client.SyncBeanDef;
import io.crysknife.client.InstanceFactory;
import io.crysknife.client.internal.InstanceImpl;
import io.crysknife.client.internal.ManagedInstanceImpl;
import io.crysknife.client.internal.BeanFactory;
import io.crysknife.client.internal.proxy.OnFieldAccessed;
import io.crysknife.client.internal.QualifierUtil;

<#if !jre>
import io.crysknife.client.internal.proxy.Interceptor;
import io.crysknife.client.internal.proxy.OnFieldAccessed;
import io.crysknife.client.Reflect;
</#if>

<#list imports as import>
import ${import};
</#list>

@SuppressWarnings({"unchecked", "rawtypes"})
public class ${clazz}_Factory extends <#if isProxy>Proxy</#if>BeanFactory<${bean}> {

    <#list fields as call>
    ${call?no_esc}
    </#list>

    <#list deps as dep>
    private Supplier<InstanceFactory<${dep.fqdn}>> ${dep.fieldName} = ${dep.call?no_esc};
    </#list>

    @Override()
    public ${bean} createInstance() {
        Set<Object> deps = new HashSet<>();
    <#if !jre>
        <#if isDependent>
            final Interceptor interceptor = new Interceptor(new ${bean}(<#if constructorParams??>${constructorParams?no_esc}</#if>));
        <#else>
            <#if isProxy>
            final Interceptor interceptor = new Interceptor(new Proxy${bean}());
            <#else>
            if (this.instance != null) {
                return (${bean}) instance;
            }
            final Interceptor interceptor = new Interceptor(new ${bean}(<#if constructorParams??>${constructorParams?no_esc}</#if>));
            </#if>
    </#if>
        <#if isDependent>${bean} </#if>instance = interceptor.getProxy();
        <#list fieldInterceptors as call>
            ${call?no_esc};
        </#list>
    <#else>

  <#if isDependent>
      ${bean} instance = new ${bean}(<#if constructorParams??>${constructorParams?no_esc}</#if>);
  <#else>
    <#if isProxy>
        instance = new Proxy${bean}();
    <#else>
        if (this.instance != null) {
          return (${bean}) instance;
        }
        instance = new ${bean}(<#if constructorParams??>${constructorParams?no_esc}</#if>);
    </#if>
  </#if>
</#if>
        addDependencyConstructor(instance, deps);
        deps.clear();
    <#list doCreateInstance as call>
        ${call?no_esc};
    </#list>
        return (${bean}) instance;
    }

    @Override()
    public void doInitInstance(${bean} instance) {
<#list doInitInstance as call>
        ${call?no_esc}
</#list>
<#list postConstruct as call>
    ${call?no_esc}
</#list>
    }
<#if isDependent>
    @Override()
    public ${bean} getInstance() {
        ${bean} instance = createInstanceInternal();
        initInstance(instance);
        return instance;
    }
<#else>
    @Override()
    public ${bean} getInstance() {
        <#if isProxy>
        if (instance == null) {
          createInstance();
          initDelegate();
        }
        <#else>
        if (instance != null) {
            return (${bean}) instance;
        }
        ${bean} instance = createInstanceInternal();
        initInstance(instance);
        </#if>
        return instance;
    }

</#if>

<#if preDestroy??>
    @Override()
    protected void onDestroy(${bean} instance) {
    <#list preDestroy as call>
        ${call?no_esc}
    </#list>
    }
</#if>

    public ${clazz}_Factory(BeanManager beanManager) {
        super(beanManager);
    }

<#if !jre>
    <#if privateMethods?? && privateMethods?size != 0>
    @JsType(isNative=true, namespace="${package}", name="${bean}")
    public static class ${bean}Proxy {
        <#list privateMethods as mangle>
            @JsProperty(name="${mangle}")
            public Function ${mangle};
        </#list>
    }
    </#if>


</#if>

}
