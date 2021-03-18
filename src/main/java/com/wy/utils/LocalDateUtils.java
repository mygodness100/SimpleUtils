package com.wy.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

import com.wy.enums.DateEnum;

/**
 * @apiNote 基于jdk1.8的时间类工具类,和DateUtils差不多
 * @author ParadiseWY
 * @date 2019年3月20日 下午4:56:06
 */
public class LocalDateUtils {

	/**
	 * 默认格式化年月日时分秒字符串yyyy-MM-dd HH:mm:ss,系统自带的默认格式化字符串中间带T
	 */
	private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter
			.ofPattern(DateEnum.DATETIME.getPattern());

	/**
	 * 定时时间格式化字符串:yyyy-MM-dd HH:mm:ss,等同于DEFAULT_FORMATTER
	 */
	public static final DateTimeFormatter FORMATTER_DATETIME = new DateTimeFormatterBuilder()
			.appendValue(ChronoField.YEAR).appendLiteral("-").appendValue(ChronoField.MONTH_OF_YEAR).appendLiteral("-")
			.appendValue(ChronoField.DAY_OF_MONTH).appendLiteral(" ").appendValue(ChronoField.HOUR_OF_DAY)
			.appendLiteral(":").appendValue(ChronoField.MINUTE_OF_HOUR).appendLiteral(":")
			.appendValue(ChronoField.SECOND_OF_MINUTE).toFormatter();

	/**
	 * 默认格式化时间字符串HH:mm:ss,系统自带的默认字符串带毫秒
	 */
	private static final DateTimeFormatter DE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DateEnum.TIME.getPattern());

	public static void main(String[] args) {
		System.out.println(DEFAULT_FORMATTER.parse("2018-12-12 12:12:12", LocalDateTime::from));
		System.out.println(DEFAULT_FORMATTER.format(LocalDateTime.now()));
		System.out.println(format(LocalDateTime.now(), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
	}

	private LocalDateUtils() {}

	private static Calendar getCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * 将LocalDateTime类型转换为Date类型
	 * 
	 * @param localDateTime 需要进行转换的时间
	 * @return 转换后的时间
	 */
	public static Date local2Date(LocalDateTime localDateTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(localDateTime.get(ChronoField.YEAR_OF_ERA), localDateTime.getMonthValue() - 1,
				localDateTime.getDayOfMonth(), localDateTime.getHour(), localDateTime.getMinute(),
				localDateTime.getSecond());
		calendar.set(Calendar.MILLISECOND, localDateTime.getNano() / 100_0000);
		return calendar.getTime();
	}

	/**
	 * @apiNote 将Date类型转换为Date类型
	 * @param date 需要进行转换的时间
	 * @return 转换后的时间
	 */
	public static LocalDateTime date2Local(Date date) {
		Calendar calendar = getCalendar(date);
		return LocalDateTime.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND) * 100_0000);
	}

	/**
	 * 获得当前时间的yyyy-MM-dd HH:mm:ss格式字符串
	 * 
	 * @return 时间字符串
	 */
	public static String formatDateTime() {
		return formatDateTime(LocalDateTime.now());
	}

	/**
	 * 将LocalDateTime转换为yyyy-MM-dd HH:mm:ss格式
	 * 
	 * @param localDateTime 需要转换的时间
	 * @return 时间字符串
	 */
	public static String formatDateTime(LocalDateTime localDateTime) {
		return formatDateTime(localDateTime, DateEnum.DATETIME.getPattern());
	}

	/**
	 * 将LocalDateTime转换为指定格式
	 * 
	 * @param localDateTime 需要转换的时间
	 * @param pattern 需要转换的格式
	 * @return 时间字符串
	 */
	public static String formatDateTime(LocalDateTime localDateTime, String pattern) {
		return format(localDateTime, pattern);
	}

	/**
	 * 将Date转换为yyyy-MM-dd HH:mm:ss格式
	 * 
	 * @param date 需要转换的时间
	 * @return 时间字符串
	 */
	public static String formatDateTime(Date date) {
		return formatDateTime(date2Local(date));
	}

	/**
	 * 将LocalDateTime转换为yyyy-MM-dd格式
	 * 
	 * @param localDateTime 需要进行转换的时间
	 * @return 时间字符串
	 */
	public static String formatDate(LocalDateTime localDateTime) {
		return DateTimeFormatter.ISO_DATE.format(localDateTime);
	}

	/**
	 * @apiNote 将LocalDate转换为yyyy-MM-dd格式
	 * @param localDate 需要进行转换的时间
	 * @return 时间字符串
	 */
	public static String formatDate(LocalDate localDate) {
		return DateTimeFormatter.ISO_DATE.format(localDate);
	}

	/**
	 * @apiNote 将LocalDateTime转换为HH:mm:ss格式
	 * @param localDateTime 需要进行转换的时间
	 * @return 时间字符串
	 */
	public static String formatTime(LocalDateTime localDateTime) {
		return DE_TIME_FORMATTER.format(localDateTime);
	}

	/**
	 * @apiNote 将LocalTime转换为HH:mm:ss格式
	 * @param localTime 需要进行转换的时间
	 * @return 时间字符串
	 */
	public static String formatTime(LocalTime localTime) {
		return DE_TIME_FORMATTER.format(localTime);
	}

	/**
	 * 将Date转换成指定格式字符串
	 * 
	 * @param date 时间
	 * @param pattern 格式化样式
	 * @return 格式化后字符串
	 */
	public static String format(Date date, String pattern) {
		return format(date, DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * 将Date转换成指定格式字符串
	 * 
	 * @param date 时间
	 * @param dateTimeFormatter 格式化样式
	 * @return 格式化后字符串
	 */
	public static String format(Date date, DateTimeFormatter dateTimeFormatter) {
		return format(date2Local(date), dateTimeFormatter);
	}

	/**
	 * 将LocalDateTime转换成指定格式字符串
	 * 
	 * @param localDateTime 时间
	 * @param pattern 格式化样式
	 * @return 格式化后字符串
	 */
	public static String format(LocalDateTime localDateTime, String pattern) {
		return format(localDateTime, DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * 将LocalDateTime转换成指定格式字符串
	 * 
	 * @param localDateTime 时间
	 * @param dateTimeFormatter 格式化样式
	 * @return 格式化后字符串
	 */
	public static String format(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
		return dateTimeFormatter.format(localDateTime);
	}

	/**
	 * 将时间格式为yyyy-MM-dd HH:mm:ss的字符串转为Date类型
	 * 
	 * @param dateTime 时间字符串
	 * @return date对象
	 */
	public static Date parseDateTime(String dateTime) {
		return local2Date(parse(dateTime));
	}

	/**
	 * 将格式为yyyy-MM-dd的时间字符串转为Date类型,时分秒都为0
	 * 
	 * @param date 时间字符串
	 * @return date对象
	 */
	public static Date parseDate(String date) {
		return local2Date(parse(date + " " + "00:00:00"));
	}

	/**
	 * LocalDateTime无论如何进行格式化都会变成yyyy-MM-ddTHH:mm:ss形式,中间的-不会变
	 * 
	 * @param localDateTime 需要进行格式化的字符串时间
	 * @return 格式化后时间
	 */
	public static LocalDateTime parse(String localDateTime) {
		if (StrUtils.contains(localDateTime, "T")) {
			return LocalDateTime.parse(localDateTime.toUpperCase());
		} else {
			return LocalDateTime.parse(localDateTime, DEFAULT_FORMATTER);
		}
	}

	/**
	 * 获得当前时间所在月份的第一天时间
	 * 
	 * @return 当前时间所在月第一天时间的时间
	 */
	public static LocalDateTime getFirstDayOfMonth() {
		// 得到上一个周六
		TemporalAdjusters.previous(DayOfWeek.SATURDAY);
		// 得到本月最后一个
		TemporalAdjusters.lastInMonth(DayOfWeek.FRIDAY);
		return LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth());
	}

	/**
	 * 获得指定时间所在月份的第一天时间
	 * 
	 * @return 指定时间所在月第一天时间的时间
	 */
	public static LocalDateTime getFirstDayOfMonth(LocalDateTime localDateTime) {
		return localDateTime.with(TemporalAdjusters.firstDayOfMonth());
	}

	/**
	 * 时间比较
	 * 
	 * @param temporalAccessor 时间
	 * @return 是否符合条件,true符合,false不符合
	 */
	public static boolean familyBirthday(TemporalAccessor temporalAccessor) {
		int month = temporalAccessor.get(ChronoField.MONTH_OF_YEAR);
		int day = temporalAccessor.get(ChronoField.DAY_OF_MONTH);
		if (month == Month.FEBRUARY.getValue() && day == 21) {
			return true;
		}
		return false;
	}
}