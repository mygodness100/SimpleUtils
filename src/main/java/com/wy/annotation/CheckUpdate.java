package com.wy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 全量更新时检测非空字段,当有@CheckAdd注解时,忽略本注解;本注解在{@link com.wy.base.BaseEntity}中使用
 * 
 * @author paradiseWy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CheckUpdate {

}