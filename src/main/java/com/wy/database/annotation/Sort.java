package com.wy.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wy.enums.Direction;


/**
 * 在baseentity中检测是否有排序字段;本注解在{@link com.wy.base.BaseEntity}中使用
 * 
 * @author paradiseWy
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Sort {

	/**
	 * 默认排序字段为实体类字段名,value值代表的字段是驼峰类字段,是数据库字段对应的驼峰字段
	 */
	String value() default "";

	/**
	 * 当value为""时,取实体类字段的名称,实体类是否需要转换为蛇形
	 */
	boolean hump2Snake() default true;
	
	/**
	 * 排序升序或降序,默认升序
	 * @return
	 */
	Direction direction() default Direction.ASC;
}