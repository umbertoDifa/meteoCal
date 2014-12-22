package utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author umboDifa
 */
public class TimeTool {

    private final static String defaultPattern = "yyyy-MM-dd";

    public static String dateToTextDay(Date date) {
        return dateToTextDay(date, defaultPattern);
    }

    public static String dateToTextDay(Date date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat();
        df.applyPattern(pattern);
        return df.format(date);
    }

    /**
     * if day1 is before day2 regardless of the time of those days
     *
     * @param day1
     * @param day2
     * @return true if day1 is before day2
     */
    public static boolean isBefore(Calendar day1, Calendar day2) {
        Calendar day1Normalized;
        Calendar day2Normalized;

        //normalizzo i giorni settando le ore a zero 
        day1Normalized = normalize(day1);
        day2Normalized = normalize(day2);

        //uso la compare di libreria
        return day1Normalized.before(day2Normalized);
    }

    /**
     * check is day1 is exactly one day before day2, withour considerind
     * hour,min,sec, and milllisec
     *
     * @param day1
     * @param day2
     * @return true if day1 is the day before day2
     */
    public static boolean isOneDayBefore(Calendar day1, Calendar day2) {
        Calendar day1Normalized;
        Calendar day2Normalized;

        //normalizzo i giorni settando le ore a zero 
        day1Normalized = normalize(day1);
        day2Normalized = normalize(day2);

        //tolgo un giorno a day2
        day2Normalized.add(Calendar.DATE, -1);

        //controllo che siano lo stesso giorno
        if (!day1Normalized.before(day2Normalized) && !day1Normalized.after(
                day2Normalized)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns a clone of the day with 0 hours 0 min and 0 secs and 0 milllisec
     *
     * @param day day to normalize
     * @return A calendar with time all resetted to 0
     */
    public static Calendar normalize(Calendar day) {
        Calendar dayNormalized = (Calendar) day.clone();

        dayNormalized.set(day.get(Calendar.YEAR), day.get(Calendar.MONTH),
                day.get(Calendar.DATE));
        dayNormalized.set(Calendar.HOUR_OF_DAY, 0);
        dayNormalized.set(Calendar.MINUTE, 0);
        dayNormalized.set(Calendar.SECOND, 0);
        dayNormalized.set(Calendar.MILLISECOND, 0);

        return dayNormalized;
    }

    /**
     * if day1 is after day2 regardless of the time of the day
     *
     * @param day1
     * @param day2
     * @return true if day 1 is after day 2
     */
    public static boolean isAfter(Calendar day1, Calendar day2) {
        return !isBefore(day1, day2);
    }

}
