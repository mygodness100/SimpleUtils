package com.wy.test;

public class TestReflectBean {
	private String username;
	private Integer age;
	private Character sex;
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
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Character getSex() {
		return sex;
	}
	public void setSex(Character sex) {
		this.sex = sex;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
