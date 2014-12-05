package EJB;

import EJB.interfaces.NotificationManager;
import java.util.List;
import model.User;
import utility.TypeOfNotification;

public class NotificationManagerImpl implements NotificationManager {

    @Override
    public boolean createNotifications(List<User> users, TypeOfNotification type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void sendEmail(User user) {
        //TODO è possibile generare un email sender con la insert code!
    }

    private boolean createNotification(User user, TypeOfNotification type) {
        //TODO
        return false;
    }

}
