package com.wy.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtils {
	private static final String DRIVERCLASS_MYSQL="com.mysql.jdbc.Driver";
	private static final String DRIVERCLASS_ORACLE="oracle.jdbc.driver.OracleDriver";
	private static final String DRIVERCLASS_SQLSERVER="com.microsoft.sqlserver.jdbc.SQLServerDriver";

	private static String driverClass;
	
	private static final HashMap<String,List<String>> SQL_TO_JAVA = new HashMap<String,List<String>>(){
		private static final long serialVersionUID = 1L;
		{
			put("Integer",new ArrayList<String>(Arrays.asList("Integer","int","tinyint")));
			put("Long",new ArrayList<String>(Arrays.asList("long","bigint","tinyint")));
			put("Double",new ArrayList<String>(Arrays.asList("float","float precision","double","double precision")));
			put("Boolean",new ArrayList<String>(Arrays.asList("bit")));
			put("String",new ArrayList<String>(Arrays.asList("varchar","varchar2","nvarchar","char","nchar","text")));
			put("Date",new ArrayList<String>(Arrays.asList("datetime","date","timestamp")));
			put("byte[]",new ArrayList<String>(Arrays.asList("blob")));
		}
	};
	private static final List<String> SPECIAL_NUMBER = new ArrayList<String>(Arrays.asList("number","numberic","real"));
	
	private DBUtils(){
		
	}
	
	/**
	 * 数据库匹配相应驱动
	 */
	public static Map<String,String> DRIVERCLASS = new HashMap<String,String>(){
		private static final long serialVersionUID = 1L;
		{
			put("mysql",DRIVERCLASS_MYSQL);
			put("oracle",DRIVERCLASS_ORACLE);
			put("sqlserver",DRIVERCLASS_SQLSERVER);
		}
	};
	
	/**
	 * 根据数据库类型选择驱动
	 */
	private static void getDriverClass(String url){
		for(String key : DRIVERCLASS.keySet()) {
			if(url.contains(key)) {
				driverClass =  DRIVERCLASS.get(key);
				break;
			}
		}
	}
	
	/**
	 * 根据数据库类型选择驱动
	 */
	public static String getDbType(String url){
		for(String key : DRIVERCLASS.keySet()) {
			if(url.contains(key)) {
				return key;
			}
		}
		return null;
	}
	
	public static Connection getConn(String url,String username,String password){
		getDriverClass(url);
		try {
			Class.forName(driverClass);
			Connection connection = DriverManager.
					getConnection(url,username,password);
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
	public static String sql2Java(String sqlType,Integer scale) {
		for(String key : SQL_TO_JAVA.keySet()) {
			if(SQL_TO_JAVA.get(key).contains(sqlType)) {
				return key;
			}else if(SPECIAL_NUMBER.contains(sqlType)) {
				if(scale != null && scale > 0) {
					return "Double";
				}else {
					return "Long";
				}
			}
		}
		return null;
	}
}
