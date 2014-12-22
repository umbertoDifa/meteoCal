package EJB;

import EJB.interfaces.UpdateManager;
import java.util.Calendar;
import javax.persistence.EntityManager;
import model.Event;
import model.PublicEvent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 *
 * @author umboDifa
 */
public class UpdateManagerImplTest {

    UpdateManagerImpl instance;

    public UpdateManagerImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new UpdateManagerImpl();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of scheduleUpdates method, of class UpdateManagerImpl.
     */
    @Test
    public void testScheduleUpdates() {
        System.out.println("scheduleUpdates");

        //creo un event
        Calendar startEvent = Calendar.getInstance();
        Calendar endEvent = Calendar.getInstance();
        endEvent.add(Calendar.DATE, 1);
        Event event = new PublicEvent("Titolo Evento", startEvent, endEvent,
                "luogo Evento", "descrizione evento", true, null);
        event.setId(Long.MAX_VALUE);

        //instance.database = mock(EntityManager.class);
        //when(instance.database)
        instance.scheduleUpdates(event);

    }

}
