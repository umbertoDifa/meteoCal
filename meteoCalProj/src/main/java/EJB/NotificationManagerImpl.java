package EJB;

import EJB.interfaces.NotificationManager;
import java.util.List;
import javax.ejb.Stateless;
import model.UserModel;
import utility.NotificationType;

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
        //TODO è possibile generare un email sender con la insert code!
    }

    private boolean createNotification(UserModel user, NotificationType type) {
        //creo la notifica
        //la persisto
        return true;
    }

}
