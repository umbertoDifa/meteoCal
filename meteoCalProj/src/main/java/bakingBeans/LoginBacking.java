package bakingBeans;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import EJB.interfaces.LoginManager;
import model.UserModel;
import javax.inject.Named;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import wrappingObjects.UserAndMessage;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Umberto
 */
@Named(value = "login")
@SessionScoped
public class LoginBacking implements Serializable {

    /**
     * Creates a new instance of Login
     */
    public LoginBacking() {
    }

    private static final long serialVersionUID = 7965455427888195913L;

    @Inject
    private CredentialsBacking credentials;

    @Inject
    private LoginManager userManager;

    private UserModel currentUser;

    public String login() {

        UserAndMessage userAndMessage = userManager.findUser(credentials);

        if (userAndMessage.getUser() != null) {
            this.currentUser = userAndMessage.getUser();
            return "/s/myCalendar.xhtml?faces-redirect=true";
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", userAndMessage.getControlMessage().getMessage()));
            RequestContext.getCurrentInstance().update("growl");
            return "/";
        }
        
    }

    public String logout() {

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Goodbye, " + currentUser.getName()));

        currentUser = null;
        return "/signUp.xhtml?faces-redirect=true";

    }

    public boolean isLoggedIn() {
        return currentUser != null;

    }

    @Produces
    public UserModel getCurrentUser() {
        return currentUser;
    }

    
    public String getEmail(){
        return currentUser.getEmail();
    }
    
    public String getName(){
        return currentUser.getName();
    }
    
    public String getSurname(){
        return currentUser.getSurname();
    }
}
