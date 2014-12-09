/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import model.CalendarModel;
import model.Event;
import model.PrivateEvent;
import model.PublicEvent;

/**
 *
 * @author Francesco
 */
@Named(value = "newEvent")
@ViewScoped
public class newEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    
    Event eventToCreate;
    
    String description;
    String location;
    boolean outdoor;
    boolean publicAccess;
    String title;
    String startDate;
    String endDate;
    String startTime;
    String endTime;
    
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
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(String date) {
        this.startDate = date;
    }

    public void setEndDate(String date) {
        this.endDate = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setOutdoor(boolean isOutdoor) {
        this.outdoor = isOutdoor;
    }
    
    public void setPublicAccess(boolean isPublic){
        this.publicAccess = isPublic;
    }
    
    public void setStartTime(String time){
        this.startTime = time;
    }

    public void setEndTime(String time){
        this.endTime = time;
    }
    
    public void setInCalendar(String calendarName) {
        List<CalendarModel> calendars = calendarManager.getCalendars(login.getCurrentUser());
        if (calendarName != null) {
            calendar = findCalendarByName(calendars, calendarName);
        } 
    }

    private CalendarModel findCalendarByName(List<CalendarModel> calendars, String name) {
        for (CalendarModel cal : calendars) {
            if (cal.getTitle().equals(name)) {
                return cal;
            }
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public boolean isOutdoor() {
        return outdoor;
    }

    public boolean isPublicAccess() {
        return publicAccess;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
    
    public String getTitle() {
        return title;
    }

    public void save() {
        if(publicAccess){
            eventToCreate = new PublicEvent();
        }else{
            eventToCreate = new PrivateEvent();
        }
        
        startDateTime = Calendar.getInstance();
        String[] startDateToken = startDate.split("-");
        String[] startTimeToken = startTime.split(":");
        startDateTime.set(Integer.parseInt(startDateToken[0]),
                          Integer.parseInt(startDateToken[1]),
                          Integer.parseInt(startDateToken[2]),
                          Integer.parseInt(startTimeToken[0]),
                          Integer.parseInt(startTimeToken[1]), 0);
        
        endDateTime = Calendar.getInstance();
        String[] endDateToken = startDate.split("-");
        String[] endTimeToken = startTime.split(":");
        endDateTime.set(Integer.parseInt(endDateToken[0]),
                          Integer.parseInt(endDateToken[1]),
                          Integer.parseInt(endDateToken[2]),
                          Integer.parseInt(endTimeToken[0]),
                          Integer.parseInt(endTimeToken[1]), 0);  
        
        eventToCreate.setDescription(description);
        eventToCreate.setTitle(title);
        eventToCreate.setLocation(location);
        eventToCreate.setIsOutdoor(outdoor);
        eventToCreate.setStartDateTime(startDateTime);
        eventToCreate.setEndDateTime(endDateTime);
        eventToCreate.setOwner(login.getCurrentUser());
        eventManager.scheduleNewEvent(login.getCurrentUser(), eventToCreate, calendar, null);
    }

}
