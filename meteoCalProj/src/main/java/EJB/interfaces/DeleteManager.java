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

}
