package com.wy.enums;

import com.wy.common.Internation;

public enum TipEnum {

	UNKNOWN(-1, "msg_unknown"),
	FAIL(0, "msg_fail"),
	SUCCESS(1, "msg_success"),

	AUTH_TIME_EMPTY(10000, "msg_timestamp_null"),
	AUTH_TIME_ERROR(10001, "msg_timestamp_error"),
	AUTH_EMPTY(10002, "msg_valid_empty"),
	AUTH_FAIL(10003, "msg_valid_fail"),

	TIP_LOGIN(-10000, "msg_login_fail"),
	TIP_LOGIN_USERNAME(-9999, "msg_login_fail_username"),
	TIP_LOGIN_PASSWORD(-9998, "msg_login_fail_password"),
	TIP_LOGIN_TIMEOUT(-9997, "msg_login_fail_timeout"),
	TIP_USER_NOT_EXIST(-9996,"msg_user_not_exist"),
	TIP_USER_NOT_DISTRIBUTE_ROLE(-9995,"msg_user_not_distribute_role"),

	TIP_ROLE_ERROR(-9000, "msg_role_not_exist"),

	TIP_PARAM(-8000, "msg_param_error"),
	TIP_PARAM_EMPTY(-7999, "msg_param_empty"),

	TIP_DB_CREATE(-7000, "msg_db_create_fail"),
	TIP_DB_DELETE(-6999, "msg_db_delete_fail"),
	TIP_DB_MODIFY(-6998, "msg_db_modify_fail"),
	TIP_DB_QUERY(-6997, "msg_db_query_fail"),

	TIP_SYS_ERROR(-1000, "msg_sys_error"),
	TIP_SYS_MAINTAIN(-999, "msg_sys_maintain");

	private Integer code;
	private String key;

	TipEnum(Integer code, String key) {
		this.code = code;
		this.key = key;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return Internation.getStr(key);
	}
}