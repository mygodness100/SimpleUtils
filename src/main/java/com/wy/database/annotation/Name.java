package com.wy.database.annotation;

/**
 * 对应字符串主键
 * @author paradiseWy 2018年11月19日
 */
public @interface Name {

	String value() default "";

	boolean hump() default false;
}