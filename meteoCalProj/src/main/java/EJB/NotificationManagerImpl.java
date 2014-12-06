package EJB;

import EJB.interfaces.NotificationManager;
import java.util.List;
import javax.ejb.Stateless;
import model.User;
import utility.NotificationType;

@Stateless
public class NotificationManagerImpl implements NotificationManager {

    @Override
    public boolean createNotifications(List<User> users, NotificationType type) {
        for(User user : users){
            createNotification(user, type);
        }
        for(User user : users){
            sendEmail(user, type);
        }
        
        return true;
    }

    private void sendEmail(User user, NotificationType type) {
        //TODO è possibile generare un email sender con la insert code!
    }

    private boolean createNotification(User user, NotificationType type) {
        //creo la notifica
        //la persisto
        return true;
    }

}
