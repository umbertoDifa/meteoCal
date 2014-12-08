package EJB;

import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.inject.Inject;
import objectAndString.WeatherForecast;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import utility.ForecastType;
import utility.LoggerLevel;
import utility.LoggerProducer;

/**
 *
 * @author umboDifa
 */
public class WeatherManagerImplTest {

    WeatherManagerImpl weatherManagerImpl;
    static final Logger logger = LoggerProducer.debugLogger(WeatherManagerImplTest.class);

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
        weatherManagerImpl = new WeatherManagerImpl();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getWeather method, of class WeatherManagerImpl.
     */
    @Test
    @Ignore
    public void testGetWeather() throws Exception {
        System.out.println("getWeather");
        Calendar day = null;
        String city = "";
        WeatherManagerImpl instance = new WeatherManagerImpl();
        WeatherForecast expResult = null;
        WeatherForecast result = instance.getWeather(day, city);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void prova() {
        Calendar dayToCheck = Calendar.getInstance();
        ForecastType type = weatherManagerImpl.inferForecastType(dayToCheck);

        logger.log(Level.INFO, "INFO:type : {0}", type);
        assertTrue(type == ForecastType.CURRENT_WEATHER);

        //aggiungo un giorno
        dayToCheck.add(Calendar.DATE, 1);
        type = weatherManagerImpl.inferForecastType(dayToCheck);
        logger.log(Level.INFO, "type : {0}", type);
        assertTrue(type == ForecastType.FORECAST_5_3HOURS);

        //aggiungo 4 giorni
        dayToCheck.add(Calendar.DATE, 4);
        type = weatherManagerImpl.inferForecastType(dayToCheck);
        logger.log(Level.INFO, "type : {0}", type);
        assertTrue(type == ForecastType.FORECAST_5_3HOURS);

        //aggiungo 3 giorni
        dayToCheck.add(Calendar.DATE, 3);
        type = weatherManagerImpl.inferForecastType(dayToCheck);
        logger.log(Level.INFO, "type : {0}", type);
        assertTrue(type == ForecastType.FORECAST_16_DAILY);

        //aggiungo 15 giorni
        dayToCheck.add(Calendar.DATE, 15);
        type = weatherManagerImpl.inferForecastType(dayToCheck);
        logger.log(Level.INFO, "type : {0}", type);
        assertTrue(type == ForecastType.UNPREDICTABLE);

    }

}
