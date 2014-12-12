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
public interface SearchManager {
    
    /**
     * Searches the kind of object passed     
     * @return  the list of object found in the database from the search
     */
    public List<UserModel> searchUsers(String stringToSearch);
    
    public List<Event> searchEvents(String stringToSearch);
    
    public UserModel findUserbyEmail(String email);
    
    public List<UserModel> searchUserForInvitation (String stringToSearch, Event event);
    
}
