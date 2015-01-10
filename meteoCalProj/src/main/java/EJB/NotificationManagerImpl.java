package EJB;

import EJB.interfaces.NotificationManager;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Event;
import model.Notification;
import model.UserModel;
import model.NotificationType;
import utility.EmailSender;
import utility.LoggerLevel;
import utility.LoggerProducer;

@Stateless
public class NotificationManagerImpl implements NotificationManager {

    Logger logger = LoggerProducer.debugLogger(NotificationManagerImpl.class);
    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Override
    public void createNotifications(List<UserModel> users, Event event, NotificationType type, boolean sendEmail) {
        logger.log(LoggerLevel.DEBUG, "Setting up the notification...");

        type.setEventName(event.getTitle()).setEventOwner(
                event.getOwner().getEmail()).setLink(event.getId());

        for (UserModel user : users) {
            createNotification(user, event, type);
        }
        if (sendEmail) {
            for (UserModel user : users) {
                type.setInviteeName(user.getName()).buildEmail();
                sendEmail(user, type);
            }
        }

    }

    private void sendEmail(UserModel user, NotificationType type) {
        String subject = type.getSubject();
        String body = type.getBodyMessage();
        EmailSender.sendEmail(user.getEmail(), subject, body);
    }

    private void createNotification(UserModel user, Event event, NotificationType type) {
        //creo la notifica
        Notification notification = new Notification(user, event, type);

        //la persisto
        database.persist(notification);

    }
    
    private int getNotificationNumber (UserModel user) {
        user = database.find(UserModel.class, user.getId());
        database.refresh(user);
        return user.getNotifications().size();
    }

}
