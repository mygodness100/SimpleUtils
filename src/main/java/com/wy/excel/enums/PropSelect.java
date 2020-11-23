package com.wy.excel.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 多值选择,规定某个单元格只能选择某些特定值
 * 
 * @author ParadiseWY
 * @date 2020-11-18 16:43:46
 * @git {@link https://github.com/mygodness100}
 */
public interface PropSelect {

	/**
	 * 根据指定参数得到枚举中指定的值,枚举无法通过字节码实例化
	 *
	 * @param args 枚举参数,通过接口.getEnumConstants()方法获得
	 * @param arg 需要进行判断的值
	 * @return 指定的值
	 */
	public static Object[] getMember(PropSelect[] propSelects) {
		if (Objects.isNull(propSelects)) {
			return null;
		}
		List<Object> result = new ArrayList<Object>();
		for (PropSelect propSelect : propSelects) {
			result.add(propSelect.getValue());
		}
		return result.toArray();
	}

	public static Object[] getMember(Enum<?>[] propSelects) {
		if (Objects.isNull(propSelects)) {
			return null;
		}
		List<Object> result = new ArrayList<Object>();
		for (Enum<?> propSelect : propSelects) {
			result.add(propSelect.name());
		}
		return result.toArray();
	}

	/**
	 * 最终返回的值
	 *
	 * @return 返回的值
	 */
	Object getValue();
}