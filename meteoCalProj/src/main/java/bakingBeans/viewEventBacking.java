/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.EventManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    private List<UserModel> noAnswerInvitations = new ArrayList<>();
    private List<UserModel> acceptedInvitations = new ArrayList<>();
    private List<UserModel> declinedInvitations = new ArrayList<>();
    private List<UserModel> publicJoinUsers = new ArrayList<>();
    private boolean showInvitees;

    @Inject
    EventManager eventManager;

    @Inject
    LoginBacking login;

    private boolean hasAnswered;

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
            //TODO: reindirizzare a pagina errore
        }
    }

    public void partecipate() {
        //invitationManager.addPartecipant(eventId, login.getId);
    }

    private void setParameters() {
        if (login.getCurrentUser().equals(eventToShow.getOwner())) {
            allowedToModify = true;
        }
        if (!login.getCurrentUser().equals(eventToShow.getOwner()) && ((eventToShow instanceof PublicEvent) || (getInvitees().contains(login.getCurrentUser())))) {
            allowedToPartecipate = true;
            InvitationAnswer answer = getAnswer();
            if (answer != null) {
                hasAnswered = true;
            }

            //TODO finire
        }
        publicAccess = eventToShow instanceof PublicEvent;
        setInvitations();
    }

    private List<UserModel> getInvitees() {
        List<UserModel> invitees = new ArrayList<UserModel>();
        List<Invitation> list = eventToShow.getInvitations();
        if (list != null && list.size() > 0) {
            for (Invitation i : list) {
                invitees.add(i.getInvitee());
            }
        }
        return invitees;
    }

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
            }
        }
    }
}
