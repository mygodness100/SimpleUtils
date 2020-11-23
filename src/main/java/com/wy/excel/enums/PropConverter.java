package com.wy.excel.enums;

import java.util.Objects;

/**
 * ExcelColumn使用,当某个字段有多个可能值时,根据具体的值进行数据转换
 * 
 * @author ParadiseWY
 * @date 2020-11-23 15:46:18
 * @git {@link https://github.com/mygodness100}
 */
public interface PropConverter {

	/**
	 * 根据指定参数得到枚举中指定的值,枚举无法通过字节码实例化
	 * 
	 * @param args 枚举参数,通过接口.getEnumConstants()方法获得
	 * @param arg 需要进行判断的值
	 * @return 指定的值
	 */
	public static Object getMember(PropConverter[] args, Object arg) {
		if (Objects.isNull(args) || Objects.isNull(arg)) {
			return "UNKNOWN";
		}
		for (PropConverter converter : args) {
			if (Objects.equals(converter.toString(), arg.toString())) {
				return converter.getValue();
			}
		}
		return "UNKNOWN";
	}

	/**
	 * 最终返回的值
	 * 
	 * @return 返回的值
	 */
	Object getValue();
}