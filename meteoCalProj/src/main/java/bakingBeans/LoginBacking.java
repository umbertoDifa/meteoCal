package bakingBeans;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import EJB.interfaces.LoginManager;
import EJB.interfaces.NotificationManager;
import EJB.interfaces.SearchManager;
import java.io.IOException;
import model.UserModel;
import javax.inject.Named;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
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

    @Inject
    private SearchManager searchManager;

    @Inject
    private NotificationManager notificationManager;

    private UserModel currentUser;

    private int notificationNumber = 0;

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

    public void forceLogout() {
        logout();
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        try {
            context.redirect(context.getRequestContextPath()
                    + "/signUp.xhtml?faces-redirect=true");
        } catch (IOException ex) {

        }
    }

    public boolean isLoggedIn() {
        return currentUser != null;

    }

    @Produces
    public UserModel getCurrentUser() {
        return currentUser;
    }

    public String getEmail() {
        return currentUser.getEmail();
    }

    public String getName() {
        return currentUser.getName();
    }

    public String getSurname() {
        return currentUser.getSurname();
    }

    public String getNotificationNumber() {
        return String.valueOf(notificationNumber);
    }

    public void refreshCurrentUser() {
        currentUser = searchManager.findUserById(currentUser.getId());
    }

    public void updateNotification() {
        System.out.println("---- DENTRO UPDATE NOTIFICATION ----");
        notificationNumber = notificationManager.getUnreadNotificationNumber(currentUser);
    }

}
