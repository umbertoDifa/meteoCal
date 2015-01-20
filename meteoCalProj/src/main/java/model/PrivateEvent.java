/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Calendar;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Luckyna
 */
@Entity
@Table(name = "PRIVATE_EVENT")
@DiscriminatorValue("PRIVATE")
public class PrivateEvent extends Event{
    

    /*
    *
    *   CONSTRUCTORS
    */

    public PrivateEvent(String title, Calendar startDateTime, Calendar endDateTime, String location, String description, boolean isOutdoor, UserModel owner) {
        super(title, startDateTime, endDateTime, location, description, isOutdoor, owner);
    }

    public PrivateEvent() {
    }
    
    
}


    


