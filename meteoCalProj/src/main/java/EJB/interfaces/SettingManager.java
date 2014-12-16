package EJB.interfaces;

import model.CalendarModel;

/**
 *
 * @author umboDifa
 */
public interface SettingManager {

    public void exportCalendar(CalendarModel c);

    public void importCalendar(String calendarName);

}
