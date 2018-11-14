package com.wy.mail;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.sun.mail.util.MailSSLSocketFactory;
import com.wy.crypto.CryptoUtils;
import com.wy.result.Result;
import com.wy.utils.StrUtils;

/**
 * 主要是mail.jar包的发送邮件方法
 * @author 万杨
 * @FIXME
 */
public class MailHelper {
	/**
	 * 邮件发送参数校验
	 */
	public static Result sendMail(final Mail mail) {
		String error = Mail.checkParam(mail);
		if (StrUtils.isNotBlank(error)) {
			return Result.error(error);
		}
		try {
			Properties prop = getProp(mail.getMailHost(), mail.getMailPort(), mail.getIsSSL());
			// 身份验证
			MailAuth auth = new MailAuth(mail.getUsername(), mail.getPassword());
			// 根据会话属性和验证构造一个发送邮件的session
			Session sessionMail = Session.getInstance(prop, auth);
			// 设置日志级别
			sessionMail.setDebug(true);
			// 创建一个邮件消息
			Message message = new MimeMessage(sessionMail);
			// 创建一个发送者地址
			Address from = new InternetAddress(mail.getFromAddress());
			message.setFrom(from);
			// 邮件接收者地址,群发
			Address[] tos = address(mail.getToAddress());
			if (tos == null || tos.length == 0) {
				return Result.error("邮件发送地址为空或添加发送地址失败");
			}
			// 抄送
			Address[] ccs = address(mail.getCc());
			// 密送
			Address[] bccs = address(mail.getBcc());
			// 设置邮件接收者
			message.setRecipients(Message.RecipientType.TO, tos);
			message.setRecipients(Message.RecipientType.CC, ccs);
			message.setRecipients(Message.RecipientType.BCC, bccs);
			// 设置主题
			message.setSubject(mail.getSubject());
			// 设置发送时间
			message.setSentDate(new Date());
			// 设置内容,普通邮件
			// multipart和content只能有一个,但是content可以包含一个multipart,也只能包含一个
			// multipart可以包含多个multibodypart
			// 设置内容,html内容,创建一个容器类,可不传参数,related标识有内嵌,mixed标识有附件
			Multipart part = new MimeMultipart("mixed");
			if(StrUtils.isNotBlank(mail.getContent())) {				
				// 创建一个包含html内容的容器
				MimeBodyPart html = new MimeBodyPart();
				// 设置html邮件的普通内容
				html.setContent(mail.getContent(), "text/html;charset=utf8");
				part.addBodyPart(html);
			}
			// 添加附件
			if (mail.getAttachs() != null && mail.getAttachs().length > 0) {
				String[] attachs = mail.getAttachs();
				for (String attach : attachs) {
					attach(part, attach, CryptoUtils.UUID());
				}
			}
			// 添加内嵌资源
			if (mail.getRelateds() != null && mail.getRelateds().length > 0) {
				String[] relateds = mail.getRelateds();
				for (String related : relateds) {
					MimeBodyPart image = new MimeBodyPart();
					String contentId = CryptoUtils.UUID();
					image.setContent(String.format("<img src=cid:%s width=500 height=600 />",
							"text/html;charset=utf8"), contentId);
					part.addBodyPart(image);
					attach(part, related, contentId);
				}
			}
			message.setContent(part);
			message.saveChanges();
			Transport.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.ok();
	}

	/**
	 * 通用邮件处理
	 */
	public static Properties getProp(String host, int port, boolean isSSL) {
		Properties prop = new Properties();
		prop.put("mail.transport.protocol", "smtp");// 邮件服务类型
		prop.put("mail.smtp.auth", "true"); // 开启验证
		prop.put("mail.host", host);// 邮件服务器
		prop.put("mail.port", port);// 邮箱端口,qq端口是465
		// 是否开启ssl加密验证
		if (isSSL) {
			try {
				MailSSLSocketFactory sf = new MailSSLSocketFactory();
				sf.setTrustAllHosts(true);
				prop.put("mail.smtp.ssl.enable", "true");
				prop.put("mail.smtp.ssl.socketFactory", sf);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return prop;
	}

	/**
	 * 添加发送地址
	 */
	private static InternetAddress[] address(String[] ads) {
		if (ads == null || ads.length == 0) {
			return null;
		}
		int length = ads.length;
		int i = 0;
		InternetAddress[] des = new InternetAddress[length];
		try {
			for (; i < length; i++) {
				des[i] = new InternetAddress(ads[i]);
			}
			return des;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 添加附件
	 */
	private static void attach(Multipart multiparts, String srcFile, String contentId)
			throws Exception {
		MimeBodyPart attach = new MimeBodyPart();
		File file = new File(srcFile);
		attach.setDataHandler(new DataHandler(new FileDataSource(file)));
		attach.setFileName(MimeUtility.encodeText(file.getName(), "UTF8", "UTF8"));
		attach.setContentID(contentId);
		multiparts.addBodyPart(attach);
	}
}
