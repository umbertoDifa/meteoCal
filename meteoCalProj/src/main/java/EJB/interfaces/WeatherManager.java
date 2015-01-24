package EJB.interfaces;

import java.util.Calendar;
import model.Event;
import model.WeatherForecast;

/**
 *
 * @author umboDifa
 */
public interface WeatherManager {

    /**
     * Consulta openWeatherApi e ottiene le informazioni sulle previsioni, in
     * base a quanto in là nel futuro è una previsione fa una chiamata all'api
     * diversa.
     *
     * @param event
     * @return Ritorna un oggetto che contiene la previsione, può essere vuoto
     */
    public WeatherForecast getWeather(Event event);

    public void updateWeather(Event event);
}
