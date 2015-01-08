package EJB;

import EJB.interfaces.CalendarManager;
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
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import model.CalendarModel;
import model.Event;
import model.PrivateEvent;
import model.UserModel;
import utility.ControlMessages;
import utility.LoggerLevel;
import utility.LoggerProducer;
import utility.WeatherMessages;
import model.WeatherForecast;
import utility.DeleteCalendarOption;

@Stateless
public class CalendarManagerImpl implements CalendarManager {

    Logger logger = LoggerProducer.debugLogger(CalendarManagerImpl.class);

    @Inject
    private WeatherManager weatherManager;

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

        boolean weatherIsOk = checkWeather(event);
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
    private boolean checkWeather(Event event) {
        WeatherForecast forecast = weatherManager.getWeather(event);

        //se non è buono ritorno false, false true
        if (forecast.getMessage() != WeatherMessages.BAD_WEATHER) {
            logger.log(LoggerLevel.DEBUG, "Bad weather found in CheckWeather");
            return false;
        }
        logger.log(LoggerLevel.DEBUG, "Good weather foudn in checkWeather");
        return true;
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
        //TODO controllare che questo metodo venga chiamato anche quando faccio l'update cambiando data/ora
        if (event != null) {
            try {
                Event firstConflict = (Event) database.createNamedQuery(
                        "isConflicting").setParameter(
                                "user", event.getOwner()).setParameter("end",
                                event.getEndDateTime()).setParameter(
                                "start", event.getStartDateTime()).setParameter(
                                "id",
                                event.getId()).getSingleResult();

                //se non alza eccezioni è perchè ha trovato esattamente un conflitto
                logger.log(LoggerLevel.DEBUG, "Conflict found");

                return true;
            } catch (NoResultException e) {
                //se non trova risultati allora non ci sono conflitti
                logger.log(LoggerLevel.DEBUG, "Conflict NOT found");
                return false;
            } catch (NonUniqueResultException e) {
                //se trova molti risultati allora ci sono conflitti
                logger.log(LoggerLevel.DEBUG, "Conflict found");
                return true;
            }
        } else {
            logger.log(Level.SEVERE, "Event is null.");
            return false;
        }
    }

    @Override
    //TODO, questa la facciamo chiamare da fra? --> sì, ma in abbinamento a isInConflict (da chiamare solo se quello ritorna true)
    public int findFreeSlots(Event event) {
        int searchRange = 15;

        Event tempEvent = new PrivateEvent(event.getTitle(),
                (Calendar) event.getStartDateTime().clone(),
                (Calendar) event.getEndDateTime().clone(), event.getLocation(),
                null, event.isIsOutdoor(), event.getOwner());

        //set temp event with event data (kind of clone)
        tempEvent.setId(event.getId());
        tempEvent.setLatitude(event.getLatitude());
        tempEvent.setLongitude(event.getLongitude());

        for (int i = 1; i < searchRange; i++) {

            //setto nuova data inizio
            tempEvent.getStartDateTime().add(Calendar.DAY_OF_MONTH, i);

            //setto nuova data fine           
            tempEvent.getEndDateTime().add(Calendar.DAY_OF_MONTH, i);

            if (!isInConflict(tempEvent) && checkWeather(tempEvent)) {
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
    //TODO qui user non serve perchè deduco l'id dal calendar
    //TODO per ora nessuno usa questa funzione!
    public boolean addCalendarToUser(UserModel user, CalendarModel cal) {
        user = database.find(UserModel.class, user.getId());
        cal.setOwner(user);
        cal.setTitle("Pubblic_Cal");
        try {
            database.persist(cal);
            logger.log(Level.INFO, "Pulic_Cal created for user: {0}",
                    user.getEmail());
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
        if (event != null) {
            event = database.find(Event.class, event.getId());
            if (event != null) {
                for (CalendarModel cal : event.getOwner().getOwnedCalendars()) {
                    cal.getEventsInCalendar().remove(event);
                }

                if (calendar != null) {

                    calendar = (CalendarModel) database.createNamedQuery(
                            "findCalbyUserAndTitle").setParameter("id",
                                    calendar.getOwner()).setParameter(
                                    "title", calendar.getTitle()).getSingleResult();

                    if (calendar.addEventInCalendar(event)) {

                        logger.log(Level.INFO,
                                "Evento {0} aggiunto al calendario {1} di {2}",
                                new Object[]{event.getTitle(),
                                    calendar.getTitle(),
                                    calendar.getOwner().getEmail()});
                        database.flush();
                        database.refresh(calendar);
                        logger.log(LoggerLevel.DEBUG,
                                "Events in calendar now: {0}",
                                calendar.getEventsInCalendar());

                        return ControlMessages.EVENT_ADDED;
                    }
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
            calendar.setTitle("Default (" + Calendar.getInstance().toString()
                    + ")");

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
        if (calendar.isIsPublic()) {
            calendar.setIsPublic(false);
        } else {
            calendar.setIsPublic(false);
        }
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
                        for (int i=0; i< calendar.getEventsInCalendar().size() ; i++) {
                            defaultCalendar.addEventInCalendar(calendar.getEventsInCalendar().get(i));
                        }
                    calendar.getEventsInCalendar().clear();
                    case DELETE_CALENDAR_ONLY:
                        calendar.getEventsInCalendar().clear();
                    case DELETE_ALL:
                    //non faccio nulla, perchè il CASCADE è già come opzione default del DB.

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

}
