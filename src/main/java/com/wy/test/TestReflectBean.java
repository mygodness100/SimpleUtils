package com.wy.test;

import com.wy.enums.Sex;
import com.wy.excel.annotation.ExcelColumn;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestReflectBean {

	public static void main(String[] args) {
	}

	private String username;

	private Integer age;

	private Character sex;

	@ExcelColumn(enumConverter = Sex.class)
	private String address;

	private String password;

	public TestReflectBean() {

	}

	public TestReflectBean(String username) {
		this.username = username;
	}

	public static void testStatic() {
		System.out.println("static");
	}

	public void test() {
		System.out.println(111111);
	}

	public void test(String str) {
		System.out.println(str);
	}
}