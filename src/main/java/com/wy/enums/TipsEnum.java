package com.wy.enums;

import java.text.MessageFormat;

/**
 * 提示或信息类,传入的信息不要带有双引号或单引号
 * @description 信息提示
 * @author paradiseWy
 * @date 2019年4月1日 下午2:33:37
 */
public enum TipsEnum {
	TIP_LOGIN_USERNAME("用户{0}不存在"),
	TIP_PARAM_EMPTY("参数{0}不能为空"),
	TIP_LOG_ERROR("@@@:{0}"),
	TIP_LOG_INFO("###:{0}");

	private String msg;

	private TipsEnum(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return msg;
	}

	public String getMsg(Object... args) {
		return MessageFormat.format(msg, args);
	}
}