package com.wy.result;

import com.wy.enums.TipEnum;

public class ResultException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private int code;

	public int getCode() {
		return code;
	}

	public ResultException() {
		this(TipEnum.TIP_EX_COMMON);
	}

	public ResultException(TipEnum tipEnum) {
		this(tipEnum.getCode(), tipEnum.getMsg(), null);
	}

	public ResultException(CharSequence message) {
		this(0, message);
	}

	public ResultException(int code, CharSequence message) {
		this(code, message, null);
	}

	public ResultException(int code, Throwable ex) {
		this(code, ex.getMessage(), ex);
	}

	public ResultException(CharSequence message, Throwable ex) {
		this(0, message, ex);
	}

	public ResultException(int code, CharSequence message, Throwable ex) {
		super(message.toString(), ex);
		this.code = code;
	}
}