package EJB.interfaces;

import model.CalendarModel;
import model.UserModel;

/**
 *
 * @author umboDifa
 */
public interface SettingManager {

    public void exportCalendar(CalendarModel c);

    public void importCalendar(UserModel user,String calendarName);

}
