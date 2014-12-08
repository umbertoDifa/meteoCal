package EJB;

import EJB.interfaces.WeatherManager;
import Exceptions.ForecastDayNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import objectAndString.WeatherForecast;
import org.json.JSONException;
import utility.ForecastType;
import utility.WeatherMessages;
import weatherLib.CurrentWeatherData;
import weatherLib.DailyForecastData;
import weatherLib.ForecastWeatherData;
import weatherLib.OpenWeatherMap;

@Stateless
public class WeatherManagerImpl implements WeatherManager {

    //tentativi di ottenere le previsioni prima di ritornare unavailable
    private final int MAX_TRIES = 4;

    //creo oggetto openWeatherMap per fare le richieste con la mia API key
    private OpenWeatherMap openWeatherMap;

    //creo l'oggetto daily forecast che si occuperà di ottenere le previsioni giornaliere
    //per un max di 16 giorni 
    private DailyForecastData dailyForecast;

    //position of the wanted day in the list of forecast
    int position;

    //creo l'oggetto che si occuperà di ottere le previsioni per i prox 5 giorni ogni 3 ore
    private ForecastWeatherData forecastFiveDays;

    //oggetto per le previsioni per il giorno stesso
    private CurrentWeatherData currentWeather;

    //tipo di previsione da fare
    private ForecastType forecastType;

    //oggetto ritornato dalle richieste di previsioni
    private WeatherForecast weatherForecast;

    //city to get forecast for
    private String city;

    @Inject
    @Default
    Logger logger;

    @PostConstruct
    private void init() {
        openWeatherMap = new OpenWeatherMap("6f165fcce7eddd2405ef5c0596000ff7");
    }

    @Override
    public WeatherForecast getWeather(Calendar day, String city) {
        this.city = city;
        //in base a che giorno è oggi e a quando è schedulato l'evento uso
        //un tipo diverso di previsioni
        this.forecastType = inferForecastType(day);

        //TODO adattare questa struttura a forecast 16 e quano è unpredictable
        createWeatherForecast();

        return weatherForecast;
    }

    private ForecastType inferForecastType(Calendar dayToCheck) {
        //creo un oggetto calendario con il giorno di oggi
        Calendar today = Calendar.getInstance();
        Calendar in5Days = Calendar.getInstance();
        Calendar in16Days = Calendar.getInstance();

        in5Days.add(Calendar.DATE, 5);
        in16Days.add(Calendar.DATE, 16);

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
        if (dayToCheck.before(in5Days)) {
            return ForecastType.FORECAST_5_3HOURS;

        }

        //se chiede il tempo entro i prox 16 giorni
        if (dayToCheck.before(in16Days)) {
            return ForecastType.FORECAST_16_DAILY;

        }

        //altrimenti
        return ForecastType.UNPREDICTABLE;

    }

    private void createWeatherForecast() {
        //se non ho superato i tentativi massimi e i dati sono accessibili
        if (downloadWeather(forecastType) <= MAX_TRIES && infoIsAvailable()) {
            //imposta i dati
            this.setWeatherObj();
        } else {
            weatherForecast.setMessage(WeatherMessages.NOT_AVAILABLE);
        }
    }

    private int downloadWeather(ForecastType forecastType) {
        int i = 0;

        //finchè non ottengo una risposta ma riprova a contattare
        //il servizio per un massimo di MAX_TRIES tentativi
        do {
            try {
                switch (forecastType) {
                    case FORECAST_5_3HOURS:
                        forecastFiveDays = openWeatherMap.forecastWeatherByCityName(city);
                        break;
                    case CURRENT_WEATHER:
                        currentWeather = openWeatherMap.currentWeatherByCityName(city);
                        break;
                    case FORECAST_16_DAILY:
                        dailyForecast = openWeatherMap.dailyForecastByCityName(city, (byte) 16);
                        break;

                    //just a bigger number than MAX_TRIES
                    default:
                        i = MAX_TRIES * 2;
                        break;
                }

            } catch (IOException | JSONException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                return MAX_TRIES * 2;//just a bigger number than MAX_TRIES
            }
            i++;
        } while (!hasWeatherList() && i <= MAX_TRIES);

        return i;
    }

    private boolean hasWeatherList() {
        switch (forecastType) {
            case FORECAST_5_3HOURS:
                return forecastFiveDays.hasForecast_List();

            case CURRENT_WEATHER:
                return currentWeather.hasWeather_List();

            case FORECAST_16_DAILY:
                return dailyForecast.hasForecast_List();

        }
        return false;
    }

    private boolean infoIsAvailable() {
        switch (forecastType) {
            case FORECAST_5_3HOURS:
                return infoIsAvailable5Days();
            case CURRENT_WEATHER:
                return infoIsAvailableCurrent();
            case FORECAST_16_DAILY:
                //TODO
                break;
        }
        return false;
    }

    private boolean infoIsAvailableCurrent() {
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

    private boolean infoIsAvailable5Days() {

        try {
            position = this.findDayPositionInForecastList(forecastFiveDays, null);
            //check all has
            if (forecastFiveDays.getForecast_List().get(position).hasWeather_List()
                    && forecastFiveDays.getForecast_List().get(position).getWeather_List().get(0).hasWeatherCode()
                    && forecastFiveDays.getForecast_List().get(position).getWeather_List().get(0).hasWeatherDescription()
                    && forecastFiveDays.getForecast_List().get(position).getWeather_List().get(0).hasWeatherIconName()
                    && forecastFiveDays.getForecast_List().get(position).getWeather_List().get(0).hasWeatherName()) {
                if (forecastFiveDays.getForecast_List().get(position).getMainData_Object().hasHumidity()
                        && forecastFiveDays.getForecast_List().get(position).getMainData_Object().hasMaxTemperature()
                        && forecastFiveDays.getForecast_List().get(position).getMainData_Object().hasMinTemperature()
                        && forecastFiveDays.getForecast_List().get(position).getMainData_Object().hasPressure()
                        && forecastFiveDays.getForecast_List().get(position).getMainData_Object().hasTemperature()) {
                    return true;
                }
            }
            return false;
        } catch (ForecastDayNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            return false;
        }
    }

    private void setWeatherObj() {
        switch (forecastType) {
            case FORECAST_5_3HOURS:
                this.setWeatherObjFromForecast5();
                break;
            case CURRENT_WEATHER:
                this.setWeatherObjFromCurrent();
                break;
            case FORECAST_16_DAILY:
                //TODO
                break;
        }
    }

    private void setWeatherObjFromForecast5() {
        weatherForecast.setDescription(forecastFiveDays.getForecast_List().get(position).getWeather_List().get(0).getWeatherDescription());
        weatherForecast.setHumidity(forecastFiveDays.getForecast_List().get(position).getMainData_Object().getHumidity());
        weatherForecast.setIcon(forecastFiveDays.getForecast_List().get(position).getWeather_List().get(0).getWeatherIconName());
        weatherForecast.setMain(forecastFiveDays.getForecast_List().get(position).getWeather_List().get(0).getWeatherName());
        weatherForecast.setMaxTemp(forecastFiveDays.getForecast_List().get(position).getMainData_Object().getMaxTemperature());
        weatherForecast.setMinTemp(forecastFiveDays.getForecast_List().get(position).getMainData_Object().getMinTemperature());
        weatherForecast.setPressure(forecastFiveDays.getForecast_List().get(position).getMainData_Object().getPressure());
        weatherForecast.setTemp(forecastFiveDays.getForecast_List().get(position).getMainData_Object().getTemperature());
        weatherForecast.setWeatherId(forecastFiveDays.getForecast_List().get(position).getWeather_List().get(0).getWeatherCode());
        setGoodOrBadWeather();
    }

    private void setWeatherObjFromCurrent() {
        weatherForecast.setDescription(currentWeather.getWeather_List().get(0).getWeatherDescription());
        weatherForecast.setHumidity(currentWeather.getMainData_Object().getHumidity());
        weatherForecast.setIcon(currentWeather.getWeather_List().get(0).getWeatherIconName());
        weatherForecast.setMain(currentWeather.getWeather_List().get(0).getWeatherName());
        weatherForecast.setMaxTemp(currentWeather.getMainData_Object().getMaxTemperature());
        weatherForecast.setMinTemp(currentWeather.getMainData_Object().getMinTemperature());
        weatherForecast.setPressure(currentWeather.getMainData_Object().getPressure());
        weatherForecast.setTemp(currentWeather.getMainData_Object().getTemperature());
        weatherForecast.setWeatherId(currentWeather.getWeather_List().get(0).getWeatherCode());
        setGoodOrBadWeather();
    }

    private int findDayPositionInForecastList(ForecastWeatherData forecast, Calendar day) throws ForecastDayNotFoundException {
        if (forecast.hasForecast_List()) {
            for (int i = 0; i < forecast.getForecast_List_Count(); i++) {
                if (forecast.getForecast_List().get(i) != null
                        && forecast.getForecast_List().get(i).hasDateTimeText()
                        && forecast.getForecast_List().get(i).getDateTimeText().contains(dayToText(day))) {
                    return i;
                }
            }
        }
        throw new ForecastDayNotFoundException();

    }

    private String dayToText(Calendar day) {
        SimpleDateFormat df = new SimpleDateFormat();
        //df.applyPattern("yyyy-MM-dd hh:mm:ss");
        df.applyPattern("yyyy-MM-dd");
        return df.format(day.getTime());
    }

    private void setGoodOrBadWeather() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
