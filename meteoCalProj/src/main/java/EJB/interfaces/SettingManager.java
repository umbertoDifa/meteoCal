package EJB.interfaces;

import java.util.List;
import model.CalendarModel;
import model.UserModel;
import wrappingObjects.Pair;

/**
 *
 * @author umboDifa
 */
public interface SettingManager {

    public void exportCalendar(CalendarModel c);

    public List<Pair<String, String>> importCalendar(UserModel user, String calendarName);

}
