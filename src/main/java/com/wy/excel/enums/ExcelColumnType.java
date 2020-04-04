package com.wy.excel.enums;

/**
 * @apiNote excel字段类型
 * @author ParadiseWY
 * @date 2020年4月2日 下午3:34:17
 */
public enum ExcelColumnType {
	NUMERIC(0),
	STRING(1);
	private final int value;

	ExcelColumnType(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}