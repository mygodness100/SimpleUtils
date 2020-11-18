package com.wy.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wy.enums.ExcelAction;
import com.wy.enums.Selects;

/**
 * 实体类导入导出时单个字段的行为,该注解的优先级高于Excel
 * 
 * @author ParadiseWY
 * @date 2020-11-18 16:22:31
 * @git {@link https://github.com/mygodness100}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelColumn {

	/** 导出时显示在excel文件中的字段名,默认为Java属性名 */
	String value() default "";

	/** 导入导出时的行为,默认导入,导出 */
	ExcelAction excelType() default ExcelAction.ALL;

	/** 当某个字段有多个值时,需要继承某个Selects接口,在导出时进行转换 */
	Class<? extends Selects> select() default Selects.class;
}