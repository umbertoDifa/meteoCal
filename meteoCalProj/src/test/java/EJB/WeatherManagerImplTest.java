package EJB;

import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import utility.LoggerProducer;
import weatherLib.OpenWeatherMap;

/**
 *
 * @author umboDifa
 */
public class WeatherManagerImplTest {

    static final Logger logger = LoggerProducer.debugLogger(
            WeatherManagerImplTest.class);

    private WeatherManagerImpl weatherManager;

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
        weatherManager = new WeatherManagerImpl();
        
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getWeather method, of class WeatherManagerImpl.
     *
     * @throws java.lang.Exception
     */
    @Ignore
    @Test
    public void testGetWeather() throws Exception {
        System.out.println("getWeather");
//        WeatherManagerImpl wm = new WeatherManagerImpl();
//        wm.initOpenWeatherMap();
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, 1);
//        WeatherForecast forecast = wm.getWeather(cal, "Rome");
//        assertTrue(forecast.getMessage() == WeatherMessages.BAD_WEATHER);
//        assertTrue("Rain".equals(forecast.getMain()));
//        System.out.println("pressione: " + forecast.getPressure());
//        System.out.println("temperatura: " + forecast.getTemp());
//        assertTrue((float)990.73 == forecast.getPressure());
//        assertTrue((float)0.55 == forecast.getTemp());
        //USATO PER FARE TESTING, I VALORI VANNO CODATI AL MOMENTO
    }
}
