/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.SignUpManager;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import model.User;

/**
 *
 * @author Francesco
 */
@Named(value = "singUp")
@RequestScoped
public class SignUpBacking {

    @Inject
    private TempUserBacking tempUser;

    private User user;

    @Inject
    private SignUpManager signUpManager;

    /**
     * Creates a new instance of SingUp
     */
    public SignUpBacking() {
    }

    public String SignUp() {
        user = new User();
        //setto user coi parametri di tempUser, MANCANO SETTER IN USER
        if (signUpManager.addUser(user)) {
            return "success";
        }
        return "";
    }

}
