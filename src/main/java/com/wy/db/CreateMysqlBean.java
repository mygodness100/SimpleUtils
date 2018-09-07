package com.wy.db;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.wy.utils.StrUtils;

/**
 * mysql根据数据库名称批量生成表
 * @author wanyang 2018年2月15日 FIXME 带主键的加注解暂时无法实现,某些字段不带注解的无法实现,一次性拿到所有字段暂时没实现
 */
public class CreateMysqlBean {
	private static final Logger logger = Logger.getLogger(CreateMysqlBean.class);
	private Connection conn;
	private String dbName;
	private String[] tableNames;
	// 实体类上是否加注解,不加则为空
	private String classAnnotationName;
	// 主键是否加注解,不加则为空
	private String primaryAnnotationName;
	// 所有表中主键字段
	private String[] primaryColumns;
	// 普通字段是否加注解,不加则为空
	private String columnAnnotationName;
	// 获取单表中所有字段,多次链接数据库
	private static final String SQL_COLUMNS = "SELECT table_name,column_name,data_type,"
			+ "character_maximum_length,column_comment,numeric_precision,numeric_scale,column_type "
			+ " FROM INFORMATION_SCHEMA.`COLUMNS` WHERE TABLE_SCHEMA =? AND TABLE_NAME = ?";
	// 生成字段类型长度及注释
	private static final String COLUMN_COMMENT = " * %s %s %s\r\n";
	// 生成不带注解的属性字段
	private static final String COLUMN_ATTR_NOT = "\tprivate %s %s;\r\n";
	// 生成带注解的属性字段
	private static final String COLUMN_ATTR = "\t@%s\r\n\tprivate %s %s;\r\n";
	// 生成get,set方法的格式
	private static final String COLUMN_METHOD = "\tpublic void set%s(%s %s) {\r\n"
			+ "\t\tthis.%s = %s;\r\n\t}\r\n\tpublic %s get%s(){\r\n" + "\t\treturn %s;\r\n\t}\r\n";

	public CreateMysqlBean(Connection conn, String[] tableNames, String dbName, String classAnnotationName,
			String primaryAnnotationName, String[] primaryColumns, String columnAnnotationName) {
		this.conn = conn;
		this.dbName = dbName;
		this.tableNames = tableNames;
		this.classAnnotationName = classAnnotationName;
		this.primaryAnnotationName = primaryAnnotationName;
		this.primaryColumns = primaryColumns;
		this.columnAnnotationName = columnAnnotationName;
		createEntitysByTables();
	}
	
	public CreateMysqlBean(Connection conn, String dbName, String classAnnotationName,
			String primaryAnnotationName, String[] primaryColumns, String columnAnnotationName) {
		this.conn = conn;
		this.dbName = dbName;
		this.classAnnotationName = classAnnotationName;
		this.primaryAnnotationName = primaryAnnotationName;
		this.primaryColumns = primaryColumns;
		this.columnAnnotationName = columnAnnotationName;
		createEntitysByDb();
	}

	public CreateMysqlBean(Connection conn,  String[] tableNames, String dbName,String classAnnotationName,
			String primaryAnnotationName, String[] primaryColumns) {
		this(conn, tableNames, dbName, classAnnotationName, primaryAnnotationName, primaryColumns, null);
	}
	
	public CreateMysqlBean(Connection conn, String dbName, String classAnnotationName,
			String primaryAnnotationName, String[] primaryColumns) {
		this(conn, dbName, classAnnotationName, primaryAnnotationName, primaryColumns, null);
	}

	public CreateMysqlBean(Connection conn, String[] tableNames, String dbName, String classAnnotationName) {
		this(conn,  tableNames, dbName,classAnnotationName, null, null);
	}
	
	public CreateMysqlBean(Connection conn, String dbName, String classAnnotationName) {
		this(conn, dbName, classAnnotationName, null, null);
	}

	public CreateMysqlBean(Connection conn, String[] tableNames,String dbName) {
		this(conn, tableNames, dbName,null);
	}
	
	public CreateMysqlBean(Connection conn, String dbName) {
		this(conn, dbName, null);
	}
	/*-----------------------------构造结束-----------------------------*/

	/**
	 * 根据数据库名称批量生成当前数据库中所有实体类
	 */
	private void createEntitysByDb() {
		try {
			String tableSql = "SELECT table_name FROM INFORMATION_SCHEMA.`COLUMNS` "
					+ "WHERE TABLE_SCHEMA =? GROUP BY table_name";
			PreparedStatement ps = conn.prepareStatement(tableSql);
			ps.setString(1, dbName);
			ps.executeQuery();
			ResultSet tables = ps.getResultSet();
			while (tables.next()) {
				createSingleTable(ps, tables.getString("table_name"));
			}
		} catch (Exception e) {
			logger.info(this.getClass().getCanonicalName(), e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					logger.info(this.getClass().getCanonicalName(), e);
				}
			}
		}
	}
	
	/**
	 * 根据数据库名和多表名生成实体类
	 */
	private void createEntitysByTables() {
		try {
			String tableSql = "SELECT table_name FROM INFORMATION_SCHEMA.`COLUMNS` "
					+ "WHERE TABLE_SCHEMA =? and TABLE_NAME IN (?) GROUP BY table_name";
			PreparedStatement ps = conn.prepareStatement(tableSql);
			Array array = ps.getConnection().createArrayOf("VARCHAR", tableNames);
			ps.setString(1, dbName);
			ps.setArray(2, array);
			ps.executeQuery();
			ResultSet tables = ps.getResultSet();
			while (tables.next()) {
				createSingleTable(ps, tables.getString("table_name"));
			}
		} catch (Exception e) {
			logger.info(this.getClass().getCanonicalName(), e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					logger.info(this.getClass().getCanonicalName(), e);
				}
			}
		}
	}

	private void createSingleTable(PreparedStatement ps, String tableName) {
		try {
			ps = conn.prepareStatement(SQL_COLUMNS);
			ps.setString(1, dbName);
			ps.setString(2, tableName);
			ps.executeQuery();
			ResultSet columns = ps.getResultSet();
			String head = createHead(tableName);
			StringBuffer commentSb = new StringBuffer("/**" + tableName + "\r\n");
			StringBuffer columnSb = new StringBuffer(
					"\tprivate static final long serialVersionUID = 1L;\r\n");
			StringBuffer getSetSb = new StringBuffer();
			while (columns.next()) {
				handleResult(columns, commentSb, columnSb, getSetSb);
			}
			createEntry(tableName, commentSb.toString(), head, columnSb.toString(),
					getSetSb.toString());
		} catch (Exception e) {
			logger.info(this.getClass().getCanonicalName(), e);
		}
	}

	private void handleResult(ResultSet columns, StringBuffer commentSb, StringBuffer columnSb,
			StringBuffer getSetSb) {
		try {
			String columnName = columns.getString("column_name");
			String dataType = columns.getString("data_type").toLowerCase();
			String strLen = columns.getString("character_maximum_length") == null ? "0"
					: columns.getString("character_maximum_length");
			String numLen = columns.getString("numeric_precision") == null ? "0"
					: columns.getString("numeric_precision");
			int size = Integer.parseInt(strLen) == 0 ? Integer.parseInt(numLen)
					: Integer.parseInt(strLen);
			String scaLen = columns.getString("numeric_scale") == null ? "0"
					: columns.getString("numeric_scale");
			createComment(commentSb, columnName, columns.getString("column_type"),
					columns.getString("column_comment"));
			createColumns(columnSb, columnName, dataType, Integer.parseInt(scaLen), size);
			createGetSet(getSetSb, columnName, dataType, Integer.parseInt(scaLen));
		} catch (Exception e) {
			logger.info(this.getClass().getCanonicalName(), e);
		}
	}
	
	private void createEntry(String tableName, String commentSb, String head, String columnSb,
			String getSetSb) {
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(StrUtils.upperFirst(tableName) + ".java");
			pw = new PrintWriter(fw);
			pw.println(commentSb + "*/" + head + columnSb + getSetSb + "}");
			pw.flush();
			pw.close();
		} catch (Exception e) {
			logger.info(this.getClass().getCanonicalName(), e);
		} finally {
			if (pw != null) {
				try {
					pw.close();
					if (fw != null) {
						fw.close();
					}
				} catch (Exception e1) {
					logger.info(this.getClass().getCanonicalName(), e1);
				}
			}
		}
	}

	/**
	 * 创建方法上的注释
	 */
	private void createComment(StringBuffer sb, String columnName, String columnType,
			String comment) {
		sb.append(String.format(COLUMN_COMMENT, columnName, columnType, comment));
	}

	/**
	 * 创建方法,是否有注解
	 */
	private String createHead(String tableName) {
		if (classAnnotationName == null) {
			return String.format("public class %s  implements Serializable {\r\n",
					StrUtils.upperFirst(tableName));
		} else {
			return String.format("@%s(\"%s.%s\")\r\npublic class %s  implements Serializable {\r\n",
					classAnnotationName, dbName, tableName, StrUtils.upperFirst(tableName));
		}
	}

	/**
	 * 创建字段
	 */
	private void createColumns(StringBuffer sb, String columnName, String dataType, int scale,
			int size) {
		if (primaryAnnotationName != null && primaryColumns != null) {
			// TODO
		} else if (columnAnnotationName == null) {
			sb.append(
					String.format(COLUMN_ATTR_NOT, DBUtils.sql2Java(dataType, scale), columnName));
		} else {
			sb.append(String.format(COLUMN_ATTR, columnAnnotationName,
					DBUtils.sql2Java(dataType, scale), columnName));
		}
	}

	/**
	 * 创建get.set
	 */
	private void createGetSet(StringBuffer sb, String columnName, String dataType, int scale) {
		sb.append(String.format(COLUMN_METHOD, StrUtils.upperFirst(columnName),
				DBUtils.sql2Java(dataType, scale), columnName, columnName, columnName,
				DBUtils.sql2Java(dataType, scale), StrUtils.upperFirst(columnName), columnName));
	}
}
