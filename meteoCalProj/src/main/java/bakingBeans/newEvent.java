/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import java.util.Calendar;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import model.CalendarModel;
import model.Event;
import model.PrivateEvent;

/**
 *
 * @author Francesco
 */
@Named(value = "newEvent")
@ViewScoped
public class newEvent {

    Event newEvent;

    CalendarModel calendar;

    LoginBacking login;
    
    java.util.Calendar startDateTime;
    
    java.util.Calendar endDateTime;
    
    @Inject
    CalendarManager calendarManager;
    
    @Inject
    EventManager eventManager;

    /**
     * Creates a new instance of newEvent
     */
    public newEvent() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        login = (LoginBacking) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{login}", LoginBacking.class);

        newEvent = new PrivateEvent();
        newEvent.setOwner(login.getCurrentUser());
    }

    public void setTitle(String title) {
        newEvent.setTitle(title);
    }

    public void setDescription(String description) {
        newEvent.setDescription(description);
    }

    public void setStartingDate(String date) {
        System.out.println("[fra]"+date+"[fra]");
        //startDateTime = Calendar.getInstance().set(year, month, date);
    }

    public void setEndDateTime(String date) {
        
    }

    public void setLocation(String location) {
        newEvent.setLocation(location);
    }

    public void setOutdoor(boolean isOutdoor) {
        newEvent.setIsOutdoor(isOutdoor);
    }

    public void setInCalendar(String calendarName) {

        List<CalendarModel> calendars = calendarManager.getCalendars(login.getCurrentUser());
        if (calendarName != null) {
            calendar = findCalendarByName(calendars, calendarName);
        }
    }
    
    private CalendarModel findCalendarByName(List<CalendarModel> calendars, String name){
        for(CalendarModel cal: calendars){
            if(cal.getTitle().equals(name)){
                return cal;
            }
        }
        return null;
    }
    
    public void save(){
    }

}
