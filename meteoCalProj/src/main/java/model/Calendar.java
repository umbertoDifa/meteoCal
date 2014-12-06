/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;



/**
 *
 * @author Luckyna
 */

@Entity
@IdClass (CalendarId.class)
public class Calendar implements Serializable {
    
    @Id
    private String title;
    @Id
    @ManyToOne
    private User owner;
    
    private boolean isPublic;

    private boolean isDefault;
    
    @ManyToMany
    @JoinTable(name = "EventInCalendar")
    private List<User> eventsInCalendar;

 

    public String getTitle() {
        return title;

    }

    public void setTitle(String calendar_title) {
        this.title = calendar_title;
    }
    
}
