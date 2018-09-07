package com.wy.result;

import java.io.Serializable;

import com.wy.utils.StrUtils;

public class Result implements Serializable {
	private static final long serialVersionUID = 1L;

	private int code;
	private String message;
	private Object data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public static Result resultOk() {
		return resultOk(null);
	}

	public static Result resultErr() {
		return resultErr(null);
	}

	public static Result resultOk(Object t) {
		return resultOk(null, t);
	}

	public static Result resultErr(Object t) {
		return resultErr(null, t);
	}

	public static Result resultOk(String message) {
		return resultOk(message, null);
	}

	public static Result resultErr(String message) {
		return resultErr(message, null);
	}

	public static Result resultOk(String message, Object t) {
		return result(1, message, t);
	}

	public static Result resultErr(String message, Object t) {
		return result(0, message, t);
	}

	public static Result resultOk(int code, String message) {
		return result(code, message, null);
	}

	public static Result resultErr(int code, String message) {
		return result(code, message, null);
	}

	public static Result resultOk(int code, Object t) {
		return result(code, "请求成功", t);
	}

	public static Result resultErr(int code, Object t) {
		return result(code, "请求失败", t);
	}

	public static Result result(boolean flag) {
		return flag ? resultOk() : resultErr();
	}

	public static Result result(Object t) {
		return t == null ? resultErr() : resultOk(t);
	}

	public static Result result(int code, String message, Object t) {
		Result res = new Result();
		res.message = StrUtils.isBlank(message) ? (code > 0 ? "请求成功" : "请求失败") : message;
		res.code = code;
		res.data = t;
		return res;
	}
}