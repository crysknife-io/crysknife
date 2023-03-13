package ${package};

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Supplier;

import jakarta.enterprise.inject.Instance;

import org.apache.commons.lang3.reflect.FieldUtils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;

import io.crysknife.client.BeanManager;
import io.crysknife.client.internal.InstanceImpl;
import io.crysknife.generator.info.Info;

@Aspect()
public class ${bean}Info extends Info {


<#if fields?? && fields?size != 0>
    <#list fields as field>

        @Before(value = "${field.target?no_esc}")
        public void ${field.method}(JoinPoint joinPoint) throws NoSuchFieldException, IllegalAccessException {
            try {
                String fieldName = "${field.field}";
                Field field = getField(joinPoint.getTarget().getClass(), fieldName);
                onInvoke(joinPoint, field, ${field.clazz?no_esc}.getInstance());
            } catch (NoSuchFieldException e) {
                throw new Error(e);
            } catch (IllegalAccessException e) {
                throw new Error(e);
            }
        }
    </#list>
</#if>

}
