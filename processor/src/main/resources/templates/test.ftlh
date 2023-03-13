package ${package};

import java.lang.annotation.Annotation;
import java.util.function.Supplier;
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


<#if imports?? && imports?size != 0>
    <#list imports as import>
import ${import};
    </#list>
</#if>

public class ${clazz}_Factory extends <#if isProxy>Proxy</#if>BeanFactory<${bean}> {

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

    @Override()
    public ${bean} createInstance() {
<#if isDependent>
    instance = new ${bean}(<#if constructorParams??>${constructorParams}</#if>);
<#else>
  <#if isProxy>
      this.instance = new Proxy${bean}();
  <#else>
      if (this.instance != null) {
        return (${bean}) instance;
      }
      instance = new ${bean}(<#if constructorParams??>${constructorParams}</#if>);

  </#if>

</#if>
    return (${bean}) instance;
}


<#--<#if jre>
<#else>
        this.interceptor = new Interceptor(new ${bean}(<#if constructorParams??>${constructorParams}</#if>));
        instance = this.interceptor.getProxy();
        this.interceptor.addGetPropertyInterceptor(Reflect.objectProperty("f_headingElement__io_crysknife_demo_client_Elemental2Bean_", this.instance), new OnFieldAccessed(() -> this._field_headingElement.get()));
        this.interceptor.addGetPropertyInterceptor(Reflect.objectProperty("f_buttonElement__io_crysknife_demo_client_Elemental2Bean_", this.instance), new OnFieldAccessed(() -> this._field_buttonElement.get()));
</#if>-->



    @Override()
    public void doInitInstance(${bean} instance) {
<#if postConstruct?? && postConstruct?size != 0>
    <#list postConstruct as call>
        ${call?no_esc};
    </#list>
</#if>
<#if doInitInstance?? && doInitInstance?size != 0>
    <#list doInitInstance as call>
        ${call?no_esc}
    </#list>
</#if>
    }
<#if isDependent>
    @Override()
    public ${bean} getInstance() {
        if (!beanDef.getScope().equals(Dependent.class)) {
            if (instance != null) {
                return (${bean}) instance;
            }
        }
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

<#if preDestroy?? && preDestroy?size != 0>
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


}
