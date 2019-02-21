package com.wy.enums;

import java.text.MessageFormat;

public enum TipsEnum {
	TIP_LOGIN_USERNAME("用户{0}不存在"),

	TIP_PARAM_EMPTY("参数{0}不能为空");

	private String msg;

	private TipsEnum(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return msg;
	}

	public String getTips(Object... args) {
		return MessageFormat.format(msg, args);
	}
}