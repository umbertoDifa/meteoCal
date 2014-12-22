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
     * Consulta openWeatherApi e ottiene le informazioni sulle previsioni,
     * in base a quanto in là nel futuro è una previsione fa una chiamata all'api 
     * diversa.
     * @param day Il giorno per cui ottenere le previsioni
     * @param city La città per cui ottenere le previsioni, può essere in due
     * formati: città o città,stato
     * @return Ritorna un oggetto che contiene la previsione, può essere vuoto
     */
    public WeatherForecast getWeather(Calendar day, String city);
    //TODO sarebbe corretto salvare le previsioni del tempo nel database
    //e aggiornarle ogni tanto senza caricarle in real time ogni volta che un user apre
    //un evento

    public void updateWeather(Event event);
}
