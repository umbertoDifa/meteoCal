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
import javax.persistence.PersistenceContext;
import model.CalendarModel;
import model.Event;
import model.UserModel;
import utility.ControlMessages;
import utility.LoggerLevel;
import utility.LoggerProducer;

@Stateless
public class CalendarManagerImpl implements CalendarManager {

    Logger logger = LoggerProducer.debugLogger(CalendarManagerImpl.class);

    @Inject
    WeatherManager weatherManager;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Override
    public boolean checkData() {
        //TODO implement method
        return false;
    }

    private void checkWeather() {
        //TODO do             
    }

    private void checkConflicts(UserModel user, Event event) {
        event = database.find(Event.class, event.getId());
        int conflictingEventIndex = database.createNamedQuery("isConflicting").setParameter(
                "user", user).setParameter("end", event.getEndDateTime()).setParameter(
                        "start", event.getStartDateTime()).setParameter("id",
                        event.getId()).getFirstResult();
        if (conflictingEventIndex != 0) {
            int offset = findFreeSlots(user, event);
            if (offset == 0) {
                //TODO MA COSA RITORNO QUI?
                ;
            }
        }
    }

    private int findFreeSlots(UserModel user, Event event) {
        int searchRange = 15;
        Calendar newStart = event.getStartDateTime();
        Calendar newEnd = event.getEndDateTime();
        for (int i = 1; i < searchRange; i++) {
            newEnd.add(Calendar.DAY_OF_MONTH, i);
            newStart.add(Calendar.DAY_OF_MONTH, i);
            int conflictingEventIndex = database.createNamedQuery(
                    "isConflicting").setParameter("user", user).setParameter(
                            "end", newEnd).setParameter("start", newStart).setParameter(
                            "id", event.getId()).getFirstResult();
            if (conflictingEventIndex == 0) {
                return i;
            }
        }
        return 0;
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
        //TODO fare check che l'event sia non nulla e che la find non sollevi eccezioni
        //si potrebbe fare un metodo a parte per tutti questi controlli legati
        //al db e exception handling visto che lo usiamo parecchio
        event = database.find(Event.class, event.getId());

        for (CalendarModel cal : event.getOwner().getOwnedCalendars()) {
            cal.getEventsInCalendar().remove(event);
        }

        if (calendar != null) {
            //TODO anche qui il check sulle exception di getSingleResult
            calendar = getCalendar(calendar);

            if (calendar.addEventInCalendar(event)) {
                //calendar.getEventsInCalendar().add(event);
                logger.log(Level.INFO,
                        "Evento {0} aggiunto al calendario {1} di {2}",
                        new Object[]{event.getTitle(),
                                     calendar.getTitle(),
                                     calendar.getOwner().getEmail()});

                logger.log(LoggerLevel.DEBUG, "Events in calendar now: {0}",
                        calendar.getEventsInCalendar());

                return ControlMessages.EVENT_ADDED;
            }
        }
        logger.log(Level.WARNING, "Evento non aggiunto al calendario");
        return ControlMessages.ERROR_ADDING_EVENT_TO_CAL;

    }

    /**
     * Get a calendar by user the owns the calendar and title of the calendar
     *
     * @param user user that owns the calendar
     * @param title title of the calendar
     * @return calendar from db
     */
    @Override
    public CalendarModel getCalendar(UserModel user, String title) {

        return (CalendarModel) database.createNamedQuery(
                "findCalbyUserAndTitle").setParameter("id",
                        user.getId()).setParameter(
                        "title", title).getSingleResult();

    }

    /**
     * Get a calendar by user the owns the calendar and title of the calendar
     *
     * @param calendar calendar to get
     * @return calendar from db
     */
    @Override
    public CalendarModel getCalendar(CalendarModel calendar) {
        return (CalendarModel) database.createNamedQuery(
                "findCalbyUserAndTitle").setParameter("id",
                        calendar.getOwner()).setParameter(
                        "title", calendar.getTitle()).getSingleResult();
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
