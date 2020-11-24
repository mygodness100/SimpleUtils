package com.wy.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据库表字段原始属性
 * @author paradiseWy
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class DBColumn {

	private String columnName;// 数据库字段名
	private String columnAlias;// java实体类字段名
	private Integer length;// 字段长度
	private Integer scale;// 数字类型的精确度
	private String sqlType;// 数据库字段类型
	private String javaClass;// java字段class
	private String javaType;// java字段类型
	private Boolean isAutoAdd;// 是否自动增加
	private Integer isNullable;// 是否可为空,{@link ResultSetMetaData columnNoNulls等0不可,1可,2未知}
	private Boolean isReadOnly;// 是否只读
	private Boolean isCurrency;// 是否为货币类型
	private String columnComment;// 字段注释
}