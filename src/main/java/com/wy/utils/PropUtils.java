package com.wy.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

public class PropUtils {
	public static ResourceBundle resource = null;

	/**
	 * 读取本地配置文件,国际化处理
	 * 
	 * @param filePath 文件名称,格式为filePath_language_country.properties
	 * @param key 需要进行查询的值
	 */
	public static String getProper(String filePath, String key) {
		resource = ResourceBundle.getBundle(filePath, Locale.getDefault());
		return resource.getString(key);
	}

	/**
	 * 一次性获取配置文件中的所有配置,迭代调用过一次next方法后,下一次调用next方法的值就会改变,调用一次就改变一次,并非不变
	 * 
	 * @param name 配置文件名称
	 * @return Map
	 */
	public static Map<String, String> getProper(String filePath) {
		Map<String, String> map = new HashMap<String, String>();
		resource = ResourceBundle.getBundle(filePath, Locale.getDefault());
		Set<String> keys = resource.keySet();
		for (String key : keys) {
			map.put(key, resource.getString(key));
		}
		return map;
	}

	/**
	 * 根据流获取配置文件中的信息
	 * 
	 * @param config 配置文件名
	 * @param key key值
	 */
	public static Properties getProp(String filePath) {
		try {
			InputStream in = Properties.class.getClassLoader().getResourceAsStream(filePath);
			Properties proper = new Properties();
			proper.load(in);
			return proper;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取配置文件内所有参数
	 */
	public static Map<String, Object> getProperty(String filePath) {
		Properties proper = getProp(filePath);
		Map<String, Object> map = new HashMap<>();
		Set<Object> keys = proper.keySet();
		for (Object key : keys) {
			map.put(key.toString(), proper.get(key));
		}
		return map;
	}
	
	/**
	 * 根据流获取配置文件中的信息
	 * 
	 * @param filePath 配置文件路径
	 */
	public static Object getProperty(String filePath, String key) {
		if (StrUtils.isBlank(filePath,key)) {
			return null;
		}
		Map<String, Object> proper = getProperty(filePath);
		if (MapUtils.isNotBlank(proper)) {
			return proper.get(key);
		}
		return null;
	}
	
	/**
	 * 根据流获取配置文件中的信息
	 * 
	 * @param filePath 配置文件路径
	 */
	public static Object getProperty(String filePath, String key,Object defaultValue) {
		if (StrUtils.isBlank(filePath,key)) {
			return null;
		}
		Map<String, Object> proper = getProperty(filePath);
		if (MapUtils.isNotBlank(proper)) {
			return proper.getOrDefault(key,defaultValue);
		}
		return null;
	}
}
