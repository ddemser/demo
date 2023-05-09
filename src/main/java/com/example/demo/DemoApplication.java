package com.example.demo;

import org.jsoup.Jsoup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		System.out.println("Hello");

		String host = "mx.times.hr";
		String mailStoreType="imap";
		String username = "dubravko.demser@osmibit.hr";
		String password = "b74921Xw1";


	/*	try {
			//receiveEmail(host, mailStoreType, username, password);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} */

		try {
			test();
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	private static void receiveEmail(String host, String mailStoreType, String username, String password) throws MessagingException, IOException, IOException {
		// TODO Auto-generated method stub

		Properties properties = new Properties();


		properties.put("mail.store.protocol", "imap");
		properties.put("mail.imap.host", host);
		properties.put("mail.imap.port", "587");
		properties.put("mail.imap.ssl.enable", "true");
		properties.put("mail.imap.starttls.enable", "true");
		properties.put("mail.imap.auth", "true");
		properties.put("mail.imap.auth.mechanisms", "XOAUTH2");
		properties.put("mail.imap.user", username);
		properties.put("mail.debug", "true");
		properties.put("mail.debug.auth", "true");



		//Session emailSession = Session.getDefaultInstance(properties);
		Session emailSession = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("dubravko.demser@osmibit.hr", "b74921Xw1");
			}
		});

		System.out.println("1");

		Store store = emailSession.getStore("imaps");
		System.out.println("2");
		store.connect(host, username, password);
		System.out.println("3");
		Folder emailFolder = store.getFolder("INBOX");
		emailFolder.open(Folder.READ_ONLY);
		//4) retrieve the messages from the folder in an array and print it
		Message[] messages = emailFolder.getMessages();
		for (int i = 0; i < messages.length; i++) {
			Message message = messages[i];
			System.out.println("---------------------------------");
			System.out.println("Email Number " + (i + 1));
			System.out.println("Subject: " + message.getSubject());
			System.out.println("From: " + message.getFrom()[0]);
			System.out.println("Text: " + message.getContent().toString());
		}



	}

 public static void test() throws MessagingException, IOException {
	 Properties emailProperties = new Properties();
	 emailProperties.put("mail.smtp.host", "mx.times.hr");
	 emailProperties.put("mail.smtp.auth", "true");
	 emailProperties.put("mail.smtp.ssl.enable", "true");

	 emailProperties.put("mail.smtp.port", "465");
	 emailProperties.put("mail.mime.charset", "utf-8");
	 emailProperties.put("mail.debug", "true");
	 emailProperties.put("mail.verbose", "true");

	 final String user = "dubravko.demser@osmibit.hr";
	 final String password = "b74921Xw1";
	 String opStatus = "OK";
	 Transport transport = null;

	 javax.mail.Session session = javax.mail
			 .Session
			 .getInstance(emailProperties, new javax.mail.Authenticator() {
				 public PasswordAuthentication getPasswordAuthentication() {
					 return new PasswordAuthentication(user, password);
				 }
			 });

	 System.out.println("1");

	 Store store = session.getStore("imap");
	 System.out.println("2");
	 store.connect("mx.times.hr", "dubravko.demser@osmibit.hr", password);


	 Calendar cal = Calendar.getInstance();
	 cal.add(Calendar.DAY_OF_MONTH, -1);


	 ReceivedDateTerm term  = new ReceivedDateTerm(ComparisonTerm.EQ,new Date(cal.getTimeInMillis()));


	 System.out.println("3");
	 Folder emailFolder = store.getFolder("INBOX");
	 emailFolder.open(Folder.READ_ONLY);
	 //4) retrieve the messages from the folder in an array and print it
	 Message[] messages = emailFolder.search(term);

	 for (int i = 0; i < messages.length; i++) {
		 Message message = messages[i];
		 System.out.println("---------------------------------");
		 System.out.println("Email Number " + (i + 1));
		 System.out.println("Subject: " + message.getSubject());
		 System.out.println("From: " + message.getFrom()[0]);
		 System.out.println("Text: " + message.getContent().toString());

		 try {
			 System.out.println("Text: " +getTextFromMessage(message));
		 } catch (Exception e) {
			 throw new RuntimeException(e);
		 }
	 }

 }


	private static String getTextFromMessage(Message message) throws MessagingException, IOException {
		if (message.isMimeType("text/plain")) {
			return message.getContent().toString();
		}
		if (message.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			return getTextFromMimeMultipart(mimeMultipart);
		}
		return "";
	}

	private  static String getTextFromMimeMultipart(
			MimeMultipart mimeMultipart)  throws MessagingException, IOException{
		String result = "";
		for (int i = 0; i < mimeMultipart.getCount(); i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/plain")) {
				return result + "\n" + bodyPart.getContent(); // without return, same text appears twice in my tests
			}
			result += parseBodyPart(bodyPart);
		}
		return result;
	}

	private  static String parseBodyPart(BodyPart bodyPart) throws MessagingException, IOException {
		if (bodyPart.isMimeType("text/html")) {
			return "\n" + org.jsoup.Jsoup
					.parse(bodyPart.getContent().toString())
					.text();
		}
		if (bodyPart.getContent() instanceof MimeMultipart){
			return getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
		}

		return "";
	}


}
