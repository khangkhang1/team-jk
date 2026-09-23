package mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {

	private String fromUser, password;

	public SendMail(String fromUser, String password) {
		this.fromUser = fromUser;
		this.password = password;
	}

	public boolean sendPassword(String toUser,
								String mailTitle, 
								String mailContent) {
		boolean success = true;
		
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", 587);
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        
        Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromUser, password);
            }
        });
 
        MimeMessage message = new MimeMessage(session);
        
        try {
            message.setFrom(new InternetAddress(fromUser));
 
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toUser));
 
            message.setSubject(mailTitle);
 
            message.setText(mailContent);
 
            Transport.send(message);
 
        } catch (AddressException e) {
            e.printStackTrace();
            success = false;
        } catch (MessagingException e) {
            e.printStackTrace();
            success = false;
        }
        
        return success;
 
    }
}
