/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Luckyna
 */
import EJB.interfaces.CalendarManager;
import EJB.interfaces.SignUpManager;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Startup;
import javax.ejb.Singleton;
import javax.inject.Inject;


@Singleton
@Startup
public class StartupBean {

    @Inject
    private SignUpManager signupManager;
    @Inject
    private CalendarManager calendarManager;
    
    private ArrayList<UserModel> users = new ArrayList<>();
    

    //METHODS
    
    @PostConstruct
    public void init() {
        
        insertUsers();
        insertPublicCalendar();

    }

    @PreDestroy
    public void destroy() {
        /* Shutdown stuff here */
    }

    private void insertUsers() {
        String[] names = new String[]{"a", "b", "c","d", "umbo", "vale", "fra"};
        String[] surnames = new String[]{"a", "b","c","d","difa","cer","ang"};
        String[] emails = new String[]{"a@a", "b@b","c@c","d@d","umbo@asp","vale@figa","fra@ang"};
        String[] pswds = new String[]{"a", "b","c","d","umbo","vale", "fra"};
        for (int i = 0; i < emails.length; i++) {
            UserModel newUser = new UserModel(names[i],surnames[i],emails[i],pswds[i]);
            signupManager.addUser(newUser);
            users.add(newUser);
        }
    }

    private void insertPublicCalendar() {
        for (UserModel user: users) {
        CalendarModel calendar = new CalendarModel("PublicCal", true, false);
        calendarManager.addCalendarToUser(user, calendar);
        }
    }
    
    private void insertEvents() {
        List<PrivateEvent> privateEvents = new ArrayList<>();
        
        
//public Event(String title, Calendar startDateTime, Calendar endDateTime, String location, String description, boolean isOutdoor, UserModel owner)
    }

    
    
}
