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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import model.PrivateEvent;
import model.PublicEvent;
import model.UserModel;
import org.primefaces.context.RequestContext;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import utility.DeleteCalendarOption;

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

    private boolean readOnly;

    /*
     *
     * SETTERS & GETTERS
     *
     */
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

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /*
     *
     * METHODS
     *
     */
    public void init() {
        setUser();
        eventsToShow = new DefaultScheduleModel();

        if (user != null) {
            calendars = calendarManager.getCalendars(user);

            // tolgo i calendari privati
            if (readOnly) {
                for (CalendarModel calendar : calendars) {
                    if (!calendar.isIsPublic()) {
                        calendars.remove(calendar);
                    }
                }
            }

            if (calendars != null && !calendars.isEmpty()) {
                calendarNames = calendarManager.getCalendarTitles(user);
                updateEventsToShow(calendars.get(0));
            }
        }

    }

    public void updateEvent(ActionEvent actionEvent) {

        manageEvent.setTitle(event.getTitle());
        manageEvent.setCalendarName(calendarSelected);
        if (event.getData() != null && ((EventDetails) event.getData()).getId() != null) {
            manageEvent.setIdEvent(Objects.toString(((EventDetails) event.getData()).getId()));
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        manageEvent.setStartDate(dateFormat.format(event.getStartDate()));
        manageEvent.setStartTime(timeFormat.format(event.getStartDate()));
        manageEvent.setEndDate(dateFormat.format(event.getEndDate()));
        manageEvent.setEndTime(timeFormat.format(event.getEndDate()));
        manageEvent.save();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        try {
            // se l evento è public o sei l owner
            if ((event.getData() != null && ((EventDetails) event.getData()).isPub()) || !readOnly) {
                context.redirect(context.getRequestContextPath() + "/s/eventPage.xhtml?id=" + ((EventDetails) event.getData()).getId());
            }
        } catch (IOException ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Redirect fallita", "");
            addMessage(message);
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
        System.out.println ("DENTRO onCalendarChange");
        //dal calendarSelected aggiorno gli eventi da visualizzare
        for (CalendarModel cal : calendars) {
            if (cal.getTitle().equals(calendarSelected)) {
                updateEventsToShow(cal);
                calendarShown = cal;
            }
        }

    }

    private void updateEventsToShow(CalendarModel cal) {
        eventsToShow.clear();

        for (Event ev : cal.getEventsInCalendar()) {
            calendarShown = cal;
            calendarSelected = cal.getTitle();
            // se l'evento è privato e non sei l'owner
            if (ev instanceof PrivateEvent && readOnly) {
                // creo una slot senza dettagli
                System.out.println("-creata slot: inizia a "+ev.getStartDateTime().getTime()+" e finisce a "+
                        ev.getEndDateTime().getTime());
                DefaultScheduleEvent e = new DefaultScheduleEvent(
                        "evento privato",
                        ev.getStartDateTime().getTime(),
                        ev.getEndDateTime().getTime(),
                        new EventDetails(ev.getId(), (ev instanceof PublicEvent)));
                eventsToShow.addEvent(e);
            } else {
                DefaultScheduleEvent e = new DefaultScheduleEvent(
                        ev.getTitle(),
                        ev.getStartDateTime().getTime(),
                        ev.getEndDateTime().getTime(),
                        new EventDetails(ev.getId(), (ev instanceof PublicEvent)));
                e.setDescription(ev.getDescription());
                eventsToShow.addEvent(e);
            }

        }

    }

    private Calendar dateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public void setUser() {
        if (userId != null) {
            if (!Objects.equals(Long.valueOf(userId).longValue(), login.getCurrentUser().getId())) {
                readOnly = true;
                System.out.println("-userId:"+ userId);
                System.out.println("-userId castato to long:" + Long.valueOf(userId).longValue());
                user = search.findUserById(Long.valueOf(userId).longValue());
                if (user == null) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Nessun utente trovato"));
                    ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
                    try {
                        context.redirect(context.getRequestContextPath() + "error.xhtml");
                    } catch (IOException ex) {
                        Logger.getLogger(ScheduleViewBacking.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Redirect fallita");
                    }
                }
            } else {
                user = login.getCurrentUser();
            }
        } else {
            user = login.getCurrentUser();
        }
    }

    private class EventDetails {

        private Long id;
        private boolean pub;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public boolean isPub() {
            return pub;
        }

        public void setPub(boolean pub) {
            this.pub = pub;
        }

        public EventDetails(Long id, boolean pub) {
            this.id = id;
            this.pub = pub;
        }

    }
    
        private void showMessage(String recipient, String msg, String advice) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(recipient, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, advice));
    }

    public void canDeleteCalendar() {
        System.out.println ("DENTRO canDeleteCalendar");
        if (calendarManager.isDefault(calendarShown)) {
            System.out.println (calendarShown.getTitle()+" è default");
            showMessage(null, "Cannot Delete Default Calendar", "You cannot delete the default calendar. Please make default another calenar and then remove this one.");
        } else {
            System.out.println (calendarShown.getTitle()+ "non è default");
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('delOpt').show();");
        }
    }
    
    public void deleteCalendar ( String response) {
        System.out.println ("DENTRO deleteCalendar");
        DeleteCalendarOption option = DeleteCalendarOption.valueOf(response);
        if (calendarManager.deleteCalendar(calendarShown, option))
            showMessage(null, "Calendar Deleted", "Your calendar has been succesfully deleted");
        else
            showMessage(null, "Cannot delete calendar", "An error has occured.");
        
    }
}
