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
import utility.LoggerProducer;

@Stateless
public class LoginManagerImpl implements LoginManager {
    
    @Inject
    SettingManager settingManager;
    
    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;
    
    private Logger logger = LoggerProducer.debugLogger(LoginManagerImpl.class);
    
    private UserAndMessage userAndMessage;
    
    @PostConstruct
    private void init() {
        userAndMessage = new UserAndMessage();
    }
    
    @Override
    public UserAndMessage findUser(CredentialsBacking credentials) {
        
        List<UserModel> results = database
                .createQuery(
                        "select u from UserModel u where u.email=:email and u.password=:password")
                .setParameter("email", credentials.getEmail())
                .setParameter("password", credentials.getPassword()).getResultList();

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
            if (results.get(0).getPassword().equals(credentials.getPassword())) {
                userAndMessage.setUser(results.get(0));
                database.refresh(results.get(0));
                userAndMessage.setControlMessage(
                        ControlMessages.LOGIN_SUCCESSFUL);
                
                //cancello la cartella di export dell'utente ogni volta che questo fa il login
                settingManager.deleteExportFolder(userAndMessage.getUser());
            } else {
                //se password sbagliata, scrivo l'errore e ritorno null
                userAndMessage.setUser(null);
                userAndMessage.setControlMessage(ControlMessages.WRONG_PASSWORD);
            }
        }
        
        return userAndMessage;
    }
}
