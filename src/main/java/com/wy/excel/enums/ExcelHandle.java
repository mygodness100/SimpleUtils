package com.wy.excel.enums;

/**
 * @apiNote excel操作类型
 * @author ParadiseWY
 * @date 2020年4月2日 下午3:27:01
 */
public enum ExcelHandle {
	/** 导入导出 */
	ALL(0),
	/** 导出 */
	EXPORT(1),
	/** 导入 */
	IMPORT(2);

	private final int value;

	ExcelHandle(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}