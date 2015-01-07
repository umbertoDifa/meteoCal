package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.EventManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import model.CalendarModel;
import model.Event;
import model.InvitationAnswer;
import model.PublicEvent;
import model.UserModel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author umboDifa
 */
public class SettingManagerImplTest {

    private SettingManagerImpl settingManager;
    private EntityManager database;
    private CalendarManager calendarManager;
    EventManager eventManager;
    private Calendar startEvent;
    private Calendar endEvent;
    private Event event;
    private UserModel owner;

    public SettingManagerImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        eventManager = mock(EventManagerImpl.class);
        database = mock(EntityManager.class);
        calendarManager = mock(CalendarManagerImpl.class);
        settingManager = new SettingManagerImpl(eventManager, calendarManager,
                database);

        owner = new UserModel("nomeDellOwner", "CognomeOwner",
                "email@owner", "passwordOwner");
        owner.setId(Long.MAX_VALUE / 2);

        startEvent = Calendar.getInstance();
        endEvent = Calendar.getInstance();
        endEvent.add(Calendar.DATE, 1);
        event = new PublicEvent("Titolo Evento", startEvent, endEvent,
                "luogo Evento", "descrizione evento", true, owner);

        event.setId(Long.MAX_VALUE);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of exportCalendar method, of class SettingManagerImpl.
     */
    @Test
    public void testExportCalendar() {
        System.out.println("exportCalendar");

        List<Event> eventList = new ArrayList<>();
        eventList.add(event);

        CalendarModel cal = mock(CalendarModel.class);

        when(cal.getTitle()).thenReturn("Titolo del calendario");
        when(cal.getOwner()).thenReturn(owner);
        when(cal.getEventsInCalendar()).thenReturn(eventList);

        //creo la lista di attendees
        List<UserModel> attendees = new ArrayList<>();
        UserModel attendee = new UserModel("Nome Primo invitato",
                "Congnome primo invitato", "in@me.com", "passwordInvitato");
        attendees.add(attendee);
        attendees.add(attendee);

        //comuqnue vada quando chiamo il metodo getInviteeFiltered ritorna la lista di atendees costruita qui
        when(eventManager.getInviteesFiltered(Matchers.any(
                Event.class), Matchers.any(InvitationAnswer.class))).thenReturn(
                        attendees);

        settingManager.exportCalendar(cal);

        //verifico che quel metodo venga chiamato una sola volta
        verify(eventManager, times(1)).getInviteesFiltered(
                Matchers.any(Event.class), Matchers.any(InvitationAnswer.class));

    }

    /**
     * Test of importCalendar method, of class SettingManagerImpl.
     */
    @Test
    public void testImportCalendar() {
        System.out.println("importCalendar");
        String calendarName = "basic.ics";

        UserModel userImporting = new UserModel("nome utente", "cognome utente",
                "email@tente", "passwordUtente");
        userImporting.setId(Long.MAX_VALUE);

        //creo evento di prova
        startEvent = Calendar.getInstance();
        endEvent = Calendar.getInstance();
        endEvent.add(Calendar.DATE, 1);
        event = new PublicEvent("Titolo Evento", startEvent, endEvent,
                "luogo Evento", "descrizione evento", true, owner);

        event.setId(Long.MAX_VALUE);

        when(database.find(UserModel.class, Long.MAX_VALUE)).thenReturn(
                userImporting);

        when(calendarManager.createDefaultCalendar(Matchers.any(
                UserModel.class))).thenReturn(new CalendarModel(
                                "Default calendar",
                                owner, true, true));

        when(database.find(Event.class, Long.MAX_VALUE)).thenReturn(
                event);
//
//        when(settingManager.eventManager.isInAnyCalendar(Matchers.any(
//                Event.class), Matchers.any(UserModel.class))).thenReturn(
//                        Boolean.FALSE);

        settingManager.importCalendar(userImporting, calendarName);

    }

    /**
     * Test of deleteExportFolder method, of class SettingManagerImpl.
     */
    @Test
    public void testDeleteExportFolder() throws Exception {
        System.out.println("deleteExportFolder");
        settingManager.deleteExportFolder(owner);

    }

}
