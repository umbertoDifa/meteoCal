/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.SignUpManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Calendar;
import model.User;

@Stateless
public class SignUpManagerImpl implements SignUpManager {

    @PersistenceContext(unitName = "meteoCalDB")
    EntityManager database;

    @Inject @Default
    Logger logger;
    
    @Inject
    CalendarManager calManager;
    
    Calendar defaultCalendar;

    @Override
    public boolean addUser(User newUser) { 
            
        defaultCalendar = calManager.setToDefault(defaultCalendar, newUser);
        
        try {
            //cerco di persistere calendario e utente
            database.persist(defaultCalendar);
            database.persist(newUser);            
            logger.log(Level.INFO, "User +{0} created", newUser.getName());
        } catch (EntityExistsException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
        return true;
    }          
}
