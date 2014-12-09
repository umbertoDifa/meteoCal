/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

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

@NamedQuery(name= "findNextPublicEvents", query= "SELECT e FROM PublicEvent e WHERE e.startDateTime>= CURRENT_TIMESTAMP AND e.owner <> :user")

public class PublicEvent extends Event {
    
    @ManyToMany
    @JoinTable(name = "PUBLIC_JOIN")
    private List<UserModel> guests;

    public List<UserModel> getGuests() {
        return guests;
    }

    public void setGuests(List<UserModel> guests) {
        this.guests = guests;
    }
   
    
}
