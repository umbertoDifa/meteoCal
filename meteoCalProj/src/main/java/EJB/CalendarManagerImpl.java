package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.InvitationManager;
import EJB.interfaces.WeatherManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.CalendarModel;
import model.Event;
import model.InvitationAnswer;
import model.PrivateEvent;
import model.PublicEvent;
import model.UserModel;
import utility.ControlMessages;
import utility.LoggerLevel;
import utility.WeatherMessages;
import model.WeatherForecast;

@Stateless
public class CalendarManagerImpl implements CalendarManager {

    @Inject
    private Logger logger;

    @Inject
    private WeatherManager weatherManager;

    @Inject
    private InvitationManager invitationManager;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    /**
     * Controlla che non ci siano conflitti nel calendario e che non sia brutto
     * tempo ritorna la lista di messaggi di controllo
     *
     * @param event Evento da controllare
     * @return lista di messaggi di errori o messaggio no_problem se tutto va
     * bene
     */
    @Override
    public List<ControlMessages> checkData(Event event) {
        List<ControlMessages> result = new ArrayList<>();
        logger.log(LoggerLevel.DEBUG, "Evento ad ora: " + event.getTitle());

        boolean weatherIsOk = isGoodWeather(event);
        boolean haveConflicts = isInConflict(event);

        //se tutto ok
        if (weatherIsOk && !haveConflicts) {
            result.add(ControlMessages.NO_PROBLEM);

        } else {
            //se il tempo non è buono 
            if (!weatherIsOk) {
                result.add(ControlMessages.BAD_WEATHER_FORECAST);
            }
            //se ci sono conflitti
            if (haveConflicts) {
                result.add(ControlMessages.CALENDAR_CONFLICTS);
            }
        }
        return result;
    }

    /**
     * Scarica le previsioni del tempo e le controlla
     *
     * @param day giorno per cui scaricare le previsioni
     * @param city città per cui scaricare le previsioni
     * @return false se il tempo è brutto, true se il tempo è buono o non sono
     * riuscito ad ottenere informazioni
     */
    private boolean isGoodWeather(Event event) {
        WeatherForecast forecast = weatherManager.getWeather(event);

        //se non è buono ritorno false
        if (forecast.getMessage() != WeatherMessages.BAD_WEATHER) {
            logger.log(LoggerLevel.DEBUG,
                    "Good weather(or no weather) found in CheckWeather");
            logger.log(LoggerLevel.DEBUG,
                    "In particolare il tempo trovato è: "
                    + forecast.getMessage().getMessage());
            return true;
        }
        logger.log(LoggerLevel.DEBUG, "Bad weather foudn in checkWeather");
        return false;
    }

    /**
     * Controlla i conflitti dell'evento che si vuole schedulare REQUIRES event
     * not null
     *
     * @param event l'evento da fare
     * @return false se non conflitti, true se ci sono conflitti
     */
    @Override
    public boolean isInConflict(Event event) {
        if (event != null) {
            Long conflictNumber;
            logger.log(LoggerLevel.DEBUG, "Checking conflicts for event:  " + event.getTitle() + ", owner: " + event.getOwner().getName() + ", id: " + event.getId());
            if (event.getId() == null) {
                conflictNumber = (Long) database.createNamedQuery(
                        "newEventConflicting").setParameter(
                                "user", event.getOwner()).setParameter("end",
                                event.getEndDateTime()).setParameter(
                                "start", event.getStartDateTime()).getSingleResult();

                logger.log(LoggerLevel.DEBUG, "Debugging query. Owned events in conflict: "+conflictNumber);

            } else {
                conflictNumber = (Long) database.createNamedQuery(
                        "isConflicting").setParameter(
                                "user", event.getOwner()).setParameter("end",
                                event.getEndDateTime()).setParameter(
                                "start", event.getStartDateTime()).setParameter(
                                "id",
                                event.getId()).getSingleResult();

//            List<Event> conflicts = (List<Event>) database.createNamedQuery(
//                    "isConflicting").setParameter(
//                            "user", event.getOwner()).setParameter("end",
//                            event.getEndDateTime()).setParameter(
//                            "start", event.getStartDateTime()).setParameter(
//                            "id",
//                            event.getId()).getResultList();
//
//            int conflictNumber = conflicts.size();
            }
            if (conflictNumber == 0) {

                //se non trova risultati allora non ci sono conflitti
                logger.log(LoggerLevel.DEBUG, "Conflict NOT found");
                return false;

            } else {

                //se trova molti risultati allora ci sono conflitti
                logger.log(LoggerLevel.DEBUG, "Conflict found");
//                for (int i = 0; i < conflictNumber; i++) {
//                    logger.log(LoggerLevel.DEBUG, "Title: " + conflicts.get(i).getTitle() + ", id: " + conflicts.get(i).getId());
//                }
                return true;
            }

        } else {
            logger.log(Level.SEVERE, "Event is null.");
            return false;
        }
    }

    /**
     * trova uno spazio libero nei porssimi quindici giorni che non abbia
     * conflitti
     *
     * @param event evento per il quale trovare una nuova posizione
     * @return tra quanti giorni è possibile fare un rischedule, -1 se non ho
     * trovato niente
     */
    @Override
    public int findFreeSlots(Event event) {
        logger.log(LoggerLevel.DEBUG, "In find free slots");

        int SEARCH_RANGE = 15;

        Event tempEvent = new PrivateEvent(event.getTitle(),
                (Calendar) event.getStartDateTime().clone(),
                (Calendar) event.getEndDateTime().clone(), event.getLocation(),
                null, event.isIsOutdoor(), event.getOwner());

        //set temp event with event data (kind of clone)
        tempEvent.setId(event.getId());
        tempEvent.setHasLocation(event.hasLocation());
        tempEvent.setLatitude(event.getLatitude());
        tempEvent.setLongitude(event.getLongitude());

        for (int i = 1; i < SEARCH_RANGE; i++) {
            logger.log(LoggerLevel.DEBUG,
                    "Parametri tempEvent:\nuser: "
                    + tempEvent.getOwner().getEmail() + "\nfine evento: "
                    + tempEvent.getEndDateTime().getTime().toString()
                    + "\ninizio evento: "
                    + tempEvent.getStartDateTime().getTime().toString()
                    + "\nid evento: " + tempEvent.getId());

            //ogni volta io aggiungo un giorno quindi la add prende come argomento
            //1 e non i 
            //setto nuova data inizio
            tempEvent.getStartDateTime().add(Calendar.DAY_OF_MONTH, 1);

            //setto nuova data fine           
            tempEvent.getEndDateTime().add(Calendar.DAY_OF_MONTH, 1);

            if (!isInConflict(tempEvent) && isGoodWeather(tempEvent)) {
                return i;
            }
        }

        //TODO modificare questo orrore
        return -1;
    }

    @Override
    public List<CalendarModel> getCalendars(UserModel user) {
        //se lo user passato è diverso da null
        if (user != null) {
            user = database.find(UserModel.class, user.getId());
            //se trovo lo user nel db
            if (user != null) {
                //refresho lo stato dello user
                database.refresh(user);
                //e ritorno i suoi calendari
                return user.getOwnedCalendars();
            }
        }
        return null;

    }

    @Override
    public CalendarModel getCalendarUpdated(CalendarModel calendar) {
        calendar = (CalendarModel) database.createNamedQuery(
                "findCalbyUserAndTitle").setParameter("id",
                        calendar.getOwner()).setParameter("title",
                        calendar.getTitle()).getSingleResult();
        database.refresh(calendar);
        return calendar;
    }

    @Override
    public List<Event> getEventsUpdated(CalendarModel calendar) {
        calendar = (CalendarModel) database.createNamedQuery(
                "findCalbyUserAndTitle").setParameter("id",
                        calendar.getOwner()).setParameter("title",
                        calendar.getTitle()).getSingleResult();
        database.refresh(calendar);
        return calendar.getEventsInCalendar();

    }

    @Override
    public boolean addCalendarToUser(CalendarModel calendar) {
        try {
            boolean makeDefault = calendar.isIsDefault();
            calendar.setIsDefault(false);
            database.persist(calendar);
            logger.log(Level.INFO, "{0} created for user {1}", new Object[]{
                calendar.getTitle(), calendar.getOwner().getEmail()});
            if (makeDefault) {
                makeDefault(calendar);
            }
            return true;
        } catch (EntityExistsException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * aggiunge un evento al calendario, cancellandolo se è presente in altri
     * calendari dello stesso utente
     *
     * @param event Evento da aggiungere
     * @param calendar Calendario in cui aggiungere l'evento
     * @return
     */
    @Override
    public ControlMessages addToCalendar(Event event, CalendarModel calendar) {
        //se gli oggetti non sono null e lo user ha i permessi di aggiungere
        //l'evento al suo calendario
        if (event != null && calendar != null && hasPermissionToAdd(
                calendar.getOwner(), event)) {
            //recupera gli oggetti dal db
            event = database.find(Event.class, event.getId());

            calendar = (CalendarModel) database.createNamedQuery(
                    "findCalbyUserAndTitle").setParameter(
                            "id", calendar.getOwner()).setParameter("title",
                            calendar.getTitle()).getSingleResult();

            if (event != null && calendar != null) {
                //elimna l'evento da qualsasi calendario dell'utenet
                for (CalendarModel cal : calendar.getOwner().getOwnedCalendars()) {
                    if (cal.getEventsInCalendar().contains(event)) {
                        cal.getEventsInCalendar().remove(event);
                        logger.log(LoggerLevel.DEBUG,
                                "Event removed from calendar: {0}",
                                cal.getTitle());
                    }
                }

                //aggiungi l'evento al calendario dell'utente
                if (calendar.addEventInCalendar(event)) {
                    logger.log(Level.INFO,
                            "Evento {0} aggiunto al calendario {1} di {2}",
                            new Object[]{event.getTitle(),
                                calendar.getTitle(),
                                calendar.getOwner().getEmail()});
                    logger.log(LoggerLevel.DEBUG,
                            "Events in calendar now: {0}",
                            calendar.getEventsInCalendar());

                    database.flush();

                    return ControlMessages.EVENT_ADDED;
                }

                logger.log(Level.WARNING, "Evento non aggiunto al calendario");
                return ControlMessages.ERROR_ADDING_EVENT_TO_CAL;
            } else {
                return ControlMessages.USER_NOT_FOUND;
            }
        } else {
            return ControlMessages.ERROR_ADDING_EVENT_TO_CAL;
        }
    }

    /**
     * un utente ha i permessi solo se come al solito è invitato o è pubblico o
     * è l'owner e ha messo che parteciperà o accettato l'invito
     *
     * @param user
     * @param event
     * @return
     */
    private boolean hasPermissionToAdd(UserModel user, Event event) {
        //se sei l'owner
        if (event.getOwner().equals(user)) {
            return true;
        }
        //se l'evento è privato e  hai un invito
        //e hai messo che parteciperai
        if ((event instanceof PrivateEvent)
                && (event.getInvitee().contains(user)) && hasAnsweredYes(user,
                        event)) {
            return true;

        } else if (event instanceof PublicEvent) {
            //se l'evento è pubblico  e hai messo che parteciperai o è pubblico
            //ma hai un invito a cui hai risposto si
            if (getPublicJoin(event).contains(user)
                    || (event.getInvitee().contains(user)
                    && hasAnsweredYes(user, event))) {
                return true;
            }
        }
        logger.log(LoggerLevel.DEBUG,
                "User do not have permission to add event to his calendar");

        return false;
    }

    private boolean hasAnsweredYes(UserModel user, Event event) {
        return invitationManager.getInvitationByUserAndEvent(user, event)
                != null && invitationManager.getInvitationByUserAndEvent(
                        user, event).getAnswer() == InvitationAnswer.YES;

    }

    /**
     * Doppione di quella in eventManager per qeustioni di dependency
     *
     * @param event
     * @return
     */
    private List<UserModel> getPublicJoin(Event event) {
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
    public
            void removeFromAllCalendars(UserModel user, Event event) {
        if (user != null && event != null) {
            user = database.find(UserModel.class, user.getId());
            event = database.find(Event.class, event.getId());

            if (user != null && event
                    != null) {
                for (CalendarModel cal : user.getOwnedCalendars()) {
                    if (cal.getEventsInCalendar().remove(event)) {
                        logger.log(LoggerLevel.DEBUG,
                                "Event removed from calendar: {0}",
                                cal.getTitle());
                    }
                }
            }
        }
    }

    /**
     * Create a calendar named default for the user provided without persisting
     *
     * @param user user to create a calendar for
     * @return a default calendar, null if the user is null
     */
    @Override
    public CalendarModel createDefaultCalendar(UserModel user) {
        if (user != null) {
            CalendarModel calendar = new CalendarModel();

            //setto proprietà calendario
            calendar.setIsDefault(true);
            calendar.setIsPublic(false);
            calendar.setOwner(user);

            //il titolo è default + timestamp
            calendar.setTitle("Default");

            logger.log(Level.INFO, "Default calendar for user "
                    + "{0} created",
                    user.getEmail());

            return calendar;
        }
        return null;
    }

    @Override
    public CalendarModel findCalendarByName(UserModel user, String name) {
        for (CalendarModel cal : this.getCalendars(user)) {
            if (cal.getTitle().equals(name)) {
                return cal;
            }
        }
        return null;
    }

   

    @Override
    public void toggleCalendarPrivacy(CalendarModel calendar) {
        calendar = (CalendarModel) database.createNamedQuery(
                "findCalbyUserAndTitle").setParameter("id", calendar.getOwner()).setParameter(
                        "title", calendar.getTitle()).getSingleResult();
        database.refresh(calendar);
        if (calendar.isIsPublic()) {
            calendar.setIsPublic(false);
        } else {
            calendar.setIsPublic(true);
        }
        database.flush();
    }

    @Override
    public boolean isDefault(CalendarModel calendar) {
        calendar = (CalendarModel) database.createNamedQuery(
                "findCalbyUserAndTitle").setParameter("id", calendar.getOwner()).setParameter(
                        "title", calendar.getTitle()).getSingleResult();
        if (calendar.isIsDefault()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean makeDefault(CalendarModel calendar) {
        if (calendar.isIsDefault()) {
            return true;
        }
        try {
            CalendarModel defaultCalendar = (CalendarModel) database.createNamedQuery(
                    "findDefaultCalendar").setParameter("user",
                            calendar.getOwner()).getSingleResult();
            defaultCalendar.setIsDefault(false);

            calendar = (CalendarModel) database.createNamedQuery(
                    "findCalbyUserAndTitle").setParameter("id",
                            calendar.getOwner()).setParameter("title",
                            calendar.getTitle()).getSingleResult();
            calendar.setIsDefault(true);
            return true;

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public CalendarModel getDefaultCalendar(UserModel user) {
        try {
            CalendarModel calendar = (CalendarModel) database.createNamedQuery(
                    "findDefaultCalendar").setParameter("user",
                            user).getSingleResult();
            database.refresh(calendar);
            return calendar;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public CalendarModel
            getCalendarOfEvent(Event event, UserModel user) {
        if (user != null && event != null) {
            user = database.find(UserModel.class, user.getId());
            database.refresh(user);
            for (CalendarModel calendar
                    : user.getOwnedCalendars()) {
                if (calendar.hasEvent(event)) {
                    return calendar;
                }
            }
        }
        return null;
    }
}
