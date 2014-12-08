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

@Singleton
@Startup
public class StartupBean {

    @Inject
    private SignUpManager signupManager;
    @Inject
    private EntityManager database;
    @Inject
    private Logger logger;

    @PostConstruct
    public void init() {
        insertUsers();
        insertOptCalendar();

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
        }
    }

    private void insertOptCalendar() {
        CalendarModel calendar = new CalendarModel();
        calendar.setIsDefault(false);
        calendar.setIsPublic(true);
        UserModel user1 = database.find(UserModel.class, 1);
        calendar.setOwner(user1);
        calendar.setTitle("Pubblic_Cal");

        logger.log(Level.INFO, "Pulic_Cal created for user:", user1.getEmail());
        database.persist(calendar);
        logger.log(Level.INFO, "User +{0} created", user1.getName());
    }

}
