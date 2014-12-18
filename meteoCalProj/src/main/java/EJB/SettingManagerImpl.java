package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import EJB.interfaces.SettingManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.CalendarModel;
import model.Event;
import model.InvitationAnswer;
import model.UserModel;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;

import utility.LoggerLevel;
import utility.LoggerProducer;
import utility.TimeTool;

public class SettingManagerImpl implements SettingManager {

    @Inject
    EventManager eventManager;

    @Inject
    CalendarManager calendarManager;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    Logger logger = LoggerProducer.debugLogger(SettingManagerImpl.class);

    @Override
    public void exportCalendar(CalendarModel c) {
        String calFile;
        //TODO si potrebbe fare anche che ogni volta che un utenet fa il login la sua cartella
        //export viene cancellata cosi la memoria non si riempie

        //creo il percorso della cartella
        try {
            boolean result = new File(".\\export\\" + c.getOwner().getId()).mkdirs();
            if (result) {
                logger.log(LoggerLevel.DEBUG,
                        "Cartella di export creata per l''utente {0}",
                        c.getOwner().getId());
            } else {
                logger.log(LoggerLevel.DEBUG,
                        "Cartella di export NON creata per l''utente {0}, probabilmente già esiste",
                        c.getOwner().getId());
            }

        } catch (SecurityException ex) {
            logger.log(Level.SEVERE,
                    ex.getMessage()
                    + c.getOwner().getId(), ex);
            //TODO return errore
        }

        //creo la stringa con l'indirizzo in cui creare il file
        calFile = ".\\export\\" + c.getOwner().getId() + "\\"
                + TimeTool.calendarToTextDay(
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
            logger.log(LoggerLevel.DEBUG, "Aggiunto evento " + eventName);
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

    /**
     * Save a calendar in the correct user folder export/userId
     *
     * @param calFile name of the calendar to save
     * @param calendar Calendar to save
     */
    private void saveExportingCalendar(String calFile, net.fortuna.ical4j.model.Calendar calendar) {
        //Saving an iCalendar file
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(calFile);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            //TODO errore magari da non catchare qui
        }

        CalendarOutputter outputter = new CalendarOutputter();
        outputter.setValidating(false);
        try {
            outputter.output(calendar, fout);
        } catch (IOException | ValidationException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            //TODO errore magari da non catchare qui

        }
        logger.log(LoggerLevel.DEBUG, "Calendario esportato con successo:\n{0}",
                calendar.toString());
    }

    @Override
    public void importCalendar(UserModel user, String calendarName) {
        //TODO works only with our exported calendars
        //Now Parsing an iCalendar file
        calendarName = ".\\import\\" + calendarName;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(calendarName);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            //TODO return error
        }

        CalendarBuilder builder = new CalendarBuilder();

        //create a ical calendar
        net.fortuna.ical4j.model.Calendar calendar;

        //TODO tutto questo codice non fa nessuna catch di eventuali eccezioni
        
        //create a new meteocal calendar to host the imported events
        //questa find serve solo se voglio essere sicuro che l'utente che mi è stato passato esista
        user = database.find(UserModel.class, user.getId());
        CalendarModel calendarForImport = calendarManager.createDefaultCalendar(
                user);
        
        //creo le liste che contengono gli eventi

        try {
            //set the calendar with the data taken from the file
            calendar = builder.build(fin);

            //Iterating over a Calendar
            for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
                Component component = (Component) i.next();
                //If the component is an event
                if (component.getName().equals("VEVENT")) {
                    logger.log(LoggerLevel.DEBUG, "Component ["
                            + component.getName() + "] found in " + calendarName);

                    //I search its proprieties
                    for (Iterator j = component.getProperties().iterator();
                            j.hasNext();) {
                        Property property = (Property) j.next();
                        //if I find the UID
                        if (property.getName().equals("UID")) {
                            logger.log(LoggerLevel.DEBUG, "Property ["
                                    + property.getName() + ", "
                                    + property.getValue() + "] found.");
                            
                            //check if the event still exists
                            Event event = database.find(Event.class,
                                    property.getValue());
                            if (event == null) {
                                //TODO gestisci lista di eventi non reimportati
                            } else {
                                //check if the event is not in any other calendar
                                //if so do not import and add to the list of unimported event
                                //otherwise
                                //add the event in the calendar
                                calendarForImport.addEventInCalendar(event);
                            }
                        }
                    }
                }

            }//for
            //persisti il nuovo calendario con gli eventi imoprtati nel db
            database.persist(calendarForImport);

            logger.log(LoggerLevel.DEBUG,
                    "Calendario importato per l''utente {0}", user.getEmail());
            logger.log(LoggerLevel.DEBUG, "Calendario importato:\n {0}",
                    calendarForImport.toString());
            
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (ParserException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

}
