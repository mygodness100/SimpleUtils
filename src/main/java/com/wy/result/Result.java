package com.wy.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.wy.common.Internation;
import com.wy.enums.TipEnum;
import com.wy.utils.MapUtils;
import com.wy.utils.StrUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Result<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	private int code;

	private String msg;

	private T data;

	private int pageIndex;

	private int pageSize;

	private long total;

	public static <T> Result<T> ok() {
		return ok(null);
	}

	public static <T> Result<T> ok(T t) {
		return ok(Internation.getStr("msg_success"), t);
	}

	public static <T> Result<T> ok(String msg, T t) {
		return result(1, msg, t);
	}

	public static <T> Result<T> ok(int code, T t) {
		return result(code, Internation.getStr("msg_success"), t);
	}

	public static <T> Result<T> error() {
		return error(null);
	}

	public static <T> Result<T> error(String msg) {
		return error(0, msg);
	}

	public static <T> Result<T> error(int code, String msg) {
		return error(code, msg, null);
	}

	public static <T> Result<T> error(int code, T t) {
		return error(code, null, t);
	}

	public static <T> Result<T> error(int code, String msg, T t) {
		return result(code, msg, t);
	}

	public static <T> Result<T> result(boolean flag) {
		return flag ? ok() : error();
	}

	public static <T> Result<T> result(T t) {
		return Objects.isNull(t) ? error() : ok(t);
	}

	public static <T> Result<T> result(TipEnum tip) {
		return result(tip, null);
	}

	public static <T> Result<T> result(TipEnum tip, T t) {
		return result(tip.getCode(), tip.getMsg(), t);
	}

	public static <T> Result<T> result(int code, String msg, T t) {
		return Result.<T>builder().code(code)
				.msg(StrUtils.isBlank(msg)
						? (code > 0 ? Internation.getStr("msg_success") : Internation.getStr("msg_fail"))
						: msg)
				.data(t).build();
	}

	public static <T> Result<T> page(T t, int pageIndex, int pageSize, long total) {
		return page(1, null, t, pageIndex, pageSize, total);
	}

	public static <T> Result<T> page(T t) {
		return page(1, null, t, 0, 0, 0);
	}

	public static <T> Result<T> page(int code, String msg, T t, int pageIndex, int pageSize, long total) {
		return Result.<T>builder().code(code)
				.msg(StrUtils.isBlank(msg)
						? (code > 0 ? Internation.getStr("msg_success") : Internation.getStr("msg_fail"))
						: msg)
				.data(t).pageIndex(pageIndex).pageSize(pageSize).total(total).build();
	}

	/**
	 * 将从数据库自定义sql语句取出的结果集的key转换为驼峰形式
	 * @param data 下划线形式的结果集
	 * @return 驼峰形式的结果集
	 */
	public static Map<String, Object> snake2Hump(Map<String, Object> data) {
		if (MapUtils.isBlank(data)) {
			return null;
		}
		Map<String, Object> result = new HashMap<>();
		for (Map.Entry<String, Object> entry : data.entrySet()) {
			result.put(StrUtils.snake2Hump(entry.getKey()), entry.getValue());
		}
		data = null;
		return result;
	}

	/**
	 * 将从数据库自定义sql语句取出的结果集的key转换为驼峰形式
	 * @param datas 下划线形式的结果集
	 * @return 驼峰形式的结果集
	 */
	public static List<Map<String, Object>> snake2Hump(List<Map<String, Object>> datas) {
		if (datas == null || datas.isEmpty()) {
			return null;
		}
		List<Map<String, Object>> result = new ArrayList<>();
		for (Map<String, Object> data : datas) {
			Map<String, Object> res = snake2Hump(data);
			result.add(res);
		}
		datas = null;
		return result;
	}
}