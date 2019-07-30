package com.wy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description 当字段上有该注解时表示,字段的值在数据库中唯一
 * @author ParadiseWY
 * @date 2019年7月30日 下午5:21:07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Unique {
	/**
	 * 数据库字段的名称
	 */
	String value() default "";

	/**
	 * 当value为""时,取实体类字段的名称,实体类是否需要转换为蛇形
	 */
	boolean hump2Snake() default true;
}
