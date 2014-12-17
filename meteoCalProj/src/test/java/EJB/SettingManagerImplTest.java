package EJB;

import EJB.interfaces.SettingManager;
import java.util.Calendar;
import model.CalendarModel;
import model.Event;
import model.PublicEvent;
import model.UserModel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author umboDifa
 */
public class SettingManagerImplTest {

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
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of exportCalendar method, of class SettingManagerImpl.
     */
    @Test
    @Ignore
    public void testExportCalendar() {
        System.out.println("exportCalendar");

        UserModel owner = new UserModel("nomeDellOwner", "CognomeOwner",
                "email@owner", "passwordOwner");
        owner.setId(Long.MAX_VALUE / 2);

        CalendarModel calendar = new CalendarModel("Titolo del calendario",
                owner, true, false);

        Calendar startEvent = Calendar.getInstance();
        Calendar endEvent = Calendar.getInstance();
        endEvent.add(Calendar.DATE, 1);

        Event event = new PublicEvent("Titolo Evento", startEvent, endEvent,
                "luogo Evento", "descrizione evento", true, owner);

        event.setId(Long.MAX_VALUE);

        calendar.init();
        calendar.addEventInCalendar(event);

        SettingManager instance = new SettingManagerImpl();
        instance.exportCalendar(calendar);

    }

    /**
     * Test of importCalendar method, of class SettingManagerImpl.
     */
    @Test
    @Ignore
    public void testImportCalendar() {
        System.out.println("importCalendar");
        String calendarName = "basic.ics";
        SettingManagerImpl instance = new SettingManagerImpl();

    }

}
