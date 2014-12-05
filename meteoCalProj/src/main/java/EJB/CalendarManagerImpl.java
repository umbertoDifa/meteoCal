package EJB;

import EJB.interfaces.CalendarManager;
import java.util.Calendar;
import java.util.List;
import model.Event;
import model.User;


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
    public List<String> getCalendarsName(User user) {
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
    public boolean addToCalendar(Event event, model.Calendar calendar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exportCalendar(model.Calendar calendar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void importCalendar(model.Calendar calendar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
