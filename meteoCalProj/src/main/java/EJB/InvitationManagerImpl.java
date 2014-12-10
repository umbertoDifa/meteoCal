package EJB;

import EJB.interfaces.InvitationManager;
import EJB.interfaces.NotificationManager;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import model.Event;
import model.UserModel;
import model.NotificationType;

@Stateless
public class InvitationManagerImpl implements InvitationManager {

    @Inject
    NotificationManager notificationManager;

    @Override
    public void createInvitations(List<UserModel> usersToInvite, Event event) {
        for (UserModel user : usersToInvite) {
            this.createInvitation(user, event);
        }
        notificationManager.createNotifications(usersToInvite, event,
                NotificationType.INVITATION);
    }

    private void createInvitation(UserModel user, Event event) {
        //verifico se esiste già un invito per quell'utente a quell'evento        
        //creo invito
        //lo persisto
        //TODO do
    }

}
