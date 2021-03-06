/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB;

import EJB.interfaces.LoginManager;
import EJB.interfaces.SettingManager;
import bakingBeans.CredentialsBacking;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.UserModel;
import wrappingObjects.UserAndMessage;
import utility.ControlMessages;
import utility.LoggerLevel;
import utility.LoggerProducer;
import utility.PasswordTool;

@Stateless
public class LoginManagerImpl implements LoginManager {

    @Inject
    SettingManager settingManager;

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    private static final Logger logger = LoggerProducer.debugLogger(LoginManagerImpl.class);

    private UserAndMessage userAndMessage;

    @PostConstruct
    private void init() {
        userAndMessage = new UserAndMessage();
    }

    @Override
    public UserAndMessage findUser(CredentialsBacking credentials) {

        List<UserModel> results = database
                .createQuery(
                        "select u from UserModel u where u.email=:email")
                .setParameter("email", credentials.getEmail()).getResultList();

        //query per cercare un utente preciso
        if (results.isEmpty()) {
            userAndMessage.setUser(null);
            userAndMessage.setControlMessage(ControlMessages.USER_NOT_FOUND);
        } else if (results.size() > 1) {
            userAndMessage.setUser(null);
            userAndMessage.setControlMessage(
                    ControlMessages.MULTIPLE_USERS_FOUND);
            logger.log(Level.SEVERE,
                    "There are multiple users corresponding to a single id.Database is corrupted.");
        } else {
            //verifico la password
            logger.log(LoggerLevel.DEBUG, "utente trovato:"
                    + ((UserModel) results.get(0)).getEmail());
            try {
                if (PasswordTool.check(credentials.getPassword(),
                        results.get(0).getPassword())) {
                    logger.log(LoggerLevel.DEBUG, "password ok");
                    userAndMessage.setUser(results.get(0));
                    database.refresh(results.get(0));
                    userAndMessage.setControlMessage(
                            ControlMessages.LOGIN_SUCCESSFUL);
                } else {
                    //se password sbagliata, scrivo l'errore e ritorno null
                    userAndMessage.setUser(null);
                    userAndMessage.setControlMessage(
                            ControlMessages.WRONG_PASSWORD);
                }
            } catch (Exception ex) {
                userAndMessage.setUser(null);
                userAndMessage.setControlMessage(
                        ControlMessages.WRONG_PASSWORD);
                logger.log(LoggerLevel.SEVERE,
                        "Something whent wrong checking the password.");
            }
        }

        return userAndMessage;
    }
}
