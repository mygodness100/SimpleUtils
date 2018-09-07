package com.wy.proxy;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class TestPoxy implements MethodInterceptor{

	@Override
	public Object intercept(Object obj, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
//		Class<?> clazz = method.getDeclaringClass();
		String tableName = "";
//		if(clazz.isAnnotationPresent(Table.class)) {
//			tableName = clazz.getAnnotation(Table.class).value();
//		}
		return methodProxy.invokeSuper(obj, new Object[] {tableName});
	}
}