package EJB;

import java.util.Calendar;
import java.util.List;
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
import utility.ControlMessages;

/**
 *
 * @author umboDifa
 */
public class CalendarManagerImplTest {

    public CalendarManagerImplTest() {
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
     * Test of getCalendars method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testGetCalendars() throws Exception {
        System.out.println("getCalendars");
        UserModel user = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        List<CalendarModel> expResult = null;
        List<CalendarModel> result = instance.getCalendars(user);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addCalendarToUser method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testAddCalendarToUser() throws Exception {
        System.out.println("addCalendarToUser");
        UserModel user = null;
        CalendarModel cal = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        boolean expResult = false;
        boolean result = instance.addCalendarToUser(user, cal);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkData method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testCheckData() throws Exception {
        System.out.println("checkData");
        CalendarManagerImpl instance = new CalendarManagerImpl();
        boolean expResult = false;
        boolean result = instance.checkData();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findFreeDay method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testFindFreeDay() throws Exception {
        System.out.println("findFreeDay");
        Calendar fromBusyDay = null;
        int weeksAhead = 0;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        Calendar expResult = null;
        Calendar result = instance.findFreeDay(fromBusyDay, weeksAhead);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToCalendar method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testAddToCalendar() throws Exception {
        System.out.println("addToCalendar");
        Event event = null;
        CalendarModel calendar = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        ControlMessages expResult = null;
        ControlMessages result = instance.addToCalendar(event, calendar);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createDefaultCalendar method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testCreateDefaultCalendar() throws Exception {
        System.out.println("createDefaultCalendar");
        UserModel user = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        CalendarModel expResult = null;
        CalendarModel result = instance.createDefaultCalendar(user);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findCalendarByName method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testFindCalendarByName() throws Exception {
        System.out.println("findCalendarByName");
        UserModel user = null;
        String name = "";
        CalendarManagerImpl instance = new CalendarManagerImpl();
        CalendarModel expResult = null;
        CalendarModel result = instance.findCalendarByName(user, name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCalendarTitles method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testGetCalendarTitles() throws Exception {
        System.out.println("getCalendarTitles");
        UserModel user = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        List<String> expResult = null;
        List<String> result = instance.getCalendarTitles(user);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
