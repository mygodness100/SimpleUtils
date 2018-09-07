package com.wy.common;

/**
 * 信息提示
 * @author wanyang 2018年2月23日
 */
public interface Tips {
	String TIP_LOGIN = "登录失败,用户名或密码密码,请重试";
	String TIP_LOGIN_USERNAME = "用户不存在";
	String TIP_LOGIN_PASSWORD = "密码错误";
	String TIP_LOGIN_USERPWD = "用户名或密码错误";
	String TIP_LOGIN_TIMEOUT = "登录超时,请重新登录!";
	
	String TIP_PARAM = "参数错误";
	String TIP_PARAM_EMPTY = "参数不能为空";
	
	String TIP_DB_CREATE = "新增失败";
	String TIP_DB_DELETE="删除失败";
	String TIP_DB_UPDATE="更新失败";
	String TIP_DB_SELECT="查询失败";
	
	String TIP_EX_COMMON="系统内部错误,请联系管理员";
	
	String TIP_SYS_SERVICE="系统维护中,请耐心等待";
}