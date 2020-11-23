package com.wy.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wy.excel.enums.ExcelAction;

/**
 * excel导入导出,只能在类上使用,默认情况final和static属性忽略 FIXME 类上注解暂时不可用
 * 
 * @author ParadiseWY
 * @date 2020-11-23 11:20:18
 * @git {@link https://github.com/mygodness100}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Excel {

	/**
	 * 导出的文件名,后缀可带可不带,不带则默认后缀为xls;导入时不检查该方法
	 * 
	 * @return 默认ExcelFile.xls
	 */
	String value() default "ExcelFile.xls";

	/**
	 * 属性导入导出类型
	 * 
	 * @apiNote 当类中属性含有@ExcelColumn,且ExcelAction存在冲突时,属性上的行为优先:<br>
	 *          当类上是ALL而属性为NOTHING时,属性既不导入也不导出<br>
	 *          当类上是IMPORT而属性为EXPORT时,属性不导入<br>
	 *          当类上是EXPORT而属性为IMPORT时,属性不导出<br>
	 *          当类上是NOTHING而属性为非NOTHING时,以属性上的ExcelAction为准
	 * 
	 * @return 导入导出行为
	 */
	ExcelAction excelAction() default ExcelAction.ALL;

	/**
	 * 排除即不导入,也不导出的Java属性
	 * 
	 * @apiNote 当该方法包含的同名属性上存在@ExcelColumn,且ExcelAction存在冲突时,属性上的行为优先:<br>
	 *          当ExcelAction为ExcelAction.ALL时,类上该方法无效<br>
	 *          当ExcelAction为ExcelAction.NOTHING时,效果等同于该方法<br>
	 *          当数据整体是导入而属性为ExcelAction.IMPORT时,类上该方法无效,ExcelAction.EXPORT则有效<br>
	 *          当数据整体是导出而属性为ExcelAction.EXPORT时,类上该方法无效,ExcelAction.IMPORT则有效
	 * 
	 * @apiNote {@link #excludes()}和{@link #excludeImports}取并集<br>
	 *          {@link #excludes()}和{@link #excludeExports}取并集
	 * 
	 * @return 需要排除导入导出时的Java属性数组
	 */
	String[] excludes() default {};

	/**
	 * 排除不导入的Java属性,ExcelAction冲突参考{@link #excludes()}
	 * 
	 * @return 需要排除导入的Java属性数组
	 */
	String[] excludeImports() default {};

	/**
	 * 排除不导出的Java属性,ExcelAction冲突参考{@link #excludes()}
	 * 
	 * @return 需要排除导出的Java属性数组
	 */
	String[] excludeExports() default {};

	/**
	 * 默认忽略final和static属性
	 * 
	 * @return true忽略,false不忽略
	 */
	boolean ignoreFinalStatic() default true;

	/**
	 * 导出时在excel中每个列的高度,单位为字符
	 */
	double height() default 14;

	/**
	 * 导出时在excel中每个列的宽 单位为字符
	 */
	double width() default 16;

	/**
	 * 数据导出时不需要title,只需要数据
	 * 
	 * @return 默认都导出
	 */
	boolean exportNoTitle() default false;

	/**
	 * 只导出excel模板,不导出数据,数据由用户自行填写
	 * 
	 * @return 默认都导出
	 */
	boolean exportTemplate() default false;
}