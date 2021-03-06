/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB.interfaces;

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

    public boolean addCalendarToUser(CalendarModel cal);

    /**
     * Dato un evento ed un calendario, inserisce l'evento nel calendario
     *
     * @param event Evento da inserire
     * @param calendar Calendario dell'utente in cui inserirlo
     * @return true se inserito,false se non
     */
    public ControlMessages addToCalendar(Event event, CalendarModel calendar);

    public CalendarModel getCalendarUpdated(CalendarModel calendar);

    public List<Event> getEventsUpdated(CalendarModel calendar);

    public CalendarModel createDefaultCalendar(UserModel user);

    public CalendarModel findCalendarByName(UserModel user, String name);

    public boolean isInConflict(Event event);

    public int findFreeSlots(Event event);

    public void toggleCalendarPrivacy(CalendarModel calendar);

    public void removeFromAllCalendars(UserModel user, Event event);

    public boolean isDefault(CalendarModel calendar);

    public boolean makeDefault(CalendarModel calendar);

    public CalendarModel getDefaultCalendar(UserModel user);

    /**
     *
     * @param event
     * @param user
     * @return a calendar if found, null if not
     */
    public CalendarModel getCalendarOfEvent(Event event, UserModel user);
}
