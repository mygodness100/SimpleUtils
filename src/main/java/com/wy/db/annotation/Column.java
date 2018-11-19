package com.wy.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当为类添加该注解时,不检查isNullable,也不检查value字段,只检查hump的值,类中所有字段都是数据库字段
 * @author wanyang 2018年11月19日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface Column {
	// 对应数据库字段名,为空则与java字段相同
	String value() default "";

	// 是否可为空,默认可
	boolean isNullable() default true;

	// 对应数据库字段是否驼峰转下划线,默认否
	boolean hump() default false;
}