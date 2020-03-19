package com.wy.mail;

import com.wy.utils.StrUtils;

/**
 * 邮件类实体类
 * @author 万杨
 */
public class Mail {
	private String mailHost = "smtp.sina.com"; // 发送邮件服务器类型,一般是默认是smtp.sina.com,smtp.qq.com
	private Integer mailPort = 25; // 服务器端口,qq是465或576,其他默认是25
	private String username; // 发送者用户名
	private String password; // 发送的密码,qq是开启smtp服务的密钥,其他是邮箱密码
	private Boolean isSSL = false;// 是否开启ssl加密验证,默认不开启
	private String fromAddress; // 发送者邮件地址
	private String[] toAddress; // 接收者邮件地址
	private String subject; // 邮件主题
	private String content; // 邮件内容
	private String[] cc; // 抄送对象
	private String[] bcc; // 密送对象
	private String[] attachs; // 附件
	private String[] relateds;// 内嵌内容

	public Mail() {

	}

	public Mail(String username, String password, String fromAddress, String[] toAddress) {
		this.username = username;
		this.password = password;
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
	}

	public Mail(String username, String password, boolean isSSL, String fromAddress,
			String[] toAddress) {
		this(username, password, fromAddress, toAddress);
		this.isSSL = isSSL;
	}

	public Mail(String username, String password, boolean isSSL, String fromAddress,
			String[] toAddress, String subject, String content) {
		this(username, password, isSSL, fromAddress, toAddress);
		this.subject = subject;
		this.content = content;
	}

	public Mail(String username, String password, boolean isSSL, String fromAddress,
			String[] toAddress, String subject, String content, String[] ccs) {
		this(username, password, isSSL, fromAddress, toAddress, subject, content);
		this.cc = ccs;
	}

	public Mail(String username, String password, boolean isSSL, String fromAddress,
			String[] toAddress, String subject, String content, String[] ccs, String[] bccs) {
		this(username, password, isSSL, fromAddress, toAddress, subject, content, ccs);
		this.bcc = bccs;
	}

	public Mail(String username, String password, boolean isSSL, String fromAddress,
			String[] toAddress, String subject, String content, String[] ccs, String[] bccs,
			String[] attachs) {
		this(username, password, isSSL, fromAddress, toAddress, subject, content, ccs, bccs);
		this.attachs = attachs;
	}

	public Mail(String username, String password, boolean isSSL, String fromAddress,
			String[] toAddress, String subject, String content, String[] ccs, String[] bccs,
			String[] attachs, String[] relateds) {
		this(username, password, isSSL, fromAddress, toAddress, subject, content, ccs, bccs,
				attachs);
		this.relateds = relateds;
	}

	public static String checkParam(Mail bean) {
		if (bean == null) {
			return "参数对象不能为空";
		}
		if (StrUtils.isBlank(bean.getUsername())) {
			return "发送邮件的用户名不能为空";
		}
		if (StrUtils.isBlank(bean.getPassword())) {
			return "发送邮件密码不能为空";
		}
		if (StrUtils.isBlank(bean.getFromAddress())) {
			return "发送者邮件地址不能为空";
		}
		if (bean.getToAddress() == null || bean.getToAddress().length == 0) {
			return "收件人邮件地址不能为空";
		}
		if (StrUtils.isBlank(bean.getSubject())) {
			bean.setSubject("来自" + bean.getFromAddress() + "的邮件");
		}
		if (StrUtils.isBlank(bean.getContent())
				&& (bean.getAttachs() == null || bean.getAttachs().length == 0)
				&& (bean.getRelateds() == null || bean.getRelateds().length == 0)) {
			return "不能发送空邮件";
		}
		return null;
	}

	public String getMailHost() {
		return mailHost;
	}

	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	public Integer getMailPort() {
		return mailPort;
	}

	public void setMailPort(int mailPort) {
		this.mailPort = mailPort;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getIsSSL() {
		return isSSL;
	}

	public void setIsSSL(Boolean isSSL) {
		this.isSSL = isSSL;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String[] getToAddress() {
		return toAddress;
	}

	public void setToAddress(String[] toAddress) {
		this.toAddress = toAddress;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String[] getCc() {
		return cc;
	}

	public void setCc(String[] cc) {
		this.cc = cc;
	}

	public String[] getBcc() {
		return bcc;
	}

	public void setBcc(String[] bcc) {
		this.bcc = bcc;
	}

	public String[] getAttachs() {
		return attachs;
	}

	public void setAttachs(String[] attachs) {
		this.attachs = attachs;
	}

	public String[] getRelateds() {
		return relateds;
	}

	public void setRelateds(String[] relateds) {
		this.relateds = relateds;
	}

}
