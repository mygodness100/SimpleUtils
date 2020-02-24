package com.wy.enums;

import com.wy.common.Internation;
import com.wy.common.TipCode;

public enum TipEnum implements TipCode {

	UNKNOWN(-1, "msg_unknown"),
	FAIL(0, "msg_fail"),
	SUCCESS(1, "msg_success"),
	
	TIP_LOGIN_FAIL(-10000, "msg_login_fail"),
	TIP_LOGIN_FAIL_USERNAME(-9999, "msg_login_fail_username"),
	TIP_LOGIN_FAIL_PASSWORD(-9998, "msg_login_fail_password"),
	TIP_LOGIN_FAIL_TIMEOUT(-9997, "msg_login_fail_timeout"),

	TIP_USER_NOT_EXIST(-9996, "msg_user_not_exist"),
	TIP_USER_NOT_DISTRIBUTE_ROLE(-9995, "msg_user_not_distribute_role"),
	
	TIP_AUTH_TIME_EMPTY(-9994, "msg_timestamp_null"),
	TIP_AUTH_TIME_ERROR(-9993, "msg_timestamp_error"),
	TIP_AUTH_EMPTY(-9992, "msg_auth_empty"),
	TIP_AUTH_FAIL(-9991, "msg_auth_fail"),
	TIP_AUTH_DENIED(-9998,"msg_auth_denied"),

	TIP_ROLE_ERROR(-9000, "msg_role_not_exist"),

	TIP_PARAM(-8000, "msg_param_error"),
	TIP_PARAM_EMPTY(-7999, "msg_param_empty"),
	TIP_PARAM_INVALID(-10001,"msg_param_invalid"),

	TIP_DB_CREATE(-7000, "msg_db_create_fail"),
	TIP_DB_DELETE(-6999, "msg_db_delete_fail"),
	TIP_DB_MODIFY(-6998, "msg_db_modify_fail"),
	TIP_DB_QUERY(-6997, "msg_db_query_fail"),

	TIP_SYS_ERROR(-1000, "msg_sys_error"),
	TIP_SYS_MAINTAIN(-999, "msg_sys_maintain"),
	TIP_SYS_BUSY(-998,"msg_sys_busy");

	private Integer code;

	private String key;

	TipEnum(Integer code, String key) {
		this.code = code;
		this.key = key;
	}

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public String getMsg() {
		return Internation.getStr(key);
	}
}