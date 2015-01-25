/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.SearchManager;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import model.CalendarModel;
import model.Event;
import model.PublicEvent;
import model.UserModel;
import utility.LoggerLevel;
import utility.LoggerProducer;

/**
 *
 * @author Francesco
 */
@Named(value = "search")
@ViewScoped
public class SearchBacking implements Serializable {

    private String searchKey;
    private List<Event> eventResults = new ArrayList<>();
    private List<UserModel> userResults = new ArrayList<>();


    @Inject
    private SearchManager searchManager;

    private static final Logger logger = LoggerProducer.debugLogger(SearchBacking.class);

    @Inject
    LoginBacking login;

    private List<UserModel> users;
    private List<Event> events;

    /*
     * 
     * SETTERS & GETTERS
     * 
     */
    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }


    public List<Event> getEventResults() {
        System.out.println("---list: " + eventResults);
        return eventResults;
    }

    public void setEventResults(List<Event> eventResults) {
        this.eventResults = eventResults;
    }

    public List<UserModel> getUserResults() {
        return userResults;
    }

    public void setUserResults(List<UserModel> userResults) {
        this.userResults = userResults;
    }

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /*
     * METHODS
     */
    /**
     * Creates a new instance of SearchBacking
     */
    public SearchBacking() {
    }

    public void redirect() {
        logger.log(LoggerLevel.DEBUG, "-dentro redirect, searchKey vale:" + searchKey);
        //redirect
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        try {
            context.redirect(context.getRequestContextPath()
                    + "/s/search.xhtml?query=" + searchKey
                    + "&&faces-redirect=true");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void doSearch() {
        users = searchManager.searchUsers(searchKey);
        events = searchManager.searchEvents(searchKey);

            for (UserModel u : users) {
                boolean add = false;
                List<CalendarModel> list = u.getOwnedCalendars();
                for (CalendarModel c : list) {
                    if (c.isIsPublic()) {
                        add = true;
                    }
                }
                if (add == true) {
                    userResults.add(u);
                }
            }
        

            for (Event ev : events) {
                if ((ev instanceof PublicEvent) || (ev.getInvitee().contains(login.getCurrentUser()) || ev.getOwner().equals(login.getCurrentUser()))) {
                    eventResults.add(ev);
                }
            
        }

        logger.log(LoggerLevel.DEBUG, "{0} eventi trovati sono :{1}", new Object[]{eventResults.size(), eventResults});
        logger.log(LoggerLevel.DEBUG, "{0} utenti trovati:{1}", new Object[]{userResults.size(), userResults});

    }

}
