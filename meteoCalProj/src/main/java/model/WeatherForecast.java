package model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import utility.WeatherMessages;

/**
 *
 * @author umboDifa
 */
@Entity
@Table(name = "WEATHER_FORECAST")
public class WeatherForecast implements Serializable {

    @Id
    @TableGenerator(name = "WEATHER_SEQ", table = "SEQUENCE",
                    pkColumnName = "SEQ_NAME",
                    valueColumnName = "SEQ_COUNT", pkColumnValue = "WEATHER_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "WEATHER_SEQ")
    private Long id;

    @Enumerated(EnumType.STRING)
    
    @Column(columnDefinition = "ENUM('BAD_WEATHER', 'GOOD_WEATHER', 'NOT_AVAILABLE')")
    private WeatherMessages message;

    private float temp;
    private float maxTemp;
    private float minTemp;

    private float pressure;
    private float humidity;

    private int openWeatherId;

    private String main;

    private String description;

    private String icon;

    @OneToOne(mappedBy = "weather")
    private Event event;

    /*
     * CONSTRUCTORS
     * 
     */
    public WeatherForecast() {
    }

    public WeatherForecast(WeatherMessages message, float temp, float maxTemp, float minTemp, float pressure, float humidity, int weatherId, String main, String description, String icon, Event event) {
        this.message = message;
        this.temp = temp;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.pressure = pressure;
        this.humidity = humidity;
        this.openWeatherId = weatherId;
        this.main = main;
        this.description = description;
        this.icon = icon;
        this.event = event;
    }

    /*
     * GETTERS & SETTERS
     * 
     */
    public WeatherMessages getMessage() {
        return message;
    }

    public void setMessage(WeatherMessages message) {
        this.message = message;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }

    public float getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public int getOpenWeatherId() {
        return openWeatherId;
    }

    public void setWeatherId(int weatherId) {
        this.openWeatherId = weatherId;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    /*
     * 
     * METHODS
     */
    @Override
    public String toString() {
        if (message == null || message == WeatherMessages.NOT_AVAILABLE) {
            return "Weather forecast not available.";
        }
        return "WeatherForecast: temp=" + temp + ", maxTemp=" + maxTemp
                + ", minTemp=" + minTemp + ", pressure=" + pressure
                + ", humidity=" + humidity + ", weatherId=" + openWeatherId
                + ", main=" + main + ", description=" + description + ", icon="
                + icon + '.';
    }

    public void update(WeatherForecast newForecast) {
        this.description = newForecast.getDescription();
        this.humidity = newForecast.getHumidity();
        this.icon = newForecast.getIcon();
        this.main = newForecast.getMain();
        this.maxTemp = newForecast.getMaxTemp();
        this.message = newForecast.getMessage();
        this.minTemp = newForecast.getMinTemp();
        this.openWeatherId = newForecast.getOpenWeatherId();
        this.pressure = newForecast.getPressure();
        this.temp = newForecast.getTemp();
    }

}
