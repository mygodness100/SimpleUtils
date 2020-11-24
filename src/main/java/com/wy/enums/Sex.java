package com.wy.enums;

import com.wy.common.PropConverter;

/**
 * 两种方式重写getValue():直接在枚举属性中实现抽象类;在枚举类中统一实现抽象类
 * 
 * @author ParadiseWY
 * @date 2020-11-23 15:48:40
 * @git {@link https://github.com/mygodness100}
 */
public enum Sex implements PropConverter {

	MALE("男") {

		@Override
		public Object getValue() {
			return "男";
		}
	},

	FEMALE("女") {

		@Override
		public Object getValue() {
			return "女";
		}
	},

	UNKNOWN("未知") {

		@Override
		public Object getValue() {
			return "未知";
		}
	};

	private String desc;

	private Sex(String desc) {
		this.desc = desc;
	}

	@Override
	public Object getValue() {
		return this.toString();
	}

	@Override
	public String toString() {
		return this.desc;
	}
}