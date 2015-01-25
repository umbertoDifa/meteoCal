/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB.interfaces;

import java.util.List;
import model.CalendarModel;
import model.Event;
import model.UserModel;

/**
 *
 * @author Umberto
 */
public interface EventManager {

    /**
     * Create the new event, persist it, call create invitation del calendar
     * manager
     *
     * @param event Event to create
     * @param insertInCalendar calendar in which the event has to be inserted,
     * null if none
     * @param invitees invitati all'evento, null if none
     * @return true if created with success, false if not
     */
    public boolean scheduleNewEvent(Event event, model.CalendarModel insertInCalendar, List<UserModel> invitees);

    public List<Event> eventOnWall(utility.EventType type, UserModel owner);

    public Event findEventbyId(Long id);

    public boolean updateEvent(Event event, CalendarModel inCalendar, List<UserModel> invitees);

    

    public List<UserModel> getPublicJoin(Event event);

    public boolean addPublicJoin(Event event, UserModel user);

    public boolean removePublicJoin(Event event, UserModel user);

    public boolean isInAnyCalendar(Event event, UserModel user);

  //  public boolean changeEventPrivacy(Event event, boolean spreadInvitations);
    
    public void updateEventLatLng(Event event) ;

}
