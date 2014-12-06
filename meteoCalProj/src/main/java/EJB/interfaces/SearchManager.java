/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB.interfaces;

import java.util.List;

/**
 *
 * @author Umberto
 */
public interface SearchManager {
    
    /**
     * Searches the kind of object passed     
     * @param thingToSearch user or event
     * @return  the list of object found in the database from the search
     */
    public List<Object> search(Object thingToSearch);
    
}
