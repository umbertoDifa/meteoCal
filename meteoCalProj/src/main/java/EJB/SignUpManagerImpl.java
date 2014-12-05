/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB;

import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.User;

public class SignUpManagerImpl implements SignUpManager {

    @PersistenceContext(unitName = "meteoCalDB")
    EntityManager database;
    
    private User newUser = new User();

    @Produces
    @Named(value ="users")
    @RequestScoped
    @Override
    public List<User> getUsers() {
        return database.createQuery("select u from User u").getResultList();

    }

    @Override
    public boolean addUser() {
        database.persist(newUser);
        
        //TODO: fix this
        return true;
    }
    
    @Produces
    @RequestScoped
    @Named
    @Override
    public User getNewUser() {
        return newUser;
    }

}
