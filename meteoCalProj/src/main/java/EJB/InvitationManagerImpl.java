package EJB;

import EJB.interfaces.InvitationManager;
import EJB.interfaces.NotificationManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Event;
import model.Invitation;
import model.InvitationAnswer;
import model.UserModel;
import model.NotificationType;
import model.PublicEvent;
import utility.LoggerLevel;
import utility.LoggerProducer;

@Stateless
public class InvitationManagerImpl implements InvitationManager {

    @Inject
    private NotificationManager notificationManager;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    private static final Logger logger = LoggerProducer.debugLogger(InvitationManagerImpl.class);

    @Override
    public void createInvitations(List<UserModel> usersToInvite, Event event) {
        for (UserModel user : usersToInvite) {
            if (!this.createInvitation(user, event)) {
                usersToInvite.remove(user);
            }
        }

        notificationManager.createNotifications(usersToInvite, event,
                NotificationType.INVITATION, true);
    }

    private boolean createInvitation(UserModel user, Event event) {
        //verifico se esiste giÃ  un invito per quell'utente a quell'evento 
        //lo cerco nello user (piÃ¹ lento probabilmente) perchÃ¨ non Ã¨ detto che quell'evento sia giÃ  stato persistito!
        logger.log(LoggerLevel.DEBUG, "dentro create invitation");

        if (user != null && event != null) {
            user = database.find(UserModel.class, user.getId());
            event = database.find(Event.class, event.getId());
            for (Invitation invitation : user.getInvitations()) {
                if (invitation.getEvent().equals(event)) {
                    return false;
                }
            }
            logger.log(LoggerLevel.DEBUG, "dopo check inviti");

            // se l'evento Ã¨ pubblico controllo se l'utente ha fatto public join
            if (event instanceof PublicEvent) {
                if (((PublicEvent) event).getGuests().contains(user)) {
                    // elimino public join, setto risp all invito a yes
                    ((PublicEvent) event).getGuests().remove(user);
                    logger.log(LoggerLevel.DEBUG, "Public join rimossa");

                    Invitation invitation = new Invitation(user, event);
                    invitation.setAnswer(InvitationAnswer.YES);
                    database.persist(invitation);                                        
                    logger.log(LoggerLevel.DEBUG, "Cambiata public join in answer yes");

                    return true;
                }
            }
            logger.log(LoggerLevel.DEBUG, "prima di creare invitation");

            Invitation invitation = new Invitation(user, event);

            database.persist(invitation);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean setAnswer(UserModel answeringUser, Event event, InvitationAnswer answer) {
        Invitation invitation = getInvitationByUserAndEvent(answeringUser, event);
        if (invitation != null) {
            //se ho un invito per qeull'evento
            try {
                invitation = (Invitation) database.createNamedQuery(
                        "findInvitation").setParameter(
                                "event", invitation.getEvent()).setParameter(
                                "user",
                                invitation.getInvitee()).getSingleResult();
                invitation.setAnswer(answer);
                database.flush();
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else {
            return false;
        }

    }

    @Override
    public Invitation
            getInvitationByUserAndEvent(UserModel user, Event event) {
        if (user != null && event != null) {
            user = database.find(UserModel.class, user.getId());
            event = database.find(Event.class, event.getId());
            if (user != null && event
                    != null) {
                List<Invitation> list = event.getInvitations();
                if (list != null && !list.isEmpty()) {
                    for (Invitation i : list) {
                        if (i.getInvitee().equals(user)) {
                            return i;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get all the invitees with a particular answer
     *
     * @param event event to serch invitees for
     * @param answer answer of the invitees
     * @return the invitees who answered like answer
     */
    @Override
    public List<UserModel> getInviteesFiltered(Event event, InvitationAnswer answer) {
        if (event != null) {
            event = database.find(Event.class, event.getId());
            List<Invitation> invitations = event.getInvitations();
            List<UserModel> users = new ArrayList<>();

            for (Invitation invitation : invitations) {
                if (invitation.getAnswer().equals(answer)) {
                    users.add(invitation.getInvitee());
                }
            }
            return users;
        } else {
            logger.log(LoggerLevel.DEBUG,
                    "L'event è null in getInviteesFiltered");
            return null;
        }

    }

}
