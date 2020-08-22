package com.wy.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
				list.add(key + "=" + URLEncoder.encode(Objects.toString(params.get(key)), charset));
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

	public static MapBuilder builder(String key, Object val) {
		return new MapBuilder(key, val);
	}

	public static class MapBuilder {

		private Map<String, Object> map = new HashMap<>();

		public MapBuilder(String key, Object val) {
			map.put(key, val);
		}

		public MapBuilder add(String key, Object val) {
			map.put(key, val);
			return this;
		}

		public MapBuilder put(String key, Object val) {
			map.put(key, val);
			return this;
		}

		public Map<String, Object> build() {
			return map;
		}
	}
}