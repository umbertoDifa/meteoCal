package EJB;

import EJB.interfaces.NotificationManager;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import model.Event;
import model.UserModel;
import model.NotificationType;
import utility.EmailSender;
import utility.LoggerLevel;
import utility.LoggerProducer;

@Stateless
public class NotificationManagerImpl implements NotificationManager {

    Logger logger = LoggerProducer.debugLogger(NotificationManagerImpl.class);

    @Override
    public boolean createNotifications(List<UserModel> users, Event event, NotificationType type) {
        logger.log(LoggerLevel.DEBUG, "Setting up the notification...");
        
        type.setEventName(event.getTitle()).setEventOwner(
                event.getOwner().getEmail());

        for (UserModel user : users) {
            createNotification(user, type);
        }
        for (UserModel user : users) {
            type.setInviteeName(user.getName()).buildEmail();
            sendEmail(user, type);
        }

        return true;
    }

    private void sendEmail(UserModel user, NotificationType type) {
        logger.log(LoggerLevel.DEBUG, "Mando email");
        String subject = type.getSubject();
        String body = type.getBodyMessage();
        EmailSender.sendEmail(user.getEmail(), subject, body);
    }

    private boolean createNotification(UserModel user, NotificationType type) {
        //creo la notifica
        //la persisto
        //TODO
        return true;
    }

}
