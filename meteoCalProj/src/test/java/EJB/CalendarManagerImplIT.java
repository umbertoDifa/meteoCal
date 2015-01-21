package EJB;

import EJB.interfaces.CalendarManager;
import Exceptions.ForecastDayNotFoundException;
import bakingBeans.CalendarBacking;
import java.util.List;
import javax.inject.Inject;
import model.CalendarModel;
import model.Event;
import model.UserModel;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import utility.ControlMessages;
import weatherLib.CurrentWeatherData;
import wrappingObjects.Pair;

/**
 *
 * @author umboDifa
 */
@RunWith(Arquillian.class)
public class CalendarManagerImplIT {

    @Inject
    CalendarManager calendarManager;

    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackage(CalendarManagerImpl.class.getPackage())
                .addPackage(CalendarManager.class.getPackage())
                .addPackage(ForecastDayNotFoundException.class.getPackage())
                .addPackage(CalendarBacking.class.getPackage())
                .addPackage(CalendarModel.class.getPackage())
                .addPackage(ControlMessages.class.getPackage())
                .addPackage(CurrentWeatherData.class.getPackage())
                .addPackage(Pair.class.getPackage())
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    @Ignore
    public void testNoExplode() throws Exception {
        assertNotNull(calendarManager);

    }

    /**
     * Test of checkData method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testCheckData() throws Exception {
        System.out.println("checkData");
        Event event = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        List<ControlMessages> expResult = null;
        List<ControlMessages> result = instance.checkData(event);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isInConflict method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testIsInConflict() throws Exception {
        System.out.println("isInConflict");
        Event event = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        boolean expResult = false;
        boolean result = instance.isInConflict(event);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findFreeSlots method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testFindFreeSlots() throws Exception {
        System.out.println("findFreeSlots");
        Event event = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        int expResult = 0;
        int result = instance.findFreeSlots(event);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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
     * Test of getCalendarUpdated method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testGetCalendarUpdated() throws Exception {
        System.out.println("getCalendarUpdated");
        CalendarModel calendar = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        CalendarModel expResult = null;
        CalendarModel result = instance.getCalendarUpdated(calendar);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEventsUpdated method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testGetEventsUpdated() throws Exception {
        System.out.println("getEventsUpdated");
        CalendarModel calendar = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        List<Event> expResult = null;
        List<Event> result = instance.getEventsUpdated(calendar);
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
        CalendarModel calendar = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        boolean expResult = false;
        boolean result = instance.addCalendarToUser(calendar);
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
     * Test of removeFromAllCalendars method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testRemoveFromAllCalendars() throws Exception {
        System.out.println("removeFromAllCalendars");
        UserModel user = null;
        Event event = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        instance.removeFromAllCalendars(user, event);
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

    /**
     * Test of toggleCalendarPrivacy method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testToggleCalendarPrivacy() throws Exception {
        System.out.println("toggleCalendarPrivacy");
        CalendarModel calendar = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        instance.toggleCalendarPrivacy(calendar);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isDefault method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testIsDefault() throws Exception {
        System.out.println("isDefault");
        CalendarModel calendar = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        boolean expResult = false;
        boolean result = instance.isDefault(calendar);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of makeDefault method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testMakeDefault() throws Exception {
        System.out.println("makeDefault");
        CalendarModel calendar = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        boolean expResult = false;
        boolean result = instance.makeDefault(calendar);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDefaultCalendar method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testGetDefaultCalendar() throws Exception {
        System.out.println("getDefaultCalendar");
        UserModel user = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        CalendarModel expResult = null;
        CalendarModel result = instance.getDefaultCalendar(user);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCalendarOfEvent method, of class CalendarManagerImpl.
     */
    @Test
    @Ignore
    public void testGetCalendarOfEvent() throws Exception {
        System.out.println("getCalendarOfEvent");
        Event event = null;
        UserModel user = null;
        CalendarManagerImpl instance = new CalendarManagerImpl();
        CalendarModel expResult = null;
        CalendarModel result = instance.getCalendarOfEvent(event, user);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
