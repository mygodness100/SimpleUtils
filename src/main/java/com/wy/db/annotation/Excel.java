package com.wy.db.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wy.excel.enums.ExcelAction;

/**
 * excel导入导出时使用,类上注解:若存在该注解,整个实体类中所有的字段都导出
 * 
 * 只能对基本类型和String类型进行操作,final,static修饰的字段不操作
 * 
 * @author ParadiseWY
 * @date 2020-11-18 16:12:48
 * @git {@link https://github.com/mygodness100}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Excel {

	/** 导出时显示的excel文件名,不带文件名后缀 */
	String value() default "数据导出";

	/** 导入导出时的行为,默认导入,导出 */
	ExcelAction excelAction() default ExcelAction.ALL;

	/**
	 * 导入时需要排除的Java属性名<br>
	 * 当同名字段上存在ExcelColumn,且ExcelType为ExcelAction.ALL或ExcelAction.IMPORT时,该属性无效
	 */
	String[] importExcludes() default {};

	/**
	 * 导出时需要排除的Java属性名<br>
	 * 当同名字段上存在ExcelColumn,且ExcelType为ExcelAction.ALL或ExcelAction.EXPORT时,该属性无效
	 */
	String[] exportExcludes() default {};
}