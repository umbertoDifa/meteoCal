package bakingBeans;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.faces.bean.ManagedProperty;
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
public class ManageEventBacking implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty("#{newEvent.idEvent}")
    String idEvent;

    Event eventToCreate;

    String description = "description";
    String location = "location";
    boolean outdoor;
    boolean publicAccess;
    String title = "initialTitle";
    String startDate = "10/12/2015";
    String endDate = "12/12/2015";
    String startTime = "01:03";
    String endTime = "05:07";
    String calendarName;

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
    public ManageEventBacking() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        //mi salvo il login per ottenere l'info di chi è loggato
        //e crea o modifica l evento
        login = (LoginBacking) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{login}", LoginBacking.class);
    }

    public void setEditModality() {
        //riempire campi title,location etc con
        //quelli dell evento con id specificato
        //nel param
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

    public void setPublicAccess(boolean isPublic) {
        this.publicAccess = isPublic;
    }

    public void setStartTime(String time) {
        this.startTime = time;
    }

    public void setEndTime(String time) {
        this.endTime = time;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setCalendarName(String nameCal) {
        this.calendarName = nameCal;
    }

    public void setInCalendar(String calendarName) {
        List<CalendarModel> calendars = calendarManager.getCalendars(login.getCurrentUser());
        if (calendarName != null) {
            //salvo in calendar l istanza di Calendar che appartiene all utente
            //e che ha il title specificato nel form
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
        //se l'utente ha impostato a public l evento
        if (publicAccess) {
            //istanzio un PublicEvent
            eventToCreate = new PublicEvent();
        } else {
            //PrivateEvent altrimenti
            eventToCreate = new PrivateEvent();
        }

        //creo un Calendar per l'inizio
        startDateTime = Calendar.getInstance();
        String[] startDateToken = startDate.split("-");
        String[] startTimeToken = startTime.split(":");
        //lo setto all'anno, al mese (contato da zero), giorno e ora, min e secondi
        startDateTime.set(Integer.parseInt(startDateToken[0]),
                Integer.parseInt(startDateToken[1]) - 1,
                Integer.parseInt(startDateToken[2]),
                Integer.parseInt(startTimeToken[0]),
                Integer.parseInt(startTimeToken[1]), 0);

        //creo un Calendar per la fine
        endDateTime = Calendar.getInstance();
        String[] endDateToken = endDate.split("-");
        String[] endTimeToken = endTime.split(":");
        //lo setto all'anno, al mese (contato da zero), giorno e ora, min e secondi
        endDateTime.set(Integer.parseInt(endDateToken[0]),
                Integer.parseInt(endDateToken[1]) - 1,
                Integer.parseInt(endDateToken[2]),
                Integer.parseInt(endTimeToken[0]),
                Integer.parseInt(endTimeToken[1]), 0);

        //riempio un entità di Event con i vari attributi
        eventToCreate.setDescription(description);
        eventToCreate.setTitle(title);
        eventToCreate.setLocation(location);
        eventToCreate.setIsOutdoor(outdoor);
        eventToCreate.setStartDateTime(startDateTime);
        eventToCreate.setEndDateTime(endDateTime);
        eventToCreate.setOwner(login.getCurrentUser());

        //setto calendar all'entità corrispondente al calendarName
        setInCalendar(calendarName);

        //passo all eventManager l'ownerId, l'evento riempito, il calendario
        //dove metterlo e la lista degli invitati
        eventManager.scheduleNewEvent(eventToCreate, calendar, null);
    }

}
