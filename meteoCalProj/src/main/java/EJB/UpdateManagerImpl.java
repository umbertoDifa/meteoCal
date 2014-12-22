package EJB;

import EJB.interfaces.UpdateManager;
import EJB.interfaces.WeatherManager;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Event;
import utility.LoggerLevel;
import utility.LoggerProducer;

@Singleton
@Startup
public class UpdateManagerImpl implements UpdateManager {

    //TODO: megaTest su tutta la classe da fare
    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    private Logger logger = LoggerProducer.debugLogger(UpdateManagerImpl.class);

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(
            5);

    private final int PERIOD = 12;

    @Override
    public void scheduleUpdates(Event event) {

        Updater updater = new Updater(event);
        updater.runTillPast(executor, PERIOD, TimeUnit.HOURS);
        //periodicamente ogni 12 ore                
        //il giorno prima notificando se è brutto(inlcuso nella notifica ogni 12 ore)
    }

    private class Updater implements Runnable {

        @Inject
        WeatherManager weatherManager;

        private Event event;

        //volatile perchè può essere modificato da thread diversi (i.e. questo e l'executor)
        private volatile ScheduledFuture<?> self;

        @Override
        public void run() {
            //refresho l'evento nel caso la data di fine sia cambiata
            database.refresh(event);
            
            //se oggi è prima della fine dell'evento
            if (Calendar.getInstance().before(event.getEndDateTime())) {
                //lo aggiorno
                weatherManager.updateWeather(event);
            } else {
                //l'evento è passato elimino il thread di update
                logger.log(LoggerLevel.DEBUG, "Canecello il task");
                self.cancel(true);
            }

        }

        /**
         * Creo un updater di un evento
         *
         * @param event evento su cui fare update periodico
         */
        public Updater(Event event) {
            //sincronizzo l'evento col database
            this.event = database.find(Event.class, event.getId());
            if (this.event == null) {
                logger.log(Level.SEVERE,
                        "The event is not in the db. Abort weather updates.");
                self.cancel(true);
            }
        }

        public ScheduledFuture<?> runTillPast(ScheduledExecutorService executor, long period, TimeUnit unit) {
            self = executor.scheduleAtFixedRate(this, 0, period, unit);
            return self;
        }

    }
}
