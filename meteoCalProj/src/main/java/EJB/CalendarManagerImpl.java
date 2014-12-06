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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean checkData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Calendar findFreeDay(Calendar fromBusyDay, int weeksAhead) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addToCalendar(Event event, CalendarModel calendar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
