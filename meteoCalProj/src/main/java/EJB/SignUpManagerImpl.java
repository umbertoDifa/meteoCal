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
import model.CalendarModel;
import model.UserModel;

@Stateless
public class SignUpManagerImpl implements SignUpManager {

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Inject @Default
    private Logger logger;    
    
    @Inject
    private CalendarManager calManager;
    
    private CalendarModel defaultCalendar;

    @Override
    public boolean addUser(UserModel newUser) { 
        
        //creo un calendario default
        defaultCalendar = calManager.createDefaultCalendar(newUser);
        
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
