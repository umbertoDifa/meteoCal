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

/**
 *
 * @author Luckyna
 */
@Entity
public class PublicEvent extends Event {
    
    @ManyToMany
    @JoinTable(name = "PublicJoin")
    private List<UserModel> guests;

    public List<UserModel> getGuests() {
        return guests;
    }

    public void setGuests(List<UserModel> guests) {
        this.guests = guests;
    }
   
    
}
