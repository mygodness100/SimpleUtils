package com.wy.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.cglib.proxy.Enhancer;

/**
 * @author 万杨
 */
public class ProxyUtils implements InvocationHandler{

	/**
	 * spring的反射代理
	 * @param args
	 */
	public static void main(String[] args) {
		TestPoxy t1 = new TestPoxy();
		Enhancer e1 = new Enhancer();
		e1.setSuperclass(TestPojo.class);
		e1.setCallback(t1);
		TestPojo p = (TestPojo)e1.create();
		p.setUsername("test1111");
		System.out.println("33333333"+p.getUsername());
	}

	/**
	 * 原生的代理,需要实现原生的接口
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return method.invoke(src, args);
	}
	
	private Object src;
	
	private ProxyUtils(Object src) {
		this.src = src;
	}
	
	public static Object getProxy(Object src) {
		return Proxy.newProxyInstance(ProxyUtils.class.getClassLoader(), src.getClass().getInterfaces(), new ProxyUtils(src));
	}
}
