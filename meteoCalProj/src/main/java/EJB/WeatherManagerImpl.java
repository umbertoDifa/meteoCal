package EJB;

import EJB.interfaces.WeatherManager;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import net.aksingh.java.api.owm.CurrentWeatherData;
import net.aksingh.java.api.owm.DailyForecastData;
import net.aksingh.java.api.owm.ForecastWeatherData;
import net.aksingh.java.api.owm.OpenWeatherMap;
import objectAndString.WeatherForecast;
import org.json.JSONException;
import utility.ForecastType;
import utility.WeatherMessages;

@Stateless
public class WeatherManagerImpl implements WeatherManager {

    //tentativi di ottenere le previsioni prima di ritornare unavailable
    private final int MAX_TRIES = 4;

    //creo oggetto openWeatherMap per fare le richieste con la mia API key
    private OpenWeatherMap openWeatherMap;

    //creo l'oggetto daily forecast che si occuperà di ottenere le previsioni giornaliere
    //per un max di 16 giorni 
    private DailyForecastData dailyForecast;

    //creo l'oggetto che si occuperà di ottere le previsioni per i prox 5 giorni ogni 3 ore
    private ForecastWeatherData forecastFiveDays;

    //oggetto per le previsioni per il giorno stesso
    private CurrentWeatherData currentWeather;

    //oggetto ritornato dalle richieste di previsioni
    private WeatherForecast weatherForecast;

    @Inject
    @Default
    Logger logger;

    @PostConstruct
    private void init() {
        openWeatherMap = new OpenWeatherMap("6f165fcce7eddd2405ef5c0596000ff7");
    }

    @Override
    public WeatherForecast getWeather(Calendar day, String city) {
        //in base a che giorno è oggi e a quando è schedulato l'evento uso
        //un tipo diverso di previsioni
        switch (inferForecastType(day)) {
            case FORECAST_5_3HOURS:
                break;
            case CURRENT_WEATHER:
                createCurrentWeatherForecast(city);
            case FORECAST_16_DAILY:
                break;
            case UNPREDICTABLE:
                break;

        }
        return weatherForecast;
    }

    private void createCurrentWeatherForecast(String city) {
        int i = 0;

        //finchè non ottengo una risposta ma riprova a contattare
        //il servizion per un massimo di MAX_TRIES tentativi
        do {
            try {
                currentWeather = openWeatherMap.currentWeatherByCityName(city);
            } catch (IOException | JSONException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                weatherForecast.setMessage(WeatherMessages.NOT_AVAILABLE);
                return;
            }
        } while (!currentWeather.hasWeather_List() && i++ <= MAX_TRIES);

        //se non ho superato i tentativi massimi e i dati sono accessibili
        if (i <= MAX_TRIES && infoIsAvailable(currentWeather)) {
            //imposta i dati
            weatherForecast.setDescription(currentWeather.getWeather_List().get(0).getWeatherDescription());
            weatherForecast.setHumidity(currentWeather.getMainData_Object().getHumidity());
            weatherForecast.setIcon(currentWeather.getWeather_List().get(0).getWeatherIconName());
            weatherForecast.setMain(currentWeather.getWeather_List().get(0).getWeatherName());
            weatherForecast.setMaxTemp(currentWeather.getMainData_Object().getMaxTemperature());
            weatherForecast.setMinTemp(currentWeather.getMainData_Object().getMinTemperature());
            weatherForecast.setPressure(currentWeather.getMainData_Object().getPressure());
            weatherForecast.setTemp(currentWeather.getMainData_Object().getTemperature());
            weatherForecast.setWeatherId(currentWeather.getWeather_List().get(0).getWeatherCode());
            decideIfGoodWeather();
        } else {
            weatherForecast.setMessage(WeatherMessages.NOT_AVAILABLE);
        }
    }

    private ForecastType inferForecastType(Calendar dayToCheck) {
        //creo un oggetto calendario con il giorno di oggi
        Calendar today = Calendar.getInstance();

        int diff = dayToCheck.compareTo(today);

        //se chiede il forecast di un giorno passato per ora il risultato 
        //è unpredictable
        //TODO vedi se riesci a fare qualcosa con la history
        if (diff < 0) {
            return ForecastType.UNPREDICTABLE;
        }

        //se chiede il tempo per oggi stesso
        if (diff == 0) {
            return ForecastType.CURRENT_WEATHER;
        }

        //se chiede il tempo entro i prossimi 5 giorni
        if (diff < TimeUnit.SECONDS.toMillis(5)) {
            return ForecastType.FORECAST_5_3HOURS;
        }

        //se chiede il tempo entro i prox 16 giorni
        if (diff < TimeUnit.SECONDS.toMillis(16)) {
            return ForecastType.FORECAST_16_DAILY;
        }

        //altrimenti
        return ForecastType.UNPREDICTABLE;

    }

    private boolean infoIsAvailable(CurrentWeatherData currentWeather) {
        if (currentWeather.hasWeather_List()
                && currentWeather.getWeather_List().get(0) != null
                && currentWeather.getWeather_List().get(0).hasWeatherCode()
                && currentWeather.getWeather_List().get(0).hasWeatherDescription()
                && currentWeather.getWeather_List().get(0).hasWeatherIconName()
                && currentWeather.getWeather_List().get(0).hasWeatherName()) {
            if (currentWeather.getMainData_Object().hasHumidity()
                    && currentWeather.getMainData_Object().hasMaxTemperature()
                    && currentWeather.getMainData_Object().hasMinTemperature()
                    && currentWeather.getMainData_Object().hasPressure()
                    && currentWeather.getMainData_Object().hasTemperature()) {
                return true;
            }
        }
        return false;
    }

    private void decideIfGoodWeather() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
