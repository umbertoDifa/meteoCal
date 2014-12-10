package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.WeatherManager;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import model.CalendarId;
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

    private void findFreeSlots() {
        //TODO do
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

        calendar = (CalendarModel) database.createNamedQuery(
                "findCalbyUserAndTitle").setParameter("id",
                        calendar.getOwner()).setParameter(
                        "title", calendar.getTitle()).getSingleResult();

        event = database.find(Event.class, event.getId());

        //database.refresh(calendar);
        //database.refresh(event);
        for (CalendarModel cal : event.getOwner().getOwnedCalendars()) {
            for (Event e : cal.getEventsInCalendar()) {
                if (e.equals(event)) {
                    calendar.getEventsInCalendar().remove(e);
                    //TODO: check this remove
                }
            }
        }
        if (calendar.addEventInCalendar(event)) {
            //calendar.getEventsInCalendar().add(event);
            logger.log(Level.INFO, "Evento " + event.getTitle()
                    + " aggiunto al calendario " + calendar.getTitle() + " di "
                    + calendar.getOwner().getEmail());

            logger.log(LoggerLevel.DEBUG, "Events in calendar now: {0}",
                    calendar.getEventsInCalendar());

            return ControlMessages.EVENT_ADDED;
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

}
