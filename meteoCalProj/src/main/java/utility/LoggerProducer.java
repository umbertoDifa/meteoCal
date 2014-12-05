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

    @Produces @Default
    private Logger createLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

}
