package net.lamad.spring.dvalue.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class DValueBeanPostProcessor implements BeanPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(DValueBeanPostProcessor.class);

    @Inject
    private Provider<DValueUpdater> dValueUpdaterProvider;

    private final List<DValueUpdater> dValueUpdaters = new ArrayList<>();

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
                TargetMethod targetMethod = new TargetMethod(bean, method, (DValue) annotation);
                dValueUpdaters.add(
                        dValueUpdaterProvider.get().watch(targetMethod)
                );
            }
        }
    }
}
