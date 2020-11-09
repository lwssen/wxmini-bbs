package com.sss.sssforum.annotation;

import java.lang.annotation.*;


@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TokenCheck {

    String value() default "";
}
