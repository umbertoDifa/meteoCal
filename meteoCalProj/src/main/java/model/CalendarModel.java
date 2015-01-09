/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Luckyna
 */
@Entity
@IdClass(CalendarId.class)
@Table(name = "CALENDAR")
@NamedQueries ({
    @NamedQuery (name = "findCalbyUserAndTitle",
            query = "SELECT c FROM CalendarModel c WHERE c.owner =:id AND c.title=:title"),
    @NamedQuery (name = "findDefaultCalendar", query = "SELECT c FROM CalendarModel c WHERE c.owner=:user AND c.isDefault = TRUE")
})

public class CalendarModel implements Serializable {

    @Id
    private String title;
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    private UserModel owner;

    private boolean isPublic;

    private boolean isDefault;

    @ManyToMany (cascade = CascadeType.REMOVE)
    @JoinTable(name = "EVENT_IN_CALENDAR")
    private List<Event> eventsInCalendar;

    /*
     *
     * CONSTRUCTURS
     */
    public CalendarModel() {
        eventsInCalendar = new ArrayList<>();
    }

    public CalendarModel(String title, UserModel owner, boolean isPublic, boolean isDefault) {
        this.title = title;
        this.owner = owner;
        this.isPublic = isPublic;
        this.isDefault = isDefault;
        eventsInCalendar = new ArrayList<>();
    }

    /*
     *
     * SETTERS & GETTERS
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

    @Override
    public String toString() {
        return "CalendarModel{" + "title=" + title + ", owner=" + owner
                + ", isPublic=" + isPublic + ", isDefault=" + isDefault
                + ", eventsInCalendar=" + eventsInCalendar + '}';
    }

    /*
     *
     * METHODS
     */
    public boolean addEventInCalendar(Event event) {
        if (this.eventsInCalendar == null) {
            this.eventsInCalendar = new ArrayList<>();
        }
        return this.eventsInCalendar.add(event);
    }
    
    public boolean removeEventInCalendar(Event event) {
        if (this.eventsInCalendar != null) {
           return eventsInCalendar.remove(event);
        }
        return false;
    }
}
