package com.wy.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

public final class ObjUtils {

	private ObjUtils() {
	};

	/**
	 * 判断参数是否为null
	 * 
	 * @param <T> 泛型
	 * @param t 参数
	 * @return 若为null,返回true
	 */
	public static <T> boolean isNull(T t) {
		return Optional.ofNullable(t).isPresent();
	}

	/**
	 * 获得参数值,有值则直接返回,若为null,返回默认值;若默认值为null,抛异常
	 * 
	 * @param <T> 泛型
	 * @param t 需要判断的值
	 * @param defaultValue 默认值
	 * @return 返回的值或异常
	 */
	public static <T> T getNullDefault(T t, T defaultValue) {
		return Optional.ofNullable(t).orElseGet(() -> {
			return Optional.ofNullable(defaultValue).orElseThrow(() -> new NullPointerException());
		});
	}

	/**
	 * 获得参数值,若参数不为null,直接返回;若为null,抛异常
	 * 
	 * @param <T> 泛型
	 * @param t 参数
	 * @return 有值返回,若为null,抛空指针异常
	 */
	public static <T> T getNullException(T t) {
		return Optional.ofNullable(t).orElseThrow(() -> new NullPointerException());
	}

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
	 * 当t为null时返回defaultValue.若t和defaultValue类型不同,返回值类型为object
	 * 
	 * @param <T> 泛型
	 * @param t 需要进行判断的参数
	 * @param defaultValue 默认值.当默认值和t是同类型时,返回值和t是同类型;若不是同类型,返回值是object
	 * @return 结果
	 */
	public static <T> T getNull(T t, T defaultValue) {
		if (!Optional.ofNullable(defaultValue).isPresent()) {
			throw new NullPointerException("defaultValue can't be null");
		}
		return Optional.ofNullable(t).isPresent() ? Optional.ofNullable(t).get() : defaultValue;
	}

	/**
	 * 简单转换httpservletrequest请求中的参数为hashmap,数组会转成list
	 * 
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