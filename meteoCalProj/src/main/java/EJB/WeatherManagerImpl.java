package EJB;

import EJB.interfaces.EventManager;
import EJB.interfaces.NotificationManager;
import EJB.interfaces.WeatherManager;
import Exceptions.ForecastDayNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Event;
import model.Invitation;
import model.InvitationAnswer;
import model.NotificationType;
import model.UserModel;
import model.WeatherForecast;
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
    private int position;

    //creo l'oggetto che si occuperà di ottere le previsioni per i prox 5 giorni ogni 3 ore
    private ForecastWeatherData forecastFiveDays;

    //oggetto per le previsioni per il giorno stesso
    private CurrentWeatherData currentWeather;

    //tipo di previsione da fare
    private ForecastType forecastType;

    //oggetto ritornato dalle richieste di previsioni
    private WeatherForecast weatherForecast;

    //event to get forecast for
    private Event event;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Inject
    private NotificationManager notificationManager;

    private Logger logger = LoggerProducer.debugLogger(WeatherManagerImpl.class);

    /**
     * Constructor
     *
     */
    public WeatherManagerImpl() {
        this.openWeatherMap = new OpenWeatherMap(
                "6f165fcce7eddd2405ef5c0596000ff7");

    }

    public WeatherManagerImpl(EntityManager database, NotificationManager notificationManager, EventManager eventManager) {
        this();
        this.database = database;
        this.notificationManager = notificationManager;
    }

    @Override
    public WeatherForecast getWeather(Event event) {
        weatherForecast = new WeatherForecast();
        this.event = event;

        //valido e aggiorno l'evento
        if (validate(this.event)) {
            Calendar day = this.event.getStartDateTime();

            //in base a che giorno è oggi e a quando è schedulato l'evento uso
            //un tipo diverso di previsioni
            this.forecastType = inferForecastType(day);
            logger.log(LoggerLevel.DEBUG, "Forecast infered: {0}", forecastType);

            if (forecastType != ForecastType.UNPREDICTABLE) {
                createWeatherForecast(day);
            } else {
                weatherForecast.setMessage(WeatherMessages.NOT_AVAILABLE);
            }
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
                    forecastFiveDays = openWeatherMap.forecastWeatherByCoordinates(
                            event.getLatitude(), event.getLongitude());
                    break;
                case CURRENT_WEATHER:
                    currentWeather = openWeatherMap.currentWeatherByCoordinates(
                            event.getLatitude(), event.getLongitude());
                    break;
                case FORECAST_16_DAILY:
                    dailyForecast = openWeatherMap.dailyForecastByCoordinates(
                            event.getLatitude(), event.getLongitude(), (byte) 16);
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

    //TODO questo controlla il get(0) cioè le previsioni per mezzanotte, sarebbe
    //opportuno ricevere le previsioni più vicine all'ora dell'eventos
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

        //create an aproximation of the date compatible with the openweather format
        int offset = day.get(Calendar.HOUR_OF_DAY) % 3;
        Calendar temp = (Calendar) day.clone();
        temp.add(Calendar.HOUR_OF_DAY, offset * -1); //approximate to the closest small hour
        temp.set(Calendar.MINUTE, 0);
        temp.set(Calendar.SECOND, 0);

        if (forecast.hasForecast_List()) {
            for (int i = 0; i < forecast.getForecast_List_Count(); i++) {
                if (forecast.getForecast_List().get(i) != null
                        && forecast.getForecast_List().get(i).hasDateTimeText()
                        && forecast.getForecast_List().get(i).getDateTimeText().
                        contains(TimeTool.dateToTextDay(temp.getTime(),
                                        "yyyy-MM-dd hh:mm:ss"))) {
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
                        contains(TimeTool.dateToTextDay(day.getTime()))) {
                    return i;
                }
            }
        }
        throw new ForecastDayNotFoundException();
    }

    private void setGoodOrBadWeather() {
        if (WeatherType.isBadWeather(weatherForecast.getOpenWeatherId())) {
            weatherForecast.setMessage(WeatherMessages.BAD_WEATHER);
        } else {
            weatherForecast.setMessage(WeatherMessages.GOOD_WEATHER);
        }
    }

    private boolean validate(Event event) {
        if(event!= null && event.getId() != null){
            //se posso aggiorno
            event = database.find(Event.class, event.getId());
        }
        if (event != null && event.hasLocation() && event.isIsOutdoor()) {            
            return true;
        }
        logger.log(LoggerLevel.DEBUG,
                "l''event \u00e8 null, o non ha location o \u00e8 indoor -> non scarico weather:\nhaslocation= {0}\nisOutodoor: {1}",
                new Object[]{event.hasLocation(),
                             event.isIsOutdoor()});
        return false;
    }

    /**
     * update Weather for an event, event must be in the db and must not be null
     *
     * @param event
     */
    @Override
    public void updateWeather(Event event) {
        //scarico il tempo
        WeatherForecast newForecast = this.getWeather(event);

        //se non esisteva una previsione
        if (event.getWeather() == null || event.getWeather().getMain() == null) {
            event.setWeather(newForecast);
            //se è la prima volta
            //aggiungo weather
            database.persist(newForecast);
            event = database.find(Event.class, event.getId());
            event.setWeather(newForecast);
            database.flush();
        } else {
            event = database.find(Event.class, event.getId());

            //se ho ottenuto qualche dato
            if (newForecast.getMessage()
                    != WeatherMessages.NOT_AVAILABLE) {
                //salvo il vecchio tempo
                WeatherForecast oldForecast = event.getWeather();
                boolean weatherChanged = !oldForecast.getMain().equals(
                        newForecast.getMain());

                //aggiorno il db            
                oldForecast.update(newForecast);
                database.flush();
                logger.log(LoggerLevel.DEBUG, "appena flushato il nuovo tempo");

                //controllo se sono tre giorni prima ed è previsto badWeather
                //nel caso avviso l'owner che può richedulare
                if (isBadWeatherNDaysBefore(event, newForecast, 3)) {
                    logger.log(LoggerLevel.DEBUG,
                            "Bad weather in three days detected");

                    //get the owner
                    List<UserModel> ownerList = new ArrayList<>();
                    ownerList.add(event.getOwner());

                    //notify The Owner
                    notificationManager.createNotifications(ownerList, event,
                            NotificationType.BAD_WEATHER_IN_THREE_DAYS, true);
                }

                //controllo se domani è il giorno dell'evento outdoor
                //perchè in quel caso se è brutto tempo li informo
                if (isBadWeatherNDaysBefore(event, newForecast, 1)) {
                    logger.log(LoggerLevel.DEBUG,
                            "Bad weather tomorrow detected");

                    List<UserModel> participants = this.getInviteesFiltered(
                            event, InvitationAnswer.YES);

                    notificationManager.createNotifications(participants, event,
                            NotificationType.BAD_WEATHER_TOMORROW, true);

                } else if (weatherChanged) {
                    //comuque se il tempo cambia li informo
                    logger.log(LoggerLevel.DEBUG,
                            "Weather changed detected");

                    List<UserModel> participants = this.getInviteesFiltered(
                            event, InvitationAnswer.YES);

                    notificationManager.createNotifications(participants, event,
                            NotificationType.WEATHER_CHANGED, true);
                } else {
                    logger.log(LoggerLevel.DEBUG,
                            "No bad weather and non changed in forecast detected");
                }

                //altrimenti non faccio nulla
            } else {
                logger.log(LoggerLevel.DEBUG, "Nussun nuovo dato disponibile");
            }
        }
    }

    //NB questa funzione esiste anche in eventManager ma per questioni di dipendenza
    //va duplicata
    private List<UserModel> getInviteesFiltered(Event event, InvitationAnswer answer) {
        if (event != null) {
            event = database.find(Event.class, event.getId());
            List<Invitation> invitations = event.getInvitations();
            List<UserModel> users = new ArrayList<>();

            for (Invitation invitation : invitations) {
                if (invitation.getAnswer().equals(answer)) {
                    users.add(invitation.getInvitee());
                }
            }
            return users;
        } else {
            logger.log(LoggerLevel.DEBUG,
                    "L'event è null in getInviteesFiltered");

            return null;
        }

    }

    private boolean isBadWeatherNDaysBefore(Event event, WeatherForecast forecast, int daysBefore) {
        Calendar today = Calendar.getInstance();

        return TimeTool.isNDayBefore(daysBefore, today, event.getStartDateTime())
                && forecast.getMessage() == WeatherMessages.BAD_WEATHER;
    }
}
