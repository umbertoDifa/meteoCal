package bakingBeans;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import EJB.interfaces.InvitationManager;
import EJB.interfaces.SearchManager;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import model.CalendarModel;
import model.Event;
import model.Invitation;
import model.PrivateEvent;
import model.PublicEvent;
import model.UserModel;

/**
 *
 * @author Francesco
 */
@Named(value = "eventToManage")
@ViewScoped
public class ManageEventBacking implements Serializable {

    private static final long serialVersionUID = 1L;

    String idEvent;

    Event eventToCreate;

    String description = "description";
    String location = "Write the location here";
    boolean outdoor;
    boolean publicAccess;
    String title = "initialTitle";
    String startDate = "10/12/2015";
    String endDate = "12/12/2015";
    String startTime = "01:03";
    String endTime = "05:07";
    String calendarName;
    String newGuestEmail = "invita qualcuno";
    List<UserModel> resultUsers;
    boolean displayResultUsers;
    private List<UserModel> noAnswerInvitations = new ArrayList<>();
    private List<UserModel> acceptedInvitations = new ArrayList<>();
    private List<UserModel> declinedInvitations = new ArrayList<>();
    private List<UserModel> publicJoinUsers = new ArrayList<>();

    boolean saved;

    CalendarModel calendar;

    List<UserModel> guests = new ArrayList<>();

    LoginBacking login;

    java.util.Calendar startDateTime;
    java.util.Calendar endDateTime;

    @Inject
    CalendarManager calendarManager;

    @Inject
    EventManager eventManager;

    @Inject
    InvitationManager invitationManager;

    @Inject
    SearchManager searchManager;

    private UserModel newGuest;

    public ManageEventBacking() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        //mi salvo il login per ottenere l'info di chi è loggato
        //e crea o modifica l evento
        login = (LoginBacking) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{login}", LoginBacking.class);

        //initialize event parameters
        title = "initialTitle";
        description = "description";
        location = "location";
        newGuestEmail = "invita qualcuno";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();
        startDate = dateFormat.format(cal.getTime());
        endDate = dateFormat.format(cal.getTime());
        startTime = timeFormat.format(cal.getTime());
        endTime = timeFormat.format(cal.getTime());

    }

    /*
     *
     * GETTERS & SETTERS
     *
     */
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

    public List<UserModel> getGuests() {
        return guests;
    }

    public void setGuests(List<UserModel> guests) {
        this.guests = guests;
    }

    public List<UserModel> getResultUsers() {
        return resultUsers;
    }

    public void setResultUsers(List<UserModel> resultUsers) {
        this.resultUsers = resultUsers;
    }

    public boolean isDisplayResultUsers() {
        return displayResultUsers;
    }

    public void setDisplayResultUsers(boolean displayResultUsers) {
        this.displayResultUsers = displayResultUsers;
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

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
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

    public String getNewGuestEmail() {
        return newGuestEmail;
    }

    public void setNewGuestEmail(String newGuestEmail) {
        this.newGuestEmail = newGuestEmail;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public List<UserModel> getNoAnswerInvitations() {
        return noAnswerInvitations;
    }

    public void setNoAnswerInvitations(List<UserModel> noAnswerInvitations) {
        this.noAnswerInvitations = noAnswerInvitations;
    }

    public List<UserModel> getAcceptedInvitations() {
        return acceptedInvitations;
    }

    public void setAcceptedInvitations(List<UserModel> acceptedInvitations) {
        this.acceptedInvitations = acceptedInvitations;
    }

    public List<UserModel> getDeclinedInvitations() {
        return declinedInvitations;
    }

    public void setDeclinedInvitations(List<UserModel> declinedInvitations) {
        this.declinedInvitations = declinedInvitations;
    }

    public List<UserModel> getPublicJoinUsers() {
        return publicJoinUsers;
    }

    public void setPublicJoinUsers(List<UserModel> publicJoinUsers) {
        this.publicJoinUsers = publicJoinUsers;
    }

    /*
     *
     * METHODS
     *
     */
    /**
     * carica l'istanza dell evento che si vuole modificare
     */
    public void setEditModality() {
        //carico istanza evento specificato in idEvent
        if (idEvent != null) {
            eventToCreate = eventManager.findEventbyId(Long.parseLong(idEvent));
            if (eventToCreate != null) {
                //initialize event parameters
                title = eventToCreate.getTitle();
                description = eventToCreate.getDescription();
                location = eventToCreate.getLocation();
                newGuestEmail = "invita qualcuno";
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat timeFormat = new SimpleDateFormat("HH:mm");
                Calendar cal = Calendar.getInstance();
                startDate = dateFormat.format(eventToCreate.getStartDateTime().getTime());
                endDate = dateFormat.format(eventToCreate.getEndDateTime().getTime());
                startTime = timeFormat.format(eventToCreate.getStartDateTime().getTime());
                endTime = timeFormat.format(eventToCreate.getEndDateTime().getTime());
                setSaved(true);
                publicAccess = eventToCreate instanceof PublicEvent;
                outdoor = eventToCreate.isIsOutdoor();
                setInvitations();
                //inizializzare calendarName
                calendarName = searchCalendarByEvent();

            } else {
                showMessage(null, "Nessun evento trovato", "");
            }
        }
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

    public String save() {
        System.out.println("-dentro save");
        createOrLoadInstance();
        setUpInstance();
        saveIt();

        return "/s/eventPage.xhtml?id=" + idEvent + "&&faces-redirect=true";
        //TODO gestire errori?
    }

    public String deleteEvent() {
        System.out.println("-dentro delete, eventToCreate vale:" + eventToCreate);
        if (eventManager.deleteEvent(eventToCreate)) {
            System.out.println("-evento cancellato");
            return "/s/myCalendar.xhtml";
        } else {
            System.out.println("-evento non cancellato");
            return "";
        }
    }

    public void invite(String emailToInvite) {
        if (emailToInvite != null) {
            UserModel userToInvite = findGuest(resultUsers, emailToInvite);
            System.out.println("--resultUsers è " + resultUsers);
            System.out.println("--emailToInvite è " + emailToInvite);

            if (userToInvite != null) {
                System.out.println("--userToInvite è" + userToInvite);
                if (!guests.contains(userToInvite)) {
                    if (!userToInvite.equals(login.getCurrentUser())) {
                        guests.add(userToInvite);
                    } else {
                        showMessage("inviteForm:email", "partecipi automaticamente ai tuoi eventi", "");
                    }
                } else {
                    showMessage("inviteForm:email", "l'utente è già in lista", "");
                }
            } else {
                showMessage("inviteForm:email", "Nessun utente trovato", "");
            }
            displayResultUsers = false;
        } else {
            System.out.println("--emailToInvite è null");
            showMessage("inviteForm:email", "Specificare un utente", "");
        }
    }

    public void showResultUsers() {
        System.out.println("-newGuestEmail" + newGuestEmail);
        resultUsers = searchManager.searchUsers(newGuestEmail);
        System.out.println("-newGuestEmail" + newGuestEmail);
        System.out.println("-resultUsers" + resultUsers);
        if (resultUsers != null && resultUsers.size() > 0) {
            displayResultUsers = true;
        } else {
            showMessage("inviteForm:email", "Nessun utente trovato", "");
        }
    }

    /**
     * se l'evento è già stato creato ne carica l'istanza solo se non è cambiata
     * la privacy. Se la privacy è stata cambiata creo un nuovo evento con la
     * privacy giusta, il vecchio lo elimino e aggiorno l idEvent. Se l'evento
     * invece è un nuovo evento lo istanzio, sempre in eventToCreate
     */
    private void createOrLoadInstance() {
        //se sto modificando un evento esistente
        if (isSaved()) {
            System.out.println("-dentro createOrL, dentro isSaved, idEvent vale:" + idEvent);
            if (idEvent != null) {
                Event eventFound = eventManager.findEventbyId(Long.parseLong(idEvent));
                if (((eventFound instanceof PublicEvent) && (publicAccess)) || ((eventFound instanceof PrivateEvent) && (!publicAccess))) {
                    eventToCreate = eventFound;
                } else {
                    if (publicAccess) {
                        eventToCreate = new PublicEvent();
                    } else {
                        eventToCreate = new PrivateEvent();
                    }
                    eventToCreate.setId(eventFound.getId());
                }
            } else {
                System.out.println("idEvent è null");
                showMessage(null, "Nessun evento trovato", "");
            }
        } else {
            System.out.println("-dentro createOrL, dentro else di isSaved");
            //se l'utente ha impostato a public l evento
            if (publicAccess) {
                //istanzio un PublicEvent
                eventToCreate = new PublicEvent();
            } else {
                //PrivateEvent altrimenti
                eventToCreate = new PrivateEvent();
            }
        }
    }

    /**
     * imposta tutti i parametri di eventToCreate con i campi del form impostati
     * dall'utente
     */
    private void setUpInstance() {
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
    }

    /**
     * fa persistere l'evento, aggiungere al calendario e creare gli inviti
     * all'eventManager
     */
    private void saveIt() {
        //passo all eventManager l'ownerId, l'evento riempito, il calendario
        //dove metterlo e la lista degli invitati
        System.out.println("-dentro save it");
        if (eventToCreate.getEndDateTime().compareTo(eventToCreate.getStartDateTime()) >= 0) {
            if (isSaved()) {
                eventManager.updateEvent(eventToCreate, calendar, guests);
            } else {
                if (eventManager.scheduleNewEvent(eventToCreate, calendar, guests)) {
                    setSaved(true);
                    idEvent = eventToCreate.getId().toString();
                    showMessage(null, "L'evento è stato salvato", "");
                }
            }
        } else {
            showMessage(login.getCurrentUser().getEmail(), "evento non salvato", "date non corrette");
        }
    }

    private UserModel findGuest(List<UserModel> users, String email) {
        if (users != null) {
            for (UserModel u : users) {
                if (u.getEmail().equals(email)) {
                    return u;
                }
            }
        }
        return null;
    }

    private void showMessage(String recipient, String msg, String advice) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(recipient, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, advice));
    }

    private void setInvitations() {
        if (eventToCreate != null) {
            List<Invitation> invitations = eventToCreate.getInvitations();
            if (invitations != null && invitations.size() > 0) {
                for (Invitation invitation : invitations) {
                    switch (invitation.getAnswer()) {
                        case YES:
                            acceptedInvitations.add(invitation.getInvitee());
                            break;
                        case NO:
                            declinedInvitations.add(invitation.getInvitee());
                            break;
                        case NA:
                            noAnswerInvitations.add(invitation.getInvitee());
                            break;
                        default:
                            noAnswerInvitations.add(invitation.getInvitee());
                            break;
                    }

                }
            }
        }
    }

    private String searchCalendarByEvent() {
        List<CalendarModel> list = calendarManager.getCalendars(login.getCurrentUser());
        for (CalendarModel c : list) {
            if (c.getEventsInCalendar().contains(eventToCreate)) {
                return c.getTitle();
            }
        }
        return null;
    }

    private void checkDates() {
        if (!startDateTime.before(endDateTime)) {
            
        }
    }
}
