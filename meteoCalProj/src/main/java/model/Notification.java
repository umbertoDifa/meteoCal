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
import javax.persistence.SequenceGenerator;

/**
 *
 * @author Luckyna
 */
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
    @JoinColumn(referencedColumnName = "ID")
    private Event relatedEvent;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('INVITATION', 'EVENT_CHANGED',"
            + " 'EVENT_CANCELLED', 'EVENT_CHANGED_TO_PUBLIC','EVENT_CHANGED_TO_PRIVATE', 'WEATHER_CHANGED','BAD_WEATHER_TOMORROW' )",
            nullable = false)
    private NotificationType type;

    /*
     *
     * CONSTRUCTORS
     */
    public Notification(UserModel recipient, Event relatedEvent, NotificationType type) {
        this.recipient = recipient;
        this.relatedEvent = relatedEvent;
        this.type = type;
    }

    public Notification() {
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
