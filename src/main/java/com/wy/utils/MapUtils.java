package com.wy.utils;

import java.util.Map;

public class MapUtils {
	public static <T, K> boolean isBlank(Map<T,K> map) {
		return map == null || map.isEmpty();
	}
	public static <T, K> boolean isNotBlank(Map<T,K> map) {
		return !isBlank(map);
	}
}
