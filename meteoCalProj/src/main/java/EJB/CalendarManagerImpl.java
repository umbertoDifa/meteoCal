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
        Calendar day = event.getStartDateTime();
        String city = event.getLocation();
        UserModel user = event.getOwner();

        List<ControlMessages> result = new ArrayList<>();

        boolean weatherIsOk = checkWeather(day, city);
        boolean haveConflicts = isInConflict(user, event);

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
    private boolean checkWeather(Calendar day, String city) {
        WeatherForecast forecast = weatherManager.getWeather(day, city);

        //se non è buono ritorno false, false true
        if (forecast.getMessage() != WeatherMessages.BAD_WEATHER) {
            return false;
        }
        return true;
    }

    /**
     * Controlla i conflitti dell'evento che si vuole schedulare
     *
     * @param user l'utente che sta facendo l'evento
     * @param event l'evento da fare
     * @return false se non conflitti, true se ci sono conflitti
     */
    @Override
    public boolean isInConflict(UserModel user, Event event) {
        //TODO controllare che questo metodo venga chiamato anche quando faccio l'update cambiando data/ora
        if (user != null && event != null) {
            try {
            Event firstConflict = (Event) database.createNamedQuery(
                    "isConflicting").setParameter(
                            "user", user).setParameter("end",
                            event.getEndDateTime()).setParameter(
                            "start", event.getStartDateTime()).setParameter("id",
                            event.getId()).getSingleResult();
            
            //se non alza eccezioni è perchè ha trovato conflitti
            return true;
            
            //TODO questo workaround è bruttino ma non ho trovato altri metodi altrettanto efficienti.
            //Un'altra opzione sarebbe quella di far tornare una lista e farne la size, ma è molto meno efficiente
            }catch (NoResultException e)  {
                return false;
                
            }
        } else {
            
            //TODO da gestire con una eccezione!
            logger.log(Level.SEVERE, "User or event is null.");
            return true;
        }
    }

    @Override
    //TODO, questa la facciamo chiamare da fra? --> sì, ma in abbinamento a isInConflict (da chiamare solo se quello ritorna true)
    public int findFreeSlots(UserModel user, Event event) {
        int searchRange = 15;
        Calendar tempDateTime;
        Event tempEvent = new PrivateEvent();
        tempEvent.setId(event.getId());
        for (int i = 1; i < searchRange; i++) {

            //setto nuova data inizio
            tempDateTime = event.getStartDateTime();
            tempDateTime.add(Calendar.DAY_OF_MONTH, i);
            tempEvent.setStartDateTime(tempDateTime);
            //setto nuova data fine
            tempDateTime = event.getEndDateTime();
            tempDateTime.add(Calendar.DAY_OF_MONTH, i);
            tempEvent.setEndDateTime(tempDateTime);
            
            
            if (!isInConflict(user, tempEvent));
            return i;
        }
        
        //TODO modificare questo orrore
        return -1;
    }

    @Override
    public List<CalendarModel> getCalendars(UserModel user) {
        user = database.find(UserModel.class, user.getId());
        database.refresh(user);
        return user.getOwnedCalendars();
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

    @Override
    public Calendar findFreeDay(Calendar fromBusyDay, int weeksAhead) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    @Override
    public CalendarModel createDefaultCalendar(UserModel user) {
        //TODO if user == null
        CalendarModel calendar = new CalendarModel();
        calendar.setIsDefault(true);
        calendar.setIsPublic(false);
        calendar.setOwner(user);
        //TODO questo set deve essere un po' più accurato, ad esempio se già esiste un
        //calendario chiamato default allora il titolo sarà tipo default2
        calendar.setTitle("Default");

        logger.log(Level.INFO, "Default calendar for user +{0} created",
                user.getEmail());

        return calendar;
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

}
