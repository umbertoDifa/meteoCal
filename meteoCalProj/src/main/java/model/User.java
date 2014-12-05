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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Luckyna
 */
@Entity

public class User implements Serializable {

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

    private char gender;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner")
    private List<Calendar> ownedCalendars;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    private List<Event> ownedEvents;
    
    @ManyToMany(mappedBy ="invitee")
    private List<Event> invitations;
    
    
    
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

    public List<Calendar> getOwnedCalendars() {
        return ownedCalendars;
    }

    public List<Event> getOwnedEvents() {
        return ownedEvents;
    }

    public List<Event> getInvitations() {    
        return invitations;
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

    public boolean equals(Object object) {
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
