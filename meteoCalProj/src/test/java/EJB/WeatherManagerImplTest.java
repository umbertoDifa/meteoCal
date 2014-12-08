package EJB;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import objectAndString.WeatherForecast;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import utility.ForecastType;

/**
 *
 * @author umboDifa
 */
public class WeatherManagerImplTest {
    WeatherManagerImpl weatherManagerImpl = lookupWeatherManagerImplBean();
    
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
    public void prova(){        
        WeatherManagerImpl wm = lookupWeatherManagerImplBean();
        Calendar dayToCheck = Calendar.getInstance();
        ForecastType type = wm.inferForecastType(dayToCheck);
        assertTrue(type == ForecastType.CURRENT_WEATHER);
    }

    private WeatherManagerImpl lookupWeatherManagerImplBean() {
        try {
            Context c = new InitialContext();
            return (WeatherManagerImpl) c.lookup("java:global/com.fravaleumbo_meteoCalProj_war_1.0-SNAPSHOT/WeatherManagerImpl!EJB.WeatherManagerImpl");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
}
