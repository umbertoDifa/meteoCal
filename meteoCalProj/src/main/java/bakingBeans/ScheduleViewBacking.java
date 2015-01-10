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
import utility.GrowlMessage;

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
    private ManageEventBacking manageEventBacking;

    @Inject
    private SearchManager search;

    private ScheduleModel eventsToShow;

    private ScheduleEvent eventToManage = new DefaultScheduleEvent();

    private List<CalendarModel> calendars;

    private List<String> calendarNames;

    private String calendarSelected;

    private CalendarModel calendarShown;

    private String userId;

    private UserModel user;

    private boolean readOnly;

    private String labelPrivacy;

    private CalendarModel calendarToCreate;

    /*
     *
     * SETTERS & GETTERS
     *
     */
    public CalendarModel getCalendarToCreate() {
        return calendarToCreate;
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

    public ScheduleEvent getEvent() {
        return eventToManage;
    }

    public void setEvent(ScheduleEvent event) {
        this.eventToManage = event;
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

    public String getLabelPrivacy() {
        return labelPrivacy;
    }

    /*
     *
     * METHODS
     *
     */
    /**
     * set the user to the current or the one specified in the id, loads the
     * calendars, set the calendar titles, fill the schedule component with the
     * event of the default calendar
     */
    public void init() {
        setUser();
        calendarToCreate = new CalendarModel();
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
                updateEventsToShow(calendarManager.getDefaultCalendar(user));
                System.out.println("-dentro init, calendar è" + calendars);
            }
        }
        switchLabel();
    }

    /**
     * salva l'evento eventToManage con i parametri impostati dalla pagina 
     * myCalendar e faccio un refresh del calendario che la pagina visualizza
     *
     * @param actionEvent
     */
    public void updateEvent(ActionEvent actionEvent) {

        manageEventBacking.setTitle(eventToManage.getTitle());
        manageEventBacking.setCalendarName(calendarSelected);
        if (eventToManage.getData() != null && ((EventDetails) eventToManage.getData()).getId() != null) {
            manageEventBacking.setIdEvent(Objects.toString(((EventDetails) eventToManage.getData()).getId()));
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        manageEventBacking.setStartDate(dateFormat.format(eventToManage.getStartDate()));
        manageEventBacking.setStartTime(timeFormat.format(eventToManage.getStartDate()));
        manageEventBacking.setEndDate(dateFormat.format(eventToManage.getEndDate()));
        manageEventBacking.setEndTime(timeFormat.format(eventToManage.getEndDate()));
        manageEventBacking.save(); //TODO bisogna chiamare check o levare

        refreshCalendar();
    }

    /**
     * chiamato quando un evento viene selezionato dal componente schedule
     * reindirizza alla pagina dell evento se l utente è il proprietario
     * dell evento o se l evento è pubblico
     * @param selectEvent 
     */
    public void onEventSelect(SelectEvent selectEvent) {
        eventToManage = (ScheduleEvent) selectEvent.getObject();
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        try {
            // se l evento è public o sei l owner
            if ((eventToManage.getData() != null && ((EventDetails) eventToManage.getData()).isPub()) || !readOnly) {
                context.redirect(context.getRequestContextPath() + "/s/eventPage.xhtml?id=" + ((EventDetails) eventToManage.getData()).getId());
            }
        } catch (IOException ex) {
            showGrowl(GrowlMessage.ERROR_REDIRECT);
        }
    }

    /**
     * chiamato quando un utente seleziona una data, uno slot libero
     * dal componente schedule. inizializza eventToManage
     * @param selectEvent 
     */
    public void onDateSelect(SelectEvent selectEvent) {
        eventToManage = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
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

    /**
     * se il parametro userId è impostato imposto la pagina calendar
     * in modalità visitatore e carico gli eventi dell utente che corrisponde
     * allo userId. Altrimenti lo imposto come calendario dell utente loggato
     */
    public void setUser() {
        if (userId != null) {
            if (!Objects.equals(Long.valueOf(userId).longValue(), login.getCurrentUser().getId())) {
                readOnly = true;
                user = search.findUserById(Long.valueOf(userId).longValue());
                if (user == null) {
                    showGrowl(GrowlMessage.ERROR_USER);
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
        if (calendarManager.isDefault(calendarShown)) {
            showGrowl(GrowlMessage.NOT_DELETE_DEFAULT);
        } else {
            context.execute("PF('delOpt').show();");
        }
    }

    public void deleteCalendar(String response) {
        DeleteCalendarOption option = DeleteCalendarOption.valueOf(response);
        if (calendarManager.deleteCalendar(calendarShown, option)) {
            showGrowl(GrowlMessage.CALENDAR_DELETED);
            init();
        } else {
            showGrowl(GrowlMessage.ERROR_DELETE);

        }

    }

    /**
     * aggiorno gli eventi (del calendario cal) che lo schedule visualizza
     *
     * @param cal
     */
    private void updateEventsToShow(CalendarModel cal) {
        eventsToShow.clear();
        cal = calendarManager.getCalendarUpdated(cal);
        calendarShown = cal;
        if (calendarManager.getEventsUpdated(cal) != null) {
            for (Event ev : calendarManager.getEventsUpdated(cal)) {
                calendarShown = cal;
                calendarSelected = cal.getTitle();
                // se l'evento è privato e non sei l'owner
                if (ev instanceof PrivateEvent && readOnly) {
                    // creo una slot senza dettagli
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

    }

    private void showGrowl(GrowlMessage growl) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(growl.getSeverity(), growl.getTitle(), growl.getMessage()));
        RequestContext.getCurrentInstance().update("growl");
    }

    private void showMsg(GrowlMessage growl) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage("msg", new FacesMessage(growl.getSeverity(), growl.getTitle(), growl.getMessage()));
        RequestContext.getCurrentInstance().update("growl");
    }

//    private void showGrowl(String recipient, String msg, String advice, FacesMessage.Severity severity) {
//        FacesContext ctx = FacesContext.getCurrentInstance();
//        ctx.addMessage(recipient, new FacesMessage(severity, msg, advice));
//        RequestContext.getCurrentInstance().update("growl");
//    }
    
    /**
     * faccio un refresh del calendario e degli eventi visualizzati nello
     * schedule
     */
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

    /**
     * imposta il calendario visualizzato correntemente come calendario
     * di default
     */
    public void makeDefault() {
        if (calendarManager.makeDefault(calendarShown)) {
            calendarShown = calendarManager.getCalendarUpdated(calendarShown);
            showGrowl(GrowlMessage.DEFAUL_CHANGED);
        } else {
            showGrowl(GrowlMessage.GENERIC_ERROR);
        }
        RequestContext context = RequestContext.getCurrentInstance();
        //context.execute("PF('confirmChangeDef').hide();");
    }

    /**
     * cambia privacy al calendario visualizzato correntemente
     */
    public void switchPrivacy() {
        System.out.println("Privacy: " + calendarShown.isIsPublic());
        calendarManager.toggleCalendarPrivacy(calendarShown);
        calendarShown = calendarManager.getCalendarUpdated(calendarShown);
        System.out.println("Privacy: " + calendarShown.isIsPublic());
        switchLabel();

        if (calendarShown.isIsPublic()) {
            showGrowl(GrowlMessage.CALENDAR_SWITCHED_TO_PUBLIC);
        } else {
            showGrowl(GrowlMessage.CALENDAR_SWITCHED_TO_PRIVATE);
        }
    }

    /**
     * cambia l etichetta del pulsante per cambiare la privacy del calendario
     */
    private void switchLabel() {
        if (calendarShown.isIsPublic()) {
            labelPrivacy = "Change to Private";
        } else {
            labelPrivacy = "Change to Public";
        }
    }

    /**
     * aggiunge un nuovo calendario con i parametri impostati dal
     * newCalendarDialog
     */
    public void addNewCalendar() {
        System.out.println("dentro addNewCalendar, calendarToCreate: " + calendarToCreate.toString());

        if (calendarToCreate.getTitle() != null && !calendarToCreate.getTitle().isEmpty()) {
            calendarToCreate.setOwner(user);
            if (calendarManager.addCalendarToUser(calendarToCreate)) {
                showGrowl(GrowlMessage.CALENDAR_CREATED);
                calendars = calendarManager.getCalendars(user);
                init();

            } else {
                showGrowl(GrowlMessage.CALENDAR_EXISTS);
            }
        } else {

        }
        calendarToCreate = new CalendarModel();
//        RequestContext context = RequestContext.getCurrentInstance();
//        context.execute("PF('newCalendarDialog').hide();");
    }

}
