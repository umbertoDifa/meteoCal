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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import model.CalendarModel;
import model.Event;
import model.PrivateEvent;
import model.PublicEvent;
import model.UserModel;
import org.primefaces.context.RequestContext;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import utility.DeleteCalendarOption;

@Named(value = "scheduleView")
@ViewScoped
public class ScheduleViewBacking implements Serializable {

    @Inject
    private LoginBacking login;

    @Inject
    private CalendarManager calendarManager;

    @Inject
    private EventManager eventManager;

    @Inject
    private ManageEventBacking manageEvent;

    @Inject
    private SearchManager search;

    private ScheduleModel eventsToShow;

    private ScheduleEvent event = new DefaultScheduleEvent();

    private List<CalendarModel> calendars;

    private List<String> calendarNames;

    private String calendarSelected;

    private CalendarModel calendarShown;

    private String userId;

    private UserModel user;

    private boolean readOnly;

    private boolean messageToAppend = false;
    private String message;
    private String messageTitle;

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
    /**
     * set the user to the current or the one specified in the id loads the
     * calendars set the calendar titles fill the schedule component with the
     * event of the first calendar
     */
    public void init() {
        setUser();
        eventsToShow = new DefaultScheduleModel();

        if (user != null) {
            calendars = calendarManager.getCalendars(user);

            // tolgo i calendari privati
            if (readOnly) {
                for (int i = 0; i < calendars.size(); i++) {
                    if (!calendars.get(i).isIsPublic()) {
                        calendars.remove(i);
                    }
                }
            }

            if (calendars != null && !calendars.isEmpty()) {
                calendarNames = calendarManager.getCalendarTitles(user);
                updateEventsToShow(calendars.get(0));
                System.out.println("-dentro init, calendar è" + calendars);
            }
        }

        System.out.println("messageToAppend è " + messageToAppend);
        if (messageToAppend) {
            showMessage(null, messageTitle, message);
        }
    }

    /**
     * salva l'evento con i parametri impostati dalla pagina myCalendar e faccio
     * un refresh del calendario che la pagina visualizza
     *
     * @param actionEvent
     */
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

        refreshCalendar();
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
            showMessage(null, "Redirect fallita", "");
        }
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }
    
    /**
     * quando il calendario cambia faccio il refresh degli eventi da
     * visualizzare
     */
    public void onCalendarChange() {
        //dal calendarSelected aggiorno gli eventi da visualizzare
        for (CalendarModel cal : calendars) {
            if (cal.getTitle().equals(calendarSelected)) {
                updateEventsToShow(cal);
                calendarShown = cal;
            }
        }

    }

    public void setUser() {
        if (userId != null) {
            if (!Objects.equals(Long.valueOf(userId).longValue(), login.getCurrentUser().getId())) {
                readOnly = true;
                System.out.println("-userId:" + userId);
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

    public void canDeleteCalendar() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('confirmationDialog').hide();");
        System.out.println("DENTRO canDeleteCalendar");
        if (calendarManager.isDefault(calendarShown)) {
            System.out.println(calendarShown.getTitle() + " è default");
            showMessage(null, "Cannot Delete Default Calendar", "You cannot delete the default calendar. Please make default another calenar and then remove this one.");
        } else {
            System.out.println(calendarShown.getTitle() + "non è default");
            context.execute("PF('delOpt').show();");
        }
    }

    public void deleteCalendar(String response) {
        System.out.println("-DENTRO deleteCalendar");
        DeleteCalendarOption option = DeleteCalendarOption.valueOf(response);
        if (calendarManager.deleteCalendar(calendarShown, option)) {
            messageTitle = "Calendar Deleted";
            message = "Your calendar has been succesfully deleted";
            messageToAppend = true;

            init();
        } else {
            messageTitle = "Cannot delete calendar";
            message = "An error has occured.";
            messageToAppend = true;
        }

    }

    /**
     * aggiorno gli eventi (del calendario cal) che lo schedule visualizza
     *
     * @param cal
     */
    private void updateEventsToShow(CalendarModel cal) {
        eventsToShow.clear();
        System.out.println("Dentro updateEvent e eventToShow dopo clear = " + eventsToShow.getEventCount());
        cal = calendarManager.getCalendarUpdated(cal);
        for (Event ev : calendarManager.getEventsUpdated(cal)) {
            System.out.println("Evento caricato è " + ev.getTitle());
            calendarShown = cal;
            calendarSelected = cal.getTitle();
            // se l'evento è privato e non sei l'owner
            if (ev instanceof PrivateEvent && readOnly) {
                // creo una slot senza dettagli
                System.out.println("-creata slot: inizia a " + ev.getStartDateTime().getTime() + " e finisce a "
                        + ev.getEndDateTime().getTime());
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

    private void showMessage(String recipient, String msg, String advice) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(recipient, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, advice));
        RequestContext.getCurrentInstance().update("growl");
    }

    private void refreshCalendar() {
        calendarShown = calendarManager.findCalendarByName(user, calendarShown.getTitle());
        updateEventsToShow(calendarShown);
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

}
