package com.wy.db;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据库表基本信息
 * @author paradiseWy
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class DBTable {
	private String catalogName;// 所属数据库
	private String tableName;// 表名
	private Integer tableCount;// 表中字段
	private String tableComment;// 表注释
	private List<DBColumn> columns;// 表中所有字段
}