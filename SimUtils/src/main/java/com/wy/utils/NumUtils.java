package com.wy.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public class NumUtils {
	
	private NumUtils() {
		
	}
	
	/**
	 * 保留多位小数
	 * @param num		要保留小数的原始值
	 * @param retain	需要保留几位小数
	 */
	public static double retainDecimal(double num,int retain) {
		BigDecimal big = new BigDecimal(num);
		BigDecimal result = big.setScale(retain,RoundingMode.HALF_UP);//四舍五入
		return Double.parseDouble(result.toString());
	}
	
	/**
	 * double类型避免精度缺失的加法运算
	 */
	public static Double add(double... ds) {
		if(ds == null || ds.length == 0) {
			return null;
		}
		BigDecimal result = new BigDecimal(ds[0]);
		for(int i=1;i<ds.length;i++) {
			result = result.add(new BigDecimal(ds[i]));
		}
		return result.doubleValue();
	}
	
	/**
	 * 将数字转化为指定的字符串格式
	 * @param dou 指定的double数字
	 * @param format 需要进行转换的格式,若是需要格式固定,则使用0占位符,num超出的整数部分会全部显示,但是不足的会补0
	 * num超出的小数部分为四舍五入,若是不足则补0
	 * example 3.4555,进行0.000kg格式化后为3.456kg,进行#.###kg为3.456kg;
	 * 34.4555进行0.000格式化为34.456,进行#.###为34.456;
	 * 34.455记性000.0000格式化为034.4550,进行###.####格式化为34.455
	 */
	public static String double2Str(double num,String format) {
		DecimalFormat decimalFormat = new DecimalFormat(format);
		return decimalFormat.format(num);//会进行四舍五入
	}
	
	/**
	 * 将数值转换为当前系统默认的货币形式
	 */
	public static String getLocalCurrency(Number num) {
		return getLocalCurrency(num,Locale.getDefault());
	}
	
	/**
	 * 将数值转换为指定格式的货币形式
	 */
	public static String getLocalCurrency(Number num,Locale locale) {
		NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		return nf.format(num);
	}

	/**
	 * 获取数组中最大值
	 */
	public static final long getMax(long[] arrs) {
		Arrays.sort(arrs);
		return arrs[arrs.length-1];
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
		return arrs[arrs.length-1];
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
		return arrs[arrs.length-1];
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
		return arrs[arrs.length-1];
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
		return arrs[arrs.length-1];
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
		return nums[nums.length-1];
	}
	
	/**
	 * 冒泡排序,默认升序
	 */
	public static int[] bubbleSort(int[] arrs,boolean asc) {
		for(int i=1;i<arrs.length;i++){
			for(int j=0;j<arrs.length - i;j++) {
				if(arrs[j] > arrs[j+1]) {
					int temp = arrs[j];
					arrs[j] = arrs[j+1];
					arrs[j+1] = temp;
				}
			}
		}
		return arrs;
	}
}