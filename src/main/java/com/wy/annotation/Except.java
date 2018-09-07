package com.wy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.FIELD,ElementType.METHOD})
public @interface Except {
	//有此注解标识该方法或字段在某个事件中除外,比如说写入某个excel文件时不写入字段
}
