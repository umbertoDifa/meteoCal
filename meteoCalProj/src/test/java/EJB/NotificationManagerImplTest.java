package EJB;

import java.util.ArrayList;
import java.util.List;
import model.Event;
import model.NotificationType;
import model.PublicEvent;
import model.UserModel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

/**
 *
 * @author umboDifa
 */
public class NotificationManagerImplTest {

    public NotificationManagerImplTest() {
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
     * Test of createNotifications method, of class NotificationManagerImpl.
     */
    @Ignore
    @Test
    public void testCreateNotifications() throws Exception {
        System.out.println("createNotifications");
        List<UserModel> users = new ArrayList<>();
        UserModel invitee = new UserModel("Umberto", "Di Fabrizio",
                "angelo.francesco.mobile@gmail.com", "123");
        UserModel owner = new UserModel("Fra", "Angelo",
                "fra.angelo@gmail.com", "123");

        users.add(invitee);

        Event event = new PublicEvent();
        event.setOwner(owner);
        event.setTitle("Titolo dell'evento");

        NotificationType type = NotificationType.INVITATION;

        NotificationManagerImpl instance = new NotificationManagerImpl();

        instance.createNotifications(users, event, type, true);
        
        type = NotificationType.EVENT_CHANGED;
        instance.createNotifications(users, event, type, true);
        
        type = NotificationType.EVENT_CANCELLED;
        instance.createNotifications(users, event, type, true);

    }

}
