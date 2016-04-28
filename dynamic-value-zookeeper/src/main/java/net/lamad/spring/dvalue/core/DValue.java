package net.lamad.spring.dvalue.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DValue {
    String path();

    String type() default "String";
    String charset() default "UTF-8";
}
