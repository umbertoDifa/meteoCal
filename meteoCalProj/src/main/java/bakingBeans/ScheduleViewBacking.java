/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.CalendarManagerImpl;
import EJB.interfaces.CalendarManager;
import EJB.interfaces.DeleteManager;
import EJB.interfaces.SearchManager;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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
import utility.LoggerLevel;
import utility.LoggerProducer;

@Named(value = "scheduleView")
@ViewScoped
public class ScheduleViewBacking implements Serializable {

    @Inject
    private LoginBacking login;

    @Inject
    private CalendarManager calendarManager;

    @Inject
    private ManageEventBacking manageEventBacking;

    @Inject
    private SearchManager search;

    @Inject
    private DeleteManager deleteManager;

    private ScheduleModel eventsToShow;

    private ScheduleEvent eventToManage = new DefaultScheduleEvent();

    private List<CalendarModel> calendars;

    private List<String> calendarNames;

    private String calendarSelected;

    private CalendarModel calendarShown;

    private String id;

    private UserModel user;

    private boolean readOnly;

    private String labelPrivacy;

    private CalendarModel calendarToCreate;

    private Logger logger = LoggerProducer.debugLogger(ScheduleViewBacking.class);


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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        // imposto se guardi il tuo calendario o il calendario di un altro utente
        // specificato nell id dell url
        setUser();
        calendarToCreate = new CalendarModel();
        // inizializzo la lista degli eventi che saranno visibili nello schedule
        eventsToShow = new DefaultScheduleModel();

        if (user != null) {
            // salvo i calendari dell utente
            calendars = calendarManager.getCalendars(user);

            // tolgo i calendari privati se visitatore
            if (readOnly) {
                for (int i = 0; i < calendars.size(); i++) {
                    if (!calendars.get(i).isIsPublic()) {
                        calendars.remove(i);
                    }
                }
            }

            if (calendars != null && !calendars.isEmpty()) {
                //riempio la tendina
                calendarNames = calendarManager.getCalendarTitles(user);
                //riempio la lista di eventi visibili nello schedule
                updateEventsToShow(calendarManager.getDefaultCalendar(user));
                calendarSelected = calendarManager.getDefaultCalendar(user).getTitle();
                calendarShown = calendarManager.getDefaultCalendar(user);
            }
            switchLabel();
        }
        //inizializzo etichetta pulsante cambio privacy
        logger.log(LoggerLevel.DEBUG, "dentro init. user: "+user+" . calendarShown: "+calendarShown+" .");
        
    }

    /**
     * chiamato quando un evento viene selezionato dal componente schedule
     * reindirizza alla pagina dell evento se l utente è il proprietario dell
     * evento o se l evento è pubblico
     *
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
     * when a date is Selected it redirects to the creation of a new event
     *
     * @param selectEvent
     */
    public void onDateSelect(SelectEvent selectEvent) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        try {
            context.redirect(context.getRequestContextPath() + "/s/manageEvent.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(ScheduleViewBacking.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Redirect fallita");
        }
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
     * se il parametro userId è impostato imposto la pagina calendar in modalità
     * visitatore e carico gli eventi dell utente che corrisponde allo userId.
     * Altrimenti lo imposto come calendario dell utente loggato
     */
    public void setUser() {
        if (id != null) {
            // provo a confrontare l'id con quello dell utente loggato
            try {
                if (!Objects.equals(Long.valueOf(id).longValue(), login.getCurrentUser().getId())) {
                    readOnly = true;
                    user = search.findUserById(Long.valueOf(id).longValue());
                    if (user == null) {
                        showGrowl(GrowlMessage.ERROR_USER);
                        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
                        try {
                            context.redirect(context.getRequestContextPath() + "/error.xhtml?faces-redirect=true");
                        } catch (IOException ex) {
                            Logger.getLogger(ScheduleViewBacking.class.getName()).log(Level.SEVERE, null, ex);
                            logger.log(LoggerLevel.DEBUG, "redirect fallita");
                        }
                    }
                } else {
                    user = login.getCurrentUser();
                }
            } catch (NumberFormatException ex) {
                showGrowl(GrowlMessage.ERROR_USER);
                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
                try {
                    context.redirect(context.getRequestContextPath() + "/error.xhtml");
                } catch (IOException e) {
                    Logger.getLogger(ScheduleViewBacking.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        } else {
            user = login.getCurrentUser();
        }
    }

    /**
     * verifica se è possibile cancellare il calendario visualizzato
     * correntemente
     */
    public void canDeleteCalendar() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('confirmationDialog').hide();");
        if (calendarManager.isDefault(calendarShown)) {
            showGrowl(GrowlMessage.NOT_DELETE_DEFAULT);
        } else {
            context.execute("PF('delOpt').show();");
        }
    }

    /**
     * cancella il calendario correntemente visualizzato
     *
     * @param response
     */
    public void deleteCalendar(String response) {
        DeleteCalendarOption option = DeleteCalendarOption.valueOf(response);
        if (deleteManager.deleteCalendar(login.getCurrentUser(), calendarShown, option)) {
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
                            "Busy Slot",
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

    /**
     * mostra un messaggio nel growl
     *
     * @param growl
     */
    private void showGrowl(GrowlMessage growl) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(growl.getSeverity(), growl.getTitle(), growl.getMessage()));
        RequestContext.getCurrentInstance().update("growl");
    }

    /**
     * faccio un refresh del calendario e degli eventi visualizzati nello
     * schedule
     */
    private void refreshCalendar() {
        calendars = calendarManager.getCalendars(user);
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
     * imposta il calendario visualizzato correntemente come calendario di
     * default
     */
    public void makeDefault() {
        if (calendarManager.makeDefault(calendarShown)) {
            refreshCalendar();
            showGrowl(GrowlMessage.DEFAULT_CHANGED);
        } else {
            showGrowl(GrowlMessage.GENERIC_ERROR);
        }

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
    }

}
