/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.NotificationManager;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import model.Notification;
import utility.NotificationCategories;

/**
 *
 * @author Luckyna
 */
@Named(value = "notification")
@RequestScoped
public class NotificationBacking implements Serializable{

    @Inject
    private NotificationManager notificationManager;

    final LoginBacking login;

    private List<Notification> invitations;
    private List<Notification> eventChanges;

    /*
     *
     *   CONSTRUCTORS
     */
    public NotificationBacking() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        login = (LoginBacking) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{login}", LoginBacking.class);

    }
    
    @PostConstruct
    public void init() {
   
        //riempio le tab
        invitations = notificationManager.getNotificationFiltred(login.getCurrentUser(), NotificationCategories.INVITATIONS);
        eventChanges = notificationManager.getNotificationFiltred(login.getCurrentUser(), NotificationCategories.EVENTS_CHANGES);
        
        for (Notification in: invitations) {
        System.out.println(in.getType().getSubject());
         System.out.println(in.getType().getBodyMessage());
        }
    }

    @PreDestroy
    public void markNotificationAsRead() {
        
        System.out.println("NOTIFICATION BEAN STA PER MORIRE");
        
        notificationManager.markAllAsRead(invitations);
        notificationManager.markAllAsRead(eventChanges);
    }
    /*
     *
     *   SETTERS & GETTERS
     */
    public List<Notification> getInvitations() {
        return invitations;
    }

    public List<Notification> getEventChanges() {
        return eventChanges;
    }

    /*
     *
     *   METHODS
     */
    
    public String redirectToEvent(Notification notification) {
        return "/s/eventPage.xhtml?id="+notification.getRelatedEvent().getId();
    }
    

}
