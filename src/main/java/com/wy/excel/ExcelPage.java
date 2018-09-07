package com.wy.excel;

import java.util.List;

/**
 * excel所有行对象集合
 * @author 万杨
 */
public class ExcelPage {
	
	private List<ExcelRow> rowDatas;		//当前页所有行数据

	public List<ExcelRow> getRowDatas() {
		return rowDatas;
	}

	public void setRowDatas(List<ExcelRow> rowDatas) {
		this.rowDatas = rowDatas;
	}

	public Integer getSize() {
		return rowDatas.size();
	}
}
