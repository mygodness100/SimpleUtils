package com.wy.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wy.enums.DateEnum;
import com.wy.excel.enums.ExcelColumnType;
import com.wy.excel.enums.ExcelHandle;

/**
 * @apiNote excel导入导出,当该注解在实体类上时表示该类字段全部导出,当该注解在字段上
 * @author ParadiseWY
 * @date 2020年4月2日 下午3:08:26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface Excel {
	/** 导出到Excel中的名字 */
	String name() default "";

	/** 当注解在类上时有效,该方法表示排除某些不导入,也不导出的字段 */
	String[] excludes() default {};

	/** 当注解在类上时有效,该方法表示排除某些不导入的字段 */
	String[] excludeImport() default {};

	/** 当注解在类上时有效,该方法表示排除某些不导出的字段 */
	String[] excludeExport() default {};

	/** 日期格式, 如: yyyy-MM-dd */
	DateEnum dateFormat() default DateEnum.DATE;

	/** 读取内容转表达式(如:0=男,1=女,2=未知) */
	String readConverterExp() default "";

	/** 导出时在excel中每个列的高度 单位为字符 */
	double height() default 14;

	/** 导出时在excel中每个列的宽 单位为字符 */
	double width() default 16;

	/** 文字后缀,如% 90 变成90% */
	String suffix() default "";

	/** 当值为空时,字段的默认值 */
	String defaultValue() default "";

	/** 提示信息 */
	String prompt() default "";

	/** 设置只能选择不能输入的列内容 */
	String[] combo() default {};

	/** 是否导出数据,应对需求:有时我们需要导出一份模板,这是标题需要但内容需要用户手工填写. */
	boolean isExport() default true;

	/** 另一个类中的属性名称,支持多级获取,以小数点隔开 */
	String targetAttr() default "";

	/** 字段类型(0数字 1字符串) */
	ExcelColumnType excelColumnType() default ExcelColumnType.STRING;

	/** 字段导入导出类型,0导出导入;1只导出;2只导入 */
	ExcelHandle excelHandle() default ExcelHandle.ALL;
}