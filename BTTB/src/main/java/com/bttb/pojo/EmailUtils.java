/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.pojo;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

/**
 *
 * @author LEGION
 */
public class EmailUtils {
//    public static boolean sendEmail(String to, String subject, String content) {
//        String from = "youremail@gmail.com";
//        String password = "your_app_password"; // dùng App Password nếu Gmail
//
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "587");
//
//        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(from, password);
//            }
//        });
//
//        try {
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(from));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
//            message.setSubject(subject);
//            message.setText(content);
//
//            Transport.send(message);
//            return true;
//
//        } catch (MessagingException e) {
//            return false;
//        }
//    }
}
