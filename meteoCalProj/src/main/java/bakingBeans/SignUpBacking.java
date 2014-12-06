/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.SignUpManager;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import model.UserModel;

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
        return "";
    }
    
    private void buildUser(){
        user = new UserModel();
        user.setEmail(tempUser.getEmail());
        user.setName(tempUser.getName());
        user.setSurname(tempUser.getSurname());
        user.setPassword(tempUser.getPassword());
    }

}
