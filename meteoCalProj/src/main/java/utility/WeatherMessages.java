package utility;

/**
 *
 * @author umboDifa
 */
public enum WeatherMessages {

    BAD_WEATHER("Bad weather for the requested day"),
    GOOD_WEATHER("Good weather for the requested day"),
    NOT_AVAILABLE("Coudln't find any weather forecast for the requested day");

    private final String message;

    WeatherMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
