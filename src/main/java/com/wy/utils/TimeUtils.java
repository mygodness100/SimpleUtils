package com.wy.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @description 基于jdk1.8的时间类工具类,和DateUtils差不多
 * @author ParadiseWY
 * @date 2019年3月20日 下午4:56:06
 * @git {@link https://github.com/mygodness100}
 */
public class TimeUtils {
	private static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern(DateUtils.DEFAULT_PATTERN);

	/**
	 * FIXME
	 * @param temporal
	 * @return
	 */
	public static String format(LocalDateTime temporal) {
		return formatter.format(temporal);
	}
}