package EJB;

import java.util.Calendar;
import java.util.logging.Logger;
import javax.ejb.embeddable.EJBContainer;
import objectAndString.WeatherForecast;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utility.LoggerProducer;

/**
 *
 * @author umboDifa
 */
public class WeatherManagerImplTest {

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
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getWeather method, of class WeatherManagerImpl.
     */
    @Test

    public void testGetWeather() throws Exception {
        System.out.println("getWeather");
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        WeatherManagerImpl instance = (WeatherManagerImpl) container.getContext().lookup("java:global/com.fravaleumbo_meteoCalProj_war_1.0-SNAPSHOT/WeatherManagerImpl!EJB.WeatherManagerImpl");
        
//        Calendar day = null;
//        String city = "";
//        WeatherForecast expResult = null;
//        WeatherForecast result = weatherManagerImpl.getWeather(day, city);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
        instance.getWeather(null, null);
        container.close();
    }

}
