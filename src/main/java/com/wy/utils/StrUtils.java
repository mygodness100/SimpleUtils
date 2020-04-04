package com.wy.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.wy.enums.RegexEnum;
import com.wy.result.ResultException;

/**
 * 字符串帮助类 正则表达式的分组: @ (),每一个对括号表示一个分组,分组的顺序是从左括号出现的顺序, @
 * $后面加一个数字表示对应某一个分组,替代分组中不可直接表达的字符串
 * @example 快快乐乐 去掉叠词为快乐,pattern((.)\\1+,$1):
 *          第一个括号表示第一个分组中的任意字符;\\1表示第一个分组,\\2表示第2个分组,需要紧跟在分组之后
 *          +表示可出现多个相同的任意字符,$1表示将分组中的.所代表的任意字符串替换到$1,$2则表示替换第二个分组
 * @author wanyang 2018年7月7日
 */
public class StrUtils extends StringUtils {

	private static final Pattern linePattern = Pattern.compile("_(\\w)");

	private static final Pattern humpPattern = Pattern.compile("[A-Z]");

	private StrUtils() {
	}

	public static boolean isNotBlank(CharSequence... args) {
		for (CharSequence arg : args) {
			if (isBlank(arg)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断数组是否为空,不判断数组中的数据是否为空
	 */
	public static boolean isBlanks(String[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 将目标字符串的首字母变成大写
	 */
	public static String upperFirst(String src) {
		return changeFirst(src, true);
	}

	/**
	 * 将目标字符串的首字母变成小写
	 */
	public static String lowerFirst(String src) {
		return changeFirst(src, false);
	}

	private static String changeFirst(String src, boolean capitalize) {
		if (isBlank(src)) {
			return src;
		}
		char baseChar = src.charAt(0);
		char updatedChar = capitalize ? Character.toUpperCase(baseChar) : Character.toLowerCase(baseChar);
		if (baseChar == updatedChar) {
			return src;
		}
		char[] chars = src.toCharArray();
		chars[0] = updatedChar;
		return new String(chars, 0, chars.length);
	}

	/**
	 * 合法性检查
	 * @param des 目标字符串
	 * @param type 检查类型,手机号,qq号等
	 */
	public static boolean checkValidity(String des, RegexEnum type) {
		if (type == null) {
			return false;
		}
		return checkValidity(des, type.toString());
	}

	/**
	 * 合法性检查
	 * @param des 目标字符串
	 * @param pattern 检查类型
	 */
	public static final boolean checkValidity(CharSequence des, String pattern) {
		if (isBlank(des) || isBlank(pattern)) {
			return false;
		}
		return des.toString().matches(pattern);
	}

	/**
	 * 判断一个字符串是否全是中文,包括中文标点符号等
	 */
	public static boolean isChinese(String str) {
		char[] array = str.toCharArray();
		for (char c : array) {
			if (!isChinese(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否为中文字符,包括标点等 CJK的意思是“Chinese，Japanese，Korea”的简写
	 * ，实际上就是指中日韩三国的象形文字的Unicode编码 CJK_UNIFIED_IDEOGRAPHS:4E00-9FBF:CJK 统一表意符号
	 * CJK_COMPATIBILITY_IDEOGRAPHS:F900-FAFF:CJK 兼容象形文字
	 * CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A:3400-4DBF:CJK 统一表意符号扩展A
	 * CJK_SYMBOLS_AND_PUNCTUATION:3000-303F:CJK 符号和标点
	 * HALFWIDTH_AND_FULLWIDTH_FORMS:FF00-FFEF:半角及全角形式
	 * GENERAL_PUNCTUATION:2000-206F:常用标点
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}

	/**
	 * 判断一个字符串中是否含有另外一个字符串,不区分大小写
	 * @param src 原字符串
	 * @param des 需要检索的字符串
	 */
	public static int indexOf(CharSequence src, CharSequence des) {
		if (isBlank(src) || isBlank(des)) {
			throw new ResultException("参数不能为空");
		}
		return (src.toString().toLowerCase()).indexOf(des.toString().toLowerCase());
	}

	/**
	 * 判断一个字符串中是否含有另外一个字符串,不区分大小写
	 */
	public static boolean contains(CharSequence src, CharSequence des) {
		return indexOf(src, des) > -1;
	}

	/**
	 * @apiNote 将带下划线的字段名(蛇底式)变成驼峰式
	 */
	public static String snake2Hump(String str) {
		str = str.toLowerCase();
		Matcher matcher = linePattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		return changeFirst(sb.toString(), false);
	}

	/**
	 * @apiNote 将驼峰转成蛇底式
	 * @param str 需要进行转换的字符串
	 * @return 转换后的字符串
	 */
	public static String hump2Snake(String str) {
		return hump2Snake(str, true);
	}

	/**
	 * @apiNote 将驼峰转成蛇底式
	 * @param str 需要进行转换的字符串
	 * @param lowerFirst 第一个字符串是否小写,true小写,false不管
	 * @return 转换后的字符串
	 */
	public static String hump2Snake(String str, boolean lowerFirst) {
		str = lowerFirst ? lowerFirst(str) : str;
		Matcher matcher = humpPattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 判断一个字符串是否能转成数字
	 */
	public static boolean isNumeric(CharSequence cs) {
		if (isBlank(cs)) {
			return false;
		}
		final int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 将数字金额转换为中文金额,若有小数点只能到毫,即最多只能有3位小数,若多余3位则四舍五入
	 */
	public static String currency2Chs(String num) {
		if (num.indexOf(".") > -1) {

		}
		return null;
	}

	public static String formatBuffer(Object... msgs) {
		StringBuffer builder = new StringBuffer();
		for (Object msg : msgs) {
			builder.append(msg);
		}
		return builder.toString();
	}

	public static String formatBuilder(Object... msgs) {
		StringBuilder builder = new StringBuilder();
		for (Object msg : msgs) {
			builder.append(msg);
		}
		return builder.toString();
	}

	/**
	 * 获得类似三元表达式的结果
	 * @param str1 若str1不为null,不为"",则返回str1.否则返回str2
	 * @param str2 若str1不存在时,返回str2
	 * @return 结果
	 */
	public static String getTernary(String str1, String str2) {
		return isBlank(str1) ? str2 : str1;
	}
}