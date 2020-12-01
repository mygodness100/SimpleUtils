package com.wy.excel.enums;

/**
 * excel导入导出时字段的行为,顺序不可修改
 * 
 * @author ParadiseWY
 * @date 2020-11-18 16:18:26
 * @git {@link https://github.com/mygodness100}
 */
public enum ExcelAction {

	/** 既不导入也不导出 */
	NOTHING,
	/** 只导入 */
	IMPORT,
	/** 只导出 */
	EXPORT,
	/** 导入和导出 */
	ALL;
}