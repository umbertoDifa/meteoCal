package utility;

/**
 *
 * @author umboDifa
 */
public enum WeatherType {

    //thunderstorm
    THUNDERSTORM_WITH_LIGHT_RAIN(200),
    THUNDERSTORM_WITH_HEAVY_DRIZZLE(232),
    //drizzle
    LIGHT_INTENSITY_DRIZZLE(300),
    LIGHT_INTENSITY_DRIZZLE_RAIN(310),
    SHOWER_DRIZZLE(321),
    //rain
    LIGHT_RAIN(500),
    RAGGED_SHOWER_RAIN(531),
    //snow
    LIGHT_SNOW(600),
    HEAVY_SHOWER_SNOW(622),
    //atmosphere(nebbie varie)
    MIST(701),
    TORNADO(781),
    //clouds
    CLEAR_SKY(800),
    FEW_CLOUDS(801),
    SCATTERED_CLOUDS(802),
    BROKEN_CLOUDS(803),
    OVERCAST_CLOUDS(804),
    //extreme
    TORNADO_EXTREME(900),
    HAIL(906),
    //additional
    CALM(951),
    FRESH_BREEZE(955),
    STRONG_BREEZE(956),
    HURRICANE(962);

    private int code;

    private WeatherType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static boolean isBadWeather(int code) {
//        if (code >= THUNDERSTORM_WITH_LIGHT_RAIN.getCode() && code
//                <= THUNDERSTORM_WITH_HEAVY_DRIZZLE.getCode()) {
//            return true;
//        } else if (code >= LIGHT_INTENSITY_DRIZZLE.getCode() && code
//                <= SHOWER_DRIZZLE.getCode()) {
//            return true;
//        } else if (code >= LIGHT_RAIN.getCode() && code
//                <= RAGGED_SHOWER_RAIN.getCode()) {
//            return true;
//        } else if (code >= LIGHT_SNOW.getCode() && code
//                <= HEAVY_SHOWER_SNOW.getCode()) {
//            return true;
//        } else if (code >= MIST.getCode() && code
//                <= TORNADO.getCode()) {
//            return true; //QUESTO forse no, è solo nebbia
//        } else if (code >= CLEAR_SKY.getCode() && code
//                <= OVERCAST_CLOUDS.getCode()) {
//            return false;
//        } else if (code >= TORNADO_EXTREME.getCode() && code
//                <= HAIL.getCode()) {
//            return true;
//        }else if(code >= CALM.getCode() && code
//                <= FRESH_BREEZE.getCode()){
//            return false;
//        }else if(code >= STRONG_BREEZE.getCode() && code
//                <= HURRICANE.getCode()){
//            return true;
//        }
        if (code >= CLEAR_SKY.getCode() && code
                <= OVERCAST_CLOUDS.getCode()) {
            return false;
        } else if (code >= CALM.getCode() && code
                <= FRESH_BREEZE.getCode()) {
            return false;
        } else if (code >= MIST.getCode() && code
                <= TORNADO.getCode()) {
            return false; //...è solo nebbia
        }
        return true;
    }

    

}
