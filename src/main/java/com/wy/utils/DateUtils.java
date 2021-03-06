package com.wy.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.wy.enums.DateEnum;
import com.wy.enums.RegexEnum;

/**
 * calender的set方法和add方法都可以重新设定时间,但是set方法设置时间后会重新计算
 * 
 * @apiNote 可查看commons-lang的DateUtils以及LocalDateUtils
 * @author paradiseWy
 */
public final class DateUtils {

	public static final long ONE_DAY = 1000 * 60 * 60 * 24;

	public static final String DEFAULT_PATTERN = DateEnum.DATETIME.getPattern();

	private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>() {

		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DEFAULT_PATTERN);
		}
	};

	private DateUtils() {}

	/**
	 * 返回指定时间是周几
	 */
	public static String getWeek(Date date) {
		return format(date, DateEnum.WEEK);
	}

	public static String getWeek(long date) {
		return format(new Date(date), DateEnum.WEEK);
	}

	/**
	 * 返回当前时间年月日字符串
	 */
	public static String formatDate() {
		return format(new Date(), DateEnum.DATE);
	}

	/**
	 * 返回指定时间年月日字符串
	 */
	public static String formatDate(long date) {
		return format(new Date(date), DateEnum.DATE);
	}

	/**
	 * 返回指定时间年月日字符串
	 */
	public static String formatDate(Date date) {
		return format(date, DateEnum.DATE);
	}

	/**
	 * 返回当前时间的时分秒
	 */
	public static String formatTime() {
		return format(new Date(), DateEnum.TIME);
	}

	/**
	 * 返回指定时间时分秒字符串
	 */
	public static String formatTime(long date) {
		return format(date, DateEnum.TIME);
	}

	/**
	 * 返回指定时间时分秒字符串
	 */
	public static String formatTime(Date date) {
		return format(date, DateEnum.TIME);
	}

	/**
	 * 返回当前时间的年月日时分秒字符串
	 */
	public static String formatDateTime() {
		return format(new Date(), DEFAULT_PATTERN);
	}

	/**
	 * 返回指定时间转换为年月日时分秒字符串
	 */
	public static String formatDateTime(long date) {
		return format(date, DEFAULT_PATTERN);
	}

	/**
	 * 返回指定时间转换为年月日时分秒字符串
	 */
	public static String formatDateTime(Date date) {
		return format(date, DEFAULT_PATTERN);
	}

	/**
	 * 返回指定时间转换为年月日时分秒字符串
	 */
	public static String format(long date, DateEnum pattern) {
		return format(date, pattern.getPattern());
	}

	/**
	 * 返回指定时间转换为年月日时分秒字符串
	 */
	public static String format(Date date, DateEnum pattern) {
		return format(date, pattern.getPattern());
	}

	/**
	 * 返回指定时间,指定格式的自定义年月日时分秒字符串
	 */
	public static String format(long date, String pattern) {
		date = date < 0 ? 0 : date;
		return format(new Date(date), pattern);
	}

	/**
	 * 返回指定时间,指定格式的自定义年月日时分秒字符串
	 */
	public static String format(Date date, String pattern) {
		date = date == null ? new Date() : date;
		if (StrUtils.isBlank(pattern)) {
			return threadLocal.get().format(date);
		}
		return get(pattern).format(date);
	}

	private static SimpleDateFormat get(String pattern) {
		SimpleDateFormat sdf = threadLocal.get();
		sdf.applyPattern(pattern);
		return sdf;
	}

	/**
	 * 根据时间格式将时间字符串转换为对应格式,年月日的分隔符只支持/,-和无,若有时分秒,必须是:
	 * 
	 * @param date 需要被转换的时间
	 * @return 转换后的时间
	 */
	public static Date parse(String date) {
		if (StrUtils.checkValidity(date, RegexEnum.REGEX_DATE)) {
			return parse(date, DateEnum.DATE);
		}
		if (StrUtils.checkValidity(date, RegexEnum.REGEX_DATE_NONE)) {
			return parse(date, DateEnum.DATE_NONE);
		}
		if (StrUtils.checkValidity(date, RegexEnum.REGEX_DATE_SLASH)) {
			return parse(date, DateEnum.DATE_SLASH);
		}
		if (StrUtils.checkValidity(date, RegexEnum.REGEX_DATETIME)) {
			return parse(date, DateEnum.DATETIME);
		}
		if (StrUtils.checkValidity(date, RegexEnum.REGEX_DATETIME_NONE)) {
			return parse(date, DateEnum.DATETIME_NONE);
		}
		if (StrUtils.checkValidity(date, RegexEnum.REGEX_DATETIME_SLASH)) {
			return parse(date, DateEnum.DATETIME_SLASH);
		}
		return parse(date, DEFAULT_PATTERN);
	}

	/**
	 * 将时间字符串转换为指定格式的date
	 */
	public static Date parse(String date, DateEnum pattern) {
		return parse(date, pattern.getPattern());
	}

	/**
	 * 将时间字符串转换为指定格式的date
	 */
	public static Date parse(String date, String pattern) {
		try {
			if (StrUtils.isAnyBlank(pattern, date)) {
				return null;
			}
			return get(pattern).parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 当前时间的指定时间位的计算
	 * 
	 * @param field Calendar的日历字段,Calender.MONTH,Calender.DAY等
	 */
	public static final Date dateAdd(int field, int amount) {
		return dateAdd(new Date(), field, amount);
	}

	/**
	 * 指定时间的指定时间位的运算
	 */
	public static final Date dateAdd(Date date, int field, int amount) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(field, amount);
		return c.getTime();
	}

	/**
	 * 返回当前时间指定时间之后的字符串值
	 */
	public static final String strAdd(int field, int amount) {
		return formatDateTime(dateAdd(field, amount));
	}

	/**
	 * 给指定的时间字段设置指定的值
	 * 
	 * @param field Calendar的日历字段,Calender.MONTH,Calender.DAY等
	 */
	public static Date dateCustom(Date date, int field, int value) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(field, value);
		return c.getTime();
	}

	/**
	 * 设置一个指定年月日,当前系统时间时分秒的时间
	 */
	public static Date dateCustom(int year, int month, int day) {
		if (month < 1) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, day);
		return c.getTime();
	}

	/**
	 * 设置一个指定年月日时分的时间
	 */
	public static Date dateCustom(int year, int month, int date, int hourOfDay, int minute) {
		return dateCustom(year, month, date, hourOfDay, minute, 0);
	}

	/**
	 * 设置一个指定年月日时分秒的时间
	 */
	public static Date dateCustom(int year, int month, int date, int hourOfDay, int minute, int second) {
		if (month < 1) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, date, hourOfDay, minute, second);
		return c.getTime();
	}

	/**
	 * 判断是否为闰年
	 */
	public static boolean isLeapYear(int yyyy) {
		if ((yyyy % 4 == 0 && yyyy % 100 != 0) || yyyy % 400 == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 获得当前时间当日开始时间
	 */
	public static long getDayBegin() {
		return getDayBegin(new Date());
	}

	/**
	 * 获得某个时间当日开始时间
	 */
	public static long getDayBegin(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}

	/**
	 * 获得当前时间当日开始时间
	 */
	public static long getDayEnd() {
		return getDayEnd(new Date());
	}

	/**
	 * 获得某个时间当日开始时间
	 */
	public static long getDayEnd(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTimeInMillis();
	}

	/**
	 * 获得当前时间的昨天开始时间
	 */
	public static Date getYesterdayBegin() {
		return getYesterdayBegin(new Date());
	}

	/**
	 * 获取某个时间昨天的开始时间
	 */
	public static Date getYesterdayBegin(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(getDayBegin(date)));
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}

	/**
	 * 获得当前时间的昨天结束时间
	 */
	public static Date getYesterdayEnd() {
		return getYesterdayEnd(new Date());
	}

	/**
	 * 获取某个时间昨天的开始时间
	 */
	public static Date getYesterdayEnd(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(getDayEnd(date)));
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}

	/**
	 * 获得当前明天的开始时间
	 */
	public static Date getTomorrowBegin() {
		return getTomorrowBegin(new Date());
	}

	/**
	 * 获得某个指定时间的明天开始时间
	 */
	public static Date getTomorrowBegin(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(getDayBegin(date)));
		c.add(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	/**
	 * 获得当天时间明天的结束时间
	 */
	public static Date getTomorrowEnd() {
		return getTomorrowEnd(new Date());
	}

	/**
	 * 获得某个指定时间的明天结束时间
	 */
	public static Date getTomorrowEnd(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(getDayEnd(date)));
		c.add(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	/**
	 * 获得本周的开始时间
	 */
	public static Date getWeekBegin() {
		return getWeekBegin(new Date());
	}

	/**
	 * 获得某个时间所在周的开始时间
	 */
	public static Date getWeekBegin(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {
			dayOfWeek += 7;
		}
		c.add(Calendar.DATE, 2 - dayOfWeek);
		return new Date(getDayBegin(c.getTime()));
	}

	/**
	 * 获得本周的结束时间
	 */
	public static Date getWeekEnd() {
		return getWeekEnd(new Date());
	}

	/**
	 * 获得某个时间所在周的结束时间
	 */
	public static Date getWeekEnd(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(getWeekBegin(date));
		c.add(Calendar.DAY_OF_WEEK, 6);
		return new Date(getDayEnd(c.getTime()));
	}

	/**
	 * 获得某个月的最大天数
	 * 
	 * @return 天数
	 */
	public static int getMonthDays(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获得当前时间所在月的第一天
	 */
	public static Date getMonthBegin() {
		return getMonthBegin(new Date());
	}

	/**
	 * 获得某个时间所在月的第一天
	 */
	public static Date getMonthBegin(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
		return new Date(getDayBegin(c.getTime()));
	}

	/**
	 * 获得当前时间所在月的最后一天
	 */
	public static Date getMonthEnd() {
		return getMonthEnd(new Date());
	}

	/**
	 * 获得某个时间所在月的最后一天
	 */
	public static Date getMonthEnd(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return new Date(getDayEnd(c.getTime()));
	}

	/**
	 * 获得当前时间所在季度的开始时间
	 */
	public static Date getSeasonBegin() {
		return getSeasonBegin(new Date());
	}

	/**
	 * 获得当前时间所在季度的开始时间
	 */
	public static Date getSeasonBegin(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MONTH);
		int season = month / 3;
		c.set(c.get(Calendar.YEAR), season * 3, 1);
		return new Date(getDayBegin(c.getTime()));
	}

	/**
	 * 获得当前时间所在季度的结束时间
	 */
	public static Date getSeasonEnd() {
		return getSeasonEnd(new Date());
	}

	/**
	 * 获得当前时间所在季度的结束时间
	 */
	public static Date getSeasonEnd(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MONTH);
		int season = month / 3;
		c.set(Calendar.MONTH, (season + 1) * 3 - 1);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return new Date(getDayEnd(c.getTime()));
	}

	/**
	 * 获得当前时间所在年的开始时间
	 */
	public static Date getYearBegin() {
		return getYearBegin(new Date());
	}

	/**
	 * 获得某个时间所在年的开始时间
	 */
	public static Date getYearBegin(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(c.get(Calendar.YEAR), Calendar.JANUARY, 1);
		return new Date(getDayBegin(c.getTime()));
	}

	/**
	 * 获得当前时间所在年的结束时间
	 */
	public static Date getYearEnd() {
		return getYearEnd(new Date());
	}

	/**
	 * 获得某个时间所在年的结束时间
	 */
	public static Date getYearEnd(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(c.get(Calendar.YEAR), Calendar.DECEMBER, 31);
		return new Date(getDayEnd(c.getTime()));
	}

	/**
	 * 获得2个时间中大的一个
	 */
	public static Date getMaxDate(Date date1, Date date2) {
		if (date1 == null) {
			return date2;
		}
		if (date2 == null) {
			return date1;
		}
		if (date1.after(date2)) {
			return date1;
		}
		return date2;
	}

	/**
	 * 获得2个时间中小的一个
	 */
	public static Date getMinDate(Date date1, Date date2) {
		if (date1 == null) {
			return date2;
		}
		if (date2 == null) {
			return date1;
		}
		if (date1.after(date2)) {
			return date2;
		}
		return date1;
	}

	/**
	 * 比较2个日期是否为同一天
	 * 
	 * @param date1 时间1
	 * @param date2 时间2
	 * @return true是,false否
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);
		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
				&& c1.get(Calendar.DATE) == c2.get(Calendar.DATE)) {
			return true;
		}
		return false;
	}

	public static long span2Date(String date1Str, String date2Str) {
		Date date1 = DateUtils.parse(date1Str);
		Date date2 = DateUtils.parse(date2Str);
		return date2.getTime() - date1.getTime();
	}

	public static long span2Date(String date1Str, Date date2) {
		Date date1 = DateUtils.parse(date1Str);
		return date2.getTime() - date1.getTime();
	}

	/**
	 * 计算2个时间yyyy-MM-dd HH:mm:ss之间的毫秒数,后减前
	 * 
	 * @param date1 开始时间
	 * @param date2 结束时间
	 * @return 相差毫秒数,可小于0
	 */
	public static long span2Date(Date date1, Date date2) {
		return date2.getTime() - date1.getTime();
	}

	/**
	 * 获得2个yyyy-MM-dd或yyyy-MM-dd HH:mm:ss之间相隔的年数
	 * 
	 * @param begintime 开始时间
	 * @param endtime 结束时间
	 * @return 间隔年数.可小于0
	 */
	public static int span2Year(String begintime, String endtime) {
		Date date1 = parse(begintime, DateEnum.DATE);
		Date date2 = parse(endtime, DateEnum.DATE);
		return span2Year(date1, date2);
	}

	public static int span2Year(Date begintime, Date endtime) {
		Calendar c = Calendar.getInstance();
		c.setTime(begintime);
		int m1 = c.get(Calendar.YEAR);
		c.setTime(endtime);
		int m2 = c.get(Calendar.YEAR);
		return m2 - m1;
	}

	/**
	 * 获得2个yyyy-MM-dd或yyyy-MM-dd HH:mm:ss之间相隔的月数
	 * 
	 * @param begintime 开始时间
	 * @param endtime 结束时间
	 * @return 相隔月数.可小于0
	 */
	public static int span2Month(String begintime, String endtime) {
		Date date1 = parse(begintime, DateEnum.DATE);
		Date date2 = parse(endtime, DateEnum.DATE);
		return span2Month(date1, date2);
	}

	public static int span2Month(Date begintime, Date endtime) {
		Calendar c = Calendar.getInstance();
		c.setTime(begintime);
		int year1 = c.get(Calendar.YEAR);
		int m1 = c.get(Calendar.MONTH);
		c.setTime(endtime);
		int year2 = c.get(Calendar.YEAR);
		int m2 = c.get(Calendar.MONTH);
		return (year2 - year1) * 12 + (m2 - m1);
	}

	/**
	 * 获得2个yyyy-MM-dd或yyyy-MM-dd HH:mm:ss之间相隔的天数
	 * 
	 * @param begintime 开始时间
	 * @param endtime 结束时间
	 * @return 相隔天数.可小于0
	 */
	public static int span2Day(String begintime, String endtime) {
		Date date1 = parse(begintime, DateEnum.DATE);
		Date date2 = parse(endtime, DateEnum.DATE);
		return span2Day(date1, date2);
	}

	public static int span2Day(Date begintime, Date endtime) {
		return (int) ((endtime.getTime() - begintime.getTime()) / ONE_DAY);
	}
}