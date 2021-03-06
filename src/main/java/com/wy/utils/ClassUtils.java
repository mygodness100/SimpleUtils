package com.wy.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.wy.utils.ListUtils.ListBuilder;

public class ClassUtils {

	/**
	 * 判断非空,只能判断String,hashMap,arraylist,hashset
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <K, V, T> boolean isEmpty(T t) {
		if (t == null) {
			return true;
		}
		Class clazz = t.getClass();
		if (String.class == clazz) {
			return StrUtils.isBlank((String) t);
		}
		if (HashMap.class == clazz || ConcurrentHashMap.class == clazz
				|| LinkedHashMap.class == clazz) {
			return MapUtils.isBlank((Map<K, V>) t);
		}
		if (ArrayList.class == clazz || HashSet.class == clazz || LinkedList.class == clazz) {
			return ListUtils.isBlank((Collection) t);
		}
		return false;
	}

	public static final <T> boolean isNotEmpty(T t) {
		return !isEmpty(t);
	}

	/**
	 * 判断是否为一个整数,byte,short,int,long
	 */
	public static boolean isIntegral(Class<?> clazz) {
		if (byte.class == clazz || Byte.class == clazz || short.class == clazz
				|| Short.class == clazz || int.class == clazz || Integer.class == clazz
				|| long.class == clazz || Long.class == clazz) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否为基础类型和字符串,字符串不属于基本类型
	 */
	public static boolean isPrimitives(Class<?> clazz) {
		if (clazz.isPrimitive() || String.class == clazz) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是基本类型的包装类 每个基本类型的包装类都有一个TYPE的字段,且都是static final
	 * Field.get(null)表示获得静态变量的值,如果是一个实例,则获得是对应实例的字段的值
	 */
	public static boolean isWrapClass(Class<?> clazz) {
		try {
			return ((Class<?>) (clazz.getField("TYPE").get(null))).isPrimitive();
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获得包装类型的对应的基本类型
	 */
	public static Class<?> getBaseClass(Class<?> clazz) {
		if (isWrapClass(clazz)) {
			try {
				return (Class<?>) (clazz.getField("TYPE").get(null));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return clazz;
	}

	/**
	 * Map转换成实体类
	 */
	public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) {
		return JSON.parseObject(JSON.toJSONString(map), clazz);
	}

	/**
	 * Map转换成实体类
	 */
	public static <T> T mapToBean2(Map<String, Object> map, Class<T> clazz) {
		try {
			if (MapUtils.isNotBlank(map)) {
				Field[] fields = clazz.getDeclaredFields();
				T t = clazz.newInstance();
				for (Field field : fields) {
					field.setAccessible(true);
					field.set(t, map.get(field.getName()));
				}
				return t;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 实体类转换为Map,实体类必须是严格按照java的set,get方式生成的
	 * 而且实体类的属性名第一个字母必须小写,不然报错
	 */
	public static <T> Map<String, Object> beanToMap(T t) {
		try {
			Class<?> clazz = t.getClass();
			Field[] fields = clazz.getDeclaredFields();
			Map<String, Object> map = new HashMap<>();
			for (Field field : fields) {
				String key = field.getName();
				map.put(key, field.get(t));
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将实体类的所有字段提取出来放到一个list中
	 * @param clazz 字节码
	 * @return 集合
	 */
	public static <T> List<String> getEntityField(Class<T> clazz) {
		return getEntityField(clazz, false);
	}

	/**
	 * 将实体类的所有字段提取出来放到一个list中
	 * @param clazz 字节码
	 * @param primitive 是否只提取基本类和字符串,默认false
	 * @return 集合
	 */
	public static <T> List<String> getEntityField(Class<T> clazz, boolean primitive) {
		Field[] fields = clazz.getDeclaredFields();
		ListBuilder<String> builder = ListUtils.getBuilder();
		for (Field field : fields) {
			field.setAccessible(true);
			if (primitive) {
				if (!isPrimitives(field.getType()) && !isWrapClass(field.getType())) {
					continue;
				}
			}
			builder = builder.add(field.getName());
		}
		return builder.build();
	}
}