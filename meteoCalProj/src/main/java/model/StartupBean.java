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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Startup;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@Startup
public class StartupBean {

    @Inject
    private SignUpManager signupManager;
    @Inject
    private CalendarManager calendarManager;
    
    private ArrayList<UserModel> users = new ArrayList<UserModel>();
    

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
        String[] names = new String[]{"a", "b", "umbo"};
        String[] surnames = new String[]{"a", "b", "difa"};
        String[] emails = new String[]{"a@a", "b@b", "umbo@asp"};
        String[] pswds = new String[]{"a", "b", "umbo"};
        for (int i = 0; i < emails.length; i++) {
            UserModel newUser = new UserModel();
            newUser.setName(names[i]);
            newUser.setSurname(surnames[i]);
            newUser.setEmail(emails[i]);
            newUser.setPassword(pswds[i]);
            signupManager.addUser(newUser);
            users.add(newUser);
        }
    }

    private void insertPublicCalendar() {
        for (UserModel user: users) {
        CalendarModel calendar = new CalendarModel();
        calendar.setIsDefault(false);
        calendar.setIsPublic(true);
        calendar.setTitle("Pubblic_Cal");
        calendarManager.addCalendarToUser(user, calendar);
        }

    }

}
