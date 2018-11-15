package com.wy.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Java2SqlEnum {

	INTEGER(
			"Integer",
			new ArrayList<String>(Arrays.asList("integer", "int", "tinyint", "smallint"))),
	LONG("Long", new ArrayList<String>(Arrays.asList("long", "bigint"))),
	DOUBLE(
			"Double",
			new ArrayList<String>(Arrays.asList("float", "float precision", "double",
					"double precision", "real"))),
	BOOLEAN("Boolean", new ArrayList<String>(Arrays.asList("bit"))),
	STRING(
			"String",
			new ArrayList<String>(
					Arrays.asList("varchar", "varchar2", "nvarchar", "char", "nchar", "text"))),
	DATE("Date", new ArrayList<String>(Arrays.asList("datetime", "date", "timestamp", "time"))),
	BYTES("byte[]", new ArrayList<String>(Arrays.asList("blob"))),
	BIGDECIMAL(
			"BigDecimal",
			new ArrayList<String>(Arrays.asList("number", "numeric", "decimal")));

	private String javaType;
	private List<String> sqlTypes;

	Java2SqlEnum(String javaType, List<String> sqlTypes) {
		this.javaType = javaType;
		this.sqlTypes = sqlTypes;
	}

	public String getJavaType() {
		return this.javaType;
	}

	public List<String> getSqlType() {
		return this.sqlTypes;
	}
}