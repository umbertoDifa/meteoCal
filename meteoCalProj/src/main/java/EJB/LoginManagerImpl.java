/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB;

import EJB.interfaces.LoginManager;
import bakingBeans.Credentials;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.User;
import utility.ControlMessages;

@Stateless
public class LoginManagerImpl implements LoginManager {

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;
    private LinkedList<ControlMessages> errorMessageQueue;

    @PostConstruct
    private void init() {
        errorMessageQueue = new LinkedList<>();
    }

    @Override
    public User findUser(Credentials credentials) {

        List<User> results = database
                .createQuery(
                        "select u from User u where u.username=:username and u.password=:password")
                .setParameter("username", credentials.getEmail())
                .setParameter("password", credentials.getPassword()).getResultList();

        //query per cercare un utente preciso
        if (results.isEmpty()) {
            errorMessageQueue.add(ControlMessages.USER_NOT_FOUND);
            return null;

        } else if (results.size() > 1) {
            throw new IllegalStateException(
                    //TODO, questa??
                    "Cannot have more than one user with the same username!");
        }

        //verifico la password
        if (results.get(0).getPassword().equals(credentials.getPassword())) {
            return results.get(0);
        }

        //se password sbagliata, scrivo l'errore e ritorno null
        errorMessageQueue.add(ControlMessages.WRONG_PASSWORD);

        return null;
    }

    @Override
    public ControlMessages getLastError() {
        return errorMessageQueue.pollFirst();
    }

}
