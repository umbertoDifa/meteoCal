package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import EJB.interfaces.InvitationManager;
import EJB.interfaces.NotificationManager;
import EJB.interfaces.SearchManager;
import EJB.interfaces.WeatherManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import model.CalendarModel;
import model.Event;
import model.Invitation;
import model.InvitationAnswer;
import model.NotificationType;
import model.PublicEvent;
import model.PrivateEvent;
import model.UserModel;
import model.WeatherForecast;

@Stateless
public class EventManagerImpl implements EventManager {

    @Inject
    SearchManager searchManager;

    @Inject
    CalendarManager calManager;

    @Inject
    InvitationManager invitationManager;

    @Inject
    NotificationManager notificationManager;

    @Inject
    WeatherManager weatherManager;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Inject
    @Default
    Logger logger;

    @Override
    public boolean scheduleNewEvent(Event event, CalendarModel insertInCalendar, List<UserModel> invitees) {
        //salvo evento nel db
        database.persist(event);
        logger.log(Level.INFO, "Event {0} created", event.getTitle());

        //aggiungo weather
        WeatherForecast forecast = weatherManager.getWeather(
                event.getStartDateTime(), event.getLocation());
        database.persist(forecast);
        event.setWeather(forecast);
        database.flush();
        logger.log(Level.INFO, "Forecast added to Event {0} ", event.getTitle());

        //schedulo updates weather
        UpdateManagerImpl.addEvent(event);       
        logger.log(Level.INFO, "Updates scheduled for Event {0} ",
                event.getTitle());

        //aggiungo l'evento al calendario se necessario
        if (insertInCalendar != null) {
            calManager.addToCalendar(event, insertInCalendar);
        }

        //faccio gli inviti
        if (invitees != null && invitees.size() > 0) {
            invitationManager.createInvitations(invitees, event);
            return true;
        }
        return true;
        //TODO attualmente il metodo ritorna sempre true
    }

    @Override
    public boolean updateEvent(Event event, CalendarModel inCalendar, List<UserModel> invitees) {
        if (updateEvent(event, inCalendar)) {
            try {
                if (invitees != null && invitees.size() > 0) {
                    invitationManager.createInvitations(invitees, event);
                }
                return true;
            } catch (PersistenceException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public List<Event> eventOnWall(utility.EventType type, int n, UserModel owner) {
        owner = database.find(UserModel.class, owner.getId());
        database.refresh(owner);
        switch (type) {
            case INVITED: {
                return invitedEventsOnWall(owner, n);
            }
            case PARTECIPATING: {
                return acceptedEventsOnWall(owner, n);
            }

            case JOINED: {
                List<Event> joinedEvents = new ArrayList<>();
                joinedEvents.addAll(joinedEventsOnWall(owner, n));
                return joinedEvents;

            }

            case OWNED: {
                return ownedEventonWall(owner, n);
            }

            case PUBLIC: {
                List<Event> publicEvents = new ArrayList<>();
                publicEvents.addAll(publicEventsOnWall(owner, n));
                return publicEvents;
            }

        }
        return null;
    }

    private List<PublicEvent> publicEventsOnWall(UserModel user, int n) {
        return database.createNamedQuery("findNextPublicEvents").setParameter(
                "user", user).setMaxResults(n).getResultList();

    }

    private List<Event> ownedEventonWall(UserModel user, int n) {
        List<Event> r = user.getOwnedEvents();
        if (n > r.size()) {
            return r;
        } else {
            return r.subList(0, n);
        }
    }

    private List<Event> acceptedEventsOnWall(UserModel user, int n) {
        List<Event> events = new ArrayList<>();
        List<Invitation> invitations = user.getInvitations();
        for (int i = 0; i < n && i < invitations.size(); i++) {
            if (invitations.get(i).getAnswer().equals(InvitationAnswer.YES)) {
                events.add(invitations.get(i).getEvent());
            }

        }
        return events;
    }

    private List<Event> invitedEventsOnWall(UserModel user, int n) {
        List<Event> events = new ArrayList<>();
        List<Invitation> invitations = user.getInvitations();
        for (int i = 0; i < n && i < invitations.size(); i++) {
            events.add(invitations.get(i).getEvent());
        }
        return events;
    }

    private List<PublicEvent> joinedEventsOnWall(UserModel user, int n) {
        List<PublicEvent> r = user.getPublicJoins();
        if (n > r.size()) {
            return r;
        } else {
            return r.subList(0, n);
        }
    }

    @Override
    public Event findEventbyId(Long id) {
        Event event = database.find(Event.class, id);
        if (event != null) {
            database.refresh(event);
            return event;
        } else //TODO ERRORE, O LO CONTROLLA FRA?
        {
            return null;
        }

    }

    @Override
    public boolean deleteEvent(Event event) {
        try {
            database.find(Event.class, event.getId());
            database.remove(event);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }

    }

    private void findFreeSlots(UserModel user, Event event) {
        int searchRange = 15;
        for (CalendarModel calendar : user.getOwnedCalendars()) {
            boolean found = false;
            for (int i = 0; (i < searchRange && !found); i++) {
                calendar.getEventsInCalendar();

            }
        }

    }

    @Override
    public List<UserModel> getInviteeFiltred(Event event, InvitationAnswer answer) {
        event = database.find(Event.class, event.getId());
        List<Invitation> invitations = event.getInvitations();
        List<UserModel> users = new ArrayList<>();
        //TODO ma qui non basta scrivere invitation.getAnswer().equals(answer) invece
        //di fare uno switch?
        switch (answer) {
            case YES: {
                for (Invitation invitation : invitations) {
                    if (invitation.getAnswer().equals(InvitationAnswer.YES)) {
                        users.add(invitation.getInvitee());
                    }
                }
            }
            case NO: {
                for (Invitation invitation : invitations) {
                    if (invitation.getAnswer().equals(InvitationAnswer.NO)) {
                        users.add(invitation.getInvitee());
                    }
                }

            }
            case NA: {
                for (Invitation invitation : invitations) {
                    if (invitation.getAnswer().equals(InvitationAnswer.NA)) {
                        users.add(invitation.getInvitee());
                    }
                }
            }
        }
        return users;
    }

    @Override
    public boolean updateEvent(Event event, CalendarModel inCalendar) {
        try {
            Event oldEvent = database.find(Event.class, event.getId());
            List<UserModel> oldinvitees = new ArrayList<>();
            for (Invitation invitation : oldEvent.getInvitations()) {
                oldinvitees.add(invitation.getInvitee());
            }
            if (oldEvent instanceof PrivateEvent && event instanceof PublicEvent) {
                // event.setId(oldEvent.getId());

                notificationManager.createNotifications(oldinvitees, oldEvent,
                        NotificationType.EVENT_PUBLIC, false);
                database.remove(oldEvent);
                database.persist(event);

            } else if (oldEvent instanceof PublicEvent
                    && event instanceof PrivateEvent) {
                //TODO implementare cambio di privacy da public a private
            }
            if (!(oldEvent.getTitle().equals(event.getTitle())
                    && oldEvent.getStartDateTime().equals(
                            event.getStartDateTime())
                    && oldEvent.getEndDateTime().equals(event.getEndDateTime()))) {
                notificationManager.createNotifications(oldinvitees, oldEvent,
                        NotificationType.EVENT_CHANGED, false);
            }

            database.flush();
            return true;

        } catch (PersistenceException e) {
            return false;
        }

    }

    @Override
    public List<UserModel> getPublicJoin(Event event) {
        try {
            if (event instanceof PublicEvent) {
                PublicEvent publicEvent = (PublicEvent) database.find(
                        Event.class, event.getId());
                return publicEvent.getGuests();
            }
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean isInAnyCalendar(Event event, UserModel user) {
        int i = (int) database.createNamedQuery("isInAnyCalendar").setParameter(
                1, event.getId()).setParameter(2, user.getId()).getSingleResult();
        return i != 0;
    }
}
