/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author Luckyna
 */
@NamedQueries({
    @NamedQuery(name = "findInvitationNotifications", query = "SELECT n FROM Notification n WHERE n.recipient=:user AND n.type = model.NotificationType.INVITATION ORDER BY n.id DESC"),
    @NamedQuery(name = "findEventNotifications", query = "SELECT n FROM Notification n WHERE n.recipient=:user AND n.type != model.NotificationType.INVITATION ORDER BY n.id DESC "),
    @NamedQuery(name = "countUnreadNotifications", query = "SELECT COUNT(n) FROM Notification n WHERE n.recipient=:user AND n.isRead = false")
})

@Entity
@SequenceGenerator(name = "notifSeq", initialValue = 50)
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notifSeq")
    private Long id;

    @ManyToOne
    private UserModel recipient;


    @ManyToOne
    @JoinColumn(referencedColumnName = "ID", nullable = true)
    private Event relatedEvent;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('INVITATION', 'EVENT_CHANGED','EVENT_CANCELLED','BAD_WEATHER_IN_THREE_DAYS', 'EVENT_CHANGED_TO_PUBLIC','EVENT_CHANGED_TO_PRIVATE', 'WEATHER_CHANGED','BAD_WEATHER_TOMORROW' )",
            nullable = false)
    private NotificationType type;

    private boolean isRead;

    private String title;

    private String message;

    /*
     *
     * CONSTRUCTORS
     */
    public Notification(UserModel recipient, Event relatedEvent, NotificationType type) {
        this.recipient = recipient;
        this.relatedEvent = relatedEvent;
        this.isRead = false;
        setType(type);
        
    }

    protected Notification() {
    }

    /*
     *
     * SETTERS & GETTERS
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserModel getRecipient() {
        return recipient;
    }

    public void setRecipient(UserModel recipient) {
        this.recipient = recipient;
    }

    public Event getRelatedEvent() {
        return relatedEvent;
    }

    public void setRelatedEvent(Event relatedEvent) {
        this.relatedEvent = relatedEvent;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
        String eventTitle = relatedEvent.getTitle();
        String eventOwner = relatedEvent.getOwner().getEmail();
        
        switch (this.type) {
            case INVITATION:
                title = "Invitation for " + eventTitle;
                message = "You have received an invitation for event " + eventTitle + " from " + eventOwner + ". Check it out!";
                break;

            case WEATHER_CHANGED:
                title = "Weather changed for event " + eventTitle;
                message = "The event " + eventTitle + " has been updated with new weaher information. Check it out!";
                break;

            case EVENT_CANCELLED:
                title = "Event " + eventTitle + " has been cancelled";
                message = "The event " + eventTitle + " created by " + eventOwner + " has been cancelled.";
                this.relatedEvent= null;
                break;
                
            case EVENT_CHANGED:
                title = "Event "+eventTitle+" has been changed";
                message = "The event "+eventTitle+" has been updated. Check out changes on the event page!";
                break;

            case EVENT_CHANGED_TO_PRIVATE: 
                title = "Event " + eventTitle + " has become Private";
                message = eventOwner + " has changed the privacy of event " + eventTitle + " from Public to Private";
                break;
            

            case EVENT_CHANGED_TO_PUBLIC: 
                title = "Event " + eventTitle + " has become Public";
                message = eventOwner + " has changed the privacy of event " + eventTitle + " from Private to Public";
                break;

            case BAD_WEATHER_TOMORROW: 
                title = "Bad weather forecast for " + eventTitle;
                message = "The weather forecast for the tomorrow event " + eventTitle + " is bad.";
                break;

            case BAD_WEATHER_IN_THREE_DAYS:
                title = "Bad weather forecast for event " + eventTitle;
                message = "The weather forecast for your event " + eventTitle + " is bad.Visit the event page if you would like to reschedule it.";
                break;
        }
             
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Notification)) {
            return false;
        }
        Notification other = (Notification) object;
        if ((this.id == null && other.id != null) || (this.id != null
                && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Notification[ id=" + id + " ]";
    }

}
