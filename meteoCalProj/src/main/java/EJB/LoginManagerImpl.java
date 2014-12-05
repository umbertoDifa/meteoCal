/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.User;

@Stateless
public class LoginManagerImpl implements LoginManager {

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Override
    public User findUser(String username, String password) {

        List<User> results = database
                .createQuery(
                        "select u from User u where u.username=:username and u.password=:password")
                .setParameter("username", username)
                .setParameter("password", password).getResultList();

        if (results.isEmpty()) {
            return null;

        } else if (results.size() > 1) {
            throw new IllegalStateException(
                    
                    "Cannot have more than one user with the same username!");

        } else {
            return results.get(0);
        }
    }

}
