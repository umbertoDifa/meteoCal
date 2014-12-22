package EJB.interfaces;

import java.util.concurrent.ScheduledFuture;
import model.Event;

/**
 *
 * @author umboDifa
 */
public interface UpdateManager {

    public ScheduledFuture<?> scheduleUpdates(Event event);

}
