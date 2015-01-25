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
import utility.NotificationCategories;

@Stateless
public class NotificationManagerImpl implements NotificationManager {

    private static final Logger logger = LoggerProducer.debugLogger(NotificationManagerImpl.class);
    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Override
    public void createNotifications(List<UserModel> users, Event event, NotificationType type, boolean sendEmail) {
        logger.log(LoggerLevel.DEBUG, "Setting up the notification...");

        if (users != null && !users.isEmpty() && event != null) {
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

    }

    @Override
    public void sendSignUpConfirmation(UserModel user) {
        logger.log(LoggerLevel.DEBUG, "Setting up the singup notification...");

        if (user != null) {
            NotificationType type = NotificationType.SIGN_UP_OK;
            type.setInviteeName(user.getName()).buildEmail();
            sendEmail(user, type);

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

    @Override
    public int getUnreadNotificationNumber(UserModel user) {
        return ((Number) database.createNamedQuery("countUnreadNotifications").setParameter(
                "user", user).getSingleResult()).intValue();
    }

    @Override
    public List<Notification> getNotificationFiltred(UserModel user, NotificationCategories type) {
        user = database.find(UserModel.class, user.getId());
        database.refresh(user);
        switch (type) {
            case INVITATIONS: {
                return database.createNamedQuery("findInvitationNotifications").setParameter(
                        "user", user).getResultList();
            }
            case EVENTS_CHANGES: {
                return database.createNamedQuery("findEventNotifications").setParameter(
                        "user", user).getResultList();
            }

            default: {
                return null;
            }
        }

    }

    @Override
    public void markAllAsRead(List<Notification> notifications) {
        for (Notification n : notifications) {
            n = database.find(Notification.class, n.getId());
            n.setRead(true);
            database.flush();
        }
    }

}
