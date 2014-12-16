/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Calendar;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Luckyna
 */
@Entity
@Table(name = "PUBLIC_EVENT")

@NamedQuery(name= "findNextPublicEvents", query= "SELECT e FROM PublicEvent e WHERE e.endDateTime>= CURRENT_TIMESTAMP AND e.owner <> :user")

public class PublicEvent extends Event {
    
    @ManyToMany
    @JoinTable(name = "PUBLIC_JOIN")
    private List<UserModel> guests;
    
    

    /**
    *
    *   CONSTRUCTORS
    */

    public PublicEvent(String title, Calendar startDateTime, Calendar endDateTime, String location, String description, boolean isOutdoor, UserModel owner) {
        super(title, startDateTime, endDateTime, location, description, isOutdoor, owner);
    }

    public PublicEvent() {
    }


    /**
    *
    *   SETTERS & GETTERS
    */    

    public List<UserModel> getGuests() {
        return guests;
    }

    public void setGuests(List<UserModel> guests) {
        this.guests = guests;
    }
   
    
}
