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
public class Result implements Serializable {
	private static final long serialVersionUID = 1L;

	private int code;
	private String msg;
	private Object data;
	private int pageIndex;
	private int pageSize;
	private int total;

	public static Result ok() {
		return ok(null);
	}

	public static Result ok(Object t) {
		return ok(Internation.getStr("msg_success"), t);
	}

	public static Result ok(String message, Object t) {
		return result(1, message, t);
	}

	public static Result ok(int code, Object t) {
		return result(code, Internation.getStr("msg_success"), t);
	}

	public static Result error() {
		return error(null);
	}

	public static Result error(String message) {
		return error(message, null);
	}

	public static Result error(String message, Object t) {
		return result(0, message, t);
	}

	public static Result error(int code, String message) {
		return result(code, message, null);
	}

	public static Result error(Object t, int code) {
		return result(code, Internation.getStr("msg_fail"), t);
	}

	public static Result result(boolean flag) {
		return flag ? ok() : error();
	}

	public static Result result(Object t) {
		return Objects.isNull(t) ? error() : ok(t);
	}

	public static Result result(TipEnum tip) {
		return result(tip.getCode(), tip.getMsg(), null);
	}

	public static Result result(int code, String message, Object t) {
		return Result.builder().data(t).code(code)
				.msg(StrUtils.isBlank(message)
						? (code > 0 ? Internation.getStr("msg_success")
								: Internation.getStr("msg_fail"))
						: message)
				.build();
	}

	public static Result page(Object t, int pageIndex, int pageSize, int total) {
		return Objects.isNull(t) ? page(0, null, null, 0, 0, 0)
				: page(1, null, t, pageIndex, pageSize, total);
	}

	public static Result page(int code, String message, Object t, int pageIndex, int pageSize,
			int total) {
		return Result.builder()
				.msg(StrUtils.isBlank(message)
						? (code > 0 ? Internation.getStr("msg_success")
								: Internation.getStr("msg_fail"))
						: message)
				.code(code).data(t).pageIndex(pageIndex).pageSize(pageSize).total(total).build();
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