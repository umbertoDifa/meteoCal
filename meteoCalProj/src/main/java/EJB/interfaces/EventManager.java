/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB.interfaces;

import java.util.List;
import model.Event;
import model.UserModel;

/**
 *
 * @author Umberto
 */
public interface EventManager {

    public List<Object> search(Object thingToSearch);

    /**
     * Store the users to invite
     *
     * @param invitee users to invite
     * @return true if stored
     */
    public boolean saveInvites(List<UserModel> invitee);

    public boolean checkData();

    /**
     * Create the new event, persist it, call create invitation del calendar
     * manager
     *
     * @param user owner of the event
     * @param event Event to create
     * @param insertInCalendar calendar in which the event has to be inserted, null if none
     * @param invitees invitati all'evento, null if none
     * @return true if created with success, false if not
     */
    public boolean scheduleNewEvent(UserModel user, Event event, model.CalendarModel insertInCalendar, List<UserModel> invitees);
}
