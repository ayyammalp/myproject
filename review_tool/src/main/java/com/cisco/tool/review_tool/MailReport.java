package com.cisco.tool.review_tool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Map.Entry;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

class MailReport{
	
public void sendEmail(String pr, Map<String, HashSet<String>> resMap){
	
	Scanner scanner1=new Scanner(System.in);
	System.out.println("Enter email id to send ICR comments: ");
	String destinationEmail = scanner1.next();
	 	      
	Properties properties = System.getProperties();  
	properties.setProperty("mail.smtp.host", "mail.cisco.com");  
	properties.put("mail.smtp.auth", "false");    
	Session session = Session.getDefaultInstance(properties);    
	try{  
		MimeMessage message = new MimeMessage(session);  
	    message.setFrom(new InternetAddress("icr-noreply@cisco.com"));  
	    message.addRecipient(Message.RecipientType.TO,new InternetAddress(destinationEmail));	
	    message.setSubject(pr);
	    
	    MimeBodyPart htmlPart = new MimeBodyPart();	    
	    
	    String con = "";
	    	    
	    if(!resMap.isEmpty()) {	    	
	    for (Entry<String, HashSet<String>> report : resMap.entrySet()) {	    	
	    	if(report.getKey() != "" && !report.getValue().isEmpty()) {	    		
	    		con += report.getKey()+"<br>"+report.getValue().toString()+"<br><br>";
		    	htmlPart.setContent(con, "text/html; charset=utf-8");
	    		}	    		   	    	    		   
	    	}
	    }
	    
	    Multipart multipart = new MimeMultipart();	   
	    multipart.addBodyPart(htmlPart);
	    message.setContent(multipart );    
	    Transport.send(message);
	    System.out.println("ICR sent to your Mail");    
	}
	catch (MessagingException ex) {
		ex.printStackTrace();
		System.out.println("Incorrect mailid");	
	}	
}
}