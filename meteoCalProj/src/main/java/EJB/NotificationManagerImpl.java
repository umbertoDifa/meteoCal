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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void sendEmail(User user) {
        //TODO è possibile generare un email sender con la insert code!
    }

    private boolean createNotification(User user, NotificationType type) {
        //TODO
        return false;
    }

}
