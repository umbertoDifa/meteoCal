package EJB;

import EJB.interfaces.InvitationManager;
import EJB.interfaces.NotificationManager;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Event;
import model.Invitation;
import model.InvitationAnswer;
import model.UserModel;
import model.NotificationType;

@Stateless
public class InvitationManagerImpl implements InvitationManager {

    @Inject
    NotificationManager notificationManager;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Override
    public void createInvitations(List<UserModel> usersToInvite, Event event) {
        for (UserModel user : usersToInvite) 
            if (!this.createInvitation(user, event)) 
                usersToInvite.remove(user);

        notificationManager.createNotifications(usersToInvite, event,
                NotificationType.INVITATION, true);
    }

    private boolean createInvitation(UserModel user, Event event) {
        //verifico se esiste già un invito per quell'utente a quell'evento 
        //lo cerco nello user (più lento probabilmente) perchè non è detto che quell'evento sia già stato persistito!
        user = database.find(UserModel.class, user.getId());
            for (Invitation invitation : user.getInvitations()) {
                if (invitation.getEvent().equals(event)) {
                    return false;
                }
            }
       Invitation invitation = new Invitation(user,event);
       database.persist(invitation);
       return true;
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
