package com.wy.enums;

import com.wy.common.PropConverter;

/**
 * 所有只有2种状态的字段比较或者0,1赋值:0非,1是
 * 
 * @author ParadiseWY
 * @date 2020年4月7日 下午4:21:12
 */
public enum BooleanEnum implements PropConverter {
	YES() {

		@Override
		public Object getValue() {
			return "是";
		}

	},
	NO() {

		@Override
		public Object getValue() {
			return "否";
		}
	}
}