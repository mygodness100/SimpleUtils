package com.wy.utils;

import java.util.Objects;

public final class ComUtils {
	
	private  ComUtils() {};
	
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
}