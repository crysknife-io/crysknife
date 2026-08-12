package ${package};

import java.util.function.BiConsumer;

import io.crysknife.ui.databinding.internal.BindableProxy;
import io.crysknife.ui.databinding.internal.BindingAgent;

public class ${bean}_BindableProxy extends ${bean} implements BindableProxy<${bean}> {

    private ${bean} target;
    private final BindingAgent agent;

<#list properties as prop>
<#if prop.bindable>
    private ${prop.type?no_esc}_BindableProxy _proxy_${prop.name};
</#if>
<#if prop.list>
    @SuppressWarnings({"unchecked", "rawtypes"})
    private io.crysknife.ui.databinding.internal.ObservableList _observable_${prop.name};
</#if>
</#list>

    public ${bean}_BindableProxy() {
        this.target = new ${bean}();
        this.agent = new BindingAgent(this, createPropertySetter());
    }

    public ${bean}_BindableProxy(${bean} target) {
        this.target = target;
        this.agent = new BindingAgent(this, createPropertySetter());
    }

    private BiConsumer<String, Object> createPropertySetter() {
        return (property, value) -> setPropertyValue(property, value);
    }

<#list properties as prop>
<#if prop.bindable>
    @Override
    public ${prop.type?no_esc} ${prop.getterName}() {
        ${prop.type?no_esc} raw = target.${prop.getterName}();
        if (raw == null) return null;
        if (_proxy_${prop.name} == null || _proxy_${prop.name}.unwrap() != raw) {
            _proxy_${prop.name} = new ${prop.type?no_esc}_BindableProxy(raw);
        }
        return _proxy_${prop.name};
    }

    @Override
    public void set${prop.capitalizedName}(${prop.type?no_esc} value) {
        ${prop.type?no_esc} oldValue = target.${prop.getterName}();
        target.set${prop.capitalizedName}(value);
        _proxy_${prop.name} = null;
        agent.onPropertyChanged(this, "${prop.name}", oldValue, value);
    }

<#elseif prop.list>
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ${prop.type?no_esc} ${prop.getterName}() {
        ${prop.type?no_esc} raw = target.${prop.getterName}();
        if (raw == null) return null;
        if (_observable_${prop.name} == null) {
            _observable_${prop.name} = new io.crysknife.ui.databinding.internal.ObservableList(
                raw, "${prop.name}", (java.util.function.Consumer<String>) propName -> agent.onCollectionChanged(propName));
        }
        return _observable_${prop.name};
    }

    @Override
    public void set${prop.capitalizedName}(${prop.type?no_esc} value) {
        ${prop.type?no_esc} oldValue = target.${prop.getterName}();
        target.set${prop.capitalizedName}(value);
        _observable_${prop.name} = null;
        agent.onPropertyChanged(this, "${prop.name}", oldValue, value);
    }

<#else>
    @Override
    public ${prop.type?no_esc} ${prop.getterName}() {
        return target.${prop.getterName}();
    }

    @Override
    public void set${prop.capitalizedName}(${prop.type?no_esc} value) {
        ${prop.type?no_esc} oldValue = target.${prop.getterName}();
        target.set${prop.capitalizedName}(value);
        agent.onPropertyChanged(this, "${prop.name}", oldValue, value);
    }

</#if>
</#list>

    @Override
    public void setTarget(${bean} target) {
        this.target = target;
<#list properties as prop>
<#if prop.bindable>
        _proxy_${prop.name} = null;
</#if>
<#if prop.list>
        _observable_${prop.name} = null;
</#if>
</#list>
    }

    @Override
    public ${bean} unwrap() {
        return target;
    }

    @Override
    public BindingAgent getBindingAgent() {
        return agent;
    }

    @Override
    public Object getPropertyValue(String propertyName) {
        switch (propertyName) {
<#list properties as prop>
            case "${prop.name}":
<#if prop.bindable || prop.list>
                return ${prop.getterName}();
<#else>
                return target.${prop.getterName}();
</#if>
</#list>
            default:
                throw new IllegalArgumentException("Unknown property: " + propertyName);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setPropertyValue(String propertyName, Object value) {
        switch (propertyName) {
<#list properties as prop>
            case "${prop.name}":
                this.set${prop.capitalizedName}((${prop.type?no_esc}) value);
                break;
</#list>
            default:
                throw new IllegalArgumentException("Unknown property: " + propertyName);
        }
    }
}
