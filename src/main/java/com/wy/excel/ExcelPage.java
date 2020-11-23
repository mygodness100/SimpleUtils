package com.wy.excel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * excel单个sheet页面所有行数据
 * 
 * @author ParadiseWY
 * @date 2020-11-23 16:10:31
 * @git {@link https://github.com/mygodness100}
 */
@Getter
@Setter
public class ExcelPage {

	/**
	 * 当前页所有行数据
	 */
	private List<ExcelRow> rowDatas;

	public int getSize() {
		return rowDatas.size();
	}

	/**
	 * 每一行的数据
	 * 
	 * @author ParadiseWY
	 * @date 2020-11-23 16:10:58
	 * @git {@link https://github.com/mygodness100}
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