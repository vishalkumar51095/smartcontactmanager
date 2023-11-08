package com.smart.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {


    public boolean sendEmail(String subject, String message,String to) {
        boolean flag = false;

        //logic
        //smtp properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.host", "smtp.gmail.com");

        String username="vishalkumar51095";
        String from = "vishalkumar51095@gmail.com";
        String password = "xium fbfb nfed olqo";


        //session
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        session.setDebug(true);

        try {

            Message message1 = new MimeMessage(session);
            message1.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message1.setFrom(new InternetAddress(from));
            message1.setContent(message,"text/html");
            message1.setSubject(subject);
//            message1.setText(message);
            Transport.send(message1);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return flag;
    }
}