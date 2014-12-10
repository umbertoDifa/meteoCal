/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Temporal;

/**
 *
 * @author Luckyna
 */
@Entity
@NamedQueries({
    //COME IMPOSTARE IL PARAMETRO: namedQuery.setParameter("search", "%" + value + "%");
    @NamedQuery(name = "findEventbyString", query = "SELECT e FROM Event e WHERE e.title LIKE :search"),

    @NamedQuery(name = "findEventbyTitle", query = "SELECT e FROM Event e WHERE e.title=:title")

})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Temporal(javax.persistence.TemporalType.DATE)
    @OrderColumn
    @Column(nullable = false)
    private java.util.Calendar startDateTime;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(nullable = false)
    private java.util.Calendar endDateTime;

    private String location;

    private String description;

    private boolean isOutdoor;

    @ManyToOne
    private UserModel owner;

    @OneToMany(mappedBy = "event")
    private List<Invitation> invitations;

    @ManyToMany(mappedBy = "eventsInCalendar")
    private List<model.CalendarModel> inCalendars;
//   EX ERROR!!

    /**
    *
    *   CONSTRUCTORS
    */
    
    //costruiscono una anonymus subclass, non bisogna MAI persisterli prima di aver creato la entity giusta!
    public Event(String title, Calendar startDateTime, Calendar endDateTime, String location, String description, boolean isOutdoor, UserModel owner) {
        this.title = title;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
        this.description = description;
        this.isOutdoor = isOutdoor;
        this.owner = owner;
    }

    //costruiscono una anonymus subclass, non bisogna MAI persisterli prima di aver creato la entity giusta!
    public Event() {
    }

    
    /**
    *
    *   SETTERS & GETTERS
    */

    public String getTitle() {
        return title;
    }

    public UserModel getOwner() {
        return owner;
    }

    public void setOwner(UserModel owner) {
        this.owner = owner;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<Invitation> invitations) {
        this.invitations = invitations;
    }

    public List<CalendarModel> getInCalendars() {
        return inCalendars;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartDateTime(Calendar startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(Calendar endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIsOutdoor(boolean isOutdoor) {
        this.isOutdoor = isOutdoor;
    }

    public Calendar getStartDateTime() {
        return startDateTime;
    }

    public Calendar getEndDateTime() {
        return endDateTime;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public boolean isIsOutdoor() {
        return isOutdoor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    /**
    *
    *   METHODS
    */

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Event[ id=" + id + " ]";
    }
    
    public String getDateStart() {
        return this.startDateTime.getTime().toString();
    }
    
    public void setDateStart() {
    }
           
}
