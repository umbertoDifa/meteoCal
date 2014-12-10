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
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Luckyna
 */
@Entity
@IdClass(CalendarId.class)
@Table(name="CALENDAR")
 @NamedQuery(name= "findCalbyUserAndTitle", query= "SELECT c FROM CalendarModel c WHERE c.owner =:id AND c.title=:title")
public class CalendarModel implements Serializable {

    @Id
    private String title;
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    private UserModel owner;

    private boolean isPublic;

    private boolean isDefault;

    @ManyToMany
    @JoinTable(name = "EVENT_IN_CALENDAR")
    private List<Event> eventsInCalendar;//FIXED
    
    
    /**
    *
    *   CONSTRUCTURS
    */
    public CalendarModel() {
    }

    public CalendarModel(String title, UserModel owner, boolean isPublic, boolean isDefault) {
        this.title = title;
        this.owner = owner;
        this.isPublic = isPublic;
        this.isDefault = isDefault;
    }

    /**
    *
    *  SETTERS & GETTERS 
    */
    
    public String getTitle() {
        return title;

    }

    public List<Event> getEventsInCalendar() {
        return eventsInCalendar;
    }


    public void setTitle(String calendar_title) {
        this.title = calendar_title;
    }

    public UserModel getOwner() {
        return owner;
    }

    public void setOwner(UserModel owner) {
        this.owner = owner;
    }

    public boolean isIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    
    /**
    *
    *  METHODS
    */
    
    public boolean addEventInCalendar(Event event) {
        return this.eventsInCalendar.add(event);
    }
}
