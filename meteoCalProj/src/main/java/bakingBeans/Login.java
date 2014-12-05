package bakingBeans;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import EJB.interfaces.LoginManager;
import model.User;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author Umberto
 */
@Named(value = "login")
@SessionScoped
public class Login implements Serializable {

    /**
     * Creates a new instance of Login
     */
    public Login() {
    }

    private static final long serialVersionUID = 7965455427888195913L;

    @Inject
    private Credentials credentials;

    @Inject
    private LoginManager userManager;

    private User currentUser;

    public void login() throws Exception {

        User user = userManager.findUser(credentials.getEmail(), credentials.getPassword());

        if (user != null) {

            this.currentUser = user;
//          FacesContext.getCurrentInstance().addMessage(null,
//                  new FacesMessage("Welcome, " + currentUser.getName()));
        }else {
//            FacesContext.getCurrentInstance().addMessage(new FacesMessage(userManager.getLastError().getMessage()));
            
        }

    }

    public void logout() {

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Goodbye, " + currentUser.getName()));

        currentUser = null;

    }

    public boolean isLoggedIn() {
        return currentUser != null;

    }

    @Produces
//    @LoggedIn
    public User getCurrentUser() {

        return currentUser;

    }

}
