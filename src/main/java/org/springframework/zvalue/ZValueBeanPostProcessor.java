package org.springframework.zvalue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ZValue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class ZValueBeanPostProcessor implements BeanPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(ZValueBeanPostProcessor.class);

    @Inject
    private Provider<ZValueUpdater> zValueUpdaterProvider;

    private final List<ZValueUpdater> zValueUpdaters = new ArrayList<ZValueUpdater>();

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        logger.debug("Process bean {} for @ZValue Annotation", beanName);
        Method[] methods = bean.getClass().getMethods();
        for (Method method: methods) {
            searchAnnotation(bean, method, beanName, bean.getClass());
        }
        return bean;
    }

    private void searchAnnotation(Object bean, Method method, String beanName, Class type) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation: annotations) {
            if (annotation instanceof ZValue) {
                logger.debug("@Zvalue found on : {}.{}", beanName, method.getName());
                zValueUpdaters.add(
                        zValueUpdaterProvider.get().watch(bean, method, ((ZValue) annotation).path())
                );
            }
        }
    }
}
