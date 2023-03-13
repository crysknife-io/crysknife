final class Proxy${bean} extends ${bean} implements io.crysknife.client.internal.proxy.CircularDependencyProxy<${bean}> {

    private Proxy${bean}() {
    super(<#if constructorParams??>${nullConstructorParams}</#if>);
    }

    private ${bean} instance;


<#if methods?? && methods?size != 0>
    <#list methods as method>
    public ${method.returnType} ${method.name}(<#if method.parameters??>${method.parameters}</#if>) {
        <#if method.isVoid>
            this.instance.${method.name}(${method.parametersNames});
        <#else>
            return this.instance.${method.name}(${method.parametersNames});
        </#if>
    }
    </#list>
</#if>

    @Override()
    public ${bean} unwrap() {
        return instance;
    }

    @Override()
    public void setInstance(${bean} delegate) {
     instance = delegate;
    }
}

    private void initDelegate() {
        ${bean} delegate = new ${bean}(<#if constructorParams??>${constructorParams}</#if>);
        ((Proxy${bean}) instance).setInstance(delegate);
        doInitInstance(instance);
    }

    @Override
    public void dependantBeanReady(Class clazz) {

    }
