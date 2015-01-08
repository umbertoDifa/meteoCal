/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB.interfaces;

import java.util.Calendar;
import java.util.List;
import model.CalendarModel;
import model.Event;
import model.UserModel;
import utility.ControlMessages;
import utility.DeleteCalendarOption;

/**
 *
 * @author Umberto
 */
public interface CalendarManager {

    public List<CalendarModel> getCalendars(UserModel user);

    public List<ControlMessages> checkData(Event event);

    public boolean addCalendarToUser(UserModel user, CalendarModel cal); 

    /**
     * Dato un evento ed un calendario, inserisce l'evento nel calendario
     *
     * @param event Evento da inserire
     * @param calendar Calendario dell'utente in cui inserirlo
     * @return true se inserito,false se non
     */
    public ControlMessages addToCalendar(Event event, CalendarModel calendar);

    public CalendarModel createDefaultCalendar(UserModel user);

    public CalendarModel findCalendarByName(UserModel user, String name);

    public List<String> getCalendarTitles(UserModel user);

    public boolean isInConflict(Event event);
    
    public int findFreeSlots(Event event);
    
    public void toggleCalendarPrivacy (CalendarModel calendar);
    
    public boolean deleteCalendar(CalendarModel calendar, DeleteCalendarOption opt);
    
    public boolean isDefault(CalendarModel calendar);
}
