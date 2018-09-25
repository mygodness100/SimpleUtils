package com.wy.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapUtils {
	public static <T, K> boolean isBlank(Map<T, K> map) {
		return map == null || map.isEmpty();
	}

	public static <T, K> boolean isNotBlank(Map<T, K> map) {
		return !isBlank(map);
	}

	public static MapBuilder getBuilder(String key, Object val) {
		return new MapBuilder(key, val);
	}

	public static class MapBuilder {
		private Map<String, Object> map = new ConcurrentHashMap<>();

		public MapBuilder(String key, Object val) {
			map.put(key, val);
		}

		public MapBuilder add(String key, Object val) {
			map.put(key, val);
			return this;
		}

		public Map<String, Object> build() {
			return map;
		}
	}
}