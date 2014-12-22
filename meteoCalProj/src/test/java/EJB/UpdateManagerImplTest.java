package EJB;

import EJB.interfaces.UpdateManager;
import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;
import javax.persistence.EntityManager;
import model.Event;
import model.PublicEvent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author umboDifa
 */
public class UpdateManagerImplTest {

    UpdateManagerImpl instance;
    EntityManager database;

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
        database = mock(EntityManager.class);
        instance = new UpdateManagerImpl(database);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of scheduleUpdates method, of class UpdateManagerImpl.
     */
    @Test
    @Ignore //il test è ignorato perchè l'esecuzione è molto lenta
    public void testScheduleUpdates() {
        System.out.println("scheduleUpdates");

        //creo un event che termina dopo 30 secondi
        Calendar startEvent = Calendar.getInstance();
        Calendar endEvent = Calendar.getInstance();
        endEvent.add(Calendar.SECOND, 30);

        Event event = new PublicEvent("Cena di natale", startEvent, endEvent,
                "casa nonna", "descrizione evento", true, null);
        event.setId(Long.MAX_VALUE);

        //creo un altro event che termina dopo  60 secondi
        Calendar startEvent2 = Calendar.getInstance();
        Calendar endEvent2 = Calendar.getInstance();
        endEvent2.add(Calendar.SECOND, 60);

        Event event2 = new PublicEvent("Cena di pasqua", startEvent2, endEvent2,
                "casa Lucia", "descrizione evento", true, null);
        event2.setId(Long.MIN_VALUE);

        //quando il db cerca l'evento lo trova
        when(database.find(Event.class, Long.MAX_VALUE)).thenReturn(event);
        when(database.find(Event.class, Long.MIN_VALUE)).thenReturn(event2);

        ScheduledFuture<?> result = instance.scheduleUpdates(event);
        ScheduledFuture<?> result2 = instance.scheduleUpdates(event2);

        
        //finche entrambi gli eventi non terminano
        while (!result.isCancelled() || ! result2.isCancelled()) {            
        };

        assertTrue("Schedule completata", result.isCancelled());

    }

}
