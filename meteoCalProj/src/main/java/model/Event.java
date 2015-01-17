/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PreRemove;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;

/**
 *
 * @author Luckyna
 */
@Entity
@NamedQueries({
    //COME IMPOSTARE IL PARAMETRO: namedQuery.setParameter("search", "%" + value + "%");
    @NamedQuery(name = "findEventbyString",
                query = "SELECT e FROM Event e WHERE e.title LIKE :search"),
    @NamedQuery(name = "findEventbyTitle",
                query = "SELECT e FROM Event e WHERE e.title=:title"),
    //Da chiamare sempre limitando i risulati ad 1 solo!
    @NamedQuery(name = "isConflicting",
                query = "SELECT e FROM Event e INNER JOIN e.inCalendars c WHERE c.owner=:user AND e.id <> :id AND "
                + "(e.startDateTime >= :start AND e.startDateTime < :end) OR (e.startDateTime < :start AND e.endDateTime > :start)")
})
@NamedQuery(name = "isInAnyCalendar",
            query = "SELECT COUNT(e) FROM Event e INNER JOIN e.inCalendars c WHERE e.id=:event AND e.owner=:user")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TYPE")
public abstract class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @TableGenerator(name = "EVENT_SEQ", table = "SEQUENCE",
                    pkColumnName = "SEQ_NAME",
                    valueColumnName = "SEQ_COUNT", pkColumnValue = "EVENT_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "EVENT_SEQ")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @OrderColumn
    @Column(nullable = false)
    private java.util.Calendar startDateTime;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private java.util.Calendar endDateTime;

    private String description;

    private boolean isOutdoor;

    @ManyToOne
    private UserModel owner;

    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE) //TODO check sta remove
    private List<Invitation> invitations;

    @ManyToMany(mappedBy = "eventsInCalendar") //no cascade perche altrimenti elimino i calendari tutti dell'utente
    private List<model.CalendarModel> inCalendars;

    @OneToOne//no cascade
    @JoinColumn
    private WeatherForecast weather;

    private String location;

    private boolean hasLocation;
    //vars to store position coordinates if any
    private double latitude;
    private double longitude;

    @OneToMany(mappedBy = "relatedEvent")
    private List<Notification> notifications;

    /*
     *
     * CONSTRUCTORS
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

    public WeatherForecast getWeather() {
        return weather;
    }

    public void setWeather(WeatherForecast weather) {
        this.weather = weather;
    }

    /**
     *
     * SETTERS & GETTERS
     */
    public boolean hasLocation() {
        return hasLocation;
    }

    public void setHasLocation(boolean hasLocation) {
        this.hasLocation = hasLocation;
    }

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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    /*
     *
     * METHODS
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.id == null && other.id != null) || (this.id != null
                && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Event{" + "id=" + id + ", title=" + title + ", startDateTime="
                + startDateTime + ", endDateTime=" + endDateTime
                + ", description=" + description + ", owner=" + owner + '}';
    }

    public String getFormattedStartDate() {
        String formattedDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm").format(
                this.startDateTime.getTime());
        return formattedDate;
    }

    public String getFormattedEndDate() {
        String formattedDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm").format(
                this.endDateTime.getTime());
        return formattedDate;
    }

    public List<UserModel> getInvitee() {
        List<UserModel> invitee = new ArrayList<>();
        for (Invitation invitation : this.getInvitations()) {
            invitee.add(invitation.getInvitee());
        }
        return invitee;
    }

    @PreRemove
    private void detachNotifications() {
        System.out.println("++++++++++dentro detach+++++++++++++");
        for (Notification n : this.notifications) {
            n.setRelatedEvent(null);
        }
    }
}
