package EJB;

import EJB.interfaces.EventManager;
import EJB.interfaces.SettingManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
import static org.junit.Assert.*;
import org.junit.Ignore;
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
        settingManager = new SettingManagerImpl();
        settingManager.eventManager = mock(EventManager.class);
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

        UserModel owner = new UserModel("nomeDellOwner", "CognomeOwner",
                "email@owner", "passwordOwner");
        owner.setId(Long.MAX_VALUE / 2);

        Calendar startEvent = Calendar.getInstance();
        Calendar endEvent = Calendar.getInstance();
        endEvent.add(Calendar.DATE, 1);
        Event event = new PublicEvent("Titolo Evento", startEvent, endEvent,
                "luogo Evento", "descrizione evento", true, owner);

        event.setId(Long.MAX_VALUE);

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
        when(settingManager.eventManager.getInviteeFiltred(Matchers.any(
                Event.class), Matchers.any(InvitationAnswer.class))).thenReturn(
                        attendees);

        settingManager.exportCalendar(cal);

        //verifico che quel metodo venga chiamato una sola volta
        verify(settingManager.eventManager, times(1)).getInviteeFiltred(
                Matchers.any(Event.class), Matchers.any(InvitationAnswer.class));

    }

    /**
     * Test of importCalendar method, of class SettingManagerImpl.
     */
    @Test
    @Ignore
    public void testImportCalendar() {
        System.out.println("importCalendar");
        String calendarName = "basic.ics";

        UserModel userImporting = new UserModel("nome utente", "cognome utente",
                "email@tente", "passwordUtente");

        settingManager.importCalendar(userImporting, calendarName);

    }

}
