package utility;

import java.util.logging.Level;

/**
 *
 * @author umboDifa
 */
public class LoggerLevel extends Level {

    public static final Level DEBUG = new LoggerLevel("DEBUG", Level.INFO.intValue() - 1);

    public LoggerLevel(String name, int value) {
        super(name, value);
    }

}
