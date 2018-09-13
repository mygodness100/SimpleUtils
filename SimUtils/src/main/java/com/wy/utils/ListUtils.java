package com.wy.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ListUtils {

	public static <T> boolean isEmpty(Collection<T> list) {
		return list == null || list.isEmpty();
	}

	public static <T> boolean isNotEmpty(Collection<T> list) {
		return !isEmpty(list);
	}

	/**
	 * 将Integer的list集合转换成int集合
	 */
	public static int[] toIntArr(List<Integer> list) {
		if (isNotEmpty(list)) {
			if (System.getProperty("java.version").contains("1.8.")) {
				return Arrays.stream(list.toArray(new Integer[list.size()])).mapToInt(Integer::valueOf).toArray();
			} else {
				int[] arr = new int[list.size()];
				for (int i = 0; i < list.size(); i++) {
					arr[i] = list.get(i);
				}
				return arr;
			}
		}
		return null;
	}

	/**
	 * 将Integer的list集合转换成int集合
	 */
	public static long[] toLongArr(List<Long> list) {
		if (isNotEmpty(list)) {
			if (System.getProperty("java.version").contains("1.8.")) {
				return Arrays.stream(list.toArray(new Long[list.size()])).mapToLong(Long::valueOf).toArray();
			} else {
				long[] arr = new long[list.size()];
				for (int i = 0; i < list.size(); i++) {
					arr[i] = list.get(i);
				}
				return arr;
			}
		}
		return null;
	}

	/**
	 * 将Integer的list集合转换成int集合
	 */
	public static double[] toDoubleArr(List<Double> list) {
		if (isNotEmpty(list)) {
			if (System.getProperty("java.version").contains("1.8.")) {
				return Arrays.stream(list.toArray(new Double[list.size()])).mapToDouble(Double::valueOf).toArray();
			} else {
				double[] arr = new double[list.size()];
				for (int i = 0; i < list.size(); i++) {
					arr[i] = list.get(i);
				}
				return arr;
			}
		}
		return null;
	}

	/**
	 * 将boolean的对象类转换为boolean基础数据集合
	 */
	public static boolean[] toBooleanArr(List<Boolean> list) {
		if (isNotEmpty(list)) {
			boolean[] arr = new boolean[list.size()];
			for (int i = 0; i < list.size(); i++) {
				arr[i] = list.get(i);
			}
			return arr;
		}
		return null;
	}
	
	/**
	 * 获得指定元素在数组中第一次出现的位置
	 */
	public static int getIndex(String[] src,String des) {
		int length = src.length;
		for(int i=0;i<length;i++) {
			if(des.equals(src[i])) {
				return i;
			}
		}
		return -1;
	}
}