package EJB;

import EJB.interfaces.CalendarManager;
import java.util.Calendar;
import java.util.List;
import javax.ejb.Stateless;
import model.CalendarModel;
import model.Event;
import model.UserModel;

@Stateless
public class CalendarManagerImpl implements CalendarManager {

    private void checkWeather(){
        //TODO do       
    }
    
    private void checkConflicts(){
        //TODO do
    }
    
    private void findFreeSlots(){
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
    public boolean addToCalendar(Event event, model.CalendarModel calendar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exportCalendar(model.CalendarModel calendar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void importCalendar(model.CalendarModel calendar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public model.CalendarModel setToDefault(model.CalendarModel calendar, UserModel user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates
        //TODO quado crei un default calendar attenzione a non dargli un nome già esistente
    }
    
}
