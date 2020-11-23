package com.wy.excel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * excel中关联的其他实体类,只能用在实体类上 FIXME 未使用
 * 
 * @author ParadiseWY
 * @date 2020年4月29日 下午1:25:02
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelRelated {

	/**
	 * 当前关联类中是否要检查类上注解@Excel和属性注解@ExcelColumn,默认不检查;当检查时,relatedAttrs()无效
	 * 
	 * @return true检查, false不检查
	 */
	boolean value() default false;

	/**
	 * 当前关联类中需要输出到excel中的字段名,需要和java字段名相同
	 * 
	 * @return 需要进行关联的属性名数组
	 */
	String[] relatedAttrs() default {};

	/**
	 * 当前关联类中是否检查关联了其他实体类,默认不检查
	 * 
	 * @return 不检查
	 */
	boolean checkNext() default false;
}