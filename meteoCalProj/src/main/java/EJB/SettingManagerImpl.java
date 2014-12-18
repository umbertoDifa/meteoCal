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
import java.util.ArrayList;
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
import wrappingObjects.Pair;

public class SettingManagerImpl implements SettingManager {

    @Inject
    EventManager eventManager;

    @Inject
    CalendarManager calendarManager;

    @PersistenceContext(unitName = "meteoCalDB")
    EntityManager database;

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
    public List<Pair<String, String>> importCalendar(UserModel user, String calendarName) {
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
        user = (UserModel) database.find(UserModel.class, user.getId());
        logger.log(LoggerLevel.DEBUG, "Trovato utente {0}", user.getEmail());

        CalendarModel calendarForImport = calendarManager.createDefaultCalendar(
                user);

        //creo la lista di coppie che contengono gli eventi non importati
        List<Pair<String, String>> unimportedEvents = new ArrayList<>();
        //string to remember the event name
        String eventName;
        String eventOwner;
        String eventId;

        try {
            //set the calendar with the data taken from the file
            calendar = builder.build(fin);

            //Iterating over a Calendar
            for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
                Component component = (Component) i.next();

                //If the component is an event
                if (component.getName().equals("VEVENT")) {
                    logger.log(LoggerLevel.DEBUG, "Component [{0}] found in {1}",
                            new Object[]{component.getName(),
                                         calendarName});

                    //I set default names
                    eventName = "noTitleFound";
                    eventOwner = "noOrganizerFound";
                    eventId = null;

                    //I search its proprieties
                    for (Iterator j = component.getProperties().iterator();
                            j.hasNext();) {
                        Property property = (Property) j.next();

                        //I save the properies I need
                        switch (property.getName()) {
                            case "SUMMARY": eventName = property.getValue();
                                break;
                            case "UID": eventId = property.getValue();
                                break;
                            case "ORGANIZER": eventOwner = property.getValue();
                                break;

                        }
                        logger.log(LoggerLevel.DEBUG,
                                "Property [{0}] with value {1}", new Object[]{
                                    property.getName(),
                                    property.getValue()});
                    }
                    if (eventId != null) {
                        try {
                            //check if the event still exists
                            Event event = database.find(Event.class,
                                    Long.parseLong(eventId));
                            //se l'evento non esiste
                            if (event == null) {
                                logger.log(LoggerLevel.DEBUG,
                                        "NON Trovato evento");

                                //aggiungo l'evento a quelli non importati
                                unimportedEvents.add(new Pair<>(eventName,
                                        eventOwner));
                            } else {
                                logger.log(LoggerLevel.DEBUG, "Trovato evento");

                                //TODO check if the event is not in any other calendar
                                if (eventManager.isInAnyCalendar(event, user)) {
                                    logger.log(LoggerLevel.DEBUG,
                                            "Evento già in calendario");

                                    //if so do not import and add to the list of unimported event
                                    unimportedEvents.add(new Pair<>(eventName,
                                            eventOwner));
                                } else {
                                    //add the event in the calendar
                                    calendarForImport.addEventInCalendar(event);
                                }

                            }
                        } catch (NumberFormatException ex) {
                            logger.log(Level.WARNING,
                                    "The eventUID is not a number, the event is not imported");
                            unimportedEvents.add(new Pair<>(eventName,
                                    eventOwner));
                        }
                    }
                }

            }//for
            //persisti il nuovo calendario con gli eventi imoprtati nel db
            database.persist(calendarForImport);

            logger.log(LoggerLevel.DEBUG,
                    "Calendario importato per l''utente {0}",
                    user.getEmail());
            logger.log(LoggerLevel.DEBUG,
                    "Calendario importato:\n {0}",
                    calendarForImport.toString());
            logger.log(LoggerLevel.DEBUG, "Eventi non importati:\n{0}",
                    unimportedEvents.toString());

            return unimportedEvents;

        } catch (IOException | ParserException ex) {
            logger.log(Level.SEVERE, null, ex);
            return null; //TODO?
        }

    }

}
