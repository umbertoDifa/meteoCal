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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 *
 * @author Luckyna
 */
@Entity
@IdClass(CalendarId.class)
public class CalendarModel implements Serializable {

    @Id
    private String title;
    @Id
    @ManyToOne
    private UserModel owner;

    private boolean isPublic;

    private boolean isDefault;

    @ManyToMany
    @JoinTable(name = "EventInCalendar")
    private List<Event> eventsInCalendar;//FIXED

    public String getTitle() {
        return title;

    }

    public List<Event> getEventsInCalendar() {
        return eventsInCalendar;
    }

    public void setEventsInCalendar(List<Event> eventsInCalendar) {
        this.eventsInCalendar = eventsInCalendar;
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

}
