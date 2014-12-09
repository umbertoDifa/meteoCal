package utility;

import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 *
 * @author umboDifa
 */
@Dependent
public class LoggerProducer {
    
    //NB. per spegnere il debug cambiare tutti i DEBUG in INFO
    @Produces
    @Default
    private Logger createLogger(InjectionPoint injectionPoint) {
        Logger logger = Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());

        logger.getParent().setLevel(LoggerLevel.DEBUG);
        logger.getParent().getHandlers()[0].setLevel(LoggerLevel.DEBUG);

        return logger;
    }

    public static Logger debugLogger(Class myClass) {
        Logger logger = Logger.getLogger(myClass.getName());
        logger.getParent().setLevel(LoggerLevel.DEBUG);
        logger.getParent().getHandlers()[0].setLevel(LoggerLevel.DEBUG);
        return logger;
    }

}
