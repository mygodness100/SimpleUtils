package com.wy.result;

import com.wy.enums.TipEnum;

public class ResultException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private int code;

	public int getCode() {
		return code;
	}

	public ResultException() {
		this(TipEnum.TIP_EX_COMMON.getErrCode(), TipEnum.TIP_EX_COMMON.getErrMsg(), null);
	}

	public ResultException(String message) {
		this(0, message);
	}

	public ResultException(int code, String message) {
		this(code, message, null);
	}

	public ResultException(int code,Throwable ex) {
		this(code, ex.getMessage(), ex);
	}

	public ResultException(String message, Throwable ex) {
		this(0, message, ex);
	}

	public ResultException(int code, String message, Throwable ex) {
		super(message, ex);
		this.code = code;
	}
}