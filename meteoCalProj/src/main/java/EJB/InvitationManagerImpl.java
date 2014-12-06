package EJB;

import EJB.interfaces.InvitationManager;
import EJB.interfaces.NotificationManager;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import model.Event;
import model.UserModel;
import utility.NotificationType;

@Stateless
public class InvitationManagerImpl implements InvitationManager {

    @Inject
    NotificationManager notificationManager;

    @Override
    public boolean createInvitations(List<UserModel> usersToInvite, Event event) {
        for (UserModel user : usersToInvite) {
            this.createInvitation(user, event);
        }
        notificationManager.createNotifications(usersToInvite, NotificationType.NEW_EVENT);
        
        return true;
    }

    private void createInvitation(UserModel user, Event event) {
        //verifico se esiste già un invito per quell'utente a quell'evento        
        //creo invito
        //lo persisto
    }

}
