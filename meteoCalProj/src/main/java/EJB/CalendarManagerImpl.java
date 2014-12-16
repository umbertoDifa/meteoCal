package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import EJB.interfaces.WeatherManager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
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
import model.UserModel;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.model.property.Organizer;
import utility.ControlMessages;
import utility.LoggerLevel;
import utility.LoggerProducer;
import utility.TimeTool;

@Stateless
public class CalendarManagerImpl implements CalendarManager {

    Logger logger = LoggerProducer.debugLogger(CalendarManagerImpl.class);

    @Inject
    WeatherManager weatherManager;

    @Inject
    EventManager eventManager;

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
                        + " aggiunto al calendario " + calendar.getTitle()
                        + " di "
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
    public void exportCalendar(CalendarModel c) {
        String calFile;
        //TODO trovare un modo per creare la cartella di un utente nella cartella export
        //se questa non esiste
        //si potrebbe fare anche che ogni volta che un utenet fa il login la sua cartella
        //export viene cancellata cosi la memoria non si riempie

        calFile = ".\\export\\" + TimeTool.calendarToTextDay(
                Calendar.getInstance(),
                "yyyy-MM-dd-hh-mm-ss") + ".ics";

        logger.log(LoggerLevel.DEBUG, "Salvo calendario con nome: {0}", calFile);

        //Creating a new calendar
        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
        calendar.getProperties().add(
                new ProdId("-//MeteoCal//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        //Define boxes to host the parameters of the events that are to be saved
        java.util.Calendar startDate;
        java.util.Calendar endDate;
        VEvent event;
        Uid uid;

        //per ogni evento in quel calendario
        for (int i = 0; i < c.getEventsInCalendar().size(); i++) {

            //Get start and end time
            startDate = c.getEventsInCalendar().get(i).getStartDateTime();
            endDate = c.getEventsInCalendar().get(i).getEndDateTime();

            // Create the event
            String eventName = c.getEventsInCalendar().get(i).getTitle();
            DateTime start = new DateTime(startDate.getTime());
            DateTime end = new DateTime(endDate.getTime());

            event = new VEvent(start, end, eventName);

            //Add uid            
            uid = new Uid(String.valueOf(c.getEventsInCalendar().get(i).getId()));
            event.getProperties().add(uid);

            //Add attendees
            List<UserModel> attendees = eventManager.getInviteeFiltred(
                    c.getEventsInCalendar().get(i), InvitationAnswer.YES);
            Attendee attendee;

            for (int j = 0; j < attendees.size(); j++) {
                attendee = new Attendee(URI.create(attendees.get(j).getEmail()));
                event.getProperties().add(attendee);
            }
            //add iCal parameters
            addIcalParameters(event, c, i);

            // Add the event to the calendar 
            calendar.getComponents().add(event);
        }

        saveExportingCalendar(calFile, calendar);
    }

    /**
     * Add location and description and owner
     *
     * @param event event which is going to have parameters addes
     * @param calendar calendar whose event comes from
     * @param eventIndexInCalendar event position in the list
     */
    private void addIcalParameters(VEvent event, CalendarModel calendar, int eventIndexInCalendar) {
        event.getProperties().add(new Location(
                calendar.getEventsInCalendar().get(eventIndexInCalendar).getLocation()));
        event.getProperties().add(new Description(
                calendar.getEventsInCalendar().get(eventIndexInCalendar).getDescription()));
        event.getProperties().add(new Organizer(URI.create(
                calendar.getEventsInCalendar().get(eventIndexInCalendar).getOwner().getEmail())));
    }

    private void saveExportingCalendar(String calFile, net.fortuna.ical4j.model.Calendar calendar) {
        //Saving an iCalendar file
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(calFile);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }

        CalendarOutputter outputter = new CalendarOutputter();
        outputter.setValidating(false);
        try {
            outputter.output(calendar, fout);
        } catch (IOException | ValidationException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }

        logger.log(LoggerLevel.DEBUG, "Calendario esportato:\n{0}",
                calendar.toString());
    }

    @Override
    public void importCalendar(String calendarName) {
        //Now Parsing an iCalendar file
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(calendarName);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        CalendarBuilder builder = new CalendarBuilder();

        //create a ical calendar
        net.fortuna.ical4j.model.Calendar calendar;

        try {
            //set the calendar with the data taken from the file
            calendar = builder.build(fin);

            //Iterating over a Calendar
            for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
                Component component = (Component) i.next();
                System.out.println("Component [" + component.getName() + "]");

                for (Iterator j = component.getProperties().iterator();
                        j.hasNext();) {
                    Property property = (Property) j.next();
                    System.out.println("Property [" + property.getName() + ", "
                            + property.getValue() + "]");
                }
            }//for
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (ParserException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public CalendarModel createDefaultCalendar(UserModel user
    ) {
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
    public CalendarModel findCalendarByName(UserModel user, String name
    ) {
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
