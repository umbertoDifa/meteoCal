package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import EJB.interfaces.InvitationManager;
import EJB.interfaces.SearchManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Event;
import model.Invitation;
import model.InvitationAnswer;
import model.PublicEvent;
import model.UserModel;
import utility.EventType;

@Stateless
public class EventManagerImpl implements EventManager {

    @Inject
    SearchManager searchManager;

    @Inject
    CalendarManager calManager;

    @Inject
    InvitationManager invitationManager;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Inject
    @Default
    Logger logger;   

    @Override
    public boolean checkData() {
        return calManager.checkData();//TODO è stupido?
    }

    @Override
    public boolean scheduleNewEvent(Event event, model.CalendarModel insertInCalendar, List<UserModel> invitees) {
        database.persist(event);
        logger.log(Level.INFO, "Event +{0} created", event.getTitle());

        if (insertInCalendar != null) {
            calManager.addToCalendar(event, insertInCalendar);
        }

        if (invitees != null && invitees.size() > 0) {
            invitationManager.createInvitations(invitees, event);
            return true;//TODO
        } else {
            return true;
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
        return database.createNamedQuery("findNextPublicEvents").setParameter("user", user ).setMaxResults(n).getResultList();

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
}
