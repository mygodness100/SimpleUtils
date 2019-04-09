package com.wy.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapUtils {
	public static <T, K> boolean isBlank(Map<T, K> map) {
		return map == null || map.isEmpty();
	}

	public static <T, K> boolean isNotBlank(Map<T, K> map) {
		return !isBlank(map);
	}

	public static String createParams(Map<String, Object> params, String charset) {
		List<String> list = new ArrayList<>();
		for (String key : params.keySet()) {
			try {
				list.add(URLEncoder.encode(key + "=" + params.get(key),
						StandardCharsets.UTF_8.displayName()));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return String.join("&", list);
	}

	public static String createParams(String url, Map<String, Object> params) {
		return createParams(url, params, StandardCharsets.UTF_8.displayName());
	}

	public static String createParams(String url, Map<String, Object> params, String charset) {
		if (isBlank(params)) {
			return url;
		}
		return MessageFormat.format("{0}?{1}", url, createParams(params, charset));
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