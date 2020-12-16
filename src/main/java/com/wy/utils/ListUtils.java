package com.wy.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ListUtils {

	public static <T> boolean isBlank(Collection<T> list) {
		return list == null || list.isEmpty();
	}

	public static <T> boolean isNotBlank(Collection<T> list) {
		return !isBlank(list);
	}

	/**
	 * 获得指定元素在数组中第一次出现的位置
	 */
	public static int getIndex(String[] src, String des) {
		int length = src.length;
		for (int i = 0; i < length; i++) {
			if (des.equals(src[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 将集合中的数据按照某个key的值进行分类
	 * 
	 * @param datas 数据集
	 * @param column 进行的分类的key
	 * @return 结果集
	 */
	public static Map<Object, List<Map<String, Object>>> varity(List<Map<String, Object>> datas, String column) {
		if (isBlank(datas)) {
			return null;
		}
		Map<Object, List<Map<String, Object>>> res = new HashMap<>();
		List<Map<String, Object>> tempData;
		for (Map<String, Object> data : datas) {
			tempData = res.get(data.get(column));
			if (isBlank(tempData)) {
				tempData = new ArrayList<>();
				res.put(data.get(column), tempData);
			}
			tempData.add(data);
		}
		return res;
	}

	public static String createParams(Collection<String> params) {
		Iterator<String> it = params.iterator();
		Collection<String> result = new ArrayList<>();
		while (it.hasNext()) {
			result.add(it.next() + "=" + it.next());
		}
		return String.join("&", result);
	}

	public static <T> ListBuilder<T> getBuilder() {
		return new ListBuilder<T>();
	}

	public static <T> ListBuilder<T> getBuilder(T val) {
		return new ListBuilder<T>(val);
	}

	public static class ListBuilder<T> {

		private List<T> list = new ArrayList<>();

		public ListBuilder() {}

		public ListBuilder(T val) {
			list.add(val);
		}

		public ListBuilder<T> add(T val) {
			list.add(val);
			return this;
		}

		public List<T> build() {
			return list;
		}
	}
}