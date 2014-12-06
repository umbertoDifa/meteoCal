/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB.interfaces;

import java.util.List;
import model.Event;
import model.User;

/**
 *
 * @author Umberto
 */
public interface EventManager {

    public List<model.Calendar> loadCalendars(User user);

    //TODO forse inutile perchè non fa che chiamare la stessa nel searchManager
    public List<Object> search(Object thingToSearch);

    /**
     * Store the users to invite
     *
     * @param invitee users to invite
     * @return true if stored
     */
    public boolean saveInvites(List<User> invitee);

    public boolean checkData(//TODO: come nel calendar manager, forse inutile
            );
    
    /**
     * Create the new event, persist it, call create invitation del calendar manager
     * @param user owner of the event
     * @param event Event to create
     * @param insertInCalendar calendar in which the event is inserted
     * @param invitees invitati all'evento
     * @return true if created with success, false if not
     */
    public boolean scheduleNewEvent(User user, Event event, model.Calendar insertInCalendar,List<User> invitees);
}
