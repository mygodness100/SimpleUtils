package com.wy.excel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * excel所有sheet页数据
 * @author 万杨
 */
@Getter
@Setter
public class ExcelModel {

	private List<ExcelPage> pageDatas; // 所有页面数据集合

	public int getSize() {
		return pageDatas.size();
	}
}