/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


/**
 *
 * @author Luckyna
 */

@Entity
@IdClass (CalendarId.class)
public class Calendar {
    
    @Id
    private String title;
    @Id
    private Long owner;
    private boolean isPublic;
    private boolean isDefault;
    
    @ManyToMany
    @JoinTable(name = "EventInCalendar", joinColumns = @JoinColumn(name = "calendar"), inverseJoinColumns = @JoinColumn(name = "event"))
    private List<User> events;

 

    public String getCalendar_title() {
        return title;

    }

    public void setCalendar_title(String calendar_title) {
        this.title = calendar_title;
    }
    
}
