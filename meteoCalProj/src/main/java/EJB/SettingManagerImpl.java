package EJB;

import EJB.interfaces.EventManager;
import EJB.interfaces.SettingManager;
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
import model.CalendarModel;
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
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import utility.LoggerLevel;
import utility.LoggerProducer;
import utility.TimeTool;

public class SettingManagerImpl implements SettingManager {

    @Inject
    EventManager eventManager;

    Logger logger = LoggerProducer.debugLogger(SettingManagerImpl.class);

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

}
