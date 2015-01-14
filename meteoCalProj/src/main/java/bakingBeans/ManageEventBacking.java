package bakingBeans;

import EJB.CalendarManagerImpl;
import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import EJB.interfaces.InvitationManager;
import EJB.interfaces.SearchManager;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
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
import org.primefaces.context.RequestContext;
import utility.ControlMessages;
import utility.LoggerLevel;
import utility.LoggerProducer;
import utility.TimeTool;

/**
 *
 * @author Francesco
 */
@Named(value = "eventToManage")
@ViewScoped
public class ManageEventBacking implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private Event eventToCreate;

    private String description;

    @Inject
    private Place place;

    //luogo dell'evento
    private String location;
    //boolean che indica se l'utente ha selezionato un luogo con l'aiuto di google
    private boolean hasLocation;

    private boolean outdoor;
    private boolean publicAccess;
    private String title;

    private String startDate;
    private String endDate;

    private Calendar rescheduleDayStart;
    private Calendar rescheduleDayEnd;

    private String startTime;
    private String endTime;
    private String calendarName;
    private String newGuestEmail;
    private List<UserModel> resultUsers;
    private boolean displayResultUsers;
    private List<UserModel> noAnswerInvitations = new ArrayList<>();
    private List<UserModel> acceptedInvitations = new ArrayList<>();
    private List<UserModel> declinedInvitations = new ArrayList<>();
    private List<UserModel> publicJoinUsers = new ArrayList<>();

    private boolean saved;

    //variabili dialogue box
    private String dialogueMessage;
    private String saveButton;
    private String rescheduleButton;
    private boolean showRescheduleButton;

    private CalendarModel calendar;

    private List<UserModel> guests = new ArrayList<>();

    @Inject
    private LoginBacking login;

    private java.util.Calendar startDateTime;
    private java.util.Calendar endDateTime;

    @Inject
    private CalendarManager calendarManager;

    @Inject
    private EventManager eventManager;

    @Inject
    private InvitationManager invitationManager;

    @Inject
    private SearchManager searchManager;

    private Logger logger = LoggerProducer.debugLogger(CalendarManagerImpl.class);

    private UserModel newGuest;

    public ManageEventBacking() {
        //initialize event parameters;
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

    public String getDialogueMessage() {
        return dialogueMessage;
    }

    public String getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(String saveButton) {
        this.saveButton = saveButton;
    }

    public String getRescheduleButton() {
        return rescheduleButton;
    }

    public void setRescheduleButton(String rescheduleButton) {
        this.rescheduleButton = rescheduleButton;
    }

    public void setDialogueMessage(String dialogueMessage) {
        this.dialogueMessage = dialogueMessage;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isShowRescheduleButton() {
        return showRescheduleButton;
    }

    public void setShowRescheduleButton(boolean showRescheduleButton) {
        this.showRescheduleButton = showRescheduleButton;
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

    public boolean isHasLocation() {
        return hasLocation;
    }

    public void setHasLocation(boolean hasLocation) {
        this.hasLocation = hasLocation;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        //carico istanza evento specificato in id
        if (id != null) {
            eventToCreate = eventManager.findEventbyId(Long.parseLong(id));
            if (eventToCreate != null) {
                //initialize event parameters
                title = eventToCreate.getTitle();
                description = eventToCreate.getDescription();
                location = eventToCreate.getLocation();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat timeFormat = new SimpleDateFormat("HH:mm");
                Calendar cal = Calendar.getInstance();
                startDate = dateFormat.format(
                        eventToCreate.getStartDateTime().getTime());
                endDate = dateFormat.format(
                        eventToCreate.getEndDateTime().getTime());
                startTime = timeFormat.format(
                        eventToCreate.getStartDateTime().getTime());
                endTime = timeFormat.format(
                        eventToCreate.getEndDateTime().getTime());
                setSaved(true);
                publicAccess = eventToCreate instanceof PublicEvent;
                outdoor = eventToCreate.isIsOutdoor();
                setInvitations();
                //inizializzare calendarName
                calendarName = searchCalendarByEvent();

                //initialize hasLocation
                hasLocation = eventToCreate.hasLocation();

            } else {
                showMessage(null, "Nessun evento trovato", "");
            }
        }
    }

    public void setInCalendar(String calendarName) {
        List<CalendarModel> calendars = calendarManager.getCalendars(
                login.getCurrentUser());
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

    public void save() {
        logger.log(LoggerLevel.DEBUG, "dentro save");

        saveIt();
        logger.log(LoggerLevel.DEBUG, "dopo saveit");

        //redirect
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        try {
            context.redirect(context.getRequestContextPath()
                    + "/s/eventPage.xhtml?id=" + id
                    + "&&faces-redirect=true");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    //alter ego della save
    public void reschedule() {
        logger.log(LoggerLevel.DEBUG, "dentro reschedule");

        //reschedule date
        eventToCreate.setStartDateTime(rescheduleDayStart);
        eventToCreate.setEndDateTime(rescheduleDayEnd);

        saveIt();
        logger.log(LoggerLevel.DEBUG, "dopo reschedule");

        //redirect
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        try {
            context.redirect(context.getRequestContextPath()
                    + "/s/eventPage.xhtml?id=" + id
                    + "&&faces-redirect=true");

        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public String deleteEvent() {
        logger.log(LoggerLevel.DEBUG, "-dentro delete, eventToCreate vale:"
                + eventToCreate);
        if (eventManager.deleteEvent(eventToCreate)) {
            logger.log(LoggerLevel.DEBUG, "-evento cancellato");
            return "/s/calendar.xhtml";
        } else {
            logger.log(LoggerLevel.DEBUG, "-evento non cancellato");
            showMessage("", "evento non cancellato", "");
            return "";
        }
    }

    public void invite(String emailToInvite) {
        if (emailToInvite != null) {
            UserModel userToInvite = findGuest(resultUsers, emailToInvite);
            logger.log(LoggerLevel.DEBUG, "--resultUsers è " + resultUsers);
            logger.log(LoggerLevel.DEBUG, "--emailToInvite è " + emailToInvite);

            if (userToInvite != null) {
                logger.log(LoggerLevel.DEBUG, "--userToInvite è" + userToInvite);
                if (!guests.contains(userToInvite)) {
                    if (!userToInvite.equals(login.getCurrentUser())) {
                        guests.add(userToInvite);
                    } else {
                        showMessage("inviteForm:email",
                                "partecipi automaticamente ai tuoi eventi", "");
                    }
                } else {
                    showMessage("inviteForm:email", "l'utente è già in lista",
                            "");
                }
            } else {
                showMessage("inviteForm:email", "Nessun utente trovato", "");
            }
            displayResultUsers = false;
        } else {
            logger.log(LoggerLevel.DEBUG, "--emailToInvite è null");
            showMessage("inviteForm:email", "Specificare un utente", "");
        }
    }

    public void showResultUsers() {
        logger.log(LoggerLevel.DEBUG, "-newGuestEmail" + newGuestEmail);
        if (saved) {
            resultUsers = searchManager.searchUserForInvitation(newGuestEmail,
                    eventToCreate);
        } else {
            resultUsers = searchManager.searchUsers(newGuestEmail);
            resultUsers.remove(login.getCurrentUser());
            for (UserModel guest : guests) {
                resultUsers.remove(guest);
            }
        }
        logger.log(LoggerLevel.DEBUG, "-newGuestEmail" + newGuestEmail);
        logger.log(LoggerLevel.DEBUG, "-resultUsers" + resultUsers);
        if (resultUsers != null && resultUsers.size() > 0) {
            displayResultUsers = true;
        } else {
            showMessage("inviteForm:email", "Nessun utente trovato", "");
        }
    }

    /**
     * se l'evento è già stato creato ne carica l'istanza solo se non è cambiata
     * la privacy. Se la privacy è stata cambiata creo un nuovo evento con la
     * privacy giusta, il vecchio lo elimino e aggiorno l id. Se l'evento invece
     * è un nuovo evento lo istanzio, sempre in eventToCreate
     */
    private void createOrLoadInstance() {
        //se sto modificando un evento esistente
        if (isSaved()) {
            logger.log(LoggerLevel.DEBUG,
                    "-dentro createOrL, dentro isSaved, idEvent vale:" + id);
            if (id != null) {
                Event eventFound = eventManager.findEventbyId(Long.parseLong(id));
                if (((eventFound instanceof PublicEvent) && (publicAccess))
                        || ((eventFound instanceof PrivateEvent)
                        && (!publicAccess))) {
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
                logger.log(LoggerLevel.DEBUG, "idEvent è null");
                showMessage(null, "Nessun evento trovato", "");
            }
        } else {
            logger.log(LoggerLevel.DEBUG,
                    "-dentro createOrL, dentro else di isSaved");
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

        if (place != null) {
            logger.log(LoggerLevel.DEBUG, "place non è null!!");
            location = place.toString();
        }

        logger.log(LoggerLevel.DEBUG, "Complete location is: " + location);
        eventToCreate.setLocation(location);
        eventToCreate.setHasLocation(hasLocation);
        eventToCreate.setIsOutdoor(outdoor);
        eventToCreate.setStartDateTime(startDateTime);
        eventToCreate.setEndDateTime(endDateTime);
        eventToCreate.setOwner(login.getCurrentUser());
        eventManager.updateEventLatLng(eventToCreate);
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
        logger.log(LoggerLevel.DEBUG, "dentro save it");

        if (isSaved()) {
            eventManager.updateEvent(eventToCreate, calendar, guests);
        } else {
            if (eventManager.scheduleNewEvent(eventToCreate, calendar,
                    guests)) {
                setSaved(true);
                id = eventToCreate.getId().toString();
                showMessage(null, "L'evento è stato salvato", "");
            } else {
                showMessage(null,
                        "Evento non salvato", "Errore durante il salvataggio");
            }
        }

    }

    /**
     * NB event must be already setted
     *
     * @return
     */
    private boolean validateEventConstraint() {
        //se fine evento prima di inzio evento
        if (eventToCreate.getEndDateTime().before(
                eventToCreate.getStartDateTime())) {
            //avvisa errore
            showMessage(login.getCurrentUser().getEmail(), "Event not saved",
                    "The event cannot end before it starts");
        } else {
            //se inizio evento nel passato
            if (eventToCreate.getStartDateTime().before(Calendar.getInstance())) {
                //avvisa errore
                showMessage(login.getCurrentUser().getEmail(),
                        "Event not saved",
                        "You cannot create an event in the past");
            } else {
                //se titolo null
                if (eventToCreate.getTitle().isEmpty()) {
                    showMessage(login.getCurrentUser().getEmail(),
                            "Event not saved",
                            "Title cannot be empty");
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public void forceReschedule() {
        logger.log(LoggerLevel.DEBUG, "dentro forceReschedule");
        createOrLoadInstance();
        setUpInstance();

        if (validateEventConstraint()) {
        }
    }

    /**
     * controlla se ci sono conflitti o brutto tempo, se tutto ok salva l'evento
     * altrimenti chiede se rischedulare suggerendo un giorno
     */
    public void checkEvent() {
        logger.log(LoggerLevel.DEBUG, "dentro checkEvent");
        createOrLoadInstance();
        setUpInstance();

        if (validateEventConstraint()) {

            //controllo se ci osno porblemi i,e, conflitti o tempo malo
            List<ControlMessages> outcome = calendarManager.checkData(
                    eventToCreate);

            //se tutto ok 
            if (outcome.contains(ControlMessages.NO_PROBLEM)) {
                //salvo l'evento/update
                save();
            } else {
                //Listo gli errori
                dialogueMessage = "";

                for (ControlMessages mex : outcome) {
                    dialogueMessage += mex.getMessage() + "\n";
                }

                // cerco un free day           
                int offset = calendarManager.findFreeSlots(eventToCreate);
                logger.log(LoggerLevel.DEBUG, "Trovato free slot: {0}",
                        offset);
                if (offset != -1) {
                    //creo le possibili date di reschedule
                    rescheduleDayStart = eventToCreate.getStartDateTime();
                    rescheduleDayStart.add(Calendar.DATE, offset);
                    rescheduleDayEnd = eventToCreate.getEndDateTime();
                    rescheduleDayEnd.add(Calendar.DATE, offset);

                    dialogueMessage += "\nDo you want to reschedule the event from the:\n"
                            + TimeTool.dateToTextDay(
                                    rescheduleDayStart.getTime(),
                                    "dd-MM-YYYY hh:mm\n") + "to the:\n"
                            + TimeTool.dateToTextDay(
                                    rescheduleDayEnd.getTime(),
                                    "dd-MM-YYYY hh:mm\n");
                    rescheduleButton = "Accept reschedule";
                    saveButton = "Ignore and Save";
                    showRescheduleButton = true;

                } else {
                    saveButton = "Ignore and Save";
                    showRescheduleButton = false;
                    dialogueMessage += "\nIt wasn't possible to find a sunny day for a reschedule.";
                }
                //informo l'utente con una dialog box
                RequestContext context = RequestContext.getCurrentInstance();
                //update pulsanti
                context.update("buttonsForm:rescheduleButton");
                context.update("buttonsForm:saveButton");

                //update messaggio
                context.update("dialogMessage");

                //esegui dialog
                context.execute("PF('conflictDialog').show();");

            }
        }//end if
        //se ci sono problemi non ti faccio comparire la dialog per salvare
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
        ctx.addMessage(recipient, new FacesMessage(FacesMessage.SEVERITY_WARN,
                msg, advice));
        RequestContext.getCurrentInstance().update("growl");
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

    //TODO spostare
    private String searchCalendarByEvent() {
        List<CalendarModel> list = calendarManager.getCalendars(
                login.getCurrentUser());
        for (CalendarModel c : list) {
            if (c.getEventsInCalendar().contains(eventToCreate)) {
                return c.getTitle();
            }
        }
        return null;
    }
}
