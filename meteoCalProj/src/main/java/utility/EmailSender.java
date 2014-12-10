package utility;

import java.util.*;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author umboDifa
 */
public class EmailSender {

    private static String USER_NAME = "meteoCalendar.notification";  // GMail user name (just the part before "@gmail.com")
    private static String PASSWORD = "fravaleumbo"; // GMail password
    private static String RECIPIENT_DEFAULT = "umberto.difabrizio@gmail.com";
    private static Logger logger = LoggerProducer.debugLogger(EmailSender.class);

    public static void sendEmail(String recipient) {
        String from = USER_NAME;
        String pass = PASSWORD;
        String[] to;
        if (recipient == null) {
            to = new String[]{RECIPIENT_DEFAULT}; // list of recipient email addresses
        } else {
            to = new String[]{recipient};
        }

        String subject = "Meteo Calendar Notification";
        String body = "DEscrizione della notifica";

        sendFromGMail(from, pass, to, subject, body);
    }

    private static void sendFromGMail(String from, String pass, String[] to,
                                      String subject, String body) {
        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for (int i = 0; i < to.length; i++) {
                toAddress[i] = new InternetAddress(to[i]);
            }

            for (int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }

            message.setSubject(subject);
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (AddressException ae) {
            logger.log(LoggerLevel.SEVERE, ae.getMessage(), ae);
        } catch (MessagingException me) {
            logger.log(LoggerLevel.SEVERE, me.getMessage(), me);

        }
    }
}
