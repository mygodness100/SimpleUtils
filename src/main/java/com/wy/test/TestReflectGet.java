//package com.wy.test;
//
//import java.beans.BeanInfo;
//import java.beans.Introspector;
//import java.beans.PropertyDescriptor;
//import java.lang.reflect.Array;
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//
//import org.apache.commons.beanutils.BeanUtils;
//import org.springframework.stereotype.Controller;
//
//
///**
// * 反射的机制原理以及应用
// * @author wanyang
// */
////@TestAnnotation(color="red",value="213")//若注解类里有多个方法,则需要全部赋值,若是有赋值了default,则也可不写
//public class TestReflectGet {
//	public static void main(String[] args) throws Exception {
//		System.out.println(TestReflectGet.class.getName());
//		TestReflectBean t1 = new TestReflectBean();
//		t1.setUsername("wanyang");
//		t1.setAddress("武汉");
//		Field[] fields = t1.getClass().getFields();//获得可见的字段,获得的字段不是对象身上,而是类上的
//		System.out.println(fields);
// 		Field[] field =  t1.getClass().getDeclaredFields();//获得所有的字段,不管是否可见,获得的字段不是对象身上,而是类上的
//		for(Field test : field){
//			test.setAccessible(true);//让private的字段也可见
//			if(test.getType() == String.class){//getType获得字节码的类型,字节码都是用等号,字节码只有一份
//				String str = (String)test.get(t1);//从指定的对象中获取字段的值
//				System.out.println(str);
//				test.set(t1, "huangm");//重新设置字段的值
//				System.out.println(test.get(t1));
//			}
//		}
//		System.out.println(t1.getClass() == TestReflectBean.class);//true
//		//获得所有的构造函数
//		Constructor<?>[] constructors = t1.getClass().getConstructors();
//		System.out.println(constructors);
//		//根据参数的类型获得指定的构造函数
//		Constructor< ? extends TestReflectBean> constructor = t1.getClass().getConstructor();
//		System.out.println("1111111"+constructor);
//		Constructor<? extends TestReflectBean> constructor2 = t1.getClass().getConstructor(String.class);
//		System.out.println("222222"+constructor2);
//		//若是利用对象中本没有的构造生成构造的class字节码,报错
////		Constructor<? extends TestReflectBean> constructor3 = t1.getClass().getConstructor(String.class,String.class);//错误
////		System.out.println("333333333"+constructor3);
//		//利用构造器实例化对象,且实例化的对象不是一个对象,实例化必须和生成的构造class类有相同的参数,否则报错
////		TestReflectBean newInstance = constructor2.newInstance();//错误,应该用constructor1
//		TestReflectBean newInstance = constructor2.newInstance("uwername");//正确
//		System.out.println("54555"+newInstance);
//		Method method1 = TestReflectBean.class.getMethod("test");//获得指定的方法
//		method1.invoke(t1);//在具体的对象上调用方法
//		Method method2 = TestReflectBean.class.getMethod("test",String.class);//可传参数确定方法
//		method2.invoke(t1,"111");
//		Method method3 = TestReflectBean.class.getMethod("testStatic");
//		method3.invoke(null);//不传具体的对象表明这个方法是静态方法
//		int[] arrint = new int[] {1,2,3,5,6,8};//数组的反射
//		Class<? extends int[]> class1 = arrint.getClass();
//		System.out.println(class1.isArray());//判断是否为数组
//		int len = Array.getLength(arrint);//获得数组长度
//		for(int i=0;i<len;i++){
//			System.out.println(Array.get(arrint, i));
//		}
//		
//		//只对get,set的javabean使用,内省,主要是解析javabean
//		TestReflectBean t2 = new TestReflectBean();
//		//获得内省的描述函数,属性名,类对象
//		PropertyDescriptor pd = new PropertyDescriptor("username", t2.getClass());
//		//获得只读方法,就是get方法
//		Method method = pd.getReadMethod();
//		//执行方法
//		Object invoke = method.invoke(t2);
//		System.out.println(invoke);
//		//获得写方法,重写其中的值
//		Method method4 =pd.getWriteMethod();
//		method4.invoke(t2, "username");
//		
//		//获得某个bean的所有属性,属于java里的底层类
//		BeanInfo beanInfo = Introspector.getBeanInfo(TestReflectBean.class);
//		//获得bean的所有属性
//		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
//		//迭代所有属性,进行操作
//		for(PropertyDescriptor p : propertyDescriptors){
//			System.out.println(p.getName());//属性名称
////			System.out.println(p.getDisplayName());//和属性名称是一样的,但不知道有什么区别
//			System.out.println(p.getPropertyType());//获得属性的类型字节码
//		}
//		
//		//BeanUtils是apache专门用来处理javabean的工具类
//		System.out.println(BeanUtils.getProperty(t1, "username"));
//		BeanUtils.setProperty(t1, "username", "this is a test");
//		
//		boolean flag =TestReflectBean.class.isAnnotationPresent(Controller.class);//判断是否含有某个注解
//		if(flag) {
//			Controller a = TestReflectBean.class.getAnnotation(Controller.class);
//			System.out.println(a);
//		}
//	}
//}
