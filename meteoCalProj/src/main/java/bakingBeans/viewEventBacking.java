/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.EventManager;
import java.io.Serializable;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import model.Event;

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
        //eventToShow = eventManager.findEventById(eventId);
    }

    public void partecipate() {
        //invitationManager.addPartecipant(eventId, login.getId);
    }
}
