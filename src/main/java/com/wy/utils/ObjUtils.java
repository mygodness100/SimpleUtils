package com.wy.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

public final class ObjUtils {

	private ObjUtils() {
	};

	/**
	 * 判断可变参数是否每个参数都为空,是true,否false
	 */
	public static boolean isNull(Object... args) {
		for (Object arg : args) {
			if (Objects.nonNull(arg)) {
				return false;
			}
		}
		return true;
	}

	public static boolean nonNull(Object... args) {
		for (Object arg : args) {
			if (Objects.isNull(arg)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 将一个对象强转为long类型
	 * @param obj 对象
	 * @return 强转后的long类型值
	 */
	public static Long parseLong(Object obj) {
		if (Objects.isNull(obj)) {
			return null;
		} else {
			return Long.parseLong(obj.toString());
		}
	}

	/**
	 * 对象强转后是否为一个整数
	 * @param obj 对象
	 * @return true是,false不是
	 */
	public static boolean positiveNum(Object obj) {
		return Objects.isNull(parseLong(obj)) ? false : true;
	}

	/**
	 * 简单转换httpservletrequest请求中的参数为hashmap,数组会转成list
	 * @return 转换后的数据
	 */
	public static Map<String, Object> transReq(HttpServletRequest request) {
		Map<String, String[]> parameters = request.getParameterMap();
		if (MapUtils.isBlank(parameters)) {
			return new HashMap<>();
		}
		Map<String, Object> result = new HashMap<>();
		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			if (1 == entry.getValue().length) {
				result.put(entry.getKey(), entry.getValue()[0]);
			} else {
				result.put(entry.getKey(), Arrays.asList(entry.getValue()));
			}
		}
		return result;
	}
}