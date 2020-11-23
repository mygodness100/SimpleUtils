package com.wy.db.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * model实体类中是否为通用字段 FIXME 利用generator生成时,通用字段若忽略,那么在xml中也会忽略,需要修改
 * 
 * @author ParadiseWY
 * @date 2020-04-01 10:56:59
 * @git {@link https://github.com/mygodness100}
 */
@Documented
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseModelField {

}