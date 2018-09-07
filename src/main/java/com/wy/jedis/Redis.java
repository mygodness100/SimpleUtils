package com.wy.jedis;

import java.util.Map;

import com.wy.utils.PropUtils;

import redis.clients.jedis.Jedis;

public class Redis {
	private static  Map<String,String> CONFIG = PropUtils.getProper("redis");
	public static Jedis jedis = new Jedis(CONFIG.get("url"),Integer.valueOf(CONFIG.get("port")));
	
	public static void main(String[] args) {
		jedis.set("test", "11111");
		System.out.println(jedis.get("test"));
	}
}
