/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import utility.Gender;

/**
 *
 * @author Luckyna
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "findOwnedCalendar",
                query = "SELECT c FROM CalendarModel c WHERE c.owner =:id"),
    //COME IMPOSTARE IL PARAMETRO: namedQuery.setParameter("search", "%" + value + "%");
    @NamedQuery(name = "findUserbyString",
                query = "SELECT u FROM UserModel u WHERE u.name LIKE :search OR u.surname LIKE :search OR u.email LIKE :search"),

    @NamedQuery(name = "findUserbyEmail",
                query = "SELECT u FROM UserModel u WHERE u.email=:email"),
    @NamedQuery(name = "findPublicJoins",
                query = "SELECT e FROM UserModel u INNER JOIN u.joinedEvents e WHERE u = :user AND e.endDateTime> CURRENT_TIMESTAMP ORDER BY e.startDateTime")

})
@Table(name = "USER")
public class UserModel implements Serializable {
    //ATTRIBUTES

    @Id
    @TableGenerator(name = "USER_SEQ", table = "SEQUENCE",
                    pkColumnName = "SEQ_NAME",
                    valueColumnName = "SEQ_COUNT", pkColumnValue = "USER_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "USER_SEQ")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('M','F')")
    private Gender gender;

    @ManyToMany(mappedBy = "guests")
    private List<PublicEvent> joinedEvents;

    @OneToMany(mappedBy = "invitee")
    private List<Invitation> invitations;

    @OneToMany(mappedBy = "recipient")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "owner")
    @OrderBy(value = "startDateTime ASC")
    private List<Event> ownedEvents;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    private List<CalendarModel> ownedCalendars;

    /*
     *
     *   CONSTRUCTORS
     */
    public UserModel() {
    }

    public UserModel(String name, String surname, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }

    /*
     *
     *   SETTERS & GETTERS
     */
    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Gender getGender() {
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

    public List<PublicEvent> getJoinedEvents() {
        return joinedEvents;
    }

    public void setJoinedEvents(List<PublicEvent> joinedEvents) {
        this.joinedEvents = joinedEvents;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setOwnedEvents(List<Event> ownedEvents) {
        this.ownedEvents = ownedEvents;
    }

    public void setOwnedCalendars(List<CalendarModel> ownedCalendars) {
        this.ownedCalendars = ownedCalendars;
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
        return !((this.id == null && other.id != null) || (this.id != null
                && !this.id.equals(other.id)));
    }

    @PreRemove
    private void detachRelations() {
        //stacco gli elmenti che non voglio cancellare in cascade
        this.joinedEvents.clear();

    }

}
