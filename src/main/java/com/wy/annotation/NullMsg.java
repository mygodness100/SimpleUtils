package com.wy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 只检查非空并且给出提示信息
 * @author wanyang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE})
public @interface NullMsg {
	//是否检查非空,默认检查
	boolean check() default true;
	//检查后的提示信息
	String msg() default "字段不能为空";
}
