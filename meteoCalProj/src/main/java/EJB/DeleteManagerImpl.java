package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.DeleteManager;
import EJB.interfaces.InvitationManager;
import EJB.interfaces.NotificationManager;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.CalendarModel;
import model.Event;
import model.Invitation;
import model.InvitationAnswer;
import model.Notification;
import model.NotificationType;
import model.PrivateEvent;
import model.PublicEvent;
import model.UserModel;
import utility.DeleteCalendarOption;
import utility.LoggerLevel;
import utility.LoggerProducer;

@Stateless
public class DeleteManagerImpl implements DeleteManager {

    @Inject
    private NotificationManager notificationManager;

    @Inject
    private InvitationManager invitationManager;

    @Inject
    private CalendarManager calendarManager;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    private static final Logger logger = LoggerProducer.debugLogger(DeleteManagerImpl.class);

    @Override
    public boolean deleteEvent(Event event, boolean silent) {

        if (event != null) {
            logger.log(LoggerLevel.DEBUG, "Event {0} is going to be cancelled.",
                    event.getTitle());
            logger.log(LoggerLevel.DEBUG, "Event id is {0} ",
                    event.getId());

            event = database.find(Event.class, event.getId());

            if (!silent) {
                notifyEventDelete(event);
            }

            //cancello l'evento
            database.remove(event);

            return true;

        }
        return false;
    }

    private void notifyEventDelete(Event event) {
        //se l'evento è pubblico avviso chi ha fatto la public join
        if (event instanceof PublicEvent) {
            PublicEvent publicEvent = (PublicEvent) event;
            notificationManager.createNotifications(
                    publicEvent.getGuests(),
                    event, NotificationType.EVENT_CANCELLED, true);
        }
        //se è pubblico o privato avviso quelli che hanno risposto si
        //all'invito
        notificationManager.createNotifications(
                invitationManager.getInviteesFiltered(event,
                        InvitationAnswer.YES), event,
                NotificationType.EVENT_CANCELLED, true);
    }

    @Override
    public boolean deleteCalendar(UserModel user, CalendarModel calendar, DeleteCalendarOption opt) {
        return this.deleteCalendar(user, calendar, opt, false);
    }

    private boolean deleteCalendar(UserModel user, CalendarModel calendar, DeleteCalendarOption opt, boolean deleteAlsoDefault) {
        logger.log(LoggerLevel.DEBUG, "Calendar {0} is going to be cancelled.",
                calendar.getTitle());

        if (hasPermissionToDelete(user, calendar)) {
            try {
                if (!calendarManager.isDefault(calendar) || deleteAlsoDefault) {
                    calendar = (CalendarModel) database.createNamedQuery(
                            "findCalbyUserAndTitle").setParameter("id",
                                    calendar.getOwner()).setParameter("title",
                                    calendar.getTitle()).getSingleResult();
                    switch (opt) {
                        case MOVE_EVENTS_AND_DELETE:
                            CalendarModel defaultCalendar = (CalendarModel) database.createNamedQuery(
                                    "findDefaultCalendar").setParameter("user",
                                            calendar.getOwner()).getSingleResult();
                            //aggiungo tutti gli eventi del calendario che sto per eliminare a
                            //quello default
                            for (int i = 0; i
                                    < calendar.getEventsInCalendar().size(); i++) {
                                defaultCalendar.addEventInCalendar(
                                        calendar.getEventsInCalendar().get(i));
                            }
                            break;
                        case DELETE_CALENDAR_ONLY:
                            break;
                        case DELETE_ALL:
                            //elimo ogni mio evento
                            for (int i = 0; i
                                    < calendar.getEventsInCalendar().size(); i++) {
                                if (calendar.getEventsInCalendar().get(i).getOwner()
                                        == calendar.getOwner()) {
                                    this.deleteEvent(
                                            calendar.getEventsInCalendar().get(i),
                                            false);
                                } else {
                                    //se non è mio metto la partecipazione a NO
                                    if (calendar.getEventsInCalendar().get(i) instanceof PrivateEvent) {
                                        invitationManager.setAnswer(user,
                                                calendar.getEventsInCalendar().get(i),
                                                InvitationAnswer.NO);
                                    } else {
                                        //se è pubblico tolgo la public join
                                        ((PublicEvent) calendar.getEventsInCalendar().get(i)).getGuests().remove(user);
                                    }
                                }
                            }
                    }

                    database.flush();

                    //elimino il calendario dall'utente
                    user = database.find(UserModel.class, user.getId());
                    user.getOwnedCalendars().remove(calendar);

                    database.remove(calendar);
                    return true;
                } else {
                    logger.log(LoggerLevel.DEBUG,
                            "Calendar cannot be cancelled because it's default");
                    return false;
                }
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else {
            logger.log(LoggerLevel.DEBUG,
                    "Calendar cannot be cancelled because user hasnot permission");
            return false;
        }
    }

    /**
     * un utente ha i permessi solo se come al solito è invitato o è pubblico o
     * è l'owner e ha messo che parteciperà o accettato l'invito
     *
     * @param user
     * @param event
     * @return
     */
    private boolean hasPermissionToDelete(UserModel user, CalendarModel calendar) {
        if (user != null && calendar != null) {
            user = database.find(UserModel.class, user.getId());

            calendar = (CalendarModel) database.createNamedQuery(
                    "findCalbyUserAndTitle").setParameter(
                            "id", calendar.getOwner()).setParameter("title",
                            calendar.getTitle()).getSingleResult();

            return calendar.getOwner() == user;
        } else {
            return false;
        }

    }

    @Override
    public boolean deleteAccount(UserModel userToDelete) {
        if (userToDelete != null) {
            userToDelete = database.find(UserModel.class, userToDelete.getId());
            if (userToDelete != null) {
                //per ogni calendario dell'utente cancello il calendario
                //con la modalità elimina evento
                while (!userToDelete.getOwnedCalendars().isEmpty()) {
                    logger.log(LoggerLevel.DEBUG,
                            "canecllo calendario ---------size is:"
                            + userToDelete.getOwnedCalendars().size());

                    this.deleteCalendar(userToDelete,
                            userToDelete.getOwnedCalendars().get(0),
                            DeleteCalendarOption.DELETE_ALL, true);
                    logger.log(LoggerLevel.DEBUG,
                            "canecllo calendario FINE--------- i"
                            + "size is: "
                            + userToDelete.getOwnedCalendars().size());

                }

                database.flush();

                //controllo se ci sono altri eventi di quell'utente in nessun calendario
                //e cancello anche quelli
                for (int i = 0; i < userToDelete.getOwnedEvents().size();
                        i++) {
                    this.deleteEvent(userToDelete.getOwnedEvents().get(i),
                            false);
                }
                database.flush();

                //rimuovo tutte le notifiche dell'utente
                for (Notification n : userToDelete.getNotifications()) {
                    database.remove(n);
                }
                database.flush();

                //rimuovo tutti gli inviti dell'utente
                for (Invitation i : userToDelete.getInvitations()) {
                    database.remove(i);
                }
                database.flush();

                //rimuovo l'utente
                logger.log(LoggerLevel.DEBUG,
                        "User {0} is going to be cancelled, id is{1}",
                        new Object[]{userToDelete.getEmail(),
                            userToDelete.getId()});

                database.remove(userToDelete);
                return true;
            } else {
                logger.log(LoggerLevel.WARNING,
                        "L'utente da cancellare non esiste nel db");
                return false;
            }
        } else {
            logger.log(LoggerLevel.WARNING, "L'utente da cancellare è null");
            return false;
        }
    }

}
