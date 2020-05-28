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

	/**
	 * 当唯一字段进行更新时,前端需要将更新前的该字段的原始值重新传过来,该方法接收原始值
	 * 1.当该参数有值时,根据反射取该参数在实体类中对应的值,若为null,抛异常
	 * 2.当该值为null时,将在该注解的字段前添加ori并变成驼峰来查找实体类中的值,如username将变成oriUsername查找
	 * 
	 * @return 修改时的原始值的java接收属性名,该属性名必须在实体类中真实存在
	 */
	String oriName() default "";
}