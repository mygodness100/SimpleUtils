package com.wy.excel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * excel单个sheet页面所有行数据
 * @author paradiseWy
 */
@Getter
@Setter
public class ExcelPage {

	private List<ExcelRow> rowDatas; // 当前页所有行数据

	public int getSize() {
		return rowDatas.size();
	}

	/**
	 * 每一行的数据
	 * @author paradiseWy
	 */
	@Getter
	@Setter
	public static class ExcelRow {
		private List<Object> colDatas;

		public int getSize() {
			return colDatas.size();
		}
	}
}