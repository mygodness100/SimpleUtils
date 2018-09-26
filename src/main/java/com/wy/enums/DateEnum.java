package com.wy.enums;

/**
 * 时间格式化枚举
 * @author wanyang
 */
public enum DateEnum {
	DATE("yyyy-MM-dd"),
	DATE_SLASH("yyyy/MM/dd"),
	DATE_NONE("yyyyMMdd"),

	DATEWEEK("yyyy-MM-dd EEE"),
	DATEWEEK_SLASH("yyyy/MM/dd EEE"),
	DATEWEEK_NONE("yyyyMMdd EEE"),

	DATETIME("yyyy-MM-dd HH:mm:ss"),
	DATETIME_SLASH("yyyy/MM/dd HH:mm:ss"),
	DATETIME_NONE("yyyyMMdd HH:mm:ss"),
	
	DATETIMEWEEK("yyyy-MM-dd HH:mm:ss EEE"),
	DATETIMEWEEK_SLASH("yyyy/MM/dd HH:mm:ss EEE"),
	DATETIMEWEEK_NONE("yyyyMMdd HH:mm:ss EEE"),

	DATEMILSEC("yyyy-MM-dd HH:mm:ss.SSS"),
	DATEMILSEC_SLASH("yyyy/MM/dd HH:mm:ss.SSS"),
	DATEMILSEC_NONE("yyyyMMddHHmmssSSS"),
	
	DATEMILSECWEEK("yyyy-MM-dd HH:mm:ss.SSS EEE"),
	DATEMILSECWEEK_SLASH("yyyy/MM/dd HH:mm:ss.SSS EEE"),
	DATEMILSECWEEK_NONE("yyyyMMdd HHmmssSSS EEE"),

	TIME("HH:mm:ss"),
	TIMEWEEK("HH:mm:ss EEE"),
	
	WEEK("EEE");

	public String format;

	DateEnum(String format) {
		this.format = format;
	}
}