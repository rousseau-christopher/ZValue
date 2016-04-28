package net.lamad.spring.dvalue.zookeeper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class TargetMethod {
    private final Object bean;
    private final Method method;
    private final ZValue zValue;

    TargetMethod(Object bean, Method method, ZValue zValue) {
        this.bean = bean;
        this.method = method;
        this.zValue = zValue;
    }

    Class getType() {
        return method.getParameterTypes()[0];
    }

    void call(Object value) throws InvocationTargetException, IllegalAccessException {
        method.invoke(bean, value);
    }

    ZValue getZValue() {
        return zValue;
    }

    String getAsFilePath() {
        String path = zValue.path().replace('.', '/');
        if (path.charAt(0) != '/') {
            path = '/' + path;
        }
        return path;
    }
}
