package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.DeleteManager;
import EJB.interfaces.EventManager;
import EJB.interfaces.InvitationManager;
import EJB.interfaces.NotificationManager;
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
import model.InvitationAnswer;
import model.NotificationType;
import model.PublicEvent;
import model.UserModel;
import model.WeatherForecast;
import utility.ControlMessages;
import utility.LoggerLevel;
import utility.TimeTool;

@Stateless
public class EventManagerImpl implements EventManager {

    @Inject
    private CalendarManager calManager;

    @Inject
    private InvitationManager invitationManager;

    @Inject
    private NotificationManager notificationManager;

    @Inject
    private WeatherManager weatherManager;

    @Inject
    DeleteManager deleteManager;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Inject
    @Default
    private Logger logger;

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

    //NB non usare il database in questo metodo perchÃ¨ l'event potrebbe non essere nel db
    @Override
    public void updateEventLatLng(Event event) {
        GeoApiContext context = new GeoApiContext().setApiKey(
                "AIzaSyCAlR8JiKO0QPZ_tm51cJITop7aGTDcnlo");
        GeocodingResult[] results;

        //se l'utente ha inserito un indirizzo e l'indirizzo Ã¨ capito da google 
        if (event.hasLocation() && event.getLocation() != null) {
            try {
                //cerco di trovare le coordinate corrispondenti
                results = GeocodingApi.geocode(context,
                        event.getLocation()).await();

                //se ho un risultato
                if (results.length > 0) {
                    //e setto le coordinate nell'evento
                    event.setLatitude(results[0].geometry.location.lat);
                    event.setLongitude(results[0].geometry.location.lng);

                    logger.log(LoggerLevel.DEBUG, "Trovate lat e long: {0} ,{1}",
                            new Object[]{event.getLatitude(),
                                event.getLongitude()});
                }
            } catch (Exception ex) {
                logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        } else {
            logger.log(LoggerLevel.DEBUG,
                    "Location null o non imposatata da google, skipping latLong");

        }

    }

    @Override
    public boolean updateEvent(Event event, CalendarModel inCalendar, List<UserModel> invitees
    ) {
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
            List<UserModel> oldInvitees = invitationManager.getInviteesFiltered(vecchioEvento, InvitationAnswer.YES);
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
                if (calManager.addToCalendar(vecchioEvento, inCalendar) != ControlMessages.EVENT_ADDED) {
                    return false;
                }
                logger.log(LoggerLevel.DEBUG, "evento aggiunto al celendario{0}",
                        inCalendar);
            }

            //cambio la privacy
//            if (changeEventPrivacy(event, false)) {
//                logger.log(LoggerLevel.DEBUG, "Event privacy CHANGED!");
//            }
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
        if (oldEvent != null) {
            //aggiorno la descrizione
            if (oldEvent.getDescription() == null ? (event.getDescription()) != null : !oldEvent.getDescription().equals(
                    event.getDescription())) {
                oldEvent.setDescription(event.getDescription());
                changed = true;
                logger.log(LoggerLevel.DEBUG, "trovata modifica descrizione!");
            }
            //aggiorno inizio evento
            if (!TimeTool.dateToTextDay(oldEvent.getStartDateTime().getTime(), "yyyy-MM-dd hh:mm").equals(TimeTool.dateToTextDay(event.getStartDateTime().getTime(), "yyyy-MM-dd hh:mm"))) {
                oldEvent.setStartDateTime(event.getStartDateTime());

                //aggiorno il tempo al nuovo giorno
                weatherManager.updateWeather(oldEvent);
                logger.log(LoggerLevel.DEBUG, "trovata modifica inizio evento!");
                changed = true;
            }
            //aggiorno fine evento
            if (!TimeTool.dateToTextDay(oldEvent.getEndDateTime().getTime(), "yyyy-MM-dd hh:mm").equals(TimeTool.dateToTextDay(event.getEndDateTime().getTime(), "yyyy-MM-dd hh:mm"))) {
                oldEvent.setEndDateTime(event.getEndDateTime());
                logger.log(LoggerLevel.DEBUG, "trovata modifica fine evento!");
                changed = true;
            }
            //aggiorno location
            if (oldEvent.getLocation() == null ? (event.getLocation()) != null : !oldEvent.getLocation().equals(
                    event.getLocation())) {

                if (!event.getLocation().isEmpty()) {
                    logger.log(LoggerLevel.DEBUG, "trovata modifica location!");
                    logger.log(LoggerLevel.DEBUG, "vecchia location: "
                            + oldEvent.getLocation() + " nuova: "
                            + event.getLocation());
                    logger.log(LoggerLevel.DEBUG, "vecchia has location: "
                            + oldEvent.hasLocation() + "nuova: "
                            + event.hasLocation());
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
                logger.log(LoggerLevel.DEBUG, "trovata modifica titolo!");
                changed = true;
            }
            //aggiorno outdoor
            if (oldEvent.isIsOutdoor() != event.isIsOutdoor()) {
                oldEvent.setIsOutdoor(event.isIsOutdoor());
                //se ora Ã¨ diventato outdoor
                if (event.isIsOutdoor()) {
                    weatherManager.updateWeather(oldEvent);
                }
                logger.log(LoggerLevel.DEBUG, "trovata modifica outdoor!");
                changed = true;
            }

            //sincronizzo ogni eventuale cambiamento col db
            database.flush();

            logger.log(LoggerLevel.DEBUG,
                    "Event Data updated with modification: {0}", changed);

            return changed;
        } else {
            return false;
        }
    }

//    @Override
//    public boolean changeEventPrivacy(Event event, boolean spreadInvitations) {
//        //mi salvo il vecchio id
//        Long oldId = event.getId();
//
//        //cerco il vecchio evento
//        Event oldEvent = database.find(Event.class, oldId);
//        logger.log(LoggerLevel.DEBUG, "Vecchio evento" + oldEvent);
//        logger.log(LoggerLevel.DEBUG, "nuovo evento:" + event);
//
//        List<UserModel> oldInvitees = oldEvent.getInvitee();
//        List<Invitation> oldInvitations = oldEvent.getInvitations();
//
//        database.detach(oldEvent);
//        database.detach(event);
//        database.clear();
//
//        //se passo da private a public
//        if (oldEvent instanceof PrivateEvent && event instanceof PublicEvent) {
//
//            logger.log(LoggerLevel.DEBUG, "Change from PRIVATE to PUBLIC event :" + oldId);
//            int rowCheck1 = (int) database.createNativeQuery("UPDATE EVENT SET TYPE = 'PUBLIC' WHERE ID = ?1;").setParameter(1, oldId).executeUpdate();
//            int rowCheck2 = (int) database.createNativeQuery("INSERT INTO PUBLIC_EVENT SELECT * FROM PRIVATE_EVENT WHERE ID = ?1;").setParameter(1, oldId).executeUpdate();
//            int rowCheck3 = (int) database.createNativeQuery("DELETE FROM PRIVATE_EVENT WHERE ID=?1;").setParameter(1, oldId).executeUpdate();
//
//            if (rowCheck1 == 0 || rowCheck2 == 0 || rowCheck3 == 0) {
//                logger.log(LoggerLevel.DEBUG, "------------------------------ No row affected - ERROR IN SQL QUERY ------------------------------------");
//                return false;
//            }
//            
//            event = database.find(Event.class, oldId);
//            boolean isRight = false;
//            if (event instanceof PublicEvent)
//                isRight = true;
//            logger.log(LoggerLevel.DEBUG, "New Event Privacy should be PUBLIC and it is : "+isRight);
//
//            notificationManager.createNotifications(oldInvitees, event,
//                    NotificationType.EVENT_CHANGED_TO_PUBLIC, false);
//            return true;
//
//        } else if (oldEvent instanceof PublicEvent
//                && event instanceof PrivateEvent) {
//            //se passo da public a private
//            logger.log(LoggerLevel.DEBUG, "Change from PUBLIC to PRIVATE event :" + oldId);
//            //semplicemente persisto un nuovo evento privato eliminando
//            //il pubblico
//
//            int rowCheck1 = (int) database.createNativeQuery("UPDATE EVENT SET TYPE = 'PRIVATE' WHERE ID = ?1;").setParameter(1, oldId).executeUpdate();
//            int rowCheck2 = (int) database.createNativeQuery("INSERT INTO PRIVATE_EVENT SELECT * FROM PUBLIC_EVENT WHERE ID = ?1;").setParameter(1, oldId).executeUpdate();
//            int rowCheck3 = (int) database.createNativeQuery("DELETE FROM PUBLIC_EVENT WHERE ID=?1;").setParameter(1, oldId).executeUpdate();
//
//            if (rowCheck1 == 0 || rowCheck2 == 0 || rowCheck3 == 0) {
//                logger.log(LoggerLevel.DEBUG, "------------------------------ No row affected - ERROR IN SQL QUERY ------------------------------------");
//                return false;
//            }
//
////
//            //se l'utente vuole creare inviti anche per la gente con le public join
//            if (spreadInvitations) {
//                List<UserModel> newInvitees = getPublicJoin(oldEvent);
//                invitationManager.createInvitations(newInvitees, event);
//            }
//
//            //persisto i cambiamenti
//            database.flush();
//
//            notificationManager.createNotifications(oldInvitees, event,
//                    NotificationType.EVENT_CHANGED_TO_PRIVATE, false);
//
//            return true;
//        }
//        return false;
//    }
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
        return database.createNamedQuery("findNextPublicEvents").setMaxResults(n).getResultList();

    }

    private List<Event> ownedEventonWall(UserModel user, int n) {
        List<Event> r = (List<Event>) database.createNamedQuery("findNextOwnedEvents").setParameter("user", user).getResultList();
        if (n > r.size()) {
            return r;
        } else {
            return r.subList(0, n);
        }
    }

    private List<Event> acceptedEventsOnWall(UserModel user, int n) {
        List<Event> events = database.createNamedQuery("findAcceptedInvitations").setParameter("user", user).getResultList();
        if (n > events.size()) {
            return events;
        } else {
            return events.subList(0, n);
        }
    }

    private List<Event> invitedEventsOnWall(UserModel user, int n) {
        List<Event> events = database.createNamedQuery("findInvitedEvents").setParameter("user", user).getResultList();
        if (n > events.size()) {
            return events;
        } else {
            return events.subList(0, n);
        }
    }

    private List<PublicEvent> joinedEventsOnWall(UserModel user, int n) {
        List<PublicEvent> r = database.createNamedQuery("findPublicJoins").setParameter("user", user).getResultList();
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
                event = database.find(Event.class, event.getId());
                if (event != null) {
                    ((PublicEvent) event).addGuest(user);
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
                event = database.find(Event.class, event.getId());
                if (event != null) {
                    ((PublicEvent) event).removeGuest(user);
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
        logger.log(LoggerLevel.DEBUG, "Evento giÃ  in " + i + " calendari!");
        return i != 0;
    }

}
