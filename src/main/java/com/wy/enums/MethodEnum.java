package com.wy.enums;

/**
 * 请求方式枚举类
 * @author wanyang
 */
public enum MethodEnum {
	GET("GET"),POST("POST"),PUT("PUT"),OPTIONS("OPTIONS"),DELETE("DELETE"),
	get("GET"),post("POST"),put("PUT"),options("OPTIONS"),delete("DELETE");
	
	private String method;
	
	MethodEnum(String method) {
		this.method = method;
	}
	
	@Override
	public String toString() {
		return method;
	}
}