package com.wy.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface TestAnnotation {
	String color() default "blue";
	String value();
	int[] arr() default {1,2};
	//可放枚举
	//注解
}
