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

    @Inject
    EventManager eventManager;

    @Inject
    LoginBacking login;

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

    public boolean isAllowedToPartecipate() {
        return allowedToPartecipate;
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
            //TODO finire
        }
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
                if(i.getInvitee().equals(login.getCurrentUser())){
                    return i.getAnswer();
                }
            }
        }
        return null;
    }
}
