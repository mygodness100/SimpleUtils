package com.wy.enums;

/**
 * 一些系统参数
 * @author wanyang 2018年3月25日
 */
public enum SysEnum {
	JAVA_VERSION("java.version"),// Java 执行时环境版本号
	JAVA_VENDOR("java.vendor"),// Java 执行时环境供应商
	JAVA_VENDOR_URL("java.vendor.url"),// Java供应商的 URL
	JAVA_HOME("java.home"),// Java 安装文件夹
	JAVA_VM_SPECIFICATION_VERSION("java.vm.specification.version"),// Java 虚拟机规范版本号
	JAVA_VM_SPECIFICATION_VENDOR("java.vm.specification.vendor"),// Java 虚拟机规范供应商
	JAVA_VM_SPECIFICATION_NAME("java.vm.specification.name"),// Java 虚拟机规范名称
	JAVA_VM_VERSION("java.vm.version"),// Java 虚拟机实现版本号
	JAVA_VM_VENDOR("java.vm.vendor"),// Java 虚拟机实现供应商
	JAVA_VM_NAME("java.vm.name"),// java.vm.name Java 虚拟机实现名称
	JAVA_SPECIFICATION_VERSION("java.specification.version"),// Java 执行时环境规范版本号
	 JAVA_SPECIFICATION_VENDOR("java.specification.vendor"),// Java 执行时环境规范供应商
	 JAVA_SPECIFICATION_NAME("java.specification.name"),// Java 执行时环境规范名称
	 JAVA_CLASS_VERSION("java.class.version"),// java.class.version Java 类格式版本号号
	 JAVA_CLASS_PATH("java.class.path"),// java.class.path Java 类路径
	 JAVA_LIBRARY_PATH("java.library.path"),// java.library.path 载入库时搜索的路径列表
	 JAVA_IO_TMPDIR("java.io.tmpdir"),// java.io.tmpdir 默认的暂时文件路径
	 JAVA_COMPILER("java.compiler"),// java.compiler 要使用的 JIT 编译器的名称
	 JAVA_EXT_DIRS("java.ext.dirs"),// java.ext.dirs 一个或多个扩展文件夹的路径
	 OS_NAME("os.name"),// os.name 操作系统的名称
	 OS_ARCH("os.arch"),// os.arch 操作系统的架构
	 OS_VERSION("os.version"),// os.version 操作系统的版本号
	 FILE_SEPARATOR("file.separator"),// file.separator 文件分隔符（在 UNIX 系统中是“/”）
	 PATH_SEPARATOR("path.separator"),// path.separator 路径分隔符（在 UNIX 系统中是“:”）
	 LINE_SEPARATOR("line.separator"),// line.separator 行分隔符（在 UNIX 系统中是“/n”）
	 USER_NAME("user.name"),// user.name 用户的账户名称
	 USER_HOME("user.home"),// user.home 用户的主文件夹
	 USER_DIR("user.dir");// user.dir 用户的当前工作文件夹
	
	private String value;
	
	SysEnum(String value){
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}