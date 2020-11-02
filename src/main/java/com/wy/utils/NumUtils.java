package com.wy.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.wy.result.ResultException;

public class NumUtils {

	private NumUtils() {

	}

	/**
	 * double类型避免精度缺失的加法运算
	 * 
	 * @param ds 做运算的参数
	 * @return 加法运算的结果
	 */
	public static Double add(double... ds) {
		if (ArrayUtils.isEmpty(ds)) {
			return null;
		}
		BigDecimal result = new BigDecimal(ds[0]);
		for (int i = 1; i < ds.length; i++) {
			result = result.add(new BigDecimal(ds[i]));
		}
		return result.doubleValue();
	}

	/**
	 * 精确的减法运算
	 * 
	 * @param arg1 被减数
	 * @param arg2 减数
	 * @return arg1减去arg2的差
	 */
	public static double subtract(double arg1, double arg2) {
		return new BigDecimal(arg1).subtract(new BigDecimal(arg2)).doubleValue();
	}

	/**
	 * 精准的乘法运算
	 * 
	 * @param arg1 被乘数
	 * @param arg2 乘数
	 * @return 两个参数的积
	 */
	public static double multiply(double arg1, double arg2) {
		return new BigDecimal(arg1).multiply(new BigDecimal(arg2)).doubleValue();
	}

	/**
	 * 精确的除法运算.当发生除不尽的情况时,默认精确到小数点以后3位,并且四舍五入
	 *
	 * @param arg1 被除数
	 * @param arg2 除数
	 * @return arg1除以arg2两个参数的商
	 */
	public static double div(double arg1, double arg2) {
		return div(arg1, arg2, 3);
	}

	/**
	 * 精确的除法运算.当发生除不尽的情况时,由scale参数指定精度,并且四舍五入
	 *
	 * @param arg1 被除数
	 * @param arg2 除数
	 * @param scale 表示表示需要精确到小数点以后几位
	 * @return arg1除以arg2的商
	 */
	public static double div(double arg1, double arg2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("精确度必须是正整数");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(arg1));
		BigDecimal b2 = new BigDecimal(Double.toString(arg2));
		if (b1.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO.doubleValue();
		}
		if (b2.compareTo(BigDecimal.ZERO) == 0) {
			throw new IllegalArgumentException("除数不可为0");
		}
		return b1.divide(b2, scale, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 保留多位小数
	 * 
	 * @param num 要保留小数的原始值
	 * @param retain 需要保留几位小数
	 */
	public static double round(double num, int retain) {
		if (retain < 0) {
			throw new ResultException("精确度必须是正整数或0");
		}
		BigDecimal big = new BigDecimal(num);
		BigDecimal result = big.setScale(retain, RoundingMode.HALF_UP);// 四舍五入
		return Double.parseDouble(result.toString());
	}

	/**
	 * 将数字转化为指定的字符串格式
	 * 
	 * @param dou 指定的double数字
	 * @param format 需要进行转换的格式,若是需要格式固定,则使用0占位符,num超出的整数部分会全部显示,但是不足的会补0 num超出的小数部分为四舍五入,若是不足则补0
	 *        example 3.4555,进行0.000kg格式化后为3.456kg,进行#.###kg为3.456kg;
	 *        34.4555进行0.000格式化为34.456,进行#.###为34.456;
	 *        34.455记性000.0000格式化为034.4550,进行###.####格式化为34.455
	 */
	public static String double2Str(double num, String format) {
		DecimalFormat decimalFormat = new DecimalFormat(format);
		return decimalFormat.format(num);// 会进行四舍五入
	}

	/**
	 * 将数值转换为当前系统默认的货币形式
	 */
	public static String getLocalCurrency(Number num) {
		return getLocalCurrency(num, Locale.getDefault());
	}

	/**
	 * 将数值转换为指定格式的货币形式
	 */
	public static String getLocalCurrency(Number num, Locale locale) {
		NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		return nf.format(num);
	}

	/**
	 * 获取数组中最大值
	 */
	public static final long getMax(long[] arrs) {
		Arrays.sort(arrs);
		return arrs[arrs.length - 1];
	}

	/**
	 * 获取数组中最小值
	 */
	public static final long getMin(long[] arrs) {
		Arrays.sort(arrs);
		return arrs[0];
	}

	/**
	 * 获取数组中最大值
	 */
	public static final int getMax(int[] arrs) {
		Arrays.sort(arrs);
		return arrs[arrs.length - 1];
	}

	/**
	 * 获取数组中最小值
	 */
	public static final int getMin(int[] arrs) {
		Arrays.sort(arrs);
		return arrs[0];
	}

	/**
	 * 获取数组中最大值
	 */
	public static final int getMax(short[] arrs) {
		Arrays.sort(arrs);
		return arrs[arrs.length - 1];
	}

	/**
	 * 获取数组中最小值
	 */
	public static final int getMin(short[] arrs) {
		Arrays.sort(arrs);
		return arrs[0];
	}

	/**
	 * 获取数组中最大值
	 */
	public static final double getMax(double[] arrs) {
		Arrays.sort(arrs);
		return arrs[arrs.length - 1];
	}

	/**
	 * 获取数组中最小值
	 */
	public static final double getMin(double[] arrs) {
		Arrays.sort(arrs);
		return arrs[0];
	}

	/**
	 * 获取数组中最大值
	 */
	public static final double getMax(float[] arrs) {
		Arrays.sort(arrs);
		return arrs[arrs.length - 1];
	}

	/**
	 * 获取数组中最小值
	 */
	public static final double getMin(float[] arrs) {
		Arrays.sort(arrs);
		return arrs[0];
	}

	/**
	 * 数字类最小值,number中可以存放任何数字类型,byte,short,int,double,float,long等
	 */
	public static Number getMin(Number[] nums) {
		Arrays.sort(nums);
		return nums[0];
	}

	/**
	 * 数字类最大值
	 */
	public static Number getMax(Number[] nums) {
		Arrays.sort(nums);
		return nums[nums.length - 1];
	}

	/**
	 * 冒泡排序,默认升序
	 */
	public static int[] bubbleSort(int[] arrs) {
		return bubbleSort(arrs, true);
	}

	/**
	 * 冒泡排序,默认升序
	 */
	public static int[] bubbleSort(int[] arrs, boolean asc) {
		for (int i = 1; i < arrs.length; i++) {
			for (int j = 0; j < arrs.length - i; j++) {
				if (asc) {
					if (arrs[j] > arrs[j + 1]) {
						swap(arrs, j, j + 1);
					}
				} else {
					if (arrs[j] < arrs[j + 1]) {
						swap(arrs, j, j + 1);
					}
				}
			}
		}
		return arrs;
	}

	/**
	 * 选择排序,默认升序
	 * 
	 * @param arrs 需要排序的数组
	 * @return 排序后数组
	 */
	public static int[] selectSort(int[] arrs) {
		return selectSort(arrs, true);
	}

	/**
	 * 选择排序
	 * 
	 * @param arrs 需要排序的数组
	 * @param asc 升序还是降序,默认true升序
	 * @return 排序后数组
	 */
	public static int[] selectSort(int[] arrs, boolean asc) {
		for (int i = 0; i < arrs.length; i++) {
			for (int j = i + 1; j < arrs.length; j++) {
				if (asc) {
					if (arrs[i] > arrs[j]) {
						swap(arrs, i, j);
					}
				} else {
					if (arrs[i] < arrs[j]) {
						swap(arrs, i, j);
					}
				}
			}
		}
		return arrs;
	}

	/**
	 * 数组数据交换
	 * 
	 * @param arr 数组
	 * @param x 数组下标
	 * @param y 数组下标
	 */
	public static void swap(int[] arr, int x, int y) {
		int temp = arr[x];
		arr[x] = arr[y];
		arr[y] = temp;
	}

	/**
	 * 将一个对象强行转换为long类型,该对象必须是一个整数字符串
	 * 
	 * @param obj 需要进行强转的对象,可以是object,string等
	 * @return 强转后的long类型值
	 */
	public static Long parseLong(Object obj) {
		Optional<Object> optional = Optional.ofNullable(obj);
		if (!optional.isPresent()) {
			throw new NullPointerException("obj can't be null");
		}
		if (NumberUtils.isDigits(optional.get().toString())) {
			throw new RuntimeException("this obj is not a number string");
		}
		return Long.parseLong(optional.get().toString());
	}

	/**
	 * 将一个对象强行转换为int类型,该对象必须是一个整数字符串
	 * 
	 * @param obj 需要进行强转的对象,可以是object,string等
	 * @return 强转后的值
	 */
	public static <T> Integer parseInt(Object obj) {
		Optional<Object> optional = Optional.ofNullable(obj);
		if (!optional.isPresent()) {
			throw new NullPointerException("obj can't be null");
		}
		if (NumberUtils.isDigits(optional.get().toString())) {
			throw new RuntimeException("this obj is not a number string");
		}
		return Integer.parseInt(optional.get().toString());
	}

	/**
	 * 对象是否能强转为一个整数,不包括小数,科学计数法等
	 * 
	 * @param obj 对象
	 * @return true是,false不是
	 */
	public static boolean isDigits(Object obj) {
		Optional<Object> optional = Optional.ofNullable(obj);
		if (!optional.isPresent()) {
			throw new NullPointerException("obj can't be null");
		}
		return NumberUtils.isDigits(optional.get().toString());
	}

	/**
	 * 对象是否能强转为一个数字,包括整数,小数,不包括科学计数法等
	 * 
	 * @param obj 对象
	 * @return true是,false不是
	 */
	public static boolean isParsable(Object obj) {
		Optional<Object> optional = Optional.ofNullable(obj);
		ObjUtils.isNull(optional);
		return NumberUtils.isParsable(optional.get().toString());
	}
}