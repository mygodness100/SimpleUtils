package com.wy.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wy.enums.DateEnum;
import com.wy.excel.enums.ExcelAction;
import com.wy.excel.enums.ExcelColumnType;

/**
 * @apiNote 类上注解暂时不可用
 * @author ParadiseWY
 * @date 2020年4月2日 下午3:08:26
 */
/**
 * excel导入导出,当该注解在实体类上时表示该类字段全部导出;当该注解在字段上,表示该字段导入或导出<br>
 * 
 * @author ParadiseWY
 * @date 2020-11-23 11:20:18
 * @git {@link https://github.com/mygodness100}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Excel {

	/**
	 * 注解在类上时有效,排除某些不导入,也不导出的字段
	 */
	String[] excludes() default {};

	/**
	 * 注解在类上时有效,排除某些不导入的字段
	 */
	String[] excludeImports() default {};

	/**
	 * 注解在类上时有效,排除某些不导出的字段
	 */
	String[] excludeExports() default {};

	/**
	 * 注解在类上时:导出的文件名,需要自带后缀;导入时不检查<br>
	 * 注解在字段上时,表示导出到Excel中的名称
	 */
	String value() default "";

	/**
	 * 当值为空时,字段的默认值
	 */
	String defaultValue() default "";

	/**
	 * 注解在字段上时有效,日期格式,默认为yyyy-MM-dd
	 */
	DateEnum dateFormat() default DateEnum.DATE;

	/**
	 * 读取内容转表达式(如:0=男,1=女,2=未知)
	 */
	String readConverterExp() default "";

	/**
	 * 导出时在excel中每个列的高度 单位为字符
	 */
	double height() default 14;

	/**
	 * 导出时在excel中每个列的宽 单位为字符
	 */
	double width() default 16;

	/**
	 * 文字后缀,如%90变成90%
	 */
	String suffix() default "";

	/**
	 * 提示信息
	 */
	String prompt() default "";

	/**
	 * 设置只能选择不能输入的列内容
	 */
	String[] combo() default {};

	/**
	 * 是否导出数据,应对需求:有时我们需要导出一份模板,此时标题需要但内容需要用户手工填写
	 */
	boolean isExport() default true;

	/**
	 * 注解在字段上时,且该字段有ExcelRelated时有效,指明另一个类中的属性名称
	 */
	String relatedAttribute() default "";

	/**
	 * 字段类型(0数字1字符串)
	 */
	ExcelColumnType excelColumnType() default ExcelColumnType.STRING;

	/**
	 * 字段导入导出类型,0导出导入;1只导出;2只导入;3什么也不做
	 */
	ExcelAction excelAction() default ExcelAction.ALL;
}