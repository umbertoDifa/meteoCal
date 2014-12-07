package EJB.interfaces;

import java.util.Calendar;
import objectAndString.WeatherForecast;

/**
 *
 * @author umboDifa
 */
public interface WeatherManager {
    public WeatherForecast getWeather(Calendar day, String city);
    //TODO sarebbe corretto salvare le previsioni del tempo nel database
    //e aggiornarle ogni tanto senza caricarle in real time ogni volta che un user apre
    //un evento
}
