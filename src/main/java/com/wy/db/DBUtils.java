package com.wy.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import com.wy.enums.Java2SqlEnum;

public class DBUtils {

	private static final String DRIVERCLASS_MYSQL = "com.mysql.jdbc.Driver";
	private static final String DRIVERCLASS_ORACLE = "oracle.jdbc.driver.OracleDriver";
	private static final String DRIVERCLASS_SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	private static String driverClass;

	private static final HashMap<String, List<String>> JAVA_TO_SQL = new HashMap<String, List<String>>() {
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

	private DBUtils() {

	}

	/**
	 * 若存在配置文件,则使用配置文件的对应关系,若不存在,则直接使用默认对应关系
	 */
	static {
		Properties props = new Properties();
		try {
			InputStream is = DBUtils.class.getClassLoader()
					.getResourceAsStream("java2sql.properties");
			if (!Objects.isNull(is)) {
				props.load(is);
				for (Map.Entry<Object, Object> entry : props.entrySet()) {
					if (!Objects.isNull(entry.getValue())) {
						if ("bytes".equals((String) entry.getKey())) {
							JAVA_TO_SQL.put("byte[]",
									Arrays.asList(((String) entry.getValue()).split(",")));
						} else {
							JAVA_TO_SQL.put((String) entry.getKey(),
									Arrays.asList(((String) entry.getValue()).split(",")));
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 数据库匹配相应驱动
	 */
	public static Map<String, String> DRIVERCLASS = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("mysql", DRIVERCLASS_MYSQL);
			put("oracle", DRIVERCLASS_ORACLE);
			put("sqlserver", DRIVERCLASS_SQLSERVER);
		}
	};

	/**
	 * 根据数据库类型选择驱动
	 */
	private static void getDriverClass(String url) {
		for (String key : DRIVERCLASS.keySet()) {
			if (url.contains(key)) {
				driverClass = DRIVERCLASS.get(key);
				break;
			}
		}
	}

	/**
	 * 根据数据库类型选择驱动
	 */
	public static String getDbType(String url) {
		for (String key : DRIVERCLASS.keySet()) {
			if (url.contains(key)) {
				return key;
			}
		}
		return null;
	}

	public static Connection getConn(String url, String username, String password) {
		getDriverClass(url);
		try {
			Class.forName(driverClass);
			Connection connection = DriverManager.getConnection(url, username, password);
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据数据库类型返回java类型,不精准,需自己校正
	 * number,decimal,numeric,real需根据保留小数位判断,若小数位为空或0,则取整,否则返回double
	 */
	public static String sql2Java(String sqlType, Integer scale) {
		for (String key : JAVA_TO_SQL.keySet()) {
			if (JAVA_TO_SQL.get(key).contains(sqlType.toLowerCase())) {
				return key;
			}
		}
		return "Object";
	}
}
