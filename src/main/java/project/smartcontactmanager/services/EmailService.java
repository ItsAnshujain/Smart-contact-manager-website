package project.smartcontactmanager.services;

import org.springframework.stereotype.Service;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

@Service
public class EmailService {
    public boolean sendEmail(String to, String subject, String message){
        String from="anshujain8844@gmail.com";
        boolean flag=false;
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.host", "smtp.gmail.com");

        String username="anshujain8844";
        String password="nysj tduz ubca swsu";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        Message m=new MimeMessage(session);
        try {
            m.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            m.setFrom(new InternetAddress(from));
            m.setSubject(subject);
            m.setContent(message, "text/html");
            Transport.send(m);
            flag=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
