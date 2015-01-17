package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.InvitationManager;
import EJB.interfaces.WeatherManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import model.CalendarModel;
import model.Event;
import model.InvitationAnswer;
import model.PrivateEvent;
import model.PublicEvent;
import model.UserModel;
import utility.ControlMessages;
import utility.LoggerLevel;
import utility.LoggerProducer;
import utility.WeatherMessages;
import model.WeatherForecast;
import utility.DeleteCalendarOption;

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
            try {
                //ha l'id a null quando faccio il check prima di inserirlo nel db
                if (event.getId() == null) {
                    //imposto l'id a -1 perchè sicuramente non esiste un evento con lo stesso
                    //id
                    //TODO trovare un modo per fare qusta cosa senza crocifissione
                    //ma con query
                    event.setId(Long.parseLong("-1"));
                }
                logger.log(LoggerLevel.DEBUG, "id event ora:" + event.getId());

                Event firstConflict = (Event) database.createNamedQuery(
                        "isConflicting").setParameter(
                                "user", event.getOwner()).setParameter("end",
                                event.getEndDateTime()).setParameter(
                                "start", event.getStartDateTime()).setParameter(
                                "id",
                                event.getId()).getSingleResult();

                //reimposto l'id dell'evento a null se lo era
                if (event.getId() == -1) {
                    event.setId(null);
                }

                logger.log(LoggerLevel.DEBUG, "id event ora: {0}", event.getId());

                //se non alza eccezioni è perchè ha trovato esattamente un conflitto
                logger.log(LoggerLevel.DEBUG, "Conflict found with event: "
                        + firstConflict.getTitle() + "id: "
                        + firstConflict.getId());

                return true;
            } catch (NoResultException e) {
                //reimposto l'id dell'evento a null se lo era
                if (event.getId() == -1) {
                    event.setId(null);
                }
                logger.log(LoggerLevel.DEBUG, "id event ora:" + event.getId());

                //se non trova risultati allora non ci sono conflitti
                logger.log(LoggerLevel.DEBUG, "Conflict NOT found");
                return false;
            } catch (NonUniqueResultException e) {
                //reimposto l'id dell'evento a null se lo era

                if (event.getId() == -1) {
                    event.setId(null);
                }
                logger.log(LoggerLevel.DEBUG, "id event ora:" + event.getId());

                //se trova molti risultati allora ci sono conflitti
                logger.log(LoggerLevel.DEBUG, "Conflict found");
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
        //se l'evento è privato e sei l owner o hai un invito
        if ((event instanceof PrivateEvent) && (event.getOwner().equals(user)
                || (event.getInvitee().contains(user)))) {
            //e hai messo che parteciperai
            if (invitationManager.getInvitationByUserAndEvent(user, event).getAnswer()
                    == InvitationAnswer.YES) {
                return true;
            }
        } else if (event instanceof PublicEvent) {
            //se l'evento è pubblico  e hai messo che parteciperai
            if (getPublicJoin(event).contains(user)) {
                return true;
            }
        }
        logger.log(LoggerLevel.DEBUG,
                "User do not have permission to add event to his calendar");
        return false;

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

    public void removeFromAllCalendars(UserModel user, Event event) {
        if (user != null && event != null) {
            user = database.find(UserModel.class, user.getId());
            event = database.find(Event.class, event.getId());
            //TODO da finire
            //Calendar calendar = findCalendarbyuserandevent;

//            if (user != null && event != null) {
//                for (CalendarModel cal : calendar.getOwner().getOwnedCalendars()) {
//                    cal.getEventsInCalendar().remove(event);
//                    logger.log(LoggerLevel.DEBUG,
//                            "Event removed from calendar: " + cal.getTitle());
//                }
//            }
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
    public List<String> getCalendarTitles(UserModel user
    ) {
        user = database.find(UserModel.class, user.getId());
        database.refresh(user);
        List<String> names = new ArrayList<>();
        for (CalendarModel cal : this.getCalendars(user)) {
            names.add(cal.getTitle());
        }
        return names;

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
    public boolean deleteCalendar(CalendarModel calendar, DeleteCalendarOption opt) {
        try {
            if (!isDefault(calendar)) {
                calendar = (CalendarModel) database.createNamedQuery(
                        "findCalbyUserAndTitle").setParameter("id",
                                calendar.getOwner()).setParameter("title",
                                calendar.getTitle()).getSingleResult();
                switch (opt) {
                    case MOVE_EVENTS_AND_DELETE:
                        CalendarModel defaultCalendar = (CalendarModel) database.createNamedQuery(
                                "findDefaultCalendar").setParameter("user",
                                        calendar.getOwner()).getSingleResult();
                        for (int i = 0; i
                                < calendar.getEventsInCalendar().size(); i++) {
                            defaultCalendar.addEventInCalendar(
                                    calendar.getEventsInCalendar().get(i));
                        }
                        calendar.getEventsInCalendar().clear();
                        break;
                    case DELETE_CALENDAR_ONLY:
                        calendar.getEventsInCalendar().clear();
                        break;
                    case DELETE_ALL:
                        Event event;
                        //per ogni evento
                        for (int i = 0; i
                                < calendar.getEventsInCalendar().size(); i++) {
                            event = calendar.getEventsInCalendar().get(i);
                            //se l'owner dell'evento è diverso da quello del calendario
                            if (!Objects.equals(event.getOwner().getId(),
                                    calendar.getOwner().getId())) {
                                //rimuovo l'evento dal calendario così quando alla fine faccio la remove
                                //la cascade non mi rimuove un evento di cui non sono il proprietario
                                calendar.getEventsInCalendar().remove(event);
                            }
                        }
                }

                database.flush();
                database.remove(calendar);
                return true;
            } else {
                return false;
            }
        } catch (IllegalArgumentException e) {
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
}
