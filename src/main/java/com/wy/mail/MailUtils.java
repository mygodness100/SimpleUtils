package com.wy.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.wy.common.Encoding;
import com.wy.result.Result;

/**
 * 邮件收发类,收发之前需要将邮箱的smtp.pop设置打开,需要mail.jar和spring的相关包
 * 
 * @author 万杨 FIXME
 */
public class MailUtils {

	private static String MAIL_HOST = "smtp.qq.com";// 邮箱格式
	// private static String SEND_HOST = "smtp.sina.com";// 邮箱发送服务器
	// private static String ACCEPT_HOST = "pop.sina.com";// 邮箱服务器
	private static String USER = "test01";// 用户名
	private static String PASSWORD = "123456";// 密码
	private static String FROM_MAIL = USER + MAIL_HOST;// 发送方邮件地址
	private static String TO_MAIL = "to@sina.com";// 接收方邮件地址
	private static String CC_MAIL = USER + MAIL_HOST;// 抄送方邮箱地址
	private static String BCC_MAIL = USER + MAIL_HOST;// 密送方邮箱地址
	private static JavaMailSenderImpl sender = new JavaMailSenderImpl();

	private static void init() {
		Properties prop = new Properties();
		prop.setProperty("mail.transport.protocol", "stmp");
		prop.setProperty("mail.host", MAIL_HOST);
		prop.setProperty("mail.smtps.ssl.enable", "true");
		prop.setProperty("mail.smtp.auth", "true");
		sender.setUsername(USER);
		sender.setPassword(PASSWORD);
		sender.setJavaMailProperties(prop);
	}

	/**
	 * 发送文本邮件
	 */
	public static void sendTextMail() {
		try {
			init();
			SimpleMailMessage smm = new SimpleMailMessage();
			smm.setFrom(FROM_MAIL);
			smm.setTo(TO_MAIL);
			smm.setReplyTo(FROM_MAIL);
			smm.setCc(CC_MAIL);
			smm.setBcc(BCC_MAIL);
			smm.setSentDate(new Date());
			smm.setSubject("主题");
			smm.setText("内容");
			sender.send(smm);
		} catch (MailException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送带内嵌内容的html的邮件
	 */
	public static void sendHtmlMail() {
		try {
			init();
			MimeMessage mm = sender.createMimeMessage();
			MimeMessageHelper mmh = new MimeMessageHelper(mm, Encoding.UTF8);
			mmh.setFrom(FROM_MAIL);
			mmh.setTo(TO_MAIL);
			mmh.setReplyTo(FROM_MAIL);
			mmh.setCc(CC_MAIL);
			mmh.setBcc(BCC_MAIL);
			mmh.setSentDate(new Date());
			mmh.setSubject("标题");
			// cid是一个固定前缀,testimg是一个资源名称
			String html = "<p>html测试</p><img src='cid:testimg' />";
			// 邮件内容,true代表是html代码
			mmh.setText(html, true);
			// 加载项目路径下资源
			ClassPathResource resource = new ClassPathResource("test.jpg");
			mmh.addInline("testimg", resource);
			sender.send(mm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送带附件的邮件
	 */
	public static void sendAttachMail() {
		try {
			init();
			MimeMessage mm = sender.createMimeMessage();
			// 指定html编码,参数true表示为multipart
			MimeMessageHelper mmh = new MimeMessageHelper(mm, true, Encoding.UTF8);
			mmh.setFrom(FROM_MAIL);
			mmh.setTo(TO_MAIL);
			mmh.setReplyTo(FROM_MAIL);
			mmh.setCc(CC_MAIL);
			mmh.setBcc(BCC_MAIL);
			mmh.setSentDate(new Date());
			mmh.setSubject("发送带附件的邮件");
			String html = "<p>带附件的测试</p>";
			mmh.setText(html, true);
			String attach = "test.docx";
			ClassPathResource resource = new ClassPathResource(attach);
			mmh.addAttachment(MimeUtility.encodeWord(attach), resource);
			sender.send(mm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 简单邮件
	 * 
	 * @param username 邮箱用户名
	 * @param password 邮箱密码
	 * @param from 发件人邮件地址
	 * @param tos 收件人
	 * @param 主题
	 * @param content 内容
	 */
	public static Result sendMail(String username, String password, String from, String[] tos, String subject,
			String content) {
		return sendMail(username, password, false, from, tos, subject, content);
	}
	
	/**
	 * 简单邮件
	 * 
	 * @param username 邮箱用户名
	 * @param password 邮箱密码
	 * @param isSSL 是否开启ssl加密验证,默认不开启
	 * @param from 发件人邮件地址
	 * @param tos 收件人
	 * @param 主题
	 * @param content 内容
	 */
	public static Result sendMail(String username, String password, boolean isSSL,String from, String[] tos, String subject,
			String content) {
		return sendMail(username, password, isSSL,from, tos, subject, content, null);
	}
	

	/**
	 * 带抄送的简单邮件
	 * 
	 * @param username 邮箱用户名
	 * @param password 邮箱密码
	 * @param from 发件人
	 * @param tos 收件人
	 * @param 主题
	 * @param content 内容
	 * @param ccs 抄送人
	 */
	public static Result sendMail(String username, String password, boolean isSSL,String from, String[] tos, String subject,
			String content, String[] ccs) {
		return sendMail(username, password, isSSL,from, tos, subject, content, ccs, null);
	}

	/**
	 * 带密送的邮件
	 * 
	 * @param username 邮箱用户名
	 * @param password 邮箱密码
	 * @param from 发件人
	 * @param tos 收件人
	 * @param 主题
	 * @param content 内容
	 * @param ccs 抄送
	 * @param bccs 密送
	 */
	public static Result sendMail(String username, String password,  boolean isSSL,String from, String[] tos, String subject,
			String content, String[] ccs, String[] bccs) {
		return sendMail(username, password, isSSL,from, tos, subject, content, ccs, bccs, null);
	}

	/**
	 * 发送带附件的邮件
	 * 
	 * @param username 邮箱用户名
	 * @param password 邮箱密码
	 * @param from 发件人
	 * @param tos 收件人
	 * @param 主题
	 * @param content 内容
	 * @param ccs 抄送
	 * @param bccs 密送
	 * @param attachs 附件地址
	 */
	public static Result sendMail(String username, String password,  boolean isSSL,String from, String[] tos, String subject,
			String content, String[] ccs, String[] bccs, String[] attachs) {
		return sendMail(username, password, isSSL,from, tos, subject, content, ccs, bccs, attachs, null);
	}

	/**
	 * 发送带附件的邮件
	 * 
	 * @param username 邮箱用户名
	 * @param password 邮箱密码
	 * @param from 发件人
	 * @param tos 收件人
	 * @param 主题
	 * @param content 内容
	 * @param ccs 抄送
	 * @param bccs 密送
	 * @param attachs 附件地址
	 * @param relateds 内嵌资源地址
	 */
	public static Result sendMail(String username, String password,  boolean isSSL,String from, String[] tos, String subject,
			String content, String[] ccs, String[] bccs, String[] attachs, String[] relateds) {
		Mail mail = new Mail(username, password, isSSL,from, tos, subject, content, ccs, bccs,attachs,relateds);
		return sendMail(mail);
	}

	public static Result sendMail(Mail mail) {
		return MailHelper.sendMail(mail);
	}
}
