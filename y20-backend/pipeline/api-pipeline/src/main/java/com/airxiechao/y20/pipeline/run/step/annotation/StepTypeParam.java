package com.airxiechao.y20.pipeline.run.step.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StepTypeParam {
    String name() default "";
    String displayName();
    int displayOrder() default 0;
    String hint() default "";
    String type();
    String defaultValue() default "";
    StepTypeSelectOption[] options() default {};
}
