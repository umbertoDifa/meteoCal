/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB;

import java.util.Calendar;
import java.util.List;
import model.User;

/**
 *
 * @author Umberto
 */
public interface CalendarManager {
    public List<String> getCalendarsName(User user);
    
    public boolean checkData(//TODO: classe backing con tutti i dati da checkare
    );
    
    /**
     * Returns the first available free day within the next weeksAhead
     * @param fromBusyDay the day that has problems and from which the system checks
     * free days
     * @param weeksAhead max number of weeks in the future to limit the search space 
     * @return a free Calendar day if one is found, null if not
     */
    public Calendar findFreeDay(Calendar fromBusyDay,int weeksAhead);
}
