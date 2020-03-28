package com.wy.enums;

import java.text.MessageFormat;

/**
 * @apiNote 日志格式化
 * @author ParadiseWY
 * @date 2020年3月23日 下午4:04:06
 */
public enum LogEnum {
	ERROR("###::{0}::###"),
	INFO("@@@::{0}::@@@");

	private String format;

	LogEnum(String format) {
		this.format = format;
	}

	private String format() {
		return format;
	}

	public String getMsg(String msg) {
		return MessageFormat.format(format(), msg);
	}
}