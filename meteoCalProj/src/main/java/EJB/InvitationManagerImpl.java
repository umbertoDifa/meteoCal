package EJB;

import EJB.interfaces.InvitationManager;
import EJB.interfaces.NotificationManager;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import model.Event;
import model.Invitation;
import model.InvitationAnswer;
import model.UserModel;
import model.NotificationType;

@Stateless
public class InvitationManagerImpl implements InvitationManager {

    @Inject
    NotificationManager notificationManager;

    @Inject
    EntityManager database;

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

    @Override
    public boolean setAnswer(Invitation invitation, InvitationAnswer answer) {
        try {
            invitation = (Invitation) database.createNamedQuery("findInvitation").setParameter("event", invitation.getEvent()).setParameter("user", invitation.getInvitee()).getSingleResult();
            invitation.setAnswer(answer);
            database.persist(invitation);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }

    }

}
