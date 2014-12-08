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
     *
     * @throws java.lang.Exception
     */
    @Test

    public void testGetWeather() throws Exception {
        System.out.println("getWeather");
    }
}
