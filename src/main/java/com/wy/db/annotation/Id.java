package com.wy.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对应数字类型主键
 * @author wanyang 2018年11月19日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {
	String value() default "";

	boolean isAutoIncrement() default true;

	boolean hump() default false;
}