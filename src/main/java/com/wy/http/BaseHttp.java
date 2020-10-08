package com.wy.http;

import java.net.URI;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wy.common.Constant;
import com.wy.utils.StrUtils;

public interface BaseHttp {

	void sendGet(String urlString);

	void sendGet(URI uri);

	void sendGet(URI uri, Map<String, Object> params);

	void sendPost(String urString);

	void sendPost(URI uri);

	void sendPost(URI uri, Map<String, Object> params);

	/**
	 * 从http请求中获得ip地址,可能不准
	 * 
	 * @param request http请求
	 * @return ip地址
	 */
	static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (StrUtils.isBlank(ip) || Constant.STR_UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StrUtils.isBlank(ip) || Constant.STR_UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StrUtils.isBlank(ip) || Constant.STR_UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (StrUtils.isBlank(ip) || Constant.STR_UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (StrUtils.isBlank(ip) || Constant.STR_UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}