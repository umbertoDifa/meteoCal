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

/**
 *
 * @author Umberto
 */
public interface CalendarManager {

    public List<CalendarModel> getCalendars(UserModel user);

    public boolean checkData(//TODO: classe backing con tutti i dati da checkare
            );

    public boolean addCalendarToUser(UserModel user, CalendarModel cal);

    /**
     * Returns the first available free day within the next weeksAhead
     *
     * @param fromBusyDay the day that has problems and from which the system
     * checks free days
     * @param weeksAhead max number of weeks in the future to limit the search
     * space
     * @return a free Calendar day if one is found, null if not
     */
    public Calendar findFreeDay(Calendar fromBusyDay, int weeksAhead);

    /**
     * Dato un evento ed un calendario, inserisce l'evento nel calendario
     *
     * @param event Evento da inserire
     * @param calendar Calendario dell'utente in cui inserirlo
     * @return true se inserito,false se non
     */
    public ControlMessages addToCalendar(Event event, CalendarModel calendar, UserModel user);

    public void exportCalendar(CalendarModel calendar);

    public void importCalendar(CalendarModel calendar);

    public CalendarModel createDefaultCalendar(UserModel user);
}
