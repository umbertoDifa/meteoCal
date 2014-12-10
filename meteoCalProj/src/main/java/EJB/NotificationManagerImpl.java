package EJB;

import EJB.interfaces.NotificationManager;
import java.util.List;
import javax.ejb.Stateless;
import model.UserModel;
import model.NotificationType;
import utility.EmailSender;

@Stateless
public class NotificationManagerImpl implements NotificationManager {

    @Override
    public boolean createNotifications(List<UserModel> users, NotificationType type) {
        for(UserModel user : users){
            createNotification(user, type);
        }
        for(UserModel user : users){
            sendEmail(user, type);
        }
        
        return true;
    }

    private void sendEmail(UserModel user, NotificationType type) {
        //TODO questo è già fatto devo solo copiare codice
        //EmailSender.sendEmail(user.getEmail());
    }

    private boolean createNotification(UserModel user, NotificationType type) {
        //creo la notifica
        //la persisto
        return true;
    }

}
