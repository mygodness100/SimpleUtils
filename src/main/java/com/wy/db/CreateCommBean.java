//package com.wy.db;
//
//import java.io.FileWriter;
//import java.io.PrintWriter;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//
//import com.wy.utils.StrUtils;
//
///**
// * 单表生成实体类,根据表名生成实体类java文件,在根目录下刷新,不考虑多线程并发场景
// * @author 万杨 Administrator 2017年8月21日 上午9:47:10
// */
//public class CreateCommBean {
//	private Connection conn;
//	private static String dbType;
//	private static String sql_single;
//	private static String[] colnames;
//
//	private static final String EX_SQLSERVER = "select top 1 * from %s";
//	private static final String EX_ORACLE = "select * from %s where rownum =1";
//	private static final String EX_MYSQL = "select * from %s limit 1";
//
//	private static final Map<String, String> DB_EX = new HashMap<String, String>() {
//		private static final long serialVersionUID = 1L;
//		{
//			put("sqlserver", EX_SQLSERVER);
//			put("oracle", EX_ORACLE);
//			put("mysql", EX_MYSQL);
//		}
//	};
//	// 生成get,set方法的格式
//	private static final String CREATE_METHOD = "\tpublic void set%s(%s %s) {\r\n"
//			+ "\t\tthis.%s = %s;\r\n\t}\r\n\tpublic %s get%s(){\r\n"
//			+ "\t\treturn %s;\r\n\t}\r\n";
//
//	private CreateCommBean(String url, String username, String password) {
//		conn = DBUtils.getConn(url, username, password);
//		dbType = DBUtils.getDbType(url);
//	}
//
//	/**
//	 * 单表生成实体类
//	 */
//	public CreateCommBean(String tableName, String url, String username, String password) {
//		this(url, username, password);
//		sql_single = String.format(DB_EX.get(dbType), tableName);
//		CreateEntry(tableName, true);
//	}
//
//	/**
//	 * 多表生成实体类
//	 */
//	public CreateCommBean(String[] tables, String url, String username, String password) {
//		this(url, username, password);
//		sql_single = String.format(DB_EX.get(dbType), tables[0]);
//		CreateEntry(tables);
//	}
//
//	/**
//	 * 根据url和数据库名批量生成实体类
//	 * @param dbName 数据库名
//	 * @param url 数据库url地址
//	 * @param username 用户名
//	 * @param password 密码
//	 */
//	public CreateCommBean(String dbName, String url, String username, String password, boolean isDB) {
//		this(url, username, password);
//		if ("sqlserver".equals(dbType)) {
//
//		} else if ("oracle".equals(dbType)) {
//
//		} else if ("mysql".equals(dbType)) {
//			new CreateMysqlBean(conn, dbName);
//		}
//	}
//	/*-----------------------------构造结束-----------------------------*/
//
//	/**
//	 * 循环生成实体类
//	 */
//	private void CreateEntry(String[] tableNames) {
//		if (StrUtils.isNotBlank(tableNames)) {
//			for (String tableName : tableNames) {
//				CreateEntry(tableName, false);
//			}
//			try {
//				if (conn != null) {
//					conn.close();
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	/**
//	 * 生成单表的实例
//	 */
//	private void CreateEntry(String tableName, boolean isCloseConn) {
//		try {
//			PreparedStatement pstmt = conn.prepareStatement(sql_single);
//			pstmt.executeQuery();
//			ResultSetMetaData rsmd = pstmt.getMetaData();
//			int size = rsmd.getColumnCount();
//			colnames = new String[size];
//			String[] colTypes = new String[size];
//			Integer[] colSizes = new Integer[size];
//			Integer[] colScale = new Integer[size];
//			for (int i = 0; i < size; i++) {
//				rsmd.getCatalogName(i+1);
//				colnames[i] = rsmd.getColumnName(i+1);
//				colTypes[i] = rsmd.getColumnTypeName(i+1).toLowerCase();
//				colScale[i] = rsmd.getScale(i+1);
//				colSizes[i] = rsmd.getPrecision(i+1);
//			}
//			String content = parse(colTypes, colSizes, colScale, tableName);
//			FileWriter fw = new FileWriter(StrUtils.upperFirst(tableName) + ".java");
//			PrintWriter pw = new PrintWriter(fw);
//			pw.println(content);
//			pw.flush();
//			pw.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (isCloseConn) {
//				try {
//					if (null != conn) {
//						conn.close();
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	/**
//	 * 解析处理(生成实体类主体代码)
//	 */
//	public String parse(String[] colTypes, Integer[] colSizes,
//			Integer[] colScales, String tableName) {
//		StringBuffer sb = new StringBuffer();
//		sb.append("\r\nimport java.io.Serializable;\r\n");
//		processColnames(colTypes, colSizes, colScales, sb, tableName);
//		sb.append("public class " + StrUtils.upperFirst(tableName)
//				+ " implements Serializable {\r\n");
//		processAllAttrs(colTypes, colSizes, colScales, sb);
//		processAllMethod(colTypes, colScales, sb);
//		sb.append("}\r\n");
//		return sb.toString();
//	}
//
//	/**
//	 * 处理列名,把空格下划线'_'去掉,同时把下划线后的首字母大写 要是整个列在3个字符及以内,则去掉'_'后,不把"_"后首字母大写.
//	 * 同时把数据库列名,列类型写到注释中以便查看,
//	 */
//	private void processColnames(String[] colTypes, Integer[] colSizes,
//			Integer[] colScale, StringBuffer sb, String tableName) {
//		sb.append("\r\n/** " + tableName + "\r\n");
//		String colsiz = "";
//		for (int i = 0; i < colnames.length; i++) {
//			colsiz = colSizes[i] <= 0 ? ""
//					: (colScale[i] <= 0 ? "(" + colSizes[i] + ")"
//							: "(" + colSizes[i] + "," + colScale[i] + ")");
//			sb.append("\t" + colnames[i].toUpperCase() + " " + colTypes[i].toUpperCase() + colsiz
//					+ "\r\n");
//			colnames[i] = StrUtils.snake2Hump(colnames[i]);
//		}
//		sb.append("*/\r\n");
//	}
//
//	/**
//	 * 解析输出属性
//	 */
//	private void processAllAttrs(String[] colTypes, Integer[] colSizes,
//			Integer[] colScale, StringBuffer sb) {
//		sb.append("\tprivate static final long serialVersionUID = 1L;\r\n");
//		for (int i = 0; i < colnames.length; i++) {
//			sb.append("\tprivate " + DBUtils.sql2Java(colTypes[i], colScale[i]) + " " + colnames[i]
//					+ ";\r\n");
//		}
//		sb.append("\r\n");
//	}
//	
//	/**
//	 * 生成所有的方法
//	 */
//	private void processAllMethod(String[] colTypes,
//			Integer[] colScale, StringBuffer sb) {
//		for (int i = 0; i < colnames.length; i++) {
//			sb.append(String.format(CREATE_METHOD, StrUtils.upperFirst(colnames[i]),
//					DBUtils.sql2Java(colTypes[i], colScale[i]), colnames[i], colnames[i],
//					colnames[i], DBUtils.sql2Java(colTypes[i], colScale[i]),
//					StrUtils.upperFirst(colnames[i]), colnames[i]));
//		}
//	}
//}