package com.wy.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 邮箱服务器验证,qq邮箱开启验证需要的是开启smtp服务时的加密密钥,其他邮箱是登录密码
 * @author 万杨
 */
public class MailAuth extends Authenticator{
	String username = null;
	String password = null;
	
	public MailAuth() {

	}
	
	public MailAuth(String username,String password) {
		this.username = username;
		this.password = password;
	}
	
	protected PasswordAuthentication getPasswordAuthentication(){
		return new PasswordAuthentication(username, password);
	}
}
