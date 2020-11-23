package com.wy.excel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wy.enums.DateEnum;
import com.wy.excel.enums.ExcelAction;
import com.wy.excel.enums.PropConverter;

/**
 * 实体类导入导出时单个字段的行为,该注解的优先级高于@Excel
 * 
 * @author ParadiseWY
 * @date 2020-11-18 16:22:31
 * @git {@link https://github.com/mygodness100}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelColumn {

	/**
	 * 导出时显示在excel文件中的字段名,默认为Java属性名
	 * 
	 * @return Java属性在导出时的名称,导入时不检查该方法
	 */
	String value() default "";

	/**
	 * 导入导出时的行为,默认导入,导出
	 * 
	 * @return 属性操作行为
	 */
	ExcelAction excelAction() default ExcelAction.ALL;

	/**
	 * 当某个字段有多个值时,需要继承某个PropConverter接口,在导出时进行转换
	 * 
	 * @return 用来进行转换的字节码文件
	 */
	Class<? extends PropConverter> propConverter() default PropConverter.class;

	/**
	 * 代替propConverter FIXME
	 * 
	 * @return 用来进行转换的字节码文件
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends Enum> enumConverter() default Enum.class;

	/**
	 * 日期格式,默认为yyyy-MM-dd
	 * 
	 * @return 日期的默认导入导出格式, FIXME 未使用
	 */
	DateEnum dateFormat() default DateEnum.DATE;

	/**
	 * 单元格提示信息
	 * 
	 * @return 默认没有提示信息, FIXME 未使用
	 */
	String prompt() default "";

	/**
	 * 当值为空时,字段的默认值
	 * 
	 * @return 只能返回String类型 FIXME 未使用
	 */
	String defaultValue() default "";

	/**
	 * 文字后缀,如%90变成90%
	 * 
	 * @return 文件后缀 FIXME 未使用
	 */
	String suffix() default "";

	/**
	 * 设置只能选择不能输入的列内容
	 * 
	 * @return 可选值字符串数组 FIXME 未使用
	 */
	String[] options() default {};

	/**
	 * 该方法只能用在其他实体类字段上,指明另一个类中的需要导入导出的同名属性
	 * 
	 * @return 关联实体类中需要导入导出的值
	 */
	ExcelRelated[] excelRelated() default {};
}