package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import EJB.interfaces.InvitationManager;
import EJB.interfaces.SearchManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.CalendarModel;
import model.Event;
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
    public List<CalendarModel> loadCalendars(UserModel user) {
               throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
 //return user.getOwnedCalendars();
    }

    @Override
    public List<Object> search(Object thingToSearch) {
        return searchManager.search(thingToSearch);
    }

    @Override
    public boolean saveInvites(List<UserModel> invitee) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //TODO secondo me questa deve stare nel backing
    }

    @Override
    public boolean checkData() {
        return calManager.checkData();
    }

    @Override
    public boolean scheduleNewEvent(UserModel user, Event event, model.CalendarModel insertInCalendar,List<UserModel> invitees) {
        database.persist(event);
        logger.log(Level.INFO, "Event +{0} created", event.getTitle());
       
        if(insertInCalendar != null){
            calManager.addToCalendar(event, insertInCalendar);
        }
        
        return invitationManager.createInvitations(invitees, event);
        
    }

}
