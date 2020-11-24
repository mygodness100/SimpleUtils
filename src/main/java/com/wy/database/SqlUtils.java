package com.wy.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wy.utils.StrUtils;

/**
 * 只用来做一些简单的sql拼接
 * 
 * @author wanyang 2018年5月17日
 *
 */
public class SqlUtils {

	// 查询记录总数前置语句
	public static final String GET_TOTAL = "select count(*) from ";
	// sql语句
	private String sql = "";
	// sql拼接所需字段的值
	private List<String> selectColumns = new ArrayList<String>();
	// sql拼接不同条件的表连接
	private List<String> selectTables = new ArrayList<>();
	// 存放where条件的字段
	private List<String> columns = new ArrayList<>();
	// 存放where条件对应的取值
	private List<Object> values = new ArrayList<>();
	// sql拼接group by的值
	private List<String> groups = new ArrayList<>();
	// 升序
	private static final String ORDER_ASC = " order by %s asc ";
	// 降序
	private static final String ORDER_DESC = " order by %s desc ";

	public SqlUtils() {

	}

	public SqlUtils(String sql) {
		this.sql = sql;
	}

	/**
	 * sql语句需要查询的字段拼接
	 */
	public SqlUtils addColumns(String... columns) {
		selectColumns.addAll(Arrays.asList(columns));
		return this;
	}

	public SqlUtils innerTable(String table) {
		selectTables.add(table);
		return this;
	}

	public SqlUtils leftTable(String table) {
		selectTables.add(table);
		return this;
	}

	public SqlUtils rightTable(String table) {
		selectTables.add(table);
		return this;
	}

	/**
	 * sql语句的where子句中添加相等的字符串条件,可链式调用
	 */
	public SqlUtils andStrEq(String column, String val) {
		if (StrUtils.isNotBlank(val)) {
			columns.add(column + "= ?");
			values.add(val);
		}
		return this;
	}

	/**
	 * sql语句的where子句中添加相等的数字条件,可链式调用
	 */
	public SqlUtils andNumEq(String column, Number val) {
		if (val != null) {
			columns.add(column + "= ?");
			values.add(val);
		}
		return this;
	}

	/**
	 * sql语句的where子句中添加相似的字符串条件,可链式调用
	 */
	public SqlUtils andLike(String column, String val) {
		if (StrUtils.isNotBlank(val)) {
			columns.add(column + " like ? ");
			values.add("%" + val + "%");
		}
		return this;
	}

	/**
	 * sql语句中添加为null的条件
	 */
	public SqlUtils isNull(String column) {
		if (StrUtils.isNotBlank(column)) {
			columns.add(column + " is null");
		}
		return this;
	}

	/**
	 * sql语句中添加为null的条件
	 */
	public SqlUtils isNotNull(String column) {
		if (StrUtils.isNotBlank(column)) {
			columns.add(column + " is not null");
		}
		return this;
	}

	/**
	 * sql语句中添加为null的条件
	 */
	public SqlUtils groupBy(String column) {
		if (StrUtils.isNotBlank(column)) {
			groups.add(column);
		}
		return this;
	}

	/**
	 * 拼接sql语句为完整的sql表达式
	 */
	public String makeSql() {
		List<String> desCols = this.getColumns();
		if (desCols == null || desCols.isEmpty()) {
			if (!this.getGroups().isEmpty()) {
				return sql + " where 1=1 " + makeGroup(this.getGroups());
			}
			return sql + " where 1=1 ";
		} else {
			if (!this.getGroups().isEmpty()) {
				return sql + " where 1=1 and " + String.join(" and ", desCols) + makeGroup(this.getGroups());
			}
			return sql + " where 1=1 and " + String.join(" and ", desCols);
		}
	}

	private String makeGroup(List<String> groups) {
		return " group by " + String.join(",", groups);
	}

	/**
	 * sql升序
	 */
	public String asc(String column) {
		if (StrUtils.isNotBlank(column)) {
			return makeSql() + String.format(ORDER_ASC, column);
		}
		return makeSql();
	}

	/**
	 * sql降序
	 */
	public String desc(String column) {
		if (StrUtils.isNotBlank(column)) {
			return makeSql() + String.format(ORDER_DESC, column);
		}
		return makeSql();
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<String> getSelectColumns() {
		return selectColumns;
	}

	public void setSelectColumns(List<String> selectColumns) {
		this.selectColumns = selectColumns;
	}

	public List<String> getSelectTables() {
		return selectTables;
	}

	public void setSelectTables(List<String> selectTables) {
		this.selectTables = selectTables;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public List<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
		this.values = values;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
}