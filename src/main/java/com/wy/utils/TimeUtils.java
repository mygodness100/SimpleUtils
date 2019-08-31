package com.wy.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @apiNote 基于jdk1.8的时间类工具类,和DateUtils差不多
 * @author ParadiseWY
 * @date 2019年3月20日 下午4:56:06
 */
public class TimeUtils {
	private static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern(DateUtils.DEFAULT_PATTERN);

	public static void main(String[] args) {
		System.out.println(format(LocalDateTime.now()));
		System.out.println(parse("2013-11-11T13:12:12"));
	}

	/**
	 * FIXME
	 * 
	 * @param temporal
	 * @return
	 */
	public static String format(LocalDateTime localDateTime) {
		return formatter.format(localDateTime);
	}

	public static LocalDateTime parse(String date) {
		// TemporalAccessor parse = formatter.parse(date);
		formatter.parse(date, t -> {
			System.out.println(t);
			return LocalDateTime.now();
		});
		return LocalDateTime.parse(date);
	}
}