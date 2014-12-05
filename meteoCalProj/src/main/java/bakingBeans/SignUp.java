/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import model.User;

/**
 *
 * @author Francesco
 */
@Named(value = "singUp")
@RequestScoped
public class SignUp {

    private User tempUser;
    
    /**
     * Creates a new instance of SingUp
     */
    public SignUp() {
    }
    
}
