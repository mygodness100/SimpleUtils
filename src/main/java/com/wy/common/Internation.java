package com.wy.common;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.http.util.Asserts;

import com.wy.utils.StrUtils;

/**
 * @description 该国际化配置类只能在本项目中使用,不可作为工具类对外使用
 * @instruction 配置文件需要至少一个默认的,不带任何后缀的properties文件.其他需要国际化的语言,
 *              需要在文件名后以_分开,如:message_zh_CN,message是默认的文件,zh是国家,CN是语言.
 *              有多个国际化类型时,需要写多个message的配置文件,必须是properties文件
 * @author ParadiseWy
 * @date 2019年6月7日 下午9:06:41
 * @git {@link https://github.com/mygodness100}
 */
public class Internation {
	private static ResourceBundle resource;

	static {
		resource = ResourceBundle.getBundle("internation/messages", Locale.getDefault());
	}

	private Internation() {
	}

	public static ResourceBundle get() {
		return resource;
	}

	public static Object get(String key) {
		Asserts.notBlank(key, "key");
		return resource.getObject(key);
	}

	public static String getStr(String key) {
		Asserts.notBlank(key, "key");
		return resource.getString(key);
	}

	public static String getStr(String key, String defaultVal) {
		Asserts.notBlank(key, "key");
		String val = resource.getString(key);
		return StrUtils.isBlank(val) ? defaultVal : val;
	}

	public static Set<String> getKeys() {
		return resource.keySet();
	}

	public static String[] getStrs(String key) {
		Asserts.notBlank(key, "key");
		return resource.getStringArray(key);
	}

	public static boolean hasKey(String key) {
		Asserts.notBlank(key, "key");
		return resource.containsKey(key);
	}

	public static Map<String, Object> getMap() {
		Map<String, Object> result = new HashMap<>();
		for (String key : resource.keySet()) {
			result.put(key, resource.getObject(key));
		}
		return result;
	}
}