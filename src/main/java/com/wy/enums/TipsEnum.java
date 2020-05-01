package com.wy.enums;

import java.text.MessageFormat;

import com.wy.common.TipCode;

/**
 * 提示或信息类,传入的信息不要带有双引号或单引号
 * 
 * @description 信息提示
 * @author paradiseWy
 * @date 2019年4月1日 下午2:33:37
 */
public enum TipsEnum implements TipCode {

	TIP_LOGIN_USERNAME(0, "用户{0}不存在"),
	TIP_PARAM_EMPTY(0, "参数{0}不能为空"),
	TIP_LOG_ERROR(0, "@@@:{0}"),
	TIP_LOG_INFO(0, "###:{0}");

	private int code;

	private String msg;

	private TipsEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	@Override
	public String toString() {
		return msg;
	}

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public String getMsg() {
		return getMsg("");
	}

	public String getMsg(String msg) {
		return MessageFormat.format(this.msg, msg);
	}
}