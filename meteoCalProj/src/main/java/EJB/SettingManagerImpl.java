package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import EJB.interfaces.InvitationManager;
import EJB.interfaces.SettingManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
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
import model.PrivateEvent;
import model.PublicEvent;
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
import org.primefaces.model.UploadedFile;

import utility.LoggerLevel;
import utility.LoggerProducer;
import utility.PasswordTool;
import utility.TimeTool;
import wrappingObjects.Pair;

@Stateless
public class SettingManagerImpl implements SettingManager {

    @Inject
    private EventManager eventManager;

    @Inject
    private CalendarManager calendarManager;

    @Inject
    private InvitationManager invitationManager;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Inject
    private Logger logger;

    /**
     * Constructor for the test
     *
     * @param eventManager
     * @param calendarManager
     * @param database
     */
    public SettingManagerImpl(EventManager eventManager, CalendarManager calendarManager, EntityManager database) {
        this.eventManager = eventManager;
        this.calendarManager = calendarManager;
        this.database = database;
    }

    /**
     * Constructor for the conatiner
     */
    public SettingManagerImpl() {

    }

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
                    "Non è stato possibile creare il path per la cartella di export",
                    ex);
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
                "." + File.separator + "export" + File.separator
                + calendar.getOwner().getId()).mkdirs();

        if (result) {
            logger.log(LoggerLevel.DEBUG,
                    "Cartella di export creata per l''utente {0}",
                    calendar.getOwner().getId());
        }
        //creo la stringa con l'indirizzo in cui creare il file
        calFile = "." + File.separator + "export" + File.separator
                + calendar.getOwner().getId() + File.separator
                + "exportCal.ics";
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
        List<UserModel> attendees = invitationManager.getInviteesFiltered(
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
     * @param uploadedFile
     * @return La lista delle coppie di eventi non importati (nome,owner), null
     * se c'è stato un errore
     */
    @Override
    //NB works only with our exported calendars
    public List<Pair<String, String>> importCalendar(UserModel user, UploadedFile uploadedFile) {
        File importFile = copyUploadedFile(user, uploadedFile);

        //create fileInputStream to create the Ical
        FileInputStream fin;
        try {
            fin = new FileInputStream(importFile);
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
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }

    }

    private File copyUploadedFile(UserModel user, UploadedFile uploadedFile) {

        //create path to save imported file
        String fileName = "." + File.separator + "import" + File.separator
                + user.getId() + File.separator + "importedCalendar.ics";

        //create the folders
        boolean result = new File(
                "." + File.separator + "import" + File.separator
                + user.getId()).mkdirs();

        logger.log(LoggerLevel.DEBUG, "File copiato in: " + fileName);

        //create the file
        File importFile = new File(fileName);

        //create input stream from uploaded file
        InputStream in;
        try {
            in = uploadedFile.getInputstream();
            //create output stream to the fileName decided
            OutputStream out;
            out = new FileOutputStream(importFile);

            //copy input to output stream
            int read = 0;
            byte[] bytes = new byte[1024];

            try {
                while ((read = in.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                in.close();
                out.flush();
                out.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }

        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE,
                    "Non è stato possible trovare il file in cui salvare l'uploaded file "
                    + ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE,
                    "Couldn't get input stream from uploaded file. "
                    + ex.getMessage(), ex);
        }
        logger.log(LoggerLevel.DEBUG, "New file created! at: " + fileName);
        return importFile;
    }

    private List<Pair<String, String>> importIcal(FileInputStream fin, UserModel user)
            throws ParserException, IOException {
        //create a new meteocal calendar to host the imported events
        String calendarNameToSave = "ImportedCalendar"
                + Calendar.getInstance().getTime().toString();
        CalendarModel calendarForImport = new CalendarModel(calendarNameToSave,
                user,
                false, false);
        calendarManager.addCalendarToUser(calendarForImport);

        //a questo punto il calendarToImport è già persistito nel db 
        //per cui lo recupero        
        calendarForImport = (CalendarModel) database.createNamedQuery(
                "findCalbyUserAndTitle").setParameter("id",
                        user).setParameter("title", calendarNameToSave).getSingleResult();

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
                        case "SUMMARY":
                            eventName = property.getValue();
                            break;
                        case "UID":
                            eventId = property.getValue();
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

        database.flush();
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
                    logger.log(LoggerLevel.DEBUG, "Trovato evento con nome:"
                            + event.getTitle() + "e id:" + event.getId());

                    //check if the event is not in any other calendar
                    if (eventManager.isInAnyCalendar(event, user)) {
                        logger.log(LoggerLevel.DEBUG,
                                "Evento già in calendario");

                        //if so do not import and add to the list of unimported event
                        unimportedEvents.add(new Pair<>(eventName,
                                eventOwner));
                    } else {
                        if (hasPermission(user, event)) {
                            //se ha i permessi per importare
                            //se è pubblico faccio la public join se manca
                            if (event instanceof PublicEvent) {
                                if (!((PublicEvent) event).getGuests().contains(
                                        user)) {
                                    //ti aggiungo
                                    eventManager.addPublicJoin(event, user);
                                }
                            } else {
                                // se è privato metto si all'inivito
                                invitationManager.setAnswer(user, event,
                                        InvitationAnswer.YES);
                            }
                            //add the event in the calendar
                            calendarForImport.addEventInCalendar(event);
                        } else {
                            logger.log(LoggerLevel.DEBUG,
                                    "Non hai i permessi per aggiungere l'evento in calsendario");

                            //if so do not import and add to the list of unimported event
                            unimportedEvents.add(new Pair<>(eventName,
                                    eventOwner));
                        }
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

    /**
     *
     * @param user
     * @param event
     * @return true se l'evento è pubblico o se è privato ma sono l'owner o un
     * invitato, false altrimenti
     */
    private boolean hasPermission(UserModel user, Event event) {
        //se l'evento è privato e sei l owner o hai un invito
        if ((event instanceof PrivateEvent) && (event.getOwner().equals(user)
                || (event.getInvitee().contains(user)))) {
            return true;
        } else if (event instanceof PublicEvent) {
            //se l'evento è pubblico             
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean changePassword(UserModel user, String oldPassword, String newPassword) {
        if (user != null && !oldPassword.isEmpty() && !newPassword.isEmpty()) {
            user = database.find(UserModel.class, user.getId());
            if (user != null) {
                try {
                    if (PasswordTool.check(oldPassword, user.getPassword())) {
                        user.setPassword(PasswordTool.getSaltedHash(newPassword));
                        database.flush();
                        return true;
                    } else {
                        logger.log(LoggerLevel.WARNING,
                                "La vecchia password è sbagliata");
                        return false;
                    }
                } catch (Exception ex) {
                    logger.log(LoggerLevel.WARNING,
                            "Errore nel check/salting della password");
                    return false;
                }
            } else {
                logger.log(LoggerLevel.WARNING,
                        "L'utente per cambiare la pw non esiste nel db");
                return false;
            }
        } else {
            logger.log(LoggerLevel.WARNING, "L'utente per cambiare la pw è null");
            return false;
        }
    }

    @Override
    public boolean changeCredentials(UserModel user, String name, String surname, String email) {
        if (user != null) {
            user = database.find(UserModel.class, user.getId());
            if (user != null) {
                //cambio i dati se necessario
                if (!user.getEmail().equals(email)) {
                    user.setEmail(email);
                }
                if (!user.getName().equals(name)) {
                    user.setName(name);
                }
                if (!user.getSurname().equals(surname)) {
                    user.setSurname(surname);
                }

                //aggiorno il db
                database.flush();

                return true;
            } else {
                logger.log(LoggerLevel.WARNING,
                        "L'utente da modificare non esiste nel db");
                return false;
            }
        } else {
            logger.log(LoggerLevel.WARNING, "L'utente da modificare è null");
            return false;
        }
    }

}
