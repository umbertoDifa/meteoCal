/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.NotificationManager;
import EJB.interfaces.SignUpManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import model.CalendarModel;
import model.UserModel;
import utility.LoggerLevel;
import utility.PasswordTool;

@Stateless
public class SignUpManagerImpl implements SignUpManager {

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Inject @Default
    private Logger logger;

    @Inject
    private CalendarManager calManager;

    @Inject
    private NotificationManager notificationManager;

    private CalendarModel defaultCalendar;

    private Pattern userNamePattern;
    private Pattern passwordPattern;
    private Pattern emailPattern;
    private Matcher matcher;

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]{3,15}$";
    private static final String PASSWORD_PATTERN = "^[a-zA-Z0-9]{6,20}$";
    private static final String EMAIL_PATTERN = "^[\\w\\.-]*[a-zA-Z0-9_]@[\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";

    @PostConstruct
    private void init() {
        userNamePattern = Pattern.compile(USERNAME_PATTERN);
        passwordPattern = Pattern.compile(PASSWORD_PATTERN);
        emailPattern = Pattern.compile(EMAIL_PATTERN);
    }

    @Override
    public boolean addUser(UserModel newUser) {

        if (validate(newUser)) {

            setUpHashedPassword(newUser);
            //creo un calendario default
            defaultCalendar = calManager.createDefaultCalendar(newUser);

            try {
                //cerco di persistere calendario e utente
                database.persist(defaultCalendar);
                database.persist(newUser);
                database.flush();
                logger.log(Level.INFO, "User +{0} created", newUser.getName());

                //inivio la notifca di registrazione con successo
                notificationManager.sendSignUpConfirmation(newUser);
            } catch (EntityExistsException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                return false;
            }
            return true;
        }
        logger.log(LoggerLevel.DEBUG, "SOmething do not match patteerns.");
        return false;
    }

    /**
     * Check if name and password respect regex
     *
     * @param newUser
     * @return
     */
    private boolean validate(UserModel newUser) {

        boolean u = userNamePattern.matcher(newUser.getName()).matches();
        boolean p = passwordPattern.matcher(newUser.getPassword()).matches();
        boolean e = emailPattern.matcher(newUser.getEmail()).matches();
        logger.log(LoggerLevel.DEBUG,
                "u:" + u + " p:" + p + " e:" + e);
        return u && p && e && !isRegisterAlready(newUser);

    }

    private void setUpHashedPassword(UserModel newUser) {
        try {
            newUser.setPassword(
                    PasswordTool.getSaltedHash(newUser.getPassword()));
        } catch (Exception ex) {
            logger.log(LoggerLevel.SEVERE,
                    "Something went wrong hashing the password");
        }
    }

    private boolean isRegisterAlready(UserModel newUser) {
        try {
            UserModel alreadyUser = (UserModel) database.createNamedQuery(
                    "findUserbyEmail").setParameter(
                            "email", newUser.getEmail()).getSingleResult();
            //se ho trovato un utente
            return alreadyUser != null;
        } catch (NoResultException ex) {
            //se non c'è un altro utente con la stessa email
            return false;
        } catch (NonUniqueResultException ex) {
            return true;
        } catch (Exception ex) {
            return true;
        }
    }

}
