final class Proxy${bean} extends ${bean} implements io.crysknife.client.internal.proxy.CircularDependencyProxy<${bean}> {

    private Proxy${bean}() {
        super(<#if constructorParams??>${nullConstructorParams?no_esc}</#if>);
    }

    private ${bean} instance;


<#if methods?? && methods?size != 0>
    <#list methods as method>
    public ${method.returnType?no_esc} ${method.name}(<#if method.parameters??>${method.parameters?no_esc}</#if>) {
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
        Set deps = new HashSet();
        <#if jre>
        ${bean} delegate = new ${bean}(<#if constructorParams??>${constructorParams}</#if>);
        <#else>
        Interceptor interceptor = new Interceptor(new ${bean}(<#if constructorParams??>${constructorParams}</#if>));
        ${bean} delegate = interceptor.getProxy();
        </#if>

        ((Proxy${bean}) instance).setInstance(delegate);
        addDependencyConstructor(delegate, deps);
        deps.clear();
        doInitInstance(delegate);
    }

    @Override
    public void dependantBeanReady(Class clazz) {

    }
