package com.wy.db;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.wy.db.DBColumn.DBColumnBuilder;
import com.wy.enums.DriverEnum;
import com.wy.utils.StrUtils;

public class DBUtils {

	private DBUtils() {
	}

	static {
		DBConfig.rewriteConfig();
	}

	/**
	 * 根据数据库类型返回java类型,不精准,需自己校正
	 */
	public static String sql2Java(String sqlType) {
		for (String key : DBConfig.DBCONFIG_JAVA2SQL.keySet()) {
			if (DBConfig.DBCONFIG_JAVA2SQL.get(key).contains(sqlType.toLowerCase())) {
				return key;
			}
		}
		return "Object";
	}

	public static Connection getConn(String url, String username, String password) {
		return getConn(DriverEnum.getDriverClass(url), url, username, password);
	}

	/**
	 * 获得数据库连接
	 * @param driverClass 驱动
	 * @param url 数据库地址
	 * @param username 用户名
	 * @param password 密码
	 * @return 数据库连接
	 */
	public static Connection getConn(String driverClass, String url, String username,
			String password) {
		Connection conn = null;
		try {
			Class.forName(driverClass);
			conn = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static List<DBTable> getDBInfo(String url, String username, String password) {
		return getTableInfo(DriverEnum.getDriverClass(url), url, username, password);
	}

	/**
	 * 从数据库中获得所有的表以及表中的字段,表字段默认下划线转驼峰
	 * @param driverClass 驱动
	 * @param url 数据库连接
	 * @param username 用户名
	 * @param password 密码
	 * @return 表信息
	 */
	public static List<DBTable> getTableInfo(String driverClass, String url, String username,
			String password) {
		return getTableInfo(driverClass, url, username, password, true);
	}

	/**
	 * 从数据库中获得所有的表以及表中的字段
	 * @param driverClass 驱动类
	 * @param url 数据库地址
	 * @param username 数据库用户名
	 * @param password 数据库密码
	 * @param columnSnake2Hump 数据库字段是否下划线转驼峰,true转,false不变
	 * @return 所有表以及字段信息
	 */
	public static List<DBTable> getTableInfo(String driverClass, String url, String username,
			String password, boolean columnSnake2Hump) {
		List<DBTable> tables = new ArrayList<>();
		try (Connection conn = getConn(driverClass, url, username, password);) {
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet result = metaData.getTables(null, null, null, new String[] { "TABLE" });
			while (result.next()) {
				tables.add(getColumns(result.getString("TABLE_NAME"), columnSnake2Hump, conn));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tables;
	}

	/**
	 * 获得单个表的字段以及相关信息
	 * @param tableName 表名
	 * @param columnSnake2Hump 字段是否下划线转驼峰
	 * @param conn 数据库连接
	 * @return 表信息
	 */
	public static DBTable getColumns(String tableName, boolean columnSnake2Hump, Connection conn) {
		try (PreparedStatement stmt = conn
				.prepareStatement(MessageFormat.format(DBConfig.COLUMN_SQL, tableName));
				ResultSet rs = stmt
						.executeQuery(MessageFormat.format(DBConfig.COLUMN_SQL, tableName));) {
			ResultSetMetaData data = rs.getMetaData();
			List<DBColumn> columns = new ArrayList<DBColumn>();
			for (int i = 1; i <= data.getColumnCount(); i++) {
				DBColumnBuilder builder = null;
				if (columnSnake2Hump) {
					builder = DBColumn.builder()
							.columnAlias(StrUtils.snake2Hump(data.getColumnName(i)));
				}
				columns.add(builder.columnName(data.getColumnName(i))
						.sqlType(data.getColumnTypeName(i)).javaClass(data.getColumnClassName(i))
						.javaType(data.getColumnClassName(i)
								.substring(data.getColumnClassName(i).lastIndexOf(".") + 1))
						.length(data.getPrecision(i)).scale(data.getScale(i))
						.isAutoAdd(data.isAutoIncrement(i)).isCurrency(data.isCurrency(i))
						.isNullable(data.isNullable(i)).isReadOnly(data.isReadOnly(i)).build());
			}
			return DBTable.builder().catalogName(conn.getCatalog()).tableName(tableName)
					.tableCount(data.getColumnCount()).columns(columns).build();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void buildFile(String url, String username, String password) {
		buildFile(DriverEnum.getDriverClass(url), url, username, password);
	}

	public static void buildFile(String driverClass, String url, String username, String password) {
		List<DBTable> tableInfo = getTableInfo(driverClass, url, username, password);
		for (DBTable table : tableInfo) {
			System.out.println(table.getTableName());
		}
	}

	public static void buildFile(List<String> templates, String configFile) {
		DBConfig.rewriteMVC(configFile);
		HashMap<String, String> dbconfigConn = DBConfig.DBCONFIG_CONN;
		for (Entry<String, String> entry : dbconfigConn.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
		List<DBTable> tableInfo = getTableInfo(dbconfigConn.get("driverClass"),
				dbconfigConn.get("url"), dbconfigConn.get("username"),
				dbconfigConn.get("password"));
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();
		Template temp = null;
		for (DBTable table : tableInfo) {
			for (String template : templates) {
				String tablePrefix = (String) DBConfig.DBCONFIG_MVC.get("table_prefix");
				String entityName = "";
				if (StrUtils.isNotBlank(tablePrefix)) {
					String[] prefixs = tablePrefix.split(",");
					for (String prefix : prefixs) {
						if (table.getTableName().startsWith(prefix)) {
							// 去除前缀之后下划线转驼峰
							entityName = StrUtils
									.snake2Hump(table.getTableName().replace(prefix, ""));
							table.setTableAlias(StrUtils.upperFirst(entityName));
							break;
						}
					}
				}
				if (template.indexOf("/") != -1) {
					entityName += template.replace(".vm", "")
							.substring(template.lastIndexOf("/") + 1);
				}
				temp = ve.getTemplate(template);
				VelocityContext context = new VelocityContext();
				context.put("dbConfig", DBConfig.DBCONFIG_MVC);
				context.put("dbTable", table);
				context.put("fileName", StrUtils.upperFirst(entityName));
				try (FileWriter fw = new FileWriter(StrUtils.upperFirst(entityName));) {
					temp.merge(context, fw);
					fw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		// buildFile("jdbc:mysql://localhost:3306/simpleoa?autoReconnect=true&amp;useUnicode=true",
		// "root", "52LDforever");
		List<String> templates = new ArrayList<>();
		templates.add("templates/Entity.java.vm");
		buildFile(templates, "generator/generator.properties");
	}
}