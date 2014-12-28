/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.EventManager;
import EJB.interfaces.InvitationManager;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import model.Event;
import model.Invitation;
import model.InvitationAnswer;
import model.PublicEvent;
import model.UserModel;

/**
 *
 * @author Francesco
 */
@Named(value = "viewEvent")
@ViewScoped
public class viewEventBacking implements Serializable {

    Long eventId;
    Event eventToShow;
    boolean allowedToPartecipate;
    boolean allowedToModify;
    boolean publicAccess;
    private boolean hasInvitation;
    private List<UserModel> noAnswerInvitations = new ArrayList<>();
    private List<UserModel> acceptedInvitations = new ArrayList<>();
    private List<UserModel> declinedInvitations = new ArrayList<>();
    private List<UserModel> publicJoinUsers = new ArrayList<>();
    private boolean showInvitees;
    private String answerMessage;

    @Inject
    EventManager eventManager;

    @Inject
    InvitationManager invitationManager;

    @Inject
    LoginBacking login;

    private boolean hasAnswered;
    private boolean publicJoin;
    private boolean partecipate;

    /**
     * Creates a new instance of viewEventBacking
     */
    public viewEventBacking() {
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

    public boolean isAllowedToModify() {
        return allowedToModify;
    }

    //posso canc?
    public void setAllowedToModify(boolean allowToModify) {
        this.allowedToModify = allowToModify;
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

    /*
     *
     * METHODS
     *
     */
    public void findEventById() {

        eventToShow = eventManager.findEventbyId(eventId);
        if (eventToShow != null) {
            System.out.println("-eventToShow è:" + eventToShow.getTitle());
            setParameters();
        } else {
            System.out.println("-eventToSHow è null");

            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            try {
                context.redirect(context.getRequestContextPath() + "/error.xhtml");
            } catch (IOException ex) {

            }
        }
    }

    public void doPartecipate() {
        if (hasInvitation) {
            invitationManager.setAnswer(getOwnedInvitation(), InvitationAnswer.YES);
            hasAnswered = true;
            partecipate = true;
            answerMessage = "parteciperai";

            addMessage(
                    "Hai risposto all'evento");
            System.out.println(
                    "-doPartecipate");
        } else {
            if (publicAccess) {
                //TODO public join
                addMessage("da implementare");
            } else {
                addMessage("non puoi partecipare");
            }
        }
    }

    public void doDecline() {
        if (hasInvitation) {
            invitationManager.setAnswer(getOwnedInvitation(), InvitationAnswer.NO);
            hasAnswered = true;
            partecipate = false;
            answerMessage = "non parteciperai";
        } else {
            addMessage("non puoi partecipare");
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
            allowedToModify = true;
        }
        //se non è il creatore dell evento e o l'evento è pubblico o ha un invito
        if (!login.getCurrentUser().equals(eventToShow.getOwner()) && ((eventToShow instanceof PublicEvent) || (getInvitees().contains(login.getCurrentUser())))) {
            //allora può partecipare
            allowedToPartecipate = true;

            //salvo se ha un invito
            hasInvitation = getInvitees().contains(login.getCurrentUser());

            //se ha un invito
            if (hasInvitation) {
                //salvo la sua risposta in answer
                InvitationAnswer answer = getAnswer();
                if (answer != null) {
                    //salvo che ha risposto
                    hasAnswered = true;
                    //e il messaggio da visualizzare sul bottone
                    if (answer.equals(InvitationAnswer.YES)) {
                        answerMessage = "parteciperai";
                        partecipate = true;
                    } else if (answer.equals(InvitationAnswer.NO)) {
                        answerMessage = "non parteciperai";
                    } else if (answer.equals(InvitationAnswer.NA)) {
                        answerMessage = "rispondi";
                    }
                }
                //se non ha un invito ma può partecipare
            } else {
                //se ha fatto public join
                if (publicJoinUsers.contains(login.getCurrentUser())) {
                    //imposto il msg da visualizzare sul bottone
                    answerMessage = "parteciperai";
                    publicJoin = true;
                } else {
                    answerMessage = "non parteciperai";
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

    private Invitation getOwnedInvitation() {
        List<Invitation> list = eventToShow.getInvitations();
        if (list != null && list.size() > 0) {
            for (Invitation i : list) {
                if (i.getInvitee().equals(login.getCurrentUser()));
                return i;
            }
        }
        return null;
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
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

}
