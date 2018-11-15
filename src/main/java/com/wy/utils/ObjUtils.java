package com.wy.utils;

import java.util.Objects;

public final class ObjUtils {
	
	private  ObjUtils() {};
	
	/**
	 * 判断可变参数是否每个参数都为空,是true,否false
	 */
	public static boolean isNull(Object... args) {
		for(Object arg : args) {
			if(Objects.nonNull(arg)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean nonNull(Object... args) {
		for(Object arg : args) {
			if(Objects.isNull(arg)) {
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
}