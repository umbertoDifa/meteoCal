/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.SignUpManager;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import model.UserModel;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Francesco
 */
@Named(value = "signUp")
@RequestScoped
public class SignUpBacking {

    @Inject
    private TempUserBacking tempUser;

    private UserModel user;

    @Inject
    private SignUpManager signUpManager;

    /**
     * Creates a new instance of SingUp
     */
    public SignUpBacking() {
    }

    public String signUp() {
        //creo un user e setto user coi parametri di tempUser
        buildUser();
        if (signUpManager.addUser(user)) {
            return "success";
        }
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error",
                "You are already registered"));
        RequestContext.getCurrentInstance().update("growl");
        return "/";
    }

    private void buildUser() {
        user = new UserModel();
        user.setEmail(tempUser.getEmail());
        user.setName(tempUser.getName());
        user.setSurname(tempUser.getSurname());
        user.setPassword(tempUser.getPassword());
    }

}
