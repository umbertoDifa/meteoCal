package EJB;

import EJB.interfaces.CalendarManager;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import model.CalendarModel;
import model.Event;
import model.UserModel;
import utility.ControlMessages;

@Stateless
public class CalendarManagerImpl implements CalendarManager {

    @Inject
    @Default
    Logger logger;

    private void checkWeather() {
        //TODO do       
    }

    private void checkConflicts() {
        //TODO do
    }

    private void findFreeSlots() {
        //TODO do
    }

    @Override
    public List<CalendarModel> getCalendars(UserModel user) {
        return user.getOwnedCalendars();
    }

    @Override
    public boolean checkData() {
        this.checkWeather();
        this.checkConflicts();
        return true;
    }

    @Override
    public Calendar findFreeDay(Calendar fromBusyDay, int weeksAhead) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ControlMessages addToCalendar(Event event, CalendarModel calendar, UserModel user) {
        //se l'evento non è in nessun calendario dell'utente
        for (CalendarModel cal : user.getOwnedCalendars()) {
            for (Event e : cal.getEventsInCalendar()) {
                if (e.equals(event)) {
                    return ControlMessages.EVENT_ALREADY_IN_CALENDARS;
                }
            }
        }
        //allora lo aggiungo
        //TODO basta aggiungere a questa lista?
        //calendar.setEventsInCalendar();
        return ControlMessages.EVENT_ADDED;
    }

    @Override
    public void exportCalendar(CalendarModel calendar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void importCalendar(CalendarModel calendar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CalendarModel createDefaultCalendar(UserModel user) {
        CalendarModel calendar = new CalendarModel();
        calendar.setIsDefault(true);
        calendar.setIsPublic(false);
        calendar.setOwner(user);
        calendar.setTitle("Default");

        logger.log(Level.INFO, "Default calendar for user +{0} created", user.getEmail());

        return calendar;
    }

}
