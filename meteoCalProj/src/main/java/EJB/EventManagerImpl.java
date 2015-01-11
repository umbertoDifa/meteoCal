package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import EJB.interfaces.InvitationManager;
import EJB.interfaces.NotificationManager;
import EJB.interfaces.SearchManager;
import EJB.interfaces.WeatherManager;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import model.CalendarModel;
import model.Event;
import model.Invitation;
import model.InvitationAnswer;
import model.NotificationType;
import model.PublicEvent;
import model.PrivateEvent;
import model.UserModel;
import model.WeatherForecast;
import utility.LoggerLevel;

@Stateless
public class EventManagerImpl implements EventManager {

    @Inject
    SearchManager searchManager;

    @Inject
    CalendarManager calManager;

    @Inject
    InvitationManager invitationManager;

    @Inject
    NotificationManager notificationManager;

    @Inject
    WeatherManager weatherManager;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Inject
    @Default
    Logger logger;

    @Override
    public boolean scheduleNewEvent(Event event, CalendarModel insertInCalendar, List<UserModel> invitees) {
        //salvo evento nel db        
        database.persist(event);

        //aggiungo coordinate all'evento
        updateEventLatLng(event);

        logger.log(Level.INFO, "Event {0} created", event.getTitle());

        database.flush();
        logger.log(LoggerLevel.DEBUG, "Attualment lat e long:{0} e {1}",
                new Object[]{event.getLatitude(),
                             event.getLongitude()});

        //aggiungo weather
        WeatherForecast forecast = weatherManager.getWeather(event);
        database.persist(forecast);
        event.setWeather(forecast);

        database.flush();

        logger.log(Level.INFO, "Forecast added to Event {0} ", event.getTitle());

        //schedulo updates weather
        UpdateManagerImpl.addEvent(event);
        logger.log(Level.INFO, "Updates scheduled for Event {0} ",
                event.getTitle());

        //aggiungo l'evento al calendario se necessario
        if (insertInCalendar != null) {
            calManager.addToCalendar(event, insertInCalendar);
        }

        //faccio gli inviti
        if (invitees != null && invitees.size() > 0) {
            invitationManager.createInvitations(invitees, event);
            return true;
        }
        return true;
    }

    //NB non usare il database in questo metodo perchè l'event potrebbe non essere nel db
    @Override
    public void updateEventLatLng(Event event) {
        GeoApiContext context = new GeoApiContext().setApiKey(
                "AIzaSyCAlR8JiKO0QPZ_tm51cJITop7aGTDcnlo");
        GeocodingResult[] results;

        //se l'utente ha inserito un indirizzo e l'indirizzo è capito da google 
        if (event.hasLocation() && event.getLocation() != null) {
            try {
                //cerco di trovare le coordinate corrispondenti
                results = GeocodingApi.geocode(context,
                        event.getLocation()).await();

                //e setto le coordinate nell'evento
                event.setLatitude(results[0].geometry.location.lat);
                event.setLongitude(results[0].geometry.location.lng);

                logger.log(LoggerLevel.DEBUG, "Trovate lat e long: {0} ,{1}",
                        new Object[]{event.getLatitude(),
                                     event.getLongitude()});

            } catch (Exception ex) {
                logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        } else {
            logger.log(LoggerLevel.DEBUG,
                    "Location null o non imposatata da google, skipping latLong");

        }

    }

    @Override
    public boolean updateEvent(Event event, CalendarModel inCalendar, List<UserModel> invitees) {
        logger.log(LoggerLevel.DEBUG, "Appena entrato in UpdateEvent");
        //se l'update dei parametri generici va a buon fine
        if (updateEvent(event, inCalendar)) {
            //cerco di creare gli inviti per i nuovi initees
            try {
                if (invitees != null && invitees.size() > 0) {
                    invitationManager.createInvitations(invitees, event);
                }
                return true;
            } catch (PersistenceException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                return false;
            }

        } else {
            logger.log(Level.SEVERE, "Failed to updateEvent");
            return false;
        }

    }

    /**
     * Aggiorna possibilmente tutti i parametri cambiati dell'evento
     *
     * @param event evento da aggiornare
     * @param inCalendar calendario in cui aggiungerlo (se necessario)
     * @return true se tutto ok, false altrimenti
     */
    private boolean updateEvent(Event event, CalendarModel inCalendar) {
        try {

            //recupero l'evento
            Event vecchioEvento = database.find(Event.class, event.getId());
            logger.log(LoggerLevel.DEBUG, "il vecchio evento \u00e8:\n{0}",
                    vecchioEvento.toString());
            logger.log(LoggerLevel.DEBUG, "il nuovo evento \u00e8:\n{0}",
                    event.toString());

            //salvo i vecchi invitati
            List<UserModel> oldInvitees = vecchioEvento.getInvitee();
            logger.log(LoggerLevel.DEBUG, "I vecchi invitati sono:\n{0}",
                    oldInvitees);

            //update event data
            if (updateEventData(event)) {
                //mando la notifica
                notificationManager.createNotifications(oldInvitees,
                        vecchioEvento,
                        NotificationType.EVENT_CHANGED, false);
            }
            //a questo punto se ci sono stati cambiamenti il vecchio evento
            //ha tutte le info del nuovo tranne la privacy

            //aggiungo l'evento al (nuovo) calendario se necessario
            if (inCalendar != null) {
                calManager.addToCalendar(vecchioEvento, inCalendar);
                logger.log(LoggerLevel.DEBUG, "evento aggiunto al celendario{0}",
                        inCalendar);
            }

            //sincronizza il db nel dubbio
            database.flush();
            return true;

        } catch (PersistenceException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }

    }

    /**
     * Aggiorna tutti i dati (tranne calendario e invitati) dell'evento
     *
     * @param event evento da aggiornare
     * @return true se alcuni parametri sono stati aggiornati
     */
    private boolean updateEventData(Event event) {
        Event oldEvent = database.find(Event.class, event.getId());
        boolean changed = false;

        //aggiorno la descrizione
        if (oldEvent.getDescription() == null ? (event.getDescription()) != null : !oldEvent.getDescription().equals(
                event.getDescription())) {
            oldEvent.setDescription(event.getDescription());
            changed = true;
        }
        //aggiorno inizio evento
        if (oldEvent.getStartDateTime() != event.getStartDateTime()) {
            oldEvent.setStartDateTime(event.getStartDateTime());
            changed = true;
        }
        //aggiorno fine evento
        if (oldEvent.getEndDateTime() != event.getEndDateTime()) {
            oldEvent.setEndDateTime(event.getEndDateTime());
            changed = true;
        }
        //aggiorno location
        if (oldEvent.getLocation() == null ? (event.getLocation()) != null : !oldEvent.getLocation().equals(
                event.getLocation())) {
            logger.log(LoggerLevel.DEBUG, "trovata modifica location!");
            logger.log(LoggerLevel.DEBUG, "vecchia location: "
                    + oldEvent.getLocation() + " nuova: " + event.getLocation());
            logger.log(LoggerLevel.DEBUG, "vecchia has location: "
                    + oldEvent.hasLocation() + "nuova: " + event.hasLocation());
            if (!event.getLocation().isEmpty()) {
                oldEvent.setLocation(event.getLocation());
                oldEvent.setHasLocation(event.hasLocation());
                this.updateEventLatLng(oldEvent);
                weatherManager.updateWeather(oldEvent);

                changed = true;
            }
        }
        //aggiorno titolo
        if (oldEvent.getTitle() == null ? (event.getTitle()) != null : !oldEvent.getTitle().equals(
                event.getTitle())) {
            oldEvent.setTitle(event.getTitle());
            changed = true;
        }
        //aggiorno outdoor
        if (oldEvent.isIsOutdoor() != event.isIsOutdoor()) {
            oldEvent.setIsOutdoor(event.isIsOutdoor());
            //se ora è diventato outdoor
            if (event.isIsOutdoor()) {
                weatherManager.updateWeather(oldEvent);
            }
            changed = true;
        }

        //sincronizzo ogni eventuale cambiamento col db
        database.flush();

        logger.log(LoggerLevel.DEBUG,
                "Event Data updated with modification: {0}", changed);

        return changed;
    }

    //TODO check
    @Override
    public boolean changeEventPrivacy(Event event, boolean spreadInvitations) {
        //mi salvo il vecchio id
        Long oldId = event.getId();

        //cerco il vecchio evento
        Event oldEvent = database.find(Event.class, oldId);

        List<UserModel> oldInvitees = oldEvent.getInvitee();
        List<Invitation> oldInvitations = oldEvent.getInvitations();

        //se passo da private a public
        if (oldEvent instanceof PrivateEvent && event instanceof PublicEvent) {

            database.remove(oldEvent);//TODO qui le cascade?            
            database.persist(event);

            //a questo punto ho creato un evento con un id nuovo, quindi 
            //ripristino il vecchio id
            event.setId(oldId);

            //risetto anche gli invitati vecchi
            event.setInvitations(oldInvitations);

            //persisto i cambiamenti
            database.flush();

            notificationManager.createNotifications(oldInvitees, event,
                    NotificationType.EVENT_CHANGED_TO_PUBLIC, false);
            return true;

        } else if (oldEvent instanceof PublicEvent
                && event instanceof PrivateEvent) {
            //se passo da public a private

            //semplicemente persisto in nuovo evento privato eliminando
            //il pubblico
            database.remove(oldEvent);//TODO qui le cascade?            
            database.persist(event);

            //a questo punto ho creato un evento con un id nuovo, quindi 
            //ripristino il vecchio id
            event.setId(oldId);

            // risetto  gli invitati vecchi
            event.setInvitations(oldInvitations);

            //se l'utente vuole creare inviti anche per la gente con le public join
            if (spreadInvitations) {
                List<UserModel> newInvitees = getPublicJoin(oldEvent);
                invitationManager.createInvitations(newInvitees, event);
            }

            //persisto i cambiamenti
            database.flush();

            notificationManager.createNotifications(oldInvitees, event,
                    NotificationType.EVENT_CHANGED_TO_PRIVATE, false);

            return true;
        }
        return false;
    }

    @Override
    public List<Event> eventOnWall(utility.EventType type, int n, UserModel owner) {
        owner = database.find(UserModel.class, owner.getId());
        database.refresh(owner);
        switch (type) {
            case INVITED: {
                return invitedEventsOnWall(owner, n);
            }
            case PARTECIPATING: {
                return acceptedEventsOnWall(owner, n);
            }

            case JOINED: {
                List<Event> joinedEvents = new ArrayList<>();
                joinedEvents.addAll(joinedEventsOnWall(owner, n));
                return joinedEvents;

            }

            case OWNED: {
                return ownedEventonWall(owner, n);
            }

            case PUBLIC: {
                List<Event> publicEvents = new ArrayList<>();
                publicEvents.addAll(publicEventsOnWall(owner, n));
                return publicEvents;
            }

        }
        return null;
    }

    private List<PublicEvent> publicEventsOnWall(UserModel user, int n) {
        return database.createNamedQuery("findNextPublicEvents").setParameter(
                "user", user).setMaxResults(n).getResultList();

    }

    private List<Event> ownedEventonWall(UserModel user, int n) {
        List<Event> r = user.getOwnedEvents();
        if (n > r.size()) {
            return r;
        } else {
            return r.subList(0, n);
        }
    }

    private List<Event> acceptedEventsOnWall(UserModel user, int n) {
        List<Event> events = new ArrayList<>();
        List<Invitation> invitations = user.getInvitations();
        for (int i = 0; i < n && i < invitations.size(); i++) {
            if (invitations.get(i).getAnswer().equals(InvitationAnswer.YES)) {
                events.add(invitations.get(i).getEvent());
            }

        }
        return events;
    }

    private List<Event> invitedEventsOnWall(UserModel user, int n) {
        List<Event> events = new ArrayList<>();
        List<Invitation> invitations = user.getInvitations();
        for (int i = 0; i < n && i < invitations.size(); i++) {
            events.add(invitations.get(i).getEvent());
        }
        return events;
    }

    private List<PublicEvent> joinedEventsOnWall(UserModel user, int n) {
        List<PublicEvent> r = user.getPublicJoins();
        if (n > r.size()) {
            return r;
        } else {
            return r.subList(0, n);
        }
    }

    @Override
    public Event findEventbyId(Long id) {
        Event event = database.find(Event.class, id);
        if (event != null) {
            database.refresh(event);
            return event;
        } else {
            return null;
        }

    }

    @Override
    public boolean deleteEvent(Event event) {
        try {
            event = database.find(Event.class, event.getId());
            if (event instanceof PublicEvent) {
                PublicEvent publicEvent = (PublicEvent) event;
                notificationManager.createNotifications(publicEvent.getGuests(),
                        event, NotificationType.EVENT_CANCELLED, false);
            }
            notificationManager.createNotifications(event.getInvitee(), event,
                    NotificationType.EVENT_CANCELLED, false);
            database.remove(event);
            return true;
        } catch (IllegalArgumentException e) {
            logger.log(LoggerLevel.DEBUG, "Evento: {0} non trovato",
                    event.getId());
            return false;
        }

    }

    /**
     * Get all the invitees with a particular answer
     *
     * @param event event to serch invitees for
     * @param answer answer of the invitees
     * @return the invitees who answered like answer
     */
    @Override
    public List<UserModel> getInviteesFiltered(Event event, InvitationAnswer answer) {
        if (event != null) {
            if(event.getId() == null){
                logger.log(LoggerLevel.DEBUG,"id eveento null");
            }
            logger.log(LoggerLevel.DEBUG,"id evento :"+event.getId());
            logger.log(LoggerLevel.DEBUG,"database :"+database.toString());
            event = database.find(Event.class, event.getId());
            List<Invitation> invitations = event.getInvitations();
            List<UserModel> users = new ArrayList<>();

            for (Invitation invitation : invitations) {
                if (invitation.getAnswer().equals(answer)) {
                    users.add(invitation.getInvitee());
                }
            }
            return users;
        } else {
            return null;
        }

    }

    @Override
    public List<UserModel> getPublicJoin(Event event) {
        try {
            if (event instanceof PublicEvent) {
                PublicEvent publicEvent = (PublicEvent) database.find(
                        Event.class, event.getId());
                return publicEvent.getGuests();
            }
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean addPublicJoin(Event event, UserModel user) {

        if (user != null && event != null) {
            if (event instanceof PublicEvent) {
                user = database.find(UserModel.class, user.getId());
                if (user != null) {
                    user.addPublicJoin((PublicEvent) event);
                    database.flush();
                    return true;
                }
            }
        }
        return false;

    }

    @Override
    public boolean removePublicJoin(Event event, UserModel user) {
        if (user != null && event != null) {
            if (event instanceof PublicEvent) {
                user = database.find(UserModel.class, user.getId());
                if (user != null) {
                    user.deletePublicJoin((PublicEvent) event);
                    database.flush();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isInAnyCalendar(Event event, UserModel user
    ) {
        Long i = (Long) database.createNamedQuery("isInAnyCalendar").setParameter(
                "event", event.getId()).setParameter("user", user).getSingleResult();
        logger.log(LoggerLevel.DEBUG, "Evento già in " + i + " calendari!");
        return i != 0;
    }

}
