package com.wy._memo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wy.utils.StrUtils;

/**
 * 写一个弱密码的判断类
 * 
 * @author 万杨
 */
public class WeakPassword {
	public static List<String> bankPassword = new ArrayList<String>(Arrays.asList("000000", "111111",
			"222222", "333333", "444444", "555555", "666666", "777777", "888888", "999999", "123456"));

	/**
	 * 判断是否是以生日为基础设置的6位密码
	 * 
	 * @param idCard
	 *            身份证号
	 */
	public static String idCardPwd(String idCard, String pwd) {
		if (bankPassword.indexOf(pwd) > -1) {
			return "密码过于简单";
		}
		if (StrUtils.isNotBlank(idCard)) {
			String birth = idCard.substring(6, 14);
			if (birth.indexOf(pwd) > -1) {
				return "不能使用自己生日作为密码";
			}
		}
		return null;
	}
}
