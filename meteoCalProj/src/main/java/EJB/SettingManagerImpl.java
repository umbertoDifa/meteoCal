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
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
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
import org.apache.commons.io.FileUtils;

import utility.LoggerLevel;
import utility.LoggerProducer;
import utility.TimeTool;
import wrappingObjects.Pair;

@Stateless
public class SettingManagerImpl implements SettingManager {

    @Inject
    EventManager eventManager;

    @Inject
    CalendarManager calendarManager;

    @PersistenceContext(unitName = "meteoCalDB")
    EntityManager database;

    private final String COMMON_PATH = "." + File.separator + "src"
            + File.separator + "main" + File.separator
            + "webapp" + File.separator + "resources" + File.separator + "ics"
            + File.separator;

    Logger logger = LoggerProducer.debugLogger(SettingManagerImpl.class);

    /**
     * Esporta il calendario passato creando un file .ics nella cartella
     * personale dell'utente
     *
     * @param calendar Calendario da esportare
     * @return true se tutto ok, false altrimenti
     */
    @Override
    public boolean exportCalendar(CalendarModel calendar) {
        String calFile;

        try {
            calFile = createExportPath(calendar);
            logger.log(LoggerLevel.DEBUG, "Salvo calendario al path: {0}",
                    calFile);

        } catch (SecurityException ex) {
            logger.log(Level.SEVERE,
                    "Non è stato possibile creare il path per la cartella di export");
            return false;
        }

        net.fortuna.ical4j.model.Calendar iCal = createIcal();

        addIcalEvents(calendar, iCal);

        try {
            saveExportingCalendar(calFile, iCal);
        } catch (IOException | ValidationException ex) {
            logger.log(Level.SEVERE,
                    "Non è stato possibile salvare il calendario.\n"
                    + ex.getMessage(), ex);
            return false;
        }

        logger.log(LoggerLevel.DEBUG, "Calendario esportato con successo:\n{0}",
                iCal.toString());
        return true;
    }

    private String createExportPath(CalendarModel calendar) {
        String calFile;
        //creo il percorso della cartella        

        boolean result = new File(
                COMMON_PATH + "export" + File.separator
                + calendar.getOwner().getId()).mkdirs();

        if (result) {
            logger.log(LoggerLevel.DEBUG,
                    "Cartella di export creata per l''utente {0}",
                    calendar.getOwner().getId());
        }
        //creo la stringa con l'indirizzo in cui creare il file
        calFile = COMMON_PATH + "export" + File.separator
                + calendar.getOwner().getId() + File.separator
                + TimeTool.dateToTextDay(Calendar.getInstance().getTime(),
                        "yyyy-MM-dd-hh-mm-ss") + ".ics";
        return calFile;
    }

    private net.fortuna.ical4j.model.Calendar createIcal() {
        //Creating a new calendar
        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
        calendar.getProperties().add(
                new ProdId("-//MeteoCal//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        return calendar;
    }

    private void addIcalEvents(CalendarModel calendar, net.fortuna.ical4j.model.Calendar iCal) {
        //Define boxes to host the parameters of the events that are to be saved
        Calendar startDate;
        Calendar endDate;
        VEvent event;
        Uid uid;

        //per ogni evento in quel calendario
        for (int i = 0; i < calendar.getEventsInCalendar().size(); i++) {

            //Get start and end time
            startDate = calendar.getEventsInCalendar().get(i).getStartDateTime();
            endDate = calendar.getEventsInCalendar().get(i).getEndDateTime();

            // Create the event
            String eventName = calendar.getEventsInCalendar().get(i).getTitle();
            DateTime start = new DateTime(startDate.getTime());
            DateTime end = new DateTime(endDate.getTime());

            event = new VEvent(start, end, eventName);

            //Add uid            
            uid = new Uid(String.valueOf(
                    calendar.getEventsInCalendar().get(i).getId()));
            event.getProperties().add(uid);

            //Add attendees           
            addIcalAttendees(calendar, i, event);

            //add iCal parameters
            addIcalParameters(event, calendar, i);

            // Add the event to the calendar 
            iCal.getComponents().add(event);
            logger.log(LoggerLevel.DEBUG, "Aggiunto evento {0}", eventName);
        }
    }

    private void addIcalAttendees(CalendarModel calendar, int eventPosition, VEvent event) {
        List<UserModel> attendees = eventManager.getInviteeFiltred(
                calendar.getEventsInCalendar().get(eventPosition),
                InvitationAnswer.YES);

        if (attendees != null) {
            Attendee attendee;

            for (UserModel u : attendees) {
                attendee = new Attendee(URI.create(u.getEmail()));
                event.getProperties().add(attendee);
            }
        } else {
            logger.log(LoggerLevel.WARNING,
                    "La lista degli invitati all'evento è nulla");
        }
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
    private void saveExportingCalendar(String calFile, net.fortuna.ical4j.model.Calendar calendar)
            throws FileNotFoundException, IOException, ValidationException {
        //Saving an iCalendar file
        FileOutputStream fout = null;

        fout = new FileOutputStream(calFile);

        CalendarOutputter outputter = new CalendarOutputter();
        outputter.setValidating(false);

        outputter.output(calendar, fout);

    }

    /**
     * Importa un calendario Costruito con MeteoCal, creando un calendario con
     * gli eventi importati per lo user
     *
     * @param user User che sta importando
     * @param calendarName Nome del file .ics da importare
     * @return La lista delle coppie di eventi non importati (nome,owner), null
     * se c'è stato un errore
     */
    @Override
    public List<Pair<String, String>> importCalendar(UserModel user, String calendarName) {
        //NB works only with our exported calendars

        //creating and checking input file
        calendarName = COMMON_PATH + "import" + File.separator + calendarName;
        FileInputStream fin;
        try {
            fin = new FileInputStream(calendarName);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }

        user = (UserModel) database.find(UserModel.class, user.getId());
        if (user != null) {
            try {
                return importIcal(fin, user);
            } catch (IOException | ParserException ex) {
                logger.log(Level.SEVERE,
                        "Non è stato possible creare il calendario, problemi con il builder. "
                        + ex.getMessage(), ex);
                return null;
            }
        } else {
            logger.log(Level.SEVERE, "L'utente passato non esiste nel db");
            return null;
        }

    }

    private List<Pair<String, String>> importIcal(FileInputStream fin, UserModel user)
            throws ParserException, IOException {
        //create a new meteocal calendar to host the imported events
        CalendarModel calendarForImport = calendarManager.createDefaultCalendar(
                user);

        CalendarBuilder builder = new CalendarBuilder();

        net.fortuna.ical4j.model.Calendar calendar;

        //string to remember the event name
        String eventName;
        String eventOwner;
        String eventId;
        //set the calendar with the data taken from the file
        calendar = builder.build(fin);

        //creo la lista di coppie che contengono gli eventi non importati
        List<Pair<String, String>> unimportedEvents = new ArrayList<>();

        //Iterating over a Calendar
        for (Iterator i = calendar.getComponents().iterator();
                i.hasNext();) {
            Component component = (Component) i.next();

            //If the component is an event
            if (component.getName().equals("VEVENT")) {

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
                        case "ORGANIZER":
                            eventOwner = property.getValue();
                            break;

                    }

                }
                addToCalendar(eventId, unimportedEvents, eventName,
                        eventOwner, user, calendarForImport);
            }

        }//for

        //TODO vedere se questa persist funziona davvero, in particolare
        //il campo inCalendars
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
    }

    private void addToCalendar(String eventId, List<Pair<String, String>> unimportedEvents, String eventName, String eventOwner, UserModel user, CalendarModel calendarForImport) {
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

    @Override
    public void deleteExportFolder(UserModel user) {

        //creo il path
        String path = COMMON_PATH + "export" + File.separator
                + user.getId();

        //creo il file
        File exportDir = new File(path);

        //checko se esiste
        if (exportDir.exists()) {
            try {
                //elimino
                FileUtils.deleteDirectory(exportDir);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

}
