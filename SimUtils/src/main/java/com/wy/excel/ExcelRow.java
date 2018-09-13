package com.wy.excel;

import java.util.List;

/**
 * excel的行对象
 * @author 万杨
 */
public class ExcelRow {

	private List<String> colDatas;//当前行的所有数据

	public List<String> getColDatas() {
		return colDatas;
	}

	public void setColDatas(List<String> colDatas) {
		this.colDatas = colDatas;
	}
	
	public int getSize() {
		return colDatas.size();
	}
}
