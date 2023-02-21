package ${package};


<#if imports?? && imports?size != 0>
    <#list imports as import>
import ${import};
    </#list>
</#if>

public class ${clazz}_Factory extends BeanFactory<${bean}> {


<#if deps?? && deps?size != 0>
    <#list deps as dep>
    private Supplier<InstanceFactory<${dep.fqdn}>> ${dep.fieldName} = ${dep.call?no_esc};
    </#list>
</#if>

    @Override()
    public ${bean} createInstance() {
        if (this.instance != null)
            return (${bean}) instance;
<#if jre>
        instance = new ${bean}(<#if constructorParams??>${constructorParams}</#if>);
<#else>
        this.interceptor = new Interceptor(new ${bean}(<#if constructorParams??>${constructorParams}</#if>));
        instance = this.interceptor.getProxy();
        this.interceptor.addGetPropertyInterceptor(Reflect.objectProperty("f_headingElement__io_crysknife_demo_client_Elemental2Bean_", this.instance), new OnFieldAccessed(() -> this._field_headingElement.get()));
        this.interceptor.addGetPropertyInterceptor(Reflect.objectProperty("f_buttonElement__io_crysknife_demo_client_Elemental2Bean_", this.instance), new OnFieldAccessed(() -> this._field_buttonElement.get()));
</#if>


        return (${bean}) instance;
    }

    public void doInitInstance(${bean} instance) {
<#if postConstruct?? && postConstruct?size != 0>
    <#list postConstruct as call>
        ${call?no_esc};
    </#list>
</#if>

    }
<#if isDependent>
    @Override()
    public ${bean} getInstance() {
    if (!beanDef.getScope().equals(Dependent.class)) {
        if (instance != null)
            return (${bean}) instance;
        }
        ${bean} instance = createInstanceInternal();
        initInstance(instance);
        return instance;
    }

    public ${clazz}_Factory(BeanManager beanManager) {
        super(beanManager);
    }
<#else>
    @Override()
    public ${bean} getInstance() {
    if (!beanDef.getScope().equals(Dependent.class)) {
    if (instance != null)
    return (${bean}) instance;
    }
    ${bean} instance = createInstanceInternal();
    initInstance(instance);
    return instance;
    }

    public ${clazz}_Factory(BeanManager beanManager) {
    super(beanManager);
    }
</#if>

<#if preDestroy??>
    @Override()
    protected void onDestroy(${bean} instance) {
        ${preDestroy?no_esc};
    }
</#if>


}
