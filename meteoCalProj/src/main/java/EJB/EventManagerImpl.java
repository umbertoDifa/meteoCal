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
    public List<Object> search(Object thingToSearch) {
        return searchManager.search(thingToSearch);
    }

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
    public List<PublicEvent> eventOnWall(UserModel user) {
        return database.createNamedQuery("findNextPublicEvents").setParameter("user", user.getId()).setMaxResults(10).getResultList();

    }

    @Override
    public List<Event> ownedEventonWall(UserModel user) {
        return user.getOwnedEvents().subList(0, 9); //TODO magic number?
    }

    @Override
    public List<Event> acceptedEventsOnWall(UserModel user) {
        List<Event> events = new ArrayList<>();
        List<Invitation> invitations = user.getInvitations();
        for (int i = 0; i < 10 || i < invitations.size(); i++) {//TODO: magic number
            if (invitations.get(i).getAnswer().equals(InvitationAnswer.YES)) {
                events.add(invitations.get(i).getEvent());
            }

        }
        return events;
    }

    @Override
    public List<Event> invitedEventsOnWall(UserModel user) {
        List<Event> events = new ArrayList<>();
        List<Invitation> invitations = user.getInvitations();
        for (int i = 0; i < 10 || i < invitations.size(); i++) {//TODO magic number?
            events.add(invitations.get(i).getEvent());
        }
        return events;
    }

    @Override
    public List<PublicEvent> joinedEventsOnWall(UserModel user) {
        return user.getPublicJoins().subList(0, 9);//TODO magic numbers?
    }
}
