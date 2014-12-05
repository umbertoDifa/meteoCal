/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB;

import bakingBeans.Credentials;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.User;

@Stateless
public class SignUpManagerImpl implements SignUpManager {

    @PersistenceContext(unitName = "meteoCalDB")
    EntityManager database;

    @Inject @Default
    Logger logger;

    @Override
    public boolean addUser(Credentials credential) {
        User newUser = new User();
        newUser.setUsername(credential.getUsername());
        newUser.setPassword(credential.getPassword());
        
        try {
            database.persist(newUser);
            logger.log(Level.INFO, "User +{0} created", newUser.getUsername());
        } catch (EntityExistsException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
        return true;
    }
}
