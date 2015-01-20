package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.DeleteManager;
import EJB.interfaces.NotificationManager;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.CalendarModel;
import model.Event;
import model.NotificationType;
import model.PublicEvent;
import model.UserModel;
import utility.DeleteCalendarOption;
import utility.LoggerLevel;

@Stateless
public class DeleteManagerImpl implements DeleteManager {

    @Inject
    private NotificationManager notificationManager;

    @Inject
    private CalendarManager calendarManager;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Inject
    @Default
    private Logger logger;

    @Override
    public boolean deleteEvent(Event event) {
        if (event != null) {
            try {
                event = database.find(Event.class, event.getId());

                if (event instanceof PublicEvent) {
                    PublicEvent publicEvent = (PublicEvent) event;
                    notificationManager.createNotifications(
                            publicEvent.getGuests(),
                            event, NotificationType.EVENT_CANCELLED, true);
                }
                notificationManager.createNotifications(event.getInvitee(),
                        event,
                        NotificationType.EVENT_CANCELLED, true);
                database.remove(event);
                return true;
            } catch (IllegalArgumentException e) {
                logger.log(LoggerLevel.DEBUG, "Evento: {0} non trovato",
                        event.getId());
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean deleteCalendar(UserModel user, CalendarModel calendar, DeleteCalendarOption opt) {
        if (hasPermissionToDelete(user, calendar)) {
            try {
                if (!calendarManager.isDefault(calendar)) {
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
                            //elimino gli eventi dal calendario che voglio eliminare
                            calendar.getEventsInCalendar().clear();
                            break;
                        case DELETE_CALENDAR_ONLY:
                            //elimino gli eventi dal calendario che voglio eliminare
                            calendar.getEventsInCalendar().clear();
                            break;
                        case DELETE_ALL:
                            //elimo ogni mio evento
                            for (int i = 0; i
                                    < calendar.getEventsInCalendar().size(); i++) {
                                if (calendar.getEventsInCalendar().get(i).getOwner()
                                        == calendar.getOwner()) {
                                    this.deleteEvent(
                                            calendar.getEventsInCalendar().get(i));
                                }
                            }
                            //elimino gli eventi dal calenadrio
                            calendar.getEventsInCalendar().clear();
                    }

                    database.flush();
                    database.remove(calendar);
                    return true;
                } else {
                    return false;
                }
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
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

}
