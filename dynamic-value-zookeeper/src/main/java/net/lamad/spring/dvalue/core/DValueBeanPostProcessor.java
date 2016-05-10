package net.lamad.spring.dvalue.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

@Component
public class DValueBeanPostProcessor implements BeanPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(DValueBeanPostProcessor.class);

    @Inject
    private List<DValueUpdaterService> dValueUpdaterServices;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        logger.debug("Process bean {} for @DValue Annotation", beanName);
        Method[] methods = bean.getClass().getMethods();
        for (Method method: methods) {
            searchAnnotation(bean, method, beanName, bean.getClass());
        }
        return bean;
    }

    private void searchAnnotation(Object bean, Method method, String beanName, Class type) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation: annotations) {
            if (annotation instanceof DValue) {
                logger.debug("@Zvalue found on : {}.{}", beanName, method.getName());
                validateMethod(method, beanName);

                TargetMethod targetMethod = new TargetMethod(bean, method, (DValue) annotation);

                findDValueServiceAndHandle(targetMethod);
            }
        }
    }

    private void validateMethod(Method method, String beanName) {
        Class[] parametersType = method.getParameterTypes();
        if (parametersType.length != 1) {
            throw new IllegalArgumentException("Method for @Dvalue must have only one parameter" + method.toString());
        }
    }

    public void findDValueServiceAndHandle(TargetMethod targetMethod) {
        for (DValueUpdaterService service : dValueUpdaterServices) {
            if (service.handlingSuccessfull(targetMethod)) {
                return ;
            }
        }
        throw new IllegalStateException("No DValueUpdaterService found for path: " + targetMethod.getDValueAnnotation().path());
    }
}
