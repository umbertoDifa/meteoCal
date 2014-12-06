/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 *
 * @author Luckyna
 */
@Entity
public class UserModel implements Serializable {
    //ATTRIBUTES

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String avatarPath;

    //non so se definire sopra una mini-enum per questo campo
    private char gender;

    @ManyToMany(mappedBy = "guests")
    private List<PublicEvent> publicJoins;

    @OneToMany(mappedBy = "invitee")
    private List<Invitation> invitations;

    @OneToMany(mappedBy = "recipient")
    private List<Notification> notifications;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_fk")
    private List<Event> ownedEvents;

    @OneToMany(mappedBy = "owner")
    private List<CalendarModel> ownedCalendars;

    //METHODS
    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public char getGender() {
        return gender;
    }

    public List<CalendarModel> getOwnedCalendars() {
        return ownedCalendars;
    }

    public List<Event> getOwnedEvents() {
        return ownedEvents;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PublicEvent> getPublicJoins() {
        return publicJoins;
    }

    public void setPublicJoins(List<PublicEvent> publicJoins) {
        this.publicJoins = publicJoins;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<Invitation> invitations) {
        this.invitations = invitations;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserModel)) {
            return false;
        }
        UserModel other = (UserModel) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

}
