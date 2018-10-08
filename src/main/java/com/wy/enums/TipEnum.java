package com.wy.enums;

public enum TipEnum {
	TIP_LOGIN(-10000,"登录失败,用户名或密码错误,请重试"),
	TIP_LOGIN_USERNAME(-9999,"用户不存在"),
	TIP_LOGIN_PASSWORD(-9998,"密码错误"),
	TIP_LOGIN_USERPWD(-9997,"用户名或密码错误"),
	TIP_LOGIN_TIMEOUT(-9996,"登录超时,请重新登录!"),
	
	TIP_ROLE_ERROR(-9000,"该角色不存在或已经删除!"),
	
	TIP_PARAM(-8000,"参数错误"),
	TIP_PARAM_EMPTY(-7999,"参数不能为空"),
	
	TIP_DB_CREATE(-7000,"新增失败"),
	TIP_DB_DELETE(-6999,"删除失败"),
	TIP_DB_UPDATE(-6998,"更新失败"),
	TIP_DB_SELECT(-6997,"查询失败"),
	
	TIP_EX_COMMON(-1000,"系统内部错误,请联系管理员"),
	
	TIP_SYS_SERVICE(-999,"系统维护中,请耐心等待");
	
	private Integer errCode;
	private String errMsg;
	
	TipEnum(Integer errCode,String errMsg){
		this.errCode = errCode;
		this.errMsg = errMsg;
	}
	
	public Integer getErrCode() {
		return errCode;
	}
	
	public String getErrMsg() {
		return errMsg;
	}
}