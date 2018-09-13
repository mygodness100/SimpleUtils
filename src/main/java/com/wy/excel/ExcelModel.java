package com.wy.excel;

import java.util.List;

/**
 * excel的模型
 * @author 万杨
 */
public class ExcelModel {
	
	private List<ExcelPage> pageDatas;		//所有页面数据集合

	public List<ExcelPage> getPageDatas() {
		return pageDatas;
	}

	public void setPageDatas(List<ExcelPage> pageDatas) {
		this.pageDatas = pageDatas;
	}
	
	public Integer getSize() {
		return pageDatas.size();
	}
}
