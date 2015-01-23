package EJB;

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
import utility.EventType;

/**
 *
 * @author umboDifa
 */
public class EventManagerImplTest {

    public EventManagerImplTest() {
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
     * Test of scheduleNewEvent method, of class EventManagerImpl.
     */
    @Test
    @Ignore
    public void testScheduleNewEvent() throws Exception {
        System.out.println("scheduleNewEvent");
        Event event = null;
        CalendarModel insertInCalendar = null;
        List<UserModel> invitees = null;
        EventManagerImpl instance = new EventManagerImpl();
        boolean expResult = false;
        boolean result = instance.scheduleNewEvent(event, insertInCalendar,
                invitees);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateEvent method, of class EventManagerImpl.
     */
    @Test
    @Ignore
    public void testUpdateEvent() throws Exception {
        System.out.println("updateEvent");        
        Event event = new PublicEvent();
        event.setId(Long.valueOf(8));
        
        CalendarModel inCalendar = null;
        
        List<UserModel> invitees = null;
        EventManagerImpl instance = new EventManagerImpl();
        boolean expResult = false;
        boolean result = instance.updateEvent(event, inCalendar, invitees);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

//    /**
//     * Test of changeEventPrivacy method, of class EventManagerImpl.
//     */
//    @Test
//    @Ignore
//    public void testChangeEventPrivacy() throws Exception {
//        System.out.println("changeEventPrivacy");
//        Event event = null;
//        boolean spreadInvitations = false;
//        EventManagerImpl instance = new EventManagerImpl();
//        boolean expResult = false;
//        boolean result = instance.changeEventPrivacy(event, spreadInvitations);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of eventOnWall method, of class EventManagerImpl.
     */
    @Test
    @Ignore
    public void testEventOnWall() throws Exception {
        System.out.println("eventOnWall");
        EventType type = null;
        int n = 0;
        UserModel owner = null;
        EventManagerImpl instance = new EventManagerImpl();
        List<Event> expResult = null;
        List<Event> result = instance.eventOnWall(type, n, owner);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findEventbyId method, of class EventManagerImpl.
     */
    @Test
    @Ignore
    public void testFindEventbyId() throws Exception {
        System.out.println("findEventbyId");
        Long id = null;
        EventManagerImpl instance = new EventManagerImpl();
        Event expResult = null;
        Event result = instance.findEventbyId(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteEvent method, of class EventManagerImpl.
     */
    @Test
    @Ignore
    public void testDeleteEvent() throws Exception {
        System.out.println("deleteEvent");
        Event event = null;
        EventManagerImpl instance = new EventManagerImpl();
        boolean expResult = false;
        //boolean result = instance.deleteEvent(event);
       // assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

 
    /**
     * Test of getPublicJoin method, of class EventManagerImpl.
     */
    @Test
    @Ignore
    public void testGetPublicJoin() throws Exception {
        System.out.println("getPublicJoin");
        Event event = null;
        EventManagerImpl instance = new EventManagerImpl();
        List<UserModel> expResult = null;
        List<UserModel> result = instance.getPublicJoin(event);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addPublicJoin method, of class EventManagerImpl.
     */
    @Test
    @Ignore
    public void testAddPublicJoin() throws Exception {
        System.out.println("addPublicJoin");
        Event event = null;
        UserModel user = null;
        EventManagerImpl instance = new EventManagerImpl();
        boolean expResult = false;
        boolean result = instance.addPublicJoin(event, user);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removePublicJoin method, of class EventManagerImpl.
     */
    @Test
    @Ignore
    public void testRemovePublicJoin() throws Exception {
        System.out.println("removePublicJoin");
        Event event = null;
        UserModel user = null;
        EventManagerImpl instance = new EventManagerImpl();
        boolean expResult = false;
        boolean result = instance.removePublicJoin(event, user);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isInAnyCalendar method, of class EventManagerImpl.
     */
    @Test
    @Ignore
    public void testIsInAnyCalendar() throws Exception {
        System.out.println("isInAnyCalendar");
        Event event = null;
        UserModel user = null;
        EventManagerImpl instance = new EventManagerImpl();
        boolean expResult = false;
        boolean result = instance.isInAnyCalendar(event, user);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
