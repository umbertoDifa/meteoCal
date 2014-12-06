/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB;

import EJB.interfaces.LoginManager;
import bakingBeans.CredentialsBacking;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.UserModel;
import objectAndString.UserAndMessage;
import utility.ControlMessages;

@Stateless
public class LoginManagerImpl implements LoginManager {

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

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
            userAndMessage.setMessage(ControlMessages.USER_NOT_FOUND);
        } else if (results.size() > 1) {
            throw new IllegalStateException(
                    //TODO, questa??
                    "Cannot have more than one user with the same username!");
        } else {
            //verifico la password
            if (results.get(0).getPassword().equals(credentials.getPassword())) {
                userAndMessage.setUser(results.get(0));
                userAndMessage.setMessage(ControlMessages.LOGIN_SUCCESSFUL);
            } else {
                //se password sbagliata, scrivo l'errore e ritorno null
                userAndMessage.setUser(null);
                userAndMessage.setMessage(ControlMessages.WRONG_PASSWORD);
            }
        }

        return userAndMessage;
    }
}
