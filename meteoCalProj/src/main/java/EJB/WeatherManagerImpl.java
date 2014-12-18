package EJB;

import EJB.interfaces.WeatherManager;
import Exceptions.ForecastDayNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import objectAndString.WeatherForecast;
import org.json.JSONException;
import utility.ForecastType;
import utility.LoggerLevel;
import utility.LoggerProducer;
import utility.TimeTool;
import utility.WeatherMessages;
import utility.WeatherType;
import weatherLib.CurrentWeatherData;
import weatherLib.DailyForecastData;
import weatherLib.ForecastWeatherData;
import weatherLib.OpenWeatherMap;

//TODO se c'è tempo, si potrebbe dirgli di usare 5 se current non c'è e usare 16 se 5 non c'è
@Stateless
public class WeatherManagerImpl implements WeatherManager {

    //tentativi di ottenere le previsioni prima di ritornare unavailable
    private final int MAX_TRIES = 10;

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

    Logger logger = LoggerProducer.debugLogger(WeatherManagerImpl.class);

    @PostConstruct
    private void init() {
        openWeatherMap = new OpenWeatherMap("6f165fcce7eddd2405ef5c0596000ff7");
    }

    //Just for testing!
    //DELETEME il test si fa con il mock!
    protected void initOpenWeatherMap() {
        openWeatherMap = new OpenWeatherMap("6f165fcce7eddd2405ef5c0596000ff7");
    }

    @Override
    public WeatherForecast getWeather(Calendar day, String city) {
        this.city = city;
        //in base a che giorno è oggi e a quando è schedulato l'evento uso
        //un tipo diverso di previsioni
        this.forecastType = inferForecastType(day);
        logger.log(LoggerLevel.DEBUG, "Forecast infered: {0}", forecastType);

        weatherForecast = new WeatherForecast();

        if (forecastType != ForecastType.UNPREDICTABLE) {
            createWeatherForecast(day);
        } else {
            weatherForecast.setMessage(WeatherMessages.NOT_AVAILABLE);
        }

        return weatherForecast;
    }

    private ForecastType inferForecastType(Calendar dayToCheck) {

        logger.log(LoggerLevel.DEBUG, "Starting to infer type of forecast...");
        //creo oggetti calendari che mi servono
        Calendar today = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        Calendar in5Days = Calendar.getInstance();
        Calendar in16Days = Calendar.getInstance();

        tomorrow.add(Calendar.DATE, 1);
        yesterday.add(Calendar.DATE, -1);
        in5Days.add(Calendar.DATE, 5);
        in16Days.add(Calendar.DATE, 16);

        //se chiede il forecast di un giorno passato per ora il risultato 
        //è unpredictable
        //TODO vedi se riesci a fare qualcosa con la history
        //se per un giorno passato allora unpredictable
        if (TimeTool.isBefore(dayToCheck, today)) {
            return ForecastType.UNPREDICTABLE;
        }

        //se per oggi allora current weather        
        if (TimeTool.isBefore(dayToCheck, tomorrow) && TimeTool.isAfter(
                dayToCheck, yesterday)) {
            return ForecastType.CURRENT_WEATHER;
        }

        //se chiede il tempo entro i prossimi 5 giorni
        if (TimeTool.isBefore(dayToCheck, in5Days)) {
            return ForecastType.FORECAST_5_3HOURS;

        }

        //se chiede il tempo entro i prox 16 giorni
        if (TimeTool.isBefore(dayToCheck, in16Days)) {
            return ForecastType.FORECAST_16_DAILY;

        }

        //altrimenti
        logger.log(LoggerLevel.DEBUG, "Giorno troppo lontano");
        return ForecastType.UNPREDICTABLE;

    }

    private void createWeatherForecast(Calendar day) {

        logger.log(LoggerLevel.DEBUG,
                "Starting to create the weather forecast...");

        boolean forecastFound = false;
        int i = 0;
        do {
            logger.log(LoggerLevel.DEBUG, "Try number {0} :", i);
            //tento di trovare le previsioni
            if (downloadWeather(forecastType) && infoIsAvailable(day)) {
                //imposta i dati
                this.setWeatherObj();
                logger.log(LoggerLevel.DEBUG, "Forecast obj created!");
                forecastFound = true;
            } else {
                i++;
                weatherForecast.setMessage(WeatherMessages.NOT_AVAILABLE);
            }
        } while (i < MAX_TRIES && !forecastFound); //fino ad un massimo di tentativi
    }

    private boolean downloadWeather(ForecastType forecastType) {

        try {

            switch (forecastType) {
                case FORECAST_5_3HOURS:
                    forecastFiveDays = openWeatherMap.forecastWeatherByCityName(
                            city);
                    break;
                case CURRENT_WEATHER:
                    currentWeather = openWeatherMap.currentWeatherByCityName(
                            city);
                    break;
                case FORECAST_16_DAILY:
                    dailyForecast = openWeatherMap.dailyForecastByCityName(city,
                            (byte) 16);
                    break;
            }
            logger.log(LoggerLevel.DEBUG, "Weather Downloaded");
            return true;

        } catch (IOException | JSONException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
    }

    private boolean infoIsAvailable(Calendar day) {
        logger.log(LoggerLevel.DEBUG, "Checking if all info is available...");
        switch (forecastType) {
            case FORECAST_5_3HOURS:
                return infoIsAvailable5Days(day);
            case CURRENT_WEATHER:
                return infoIsAvailableCurrent();
            case FORECAST_16_DAILY:
                return infoIsAvailable16Days(day);
            default:
                throw new UnsupportedOperationException(
                        "Non è possibile controllare le info");
        }

    }

    private boolean infoIsAvailableCurrent() {
        if (currentWeather.hasWeather_List()
                && currentWeather.getWeather_List().get(0) != null
                && currentWeather.getWeather_List().get(0).hasWeatherCode()
                && currentWeather.getWeather_List().get(0).
                hasWeatherDescription()
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

    private boolean infoIsAvailable16Days(Calendar day) {
        try {
            position = this.findDayPositionInForecastList(dailyForecast, day);
            logger.log(LoggerLevel.DEBUG, "Position found in {0}", position);

            //check all has
            if (dailyForecast.getForecast_List().get(position).hasWeather_List()
                    && dailyForecast.getForecast_List().get(position).
                    getWeather_List().get(0).hasWeatherCode()
                    && dailyForecast.getForecast_List().get(position).
                    getWeather_List().get(0).hasWeatherDescription()
                    && dailyForecast.getForecast_List().get(position).
                    getWeather_List().get(0).hasWeatherIconName()
                    && dailyForecast.getForecast_List().get(position).
                    getWeather_List().get(0).hasWeatherName()) {
                if (dailyForecast.getForecast_List().get(position).hasHumidity()
                        && dailyForecast.getForecast_List().get(position).
                        hasHumidity()
                        && dailyForecast.getForecast_List().get(position).
                        hasPressure()) {
                    return true;
                }
            }
            return false;
        } catch (ForecastDayNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            return false;
        }
    }

    private boolean infoIsAvailable5Days(Calendar day) {
        try {
            position = this.findDayPositionInForecastList(forecastFiveDays, day);
            logger.log(LoggerLevel.DEBUG, "Position found in {0}", position);

            //check all has
            if (forecastFiveDays.getForecast_List().get(position).
                    hasWeather_List()
                    && forecastFiveDays.getForecast_List().get(position).
                    getWeather_List().get(0).hasWeatherCode()
                    && forecastFiveDays.getForecast_List().get(position).
                    getWeather_List().get(0).hasWeatherDescription()
                    && forecastFiveDays.getForecast_List().get(position).
                    getWeather_List().get(0).hasWeatherIconName()
                    && forecastFiveDays.getForecast_List().get(position).
                    getWeather_List().get(0).hasWeatherName()) {
                if (forecastFiveDays.getForecast_List().get(position).
                        getMainData_Object().hasHumidity()
                        && forecastFiveDays.getForecast_List().get(position).
                        getMainData_Object().hasMaxTemperature()
                        && forecastFiveDays.getForecast_List().get(position).
                        getMainData_Object().hasMinTemperature()
                        && forecastFiveDays.getForecast_List().get(position).
                        getMainData_Object().hasPressure()
                        && forecastFiveDays.getForecast_List().get(position).
                        getMainData_Object().hasTemperature()) {
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
        logger.log(LoggerLevel.DEBUG, "Starting to set the forecast object...");
        switch (forecastType) {
            case FORECAST_5_3HOURS:
                this.setWeatherObjFromForecast5();
                break;
            case CURRENT_WEATHER:
                this.setWeatherObjFromCurrent();
                break;
            case FORECAST_16_DAILY:
                this.setWeatherObjFromForecast16();
                break;
        }
    }

    private void setWeatherObjFromForecast16() {
        weatherForecast.setDescription(dailyForecast.getForecast_List().get(
                position).getWeather_List().get(0).getWeatherDescription());
        weatherForecast.setHumidity(dailyForecast.getForecast_List().get(
                position).getHumidity());
        weatherForecast.setIcon(dailyForecast.getForecast_List().get(position).
                getWeather_List().get(0).getWeatherIconName());
        weatherForecast.setMain(dailyForecast.getForecast_List().get(position).
                getWeather_List().get(0).getWeatherName());
        weatherForecast.setMaxTemp(dailyForecast.getForecast_List().
                get(position).getTemperature_Object().getMaximumTemperature());
        weatherForecast.setMinTemp(dailyForecast.getForecast_List().
                get(position).getTemperature_Object().getMinimumTemperature());
        weatherForecast.setPressure(dailyForecast.getForecast_List().get(
                position).getPressure());
        weatherForecast.setTemp(dailyForecast.getForecast_List().get(position).
                getTemperature_Object().getDayTemperature());
        weatherForecast.setWeatherId(dailyForecast.getForecast_List().get(
                position).getWeather_List().get(0).getWeatherCode());
        setGoodOrBadWeather();
    }

    private void setWeatherObjFromForecast5() {
        weatherForecast.setDescription(forecastFiveDays.getForecast_List().get(
                position).getWeather_List().get(0).getWeatherDescription());
        weatherForecast.setHumidity(forecastFiveDays.getForecast_List().get(
                position).getMainData_Object().getHumidity());
        weatherForecast.setIcon(forecastFiveDays.getForecast_List().
                get(position).getWeather_List().get(0).getWeatherIconName());
        weatherForecast.setMain(forecastFiveDays.getForecast_List().
                get(position).getWeather_List().get(0).getWeatherName());
        weatherForecast.setMaxTemp(forecastFiveDays.getForecast_List().get(
                position).getMainData_Object().getMaxTemperature());
        weatherForecast.setMinTemp(forecastFiveDays.getForecast_List().get(
                position).getMainData_Object().getMinTemperature());
        weatherForecast.setPressure(forecastFiveDays.getForecast_List().get(
                position).getMainData_Object().getPressure());
        weatherForecast.setTemp(forecastFiveDays.getForecast_List().
                get(position).getMainData_Object().getTemperature());
        weatherForecast.setWeatherId(forecastFiveDays.getForecast_List().get(
                position).getWeather_List().get(0).getWeatherCode());
        setGoodOrBadWeather();
    }

    private void setWeatherObjFromCurrent() {
        weatherForecast.setDescription(currentWeather.getWeather_List().get(0).
                getWeatherDescription());
        weatherForecast.setHumidity(currentWeather.getMainData_Object().
                getHumidity());
        weatherForecast.setIcon(currentWeather.getWeather_List().get(0).
                getWeatherIconName());
        weatherForecast.setMain(currentWeather.getWeather_List().get(0).
                getWeatherName());
        weatherForecast.setMaxTemp(currentWeather.getMainData_Object().
                getMaxTemperature());
        weatherForecast.setMinTemp(currentWeather.getMainData_Object().
                getMinTemperature());
        weatherForecast.setPressure(currentWeather.getMainData_Object().
                getPressure());
        weatherForecast.setTemp(currentWeather.getMainData_Object().
                getTemperature());
        weatherForecast.setWeatherId(currentWeather.getWeather_List().get(0).
                getWeatherCode());

        setGoodOrBadWeather();

    }

    private int findDayPositionInForecastList(ForecastWeatherData forecast,
                                              Calendar day) throws ForecastDayNotFoundException {
        logger.log(LoggerLevel.DEBUG, "Trying to find the forecast position...");

        if (forecast.hasForecast_List()) {
            for (int i = 0; i < forecast.getForecast_List_Count(); i++) {
                if (forecast.getForecast_List().get(i) != null
                        && forecast.getForecast_List().get(i).hasDateTimeText()
                        && forecast.getForecast_List().get(i).getDateTimeText().
                        contains(TimeTool.calendarToTextDay(day))) {
                    return i;
                }
            }
        }
        throw new ForecastDayNotFoundException();

    }

    private int findDayPositionInForecastList(DailyForecastData dailyForecast,
                                              Calendar day) throws ForecastDayNotFoundException {
        logger.log(LoggerLevel.DEBUG, "Trying to find the forecast position...");

        if (dailyForecast.hasForecast_List()) {
            for (int i = 0; i < dailyForecast.getForecast_List_Count(); i++) {
                if (dailyForecast.getForecast_List().get(i) != null
                        && dailyForecast.getForecast_List().get(i).hasDateTime()
                        && TimeTool.dateToTextDay(dailyForecast.
                                getForecast_List().get(i).getDateTime()).
                        contains(TimeTool.calendarToTextDay(day))) {
                    return i;
                }
            }
        }
        throw new ForecastDayNotFoundException();
    }

    private void setGoodOrBadWeather() {
        if (WeatherType.isBadWeather(weatherForecast.getWeatherId())) {
            weatherForecast.setMessage(WeatherMessages.BAD_WEATHER);
        } else {
            weatherForecast.setMessage(WeatherMessages.GOOD_WEATHER);
        }
    }

}
