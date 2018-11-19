package com.wy.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 外键,foreignClass外键约束对应的实体类,foreignKey为java字段名
 * @author 万杨
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Foreign {
	Class<?> foreignClass();

	String foreignKey();
}