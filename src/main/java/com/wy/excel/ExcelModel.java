package com.wy.excel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * excel所有sheet页数据
 * 
 * @author ParadiseWY
 * @date 2020-11-23 16:09:56
 * @git {@link https://github.com/mygodness100}
 */
@Getter
@Setter
public class ExcelModel {

	/**
	 * 所有页面数据集合
	 */
	private List<ExcelPage> pageDatas;

	public int getSize() {
		return pageDatas.size();
	}
}