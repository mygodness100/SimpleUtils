package com.wy.excel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * excel中关联的其他实体类
 * 
 * @author ParadiseWY
 * @date 2020年4月29日 下午1:25:02
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelRelated {

	/**
	 * 关联的类中需要输出到excel中的字段名,需要和java字段名相同
	 * 
	 * @return 该字节码中需要进行关联的字段
	 */
	Excel[] value();

	/**
	 * 是否检查关联类中的字段仍然关联了其他实体类,默认不检查
	 * 
	 * @return 不检查
	 */
	boolean checkNext() default false;
}