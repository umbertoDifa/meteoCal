package EJB.interfaces;

import model.CalendarModel;
import model.Event;
import model.UserModel;
import utility.DeleteCalendarOption;

/**
 *
 * @author umboDifa
 */
public interface DeleteManager {

    public boolean deleteEvent(Event event,boolean silent);
    public boolean deleteCalendar(UserModel user,CalendarModel calendar, DeleteCalendarOption opt);
    
    /**
     * Cancella un account se lo userToDelete viene trovato ed e not null
     * @param userToDelete user da eliminare 
     * @return true se lo user è eliminato, false se non è stato possibile eliminarlo
     */
    public boolean deleteAccount(UserModel userToDelete);
}
