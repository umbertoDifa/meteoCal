package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.WeatherManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.CalendarModel;
import model.Event;
import model.UserModel;
import utility.ControlMessages;
import utility.LoggerLevel;

@Stateless
public class CalendarManagerImpl implements CalendarManager {

    @Inject
    @Default
    Logger logger;

    @Inject
    WeatherManager weatherManager;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    private void checkWeather() {
        //TODO do             
    }

    private void checkConflicts() {
        //TODO do
    }

    private void findFreeSlots(UserModel user, Event event) {
        int searchRange = 15;
        //TODO do
        for (CalendarModel calendar : user.getOwnedCalendars()) {
            // cerca un giorno in cui non ci sia un evento programmato per la stessa ora dell'evento schedulato.
        }

    }

    @Override
    public List<CalendarModel> getCalendars(UserModel user) {
        user = database.find(UserModel.class, user.getId());
        database.refresh(user);
        return user.getOwnedCalendars();
    }

    @Override
    //TODO qui user non serve perchè deduco l'id dal calendar
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
    public boolean checkData() {
        this.checkWeather();
        this.checkConflicts();
        return true;//TODO do
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
        event = database.find(Event.class, event.getId());
        for (CalendarModel cal : event.getOwner().getOwnedCalendars()) {
            cal.getEventsInCalendar().remove(event);
        }
        if (calendar != null) {
            calendar = (CalendarModel) database.createNamedQuery(
                    "findCalbyUserAndTitle").setParameter("id",
                            calendar.getOwner()).setParameter(
                            "title", calendar.getTitle()).getSingleResult();

            if (calendar.addEventInCalendar(event)) {
                //calendar.getEventsInCalendar().add(event);
                logger.log(Level.INFO, "Evento " + event.getTitle()
                        + " aggiunto al calendario " + calendar.getTitle() + " di "
                        + calendar.getOwner().getEmail());

                logger.log(LoggerLevel.DEBUG, "Events in calendar now: {0}",
                        calendar.getEventsInCalendar());

                return ControlMessages.EVENT_ADDED;
            }
        }
        logger.log(Level.WARNING, "Evento non aggiunto al calendario");
        return ControlMessages.ERROR_ADDING_EVENT_TO_CAL;

    }

    @Override
    public void exportCalendar(CalendarModel calendar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void importCalendar(CalendarModel calendar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CalendarModel createDefaultCalendar(UserModel user) {
        CalendarModel calendar = new CalendarModel();
        calendar.setIsDefault(true);
        calendar.setIsPublic(false);
        calendar.setOwner(user);
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
    public List<String> getCalendarTitles(UserModel user) {
        List<String> names = new ArrayList<>();
        for (CalendarModel cal : this.getCalendars(user)) {
            names.add(cal.getTitle());
        }
        return names;

    }

}
