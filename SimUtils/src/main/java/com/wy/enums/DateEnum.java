package com.wy.enums;

/**
 * 时间格式化枚举
 * @author wanyang
 */
public enum DateEnum {
	DATE("yyyy-MM-dd"),DATETIME("yyyy-MM-dd HH:mm:ss");
	
	private String value;
	
	DateEnum(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
