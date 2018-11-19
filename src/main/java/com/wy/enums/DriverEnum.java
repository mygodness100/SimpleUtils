package com.wy.enums;

public enum DriverEnum {

	MYSQL("mysql", "com.mysql.jdbc.Driver"),
	ORACLE("oracle", "oracle.jdbc.driver.OracleDriver"),
	SQLSERVER("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
	SQLITE("sqlite", "org.sqlite.JDBC"),
	POSTGRESQL("postgresql", "org.postgresql.Driver"),
	DB2("db2", "com.ibm.db2.jdbc.app.DB2Driver");

	private String name;
	private String driverClass;

	DriverEnum(String name, String driverClass) {
		this.name = name;
		this.driverClass = driverClass;
	}

	public String getDriverClass() {
		return this.driverClass;
	}

	/**
	 * 根据数据库url拿到数据库匹配的驱动
	 * @param url 数据库地址
	 * @return 数据库驱动
	 */
	public static String getDriverClass(String url) {
		DriverEnum[] values = DriverEnum.values();
		for (DriverEnum value : values) {
			if (url.indexOf(value.name) != -1) {
				return value.driverClass;
			}
		}
		return null;
	}
}