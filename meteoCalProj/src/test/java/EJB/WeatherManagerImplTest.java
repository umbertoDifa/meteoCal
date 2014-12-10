package EJB;

import java.util.Calendar;
import java.util.logging.Logger;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import objectAndString.WeatherForecast;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import utility.LoggerLevel;
import utility.LoggerProducer;
import utility.WeatherMessages;

/**
 *
 * @author umboDifa
 */
public class WeatherManagerImplTest {

    static final Logger logger = LoggerProducer.debugLogger(
            WeatherManagerImplTest.class);

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
    @Ignore
    @Test
    public void testManual() {
        Logger logger = LoggerProducer.debugLogger(WeatherManagerImplTest.class);
        WeatherManagerImpl wm = new WeatherManagerImpl();
        wm.initOpenWeatherMap();

        //prova con oggi        
        Calendar cal = Calendar.getInstance();
        logger.log(LoggerLevel.DEBUG, "Tempo del: {0}", cal.getTime().
                toString());
        WeatherForecast forecast = wm.getWeather(cal, "Rome");
        logger.log(LoggerLevel.DEBUG, forecast.toString());

        wm = new WeatherManagerImpl();
        wm.initOpenWeatherMap();
        //prova con domani
        cal.add(Calendar.DATE, 1);
        logger.log(LoggerLevel.DEBUG, "Tempo del: {0}", cal.getTime().
                toString());
        forecast = wm.getWeather(cal, "Rome");
        logger.log(LoggerLevel.DEBUG, forecast.toString());

        wm = new WeatherManagerImpl();
        wm.initOpenWeatherMap();
        //prova fra 2 giorni
        cal.add(Calendar.DATE, 1);
        logger.log(LoggerLevel.DEBUG, "Tempo del: {0}", cal.getTime().
                toString());
        forecast = wm.getWeather(cal, "Rome");
        logger.log(LoggerLevel.DEBUG, forecast.toString());

        wm = new WeatherManagerImpl();
        wm.initOpenWeatherMap();
        //prova fra 4 giorni
        cal.add(Calendar.DATE, 2);
        logger.log(LoggerLevel.DEBUG, "Tempo del: {0}", cal.getTime().
                toString());
        forecast = wm.getWeather(cal, "Rome");
        logger.log(LoggerLevel.DEBUG, forecast.toString());

        wm = new WeatherManagerImpl();
        wm.initOpenWeatherMap();
        //prova fra 5 giorni
        cal.add(Calendar.DATE, 1);
        logger.log(LoggerLevel.DEBUG, "Tempo del: {0}", cal.getTime().
                toString());
        forecast = wm.getWeather(cal, "Rome");
        logger.log(LoggerLevel.DEBUG, forecast.toString());

        wm = new WeatherManagerImpl();
        wm.initOpenWeatherMap();
        //prova fra 6 giorni
        cal.add(Calendar.DATE, 1);
        logger.log(LoggerLevel.DEBUG, "Tempo del: {0}", cal.getTime().
                toString());
        forecast = wm.getWeather(cal, "Rome");
        logger.log(LoggerLevel.DEBUG, forecast.toString());

        wm = new WeatherManagerImpl();
        wm.initOpenWeatherMap();
        //prova fra 10 giorni
        cal.add(Calendar.DATE, 4);
        logger.log(LoggerLevel.DEBUG, "Tempo del: {0}", cal.getTime().
                toString());
        forecast = wm.getWeather(cal, "Rome");
        logger.log(LoggerLevel.DEBUG, forecast.toString());
    }
}
