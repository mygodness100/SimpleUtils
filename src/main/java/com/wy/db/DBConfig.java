package com.wy.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import com.wy.enums.Java2SqlEnum;
import com.wy.utils.StrUtils;

/**
 * 数据库配置文件
 * @author paradiseWy
 */
public class DBConfig {
	public static final String COLUMN_SQL = "select * from {0}";
	// 需要生成的文件地址0资源文件路径1分隔符2包路径3分隔符4文件名
	public static final String FILE_DES = "{0}{1}{2}{3}{4}";

	public static final String FILE_ = null;

	public static final HashMap<String, String> DBCONFIG_CONN = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("dirverClass", "");
			put("url", "");
			put("username", "");
			put("password", "");
		}
	};

	/**
	 * 默认java类型和sql类型对照关系
	 */
	public static final HashMap<String, List<String>> DBCONFIG_JAVA2SQL = new HashMap<String, List<String>>() {
		private static final long serialVersionUID = 1L;
		{
			put("Integer", Java2SqlEnum.INTEGER.getSqlType());
			put("Long", Java2SqlEnum.LONG.getSqlType());
			put("Double", Java2SqlEnum.DOUBLE.getSqlType());
			put("Boolean", Java2SqlEnum.BOOLEAN.getSqlType());
			put("String", Java2SqlEnum.STRING.getSqlType());
			put("Date", Java2SqlEnum.DATE.getSqlType());
			put("byte[]", Java2SqlEnum.BYTES.getSqlType());
			put("BigDecimal", Java2SqlEnum.BIGDECIMAL.getSqlType());
		}
	};

	/**
	 * 默认生成mvc相关文件的配置
	 */
	public static final HashMap<String, Object> DBCONFIG_MVC = new HashMap<String, Object>() {
		private static final long serialVersionUID = 1L;
		{
			put("project_entity", "src/main/java");
			put("project_dao", "src/main/java");
			put("project_mapper", "src/main/java");
			put("project_xml", "src/main/java");
			put("project_service", "src/main/java");
			put("project_controller", "src/main/java");
			put("package_entity", "");
			put("package_dao", "");
			put("package_mapper", "");
			put("package_xml", "");
			put("package_service", "");
			put("package_controller", "");
			put("base_entity", "");
			put("base_dao", "");
			put("base_mapper", "");
			put("base_xml", "");
			put("base_service", "");
			put("base_controller", "");
			put("table_prefix", "");
			put("column_toHump", true);
		}
	};

	/**
	 * 重写所有的资源配置文件
	 */
	public static void rewriteConfig() {
		rewriteJava2Sql();
		rewriteMVC();
	}

	/**
	 * 重写配置文件,默认配置文件放在资源目录下的generator/generator.properties里
	 */
	public static void rewriteMVC() {
		rewriteMVC(null);
	}

	/**
	 * 若存在配置文件,则使用配置文件的对应关系,若不存在,则直接使用默认对应关系
	 * @param configFile 资源配置文件牡蛎
	 */
	public static void rewriteMVC(String configFile) {
		configFile = StrUtils.isBlank(configFile) ? "generator/generator.properties" : configFile;
		Properties props = new Properties();
		try (InputStream db = DBUtils.class.getClassLoader().getResourceAsStream(configFile);) {
			// 加载生成mvc文件的配置文件
			if (!Objects.isNull(db)) {
				props.clear();
				props.load(db);
				for (Entry<Object, Object> entry : props.entrySet()) {
					String key = (String) entry.getKey();
					if (key.indexOf("driverClass") != -1 || key.indexOf("url") != -1
							|| key.indexOf("username") != -1 || key.indexOf("password") != -1) {
						DBCONFIG_CONN.put(key, (String) entry.getValue());
					} else {
						if (!Objects.isNull(entry.getValue())) {
							DBCONFIG_MVC.put(key, entry.getValue());
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 重写java与sql类型的关系对照表,默认配置文件放在资源文件的generator/java2sql.properties中
	 */
	public static void rewriteJava2Sql() {
		rewriteJava2Sql(null);
	}

	/**
	 * 重写java与sql类型的关系对照表
	 * @param configFile 配置文件地址
	 */
	public static void rewriteJava2Sql(String configFile) {
		configFile = StrUtils.isBlank(configFile) ? "generator/java2sql.properties" : configFile;
		Properties props = new Properties();
		try (InputStream is = DBUtils.class.getClassLoader().getResourceAsStream(configFile);) {
			// 加载java和sql类型的对照文件
			if (!Objects.isNull(is)) {
				props.clear();
				props.load(is);
				for (Map.Entry<Object, Object> entry : props.entrySet()) {
					if (!Objects.isNull(entry.getValue())) {
						if ("bytes".equals((String) entry.getKey())) {
							DBCONFIG_JAVA2SQL.put("byte[]",
									Arrays.asList(((String) entry.getValue()).split(",")));
						} else {
							DBCONFIG_JAVA2SQL.put((String) entry.getKey(),
									Arrays.asList(((String) entry.getValue()).split(",")));
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}