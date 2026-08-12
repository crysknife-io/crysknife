package ${package};

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import io.crysknife.client.BeanManager;
import io.crysknife.client.BeanManagerImpl;
import io.crysknife.client.internal.proxy.AroundInvokeCallback;
import io.crysknife.client.internal.proxy.JreInvocationContextImpl;

@Aspect()
public class ${bean}AroundInvokeAspect {

    private final BeanManager beanManager = BeanManagerImpl.get();

<#list methods as method>
    @Around(value = "${method.pointcut?no_esc}")
    public Object ${method.adviceName}(ProceedingJoinPoint pjp) throws Throwable {
        List<AroundInvokeCallback> chain = List.of(
            ${method.chain?no_esc}
        );
        java.lang.reflect.Method targetMethod =
            ((MethodSignature) pjp.getSignature()).getMethod();
        JreInvocationContextImpl ctx = new JreInvocationContextImpl(
            pjp.getTarget(),
            pjp.getArgs(),
            chain,
            (ic) -> {
                try {
                    return pjp.proceed(ic.getParameters());
                } catch (Exception e) {
                    throw e;
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            },
            targetMethod
        );
        return ctx.proceed();
    }
</#list>
}
