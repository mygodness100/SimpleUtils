package com.wy.test;

import java.util.List;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * @author 万杨 Administrator 2017年6月20日 下午5:09:17 TODO
 */
public class JedisTest {
	private static final String MONITOR = "MONITOR";
	private static final Integer PORT = 6379;
	private static Jedis JEDIS = new Jedis("localhost", PORT);

	public static void main(String[] args) {
		try {
			// 监控monitor对象
			JEDIS.watch(MONITOR);
			// 从jedis获得的是字符串
			String value = JEDIS.get(MONITOR);
			Integer valInt = Integer.valueOf(value);
			String userInfo = UUID.randomUUID().toString();
			if (valInt < 10) {
				// 开启redis事务,乐观锁
				Transaction tx = JEDIS.multi();
				// 将key值加1,若可以不存在则设置为0,若value类型错误,则返回错误
				tx.incr(MONITOR);
				// 提交事务,若此时monitor的值改变则会失败,返回null,若没有改变则成功,返回不为null
				List<Object> exec = tx.exec();
				// 当monitor的值改变的时候获取到的值就为空,即失败
				if (exec == null) {
					JEDIS.sadd("fail", userInfo);
				} else {
					JEDIS.sadd("success", userInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JEDIS.unwatch();
			JEDIS.close();
		}
	}
}
