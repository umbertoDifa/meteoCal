package utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author umboDifa
 */
public class TimeTool {

    public static String calendarToTextDay(Calendar day) {
        SimpleDateFormat df = new SimpleDateFormat();
        //df.applyPattern("yyyy-MM-dd hh:mm:ss");
        df.applyPattern("yyyy-MM-dd");

        return df.format(day.getTime());
    }

    public static String dateToTextDay(Date date) {
        SimpleDateFormat df = new SimpleDateFormat();
        df.applyPattern("yyyy-MM-dd");
        return df.format(date);

    }

    //TODO DELETEME
//    public static String unixTimeToHumanTime(String unixTime) {
//        long unixSeconds = Long.parseLong(unixTime);
//
//        Calendar cal = new GregorianCalendar();
//        cal.setTimeInMillis(unixSeconds * 1000L);// *1000 is to convert seconds to milliseconds
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // the format of your date        
//        return sdf.format(cal.getTime());
//    }

}
