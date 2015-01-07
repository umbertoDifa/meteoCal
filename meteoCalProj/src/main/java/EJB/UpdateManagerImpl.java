package EJB;

import EJB.interfaces.WeatherManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import model.Event;
import model.PrivateEvent;
import utility.EntityManagerHelper;
import utility.LoggerLevel;
import utility.LoggerProducer;

@Singleton
@Startup
public class UpdateManagerImpl {

    @Inject
    WeatherManager weatherManager;

    @Resource//TODO check if needed
    TimerService timerService;

    private Logger logger = LoggerProducer.debugLogger(UpdateManagerImpl.class);

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(
            5);

    private static Queue<Event> eventsToCreateUpdater;

    @PostConstruct
    private void init() {
        eventsToCreateUpdater = new LinkedList<>();
        //per cancellare timer rimasti attivi nelle prove
//        for (Object obj : timerService.getTimers()) {
//            Timer t = (Timer) obj;
//            logger.log(LoggerLevel.DEBUG, "Timer cancellato: " + t.toString());
//            t.cancel();
//        }
    }

    private final static int PERIOD = 12;

    //TODO
    //all'avvio bisognerebbe prendere tutti gli eventi nel db
    //e metterli in lista perchè gli venga attacato un updater
    //ammenocchè il timer non sia persistent=true
    /**
     * Constructor for container
     */
    public UpdateManagerImpl() {

    }

    /**
     * Con questo metodo è possibile aggiungere un evento per cui creare un
     * updater
     *
     * @param event
     */
    public static void addEvent(Event event) {
        eventsToCreateUpdater.add(event);
    }

    //every six hours events are checked to see if there is any that needs
    //an updater to be created
    @Schedule(hour = "*/6", minute = "*", second = "*", persistent = false) //TODO fixme sia l'ora che il persistent da discutere
    private void refreshEventToUpdate() {
        //while there is any element in the queue
        while (!eventsToCreateUpdater.isEmpty()) {
            //schedule an updater for that event
            this.scheduleUpdates(eventsToCreateUpdater.poll());
        }

    }

    /**
     * schedule weather updater for an event
     *
     * @param event event for which schedule the updates
     * @return a scheduledFuture which is the result of the update thread, null
     * if it wasn't possible to create the updater
     */
    private ScheduledFuture<?> scheduleUpdates(Event event) {

        try {
            Updater updater = new Updater(event, weatherManager);
            logger.log(LoggerLevel.DEBUG, "Created updater for event {0}",
                    event.getTitle());
            ScheduledFuture<?> res = updater.runTillPast(executor, PERIOD,
                    TimeUnit.HOURS);//TODO fixme
            return res;
            //periodicamente ogni 12 ore                
            //il giorno prima notificando se è brutto(inlcuso nella notifica ogni 12 ore)
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

    private class Updater implements Runnable {

        private WeatherManager weatherManager;

        private EntityManager database;

        private Event event;

        //volatile perchè può essere modificato da thread diversi (i.e. questo e l'executor)
        private volatile ScheduledFuture<?> self;

        @Override
        public void run() {
            this.event = database.find(Event.class, event.getId());

            //refresho l'evento nel caso la data di fine sia cambiata
            database.refresh(event);

            //se oggi è prima della fine dell'evento
            if (Calendar.getInstance().before(event.getEndDateTime())) {
                //lo aggiorno
                logger.log(LoggerLevel.DEBUG,
                        "Update weather fired for event {0}",
                        event.getTitle());
                weatherManager.updateWeather(event);
            } else {
                //l'evento è passato elimino il thread di update
                logger.log(LoggerLevel.DEBUG, "Updater terminated for event{0}",
                        event.getTitle());
                self.cancel(true);
            }

        }

        public Updater(Event event, WeatherManager wm) {
            this.database = EntityManagerHelper.getEntityManager();
            this.weatherManager = wm;

            //sincronizzo l'evento col database
            if (event == null) {
                throw new IllegalArgumentException(
                        "The event is null. Abort weather updates.");
            } else {
                this.event = database.find(Event.class, event.getId());
                if (this.event == null) {
                    throw new IllegalArgumentException(
                            "The event is not in the db. Abort weather updates.");
                }
            }
        }

        public ScheduledFuture<?> runTillPast(ScheduledExecutorService executor, long period, TimeUnit unit) {
            self = executor.scheduleAtFixedRate(this, 0, period, unit);
            return self;
        }

    }
}
