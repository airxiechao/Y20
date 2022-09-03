package com.airxiechao.y20.pipeline.run.step.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StepRun {
    String type();
    String name();
    String icon() default "";
    String description() default "";
    String category() default "";
    int order() default 0;
    Class paramClass();
    String[] requires() default {};
    String[] outputs() default {};
    boolean visible() default false;
}
