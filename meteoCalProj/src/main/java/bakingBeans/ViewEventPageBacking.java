/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import EJB.interfaces.InvitationManager;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import model.CalendarModel;
import model.Event;
import model.Invitation;
import model.InvitationAnswer;
import model.PublicEvent;
import model.UserModel;
import org.primefaces.context.RequestContext;
import utility.GrowlMessage;
import utility.TimeTool;

/**
 *
 * @author Francesco
 */
@Named(value = "viewEvent")
@ViewScoped
public class ViewEventPageBacking implements Serializable {

    private Long eventId;
    private Event eventToShow;
    // if the user can partecipate
    boolean allowedToPartecipate;

    // if the user owns the event
    boolean eventMine;
    // if the event is public
    boolean publicAccess;
    // if the user has an invitation
    private boolean hasInvitation;

    private List<UserModel> noAnswerInvitations = new ArrayList<>();
    private List<UserModel> acceptedInvitations = new ArrayList<>();
    private List<UserModel> declinedInvitations = new ArrayList<>();
    private List<UserModel> publicJoinUsers = new ArrayList<>();

    private boolean showInvitees;
    private String answerMessage;
    // the name of the calendar where the user keep the event
    private String calendarName;

    @Inject
    EventManager eventManager;

    @Inject
    InvitationManager invitationManager;

    @Inject
    LoginBacking login;

    // if the user has answer to the invitation or does a public join
    private boolean hasAnswered;
    // if he will partecipate as public join guest
    private boolean publicJoin;
    // if he will partecipate as invited guest
    private boolean partecipate;

    @Inject
    private CalendarManager calendarManager;

    /**
     * Creates a new instance of viewEventBacking
     */
    public ViewEventPageBacking() {
    }

    /**
     *
     * GETTERS & SETTERS
     *
     */
    public Long getEventId() {
        return eventId;
    }

    public Event getEventToShow() {
        return eventToShow;
    }

    public boolean isHasAnswered() {
        return hasAnswered;
    }

    public boolean isAllowedToPartecipate() {
        return allowedToPartecipate;
    }

    public void setHasAnswered(boolean hasAnswered) {
        this.hasAnswered = hasAnswered;
    }

    //posso canc?
    public void setAllowedToPartecipate(boolean allowToPartecipate) {
        this.allowedToPartecipate = allowToPartecipate;
    }

    public boolean isEventMine() {
        return eventMine;
    }

    public void setEventMine(boolean eventMine) {
        this.eventMine = eventMine;
    }

    public void setEventToShow(Event event) {
        this.eventToShow = event;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
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

    public boolean isPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    public boolean isShowInvitees() {
        return showInvitees;
    }

    public void setShowInvitees(boolean showInvitees) {
        this.showInvitees = showInvitees;
    }

    public String getAnswerMessage() {
        return answerMessage;
    }

    public void setAnswerMessage(String answerMessage) {
        this.answerMessage = answerMessage;
    }

    public boolean isHasInvitation() {
        return hasInvitation;
    }

    public void setHasInvitation(boolean hasInvitation) {
        this.hasInvitation = hasInvitation;
    }

    public boolean isPublicJoin() {
        return publicJoin;
    }

    public void setPublicJoin(boolean publicJoin) {
        this.publicJoin = publicJoin;
    }

    public boolean isPartecipate() {
        return partecipate;
    }

    public void setPartecipate(boolean partecipate) {
        this.partecipate = partecipate;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    /*
     *
     * METHODS
     *
     */
    public void findEventById() {
        boolean redirectToErrorPage = false;
        // se l id specificato non è nullo cerca l evento corrispondente
        if (eventId != null) {
            eventToShow = eventManager.findEventbyId(eventId);
        } else {
            redirectToErrorPage = true;
        }
        // se l hai trovato setta i parametri
        if (eventToShow != null) {
            setParameters();
        } else {
            redirectToErrorPage = true;
        }
        // seè andato storto qualcosa reindirizza
        if (redirectToErrorPage == true) {
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            try {
                context.redirect(context.getRequestContextPath()
                        + "/error.xhtml");
                addMessage("No event found");
            } catch (IOException ex) {

            }
        }
        calendarName = searchCalendarByEvent();
    }

    public void doPartecipate() {
        if (hasInvitation) {
            invitationManager.setAnswer(login.getCurrentUser(), eventToShow,
                    InvitationAnswer.YES);
            hasAnswered = true;
            partecipate = true;
            answerMessage = "You will participate";
            if (!acceptedInvitations.contains(login.getCurrentUser())) {
                acceptedInvitations.add(login.getCurrentUser());
            }
            if (declinedInvitations.contains(login.getCurrentUser())) {
                declinedInvitations.remove(login.getCurrentUser());
            }
            if (noAnswerInvitations.contains(login.getCurrentUser())) {
                noAnswerInvitations.remove(login.getCurrentUser());
            }
            if (publicJoinUsers.contains(login.getCurrentUser())) {
                publicJoinUsers.remove(login.getCurrentUser());
            }
            addMessage("You have answered to the event");

        } else {
            if (publicAccess) {
                eventManager.addPublicJoin(eventToShow, login.getCurrentUser());
                publicJoin = true;
                hasAnswered = true;
                answerMessage = "You will participate";
                addMessage("You have joined the event");
                if (!publicJoinUsers.contains(login.getCurrentUser())) {
                    publicJoinUsers.add(login.getCurrentUser());
                }

            } else {
                addMessage("You cannot participate");
            }
        }
    }

    public void doDecline() {
        if (hasInvitation) {
            invitationManager.setAnswer(login.getCurrentUser(), eventToShow,
                    InvitationAnswer.NO);
            hasAnswered = true;
            partecipate = false;
            answerMessage = "You won't participate";

            if (acceptedInvitations.contains(login.getCurrentUser())) {
                acceptedInvitations.remove(login.getCurrentUser());
            }
            if (!declinedInvitations.contains(login.getCurrentUser())) {
                declinedInvitations.add(login.getCurrentUser());
            }
            if (noAnswerInvitations.contains(login.getCurrentUser())) {
                noAnswerInvitations.remove(login.getCurrentUser());
            }
            if (publicJoinUsers.contains(login.getCurrentUser())) {
                publicJoinUsers.remove(login.getCurrentUser());
            }
        } else {
            if (publicAccess) {
                eventManager.removePublicJoin(eventToShow,
                        login.getCurrentUser());
                publicJoin = false;
                hasAnswered = false;
                answerMessage = "You won't participate";

                addMessage("You won't join the event");
                if (publicJoinUsers.contains(login.getCurrentUser())) {
                    publicJoinUsers.remove(login.getCurrentUser());
                }

            } else {
                addMessage("You cannot decline");
            }
        }
    }

    private void setParameters() {
        //salvo se è public
        publicAccess = eventToShow instanceof PublicEvent;

        //salvo la lista invitati con varie risposte
        setInvitations();

        if (publicAccess) {
            //salvo le public join se public
            setUpPublicJoin();
        }

        //se è il creatore
        if (login.getCurrentUser().equals(eventToShow.getOwner())) {
            //salvo che può modificare
            eventMine = true;
        }
        //se non è il creatore dell evento e o l'evento è pubblico o ha un invito
        if (!login.getCurrentUser().equals(eventToShow.getOwner())
                && ((eventToShow instanceof PublicEvent)
                || (getInvitees().contains(login.getCurrentUser())))) {
            //allora può partecipare
            allowedToPartecipate = true;

            //salvo se ha un invito
            hasInvitation = getInvitees().contains(login.getCurrentUser());

            //se ha un invito
            if (hasInvitation) {
                //salvo la sua risposta in answer
                InvitationAnswer answer = getAnswer();
                if (answer != null) {

                    //e il messaggio da visualizzare sul bottone
                    if (answer.equals(InvitationAnswer.YES)) {
                        answerMessage = "You will participate";
                        partecipate = true;
                        //salvo che ha risposto
                        hasAnswered = true;
                    } else if (answer.equals(InvitationAnswer.NO)) {
                        answerMessage = "You won't participate";
                        //salvo che ha risposto
                        hasAnswered = true;
                    } else if (answer.equals(InvitationAnswer.NA)) {
                        answerMessage = "Answer";
                        //salvo che non ha risposto
                        hasAnswered = false;
                    }
                }
                //se non ha un invito ma può partecipare
            } else {
                //se ha fatto public join
                if (publicJoinUsers.contains(login.getCurrentUser())) {
                    //imposto il msg da visualizzare sul bottone
                    answerMessage = "You will paritcipate";
                    publicJoin = true;
                    hasAnswered = true;
                } else {
                    publicJoin = false;
                    answerMessage = "You won't participate";
                }
            }

        }
    }

    private List<UserModel> getInvitees() {
        List<UserModel> invitees = new ArrayList<>();
        List<Invitation> list = eventToShow.getInvitations();
        if (list != null && list.size() > 0) {
            for (Invitation i : list) {
                invitees.add(i.getInvitee());
            }
        }
        return invitees;
    }

    public boolean isFuture() {
        Calendar today = Calendar.getInstance();
        return TimeTool.isBefore(today, eventToShow.getEndDateTime());
    }

    /**
     *
     * @return it returns the InvitationAnswer of the current user
     */
    private InvitationAnswer getAnswer() {
        List<Invitation> list = eventToShow.getInvitations();
        if (list != null && list.size() > 0) {
            for (Invitation i : list) {
                if (i.getInvitee().equals(login.getCurrentUser())) {
                    return i.getAnswer();
                }
            }
        }
        return null;
    }

    //TODO da spostare
    private void setInvitations() {
        if (eventToShow != null) {
            List<Invitation> invitations = eventToShow.getInvitations();
            if (invitations != null && invitations.size() > 0) {
                showInvitees = true;
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
            } else {
                showInvitees = false;
            }
        }
    }

    private void setUpPublicJoin() {
        publicJoinUsers = eventManager.getPublicJoin(eventToShow);
    }

    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
                summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    private String searchCalendarByEvent() {
        List<CalendarModel> list = calendarManager.getCalendars(
                login.getCurrentUser());
        for (CalendarModel c : list) {
            if (c.getEventsInCalendar().contains(eventToShow)) {
                return c.getTitle();
            }
        }
        return null;
    }

    public void addToCalendar() {
        if (calendarName != null && calendarName != "") {
            CalendarModel calendarWhereAdd = calendarManager.findCalendarByName(login.getCurrentUser(), calendarName);
            calendarManager.addToCalendar(eventToShow, calendarWhereAdd);
            showGrowl(GrowlMessage.EVENT_ADDED);
        } else {
            showGrowl(GrowlMessage.EVENT_NOT_ADDED_TO_CALENDAR);
        }
    }

    private void showGrowl(GrowlMessage growl) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(growl.getSeverity(), growl.getTitle(), growl.getMessage()));
        RequestContext.getCurrentInstance().update("growl");
    }

}
