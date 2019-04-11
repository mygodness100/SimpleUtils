package com.wy.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wy.enums.TipEnum;

/**
 * 当新增数据时检测非空字段;本注解在{@link com.wy.base.AbsBaseModel}中使用
 * 
 * @author paradiseWy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface CheckAdd {
	int code() default 0;

	String value() default "参数不能为空";

	TipEnum error() default TipEnum.UNKNOWN;
}