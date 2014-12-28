/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import EJB.interfaces.SearchManager;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import model.CalendarModel;
import model.Event;
import model.UserModel;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

@Named(value = "scheduleView")
@SessionScoped
public class ScheduleViewBacking implements Serializable {

    @Inject
    LoginBacking login;

    @Inject
    CalendarManager calendarManager;

    @Inject
    EventManager eventManager;

    @Inject
    ManageEventBacking manageEvent;
    
    @Inject
    SearchManager search;

    private ScheduleModel eventsToShow;

    private ScheduleEvent event = new DefaultScheduleEvent();

    private List<CalendarModel> calendars;

    private List<String> calendarNames;

    private String calendarSelected;

    private CalendarModel calendarShown;

    private String userId;
    
    private UserModel user;

    @PostConstruct
    public void init() {
        eventsToShow = new DefaultScheduleModel();

        calendars = login.getCurrentUser().getOwnedCalendars();

        if (calendars != null && calendars.size() > 0) {
            calendarNames = titlesCalendar(calendars);
            updateEventsToShow(calendars.get(0));
        }
    }

    public Date getRandomDate(Date base) {
        Calendar date = Calendar.getInstance();
        date.setTime(base);
        date.add(Calendar.DATE, ((int) (Math.random() * 30)) + 1);    //set random day of month

        return date.getTime();
    }

    public Date getInitialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);
        return calendar.getTime();
    }

    public ScheduleModel getEventsToShow() {
        return eventsToShow;
    }

    public void setCalendarNames(List<String> calendarNames) {
        this.calendarNames = calendarNames;
    }

    public List<String> getCalendarNames() {
        return calendarNames;
    }

    public String getCalendarSelected() {
        return calendarSelected;
    }

    public void setCalendarSelected(String calendarSelected) {
        this.calendarSelected = calendarSelected;
    }

    private Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public CalendarModel getCalendarShown() {
        return calendarShown;
    }

    public void setCalendarShown(CalendarModel calendarShown) {
        this.calendarShown = calendarShown;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void updateEvent(ActionEvent actionEvent) {

        manageEvent.setTitle(event.getTitle());
        manageEvent.setCalendarName(calendarSelected);
        if (event.getData() != null) {
            manageEvent.setIdEvent((String) event.getData());
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        manageEvent.setStartDate(dateFormat.format(event.getStartDate()));
        manageEvent.setStartTime(timeFormat.format(event.getStartDate()));
        manageEvent.setEndDate(dateFormat.format(event.getEndDate()));
        manageEvent.setEndTime(timeFormat.format(event.getEndDate()));
        manageEvent.save();

//        //se l'evento è nuovo
//        if (event.getId() == null) {
//            //lo istanzio, privato, senza invitati e non all'aperto
//            Event eventToCreate = new PrivateEvent(event.getTitle(),
//                    dateToCalendar(event.getStartDate()),
//                    dateToCalendar(event.getEndDate()),
//                    null,
//                    event.getDescription(),
//                    false,
//                    login.getCurrentUser());
//            //lo persisto            
//            eventManager.scheduleNewEvent(eventToCreate, calendarManager.findCalendarByName(login.getCurrentUser(), calendarSelected), null);
//            //lo visualizzo
//            eventsToShow.addEvent(event);
//        } else {
//            //lo cerco
//            Event eventToUpdate = eventManager.findEventbyId((Long) event.getData());
//            //lo aggiorno nel db
//            eventManager.updateEvent(eventToUpdate, calendarManager.findCalendarByName(login.getCurrentUser(), calendarSelected));
//            //lo aggiorno a video
//            eventsToShow.updateEvent(event);
//        }
//
//        event = new DefaultScheduleEvent();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        try {
            context.redirect(context.getRequestContextPath() + "/s/eventPage.xhtml?id=" + event.getData());
        } catch (IOException ex) {
            //TODO msg error
        }
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {

        updateEvent(null);
        //persisto l event con manageEventBacking
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {

        updateEvent(null);
        //persisto l event con manageEventBacking
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onCalendarChange() {
        //dal calendarSelected aggiorno gli eventi da visualizzare
        for (CalendarModel cal : calendars) {
            if (cal.getTitle().equals(calendarSelected)) {
                updateEventsToShow(cal);
                calendarShown = cal;
            }
        }

    }

    private List<String> titlesCalendar(List<model.CalendarModel> c) {
        List<String> result = new ArrayList<>();
        if (c != null) {
            for (model.CalendarModel b : c) {
                result.add(b.getTitle());
            }
        } else {
            System.out.println("Lista calendari null");
        }
        return result;
    }

    private void updateEventsToShow(CalendarModel cal) {
        eventsToShow.clear();

        for (Event ev : cal.getEventsInCalendar()) {
            calendarShown = cal;
            calendarSelected = cal.getTitle();
            DefaultScheduleEvent e = new DefaultScheduleEvent(ev.getTitle(), ev.getStartDateTime().getTime(), ev.getEndDateTime().getTime(), ev.getId());
            e.setDescription(ev.getDescription());
            eventsToShow.addEvent(e);
        }

    }

    private Calendar dateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
    
    public void setUser(){
        //user = search.findUserbyId(userId);
        
    }
}
