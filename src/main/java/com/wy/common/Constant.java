package com.wy.common;

/**
 * 公共参数
 *
 * @author ParadiseWY
 * @date 2018-08-31 15:08:37
 * @git {@link https://github.com/mygodness100}
 */
public interface Constant {

	String[] MONEY_NUM = new String[] { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾", "佰", "仟", "万", "亿" };

	String[] MONEY_UNIT = new String[] { "分", "角", "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆", "拾",
			"佰", "仟" };

	/**
	 * excel的sheet页中的最大行数
	 */
	int EXCEL_SHEET_MAX = 65535;

	/**
	 * exce导出时的默认文件名
	 */
	String EXCEL_FILE_NAME = "数据导出";

	/**
	 * excel文件不带后缀时的默认后缀
	 */
	String EXCEL_FILE_NAME_SUFFIX = "xls";

	/**
	 * 未知
	 */
	String STR_UNKNOWN = "UNKNOWN";

	/**
	 * 成功
	 */
	String STR_SUCCESS = "SUCCESS";

	/**
	 * 失败
	 */
	String STR_FAILURE = "FAILURE";

	/**
	 * 错误
	 */
	String STR_ERROR = "ERROR";
}