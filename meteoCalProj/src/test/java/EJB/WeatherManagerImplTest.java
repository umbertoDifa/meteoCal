package EJB;

import EJB.interfaces.EventManager;
import EJB.interfaces.NotificationManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import model.Event;
import model.InvitationAnswer;
import model.NotificationType;
import model.PublicEvent;
import model.UserModel;
import model.WeatherForecast;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import utility.LoggerLevel;
import utility.LoggerProducer;

/**
 *
 * @author umboDifa
 */
public class WeatherManagerImplTest {

    private Logger logger = LoggerProducer.debugLogger(
            WeatherManagerImplTest.class);

    private WeatherManagerImpl weatherManager;
    private EventManager eventManagerMock;
    private NotificationManager notificationManagerMock;
    private EntityManager databaseMock;

    public WeatherManagerImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        eventManagerMock = mock(EventManagerImpl.class);
        notificationManagerMock = mock(NotificationManagerImpl.class);
        databaseMock = mock(EntityManager.class);
        weatherManager = new WeatherManagerImpl(databaseMock,
                notificationManagerMock, eventManagerMock);

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getWeather method, of class WeatherManagerImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    @Ignore
    public void testGetWeather() throws Exception {
        System.out.println("getWeather");

        //creo un event che inizia domani e  termina dopo 30 secondi
        Calendar startEvent = Calendar.getInstance();
        startEvent.add(Calendar.DATE, 1);
        Calendar endEvent = Calendar.getInstance();
        endEvent.add(Calendar.SECOND, 30);
        Event event = new PublicEvent("Cena di natale", startEvent, endEvent,
                "London,UK", "descrizione evento", true, null);
        event.setId(Long.MAX_VALUE);
        logger.log(LoggerLevel.DEBUG, weatherManager.getWeather(event).toString());
    }

    /**
     * Test of updateWeather method, of class WeatherManagerImpl.
     */
    @Test
    @Ignore
    public void testUpdateWeather() throws Exception {
        System.out.println("updateWeather");

        //creo un event che termina dopo 30 secondi
        Calendar startEvent = Calendar.getInstance();
        Calendar endEvent = Calendar.getInstance();
        endEvent.add(Calendar.SECOND, 30);
        Event event = new PublicEvent("Cena di natale", startEvent, endEvent,
                "Moscow", "descrizione evento", true, null);
        event.setId(Long.MAX_VALUE);
        //setto il weather
        WeatherForecast forecast = mock(WeatherForecast.class);
        event.setWeather(forecast);

        //Creo uno user a cui vengono inviate le notifiche
        UserModel me = new UserModel("umberto", "di fabrizio",
                "umberto.difabrizio@gmail.com", null);
        List<UserModel> invitee = new ArrayList<>();
        invitee.add(me);

        //quando cerca un evento nel db lo trova
        when(databaseMock.find(Event.class, Long.MAX_VALUE)).thenReturn(event);

        //quando cerca il tempo vecchio lo trova
        when(forecast.getMain()).thenReturn("Clear");
//
//        //le notifiche vanno mandate a la lista di invitati
//        when(eventManagerMock.getInviteesFiltered(event, InvitationAnswer.YES)).thenReturn(
//                invitee);

        weatherManager.updateWeather(event);

        startEvent.add(Calendar.DATE, 1);

        weatherManager.updateWeather(event);
    }
}
