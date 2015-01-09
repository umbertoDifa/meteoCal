/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.SearchManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import model.Event;
import model.UserModel;
import utility.LoggerLevel;
import utility.SearchResult;

/**
 *
 * @author Francesco
 */
@Named(value = "search")
@RequestScoped
public class SearchBacking {

    private String searchKey;
    private List<SearchResult> eventResults = new ArrayList<>();
    private List<SearchResult> userResults = new ArrayList<>();
    private boolean searchForUsers;
    private boolean searchForEvents;

    @Inject
    private SearchManager searchManager;

    @Inject
    Logger logger;

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

    public boolean isSearchForUsers() {
        return searchForUsers;
    }

    public void setSearchForUsers(boolean searchForUsers) {
        this.searchForUsers = searchForUsers;
    }

    public boolean isSearchForEvents() {
        return searchForEvents;
    }

    public void setSearchForEvents(boolean searchForEvents) {
        this.searchForEvents = searchForEvents;
    }

    public List<SearchResult> getEventResults() {
        return eventResults;
    }

    public void setEventResults(List<SearchResult> eventResults) {
        this.eventResults = eventResults;
    }

    public List<SearchResult> getUserResults() {
        return userResults;
    }

    public void setUserResults(List<SearchResult> userResults) {
        this.userResults = userResults;
    }

    /*
     * METHODS
     */
    /**
     * Creates a new instance of SearchBacking
     */
    public SearchBacking() {
    }

    public void doSearch() {
        List<UserModel> users = searchManager.searchUsers(searchKey);
        List<Event> events = searchManager.searchEvents(searchKey);

        if (searchForUsers || (!searchForEvents && !searchForUsers)) {
            for (UserModel u : users) {
                userResults.add(new SearchResult(u.getName() + u.getSurname(), u.getEmail(), u.getAvatarPath(), String.valueOf(u.getId())));
            }
        }

        if (searchForEvents || (!searchForEvents && !searchForUsers)) {
            for (Event ev : events) {
                eventResults.add(new SearchResult(ev.getTitle(), ev.getDescription(), ev.getImgPath(), String.valueOf(ev.getId())));
            }
        }

        logger.log(LoggerLevel.DEBUG, "{0} eventi trovati sono :{1}", new Object[]{eventResults.size(), eventResults});
        logger.log(LoggerLevel.DEBUG, "{0} utenti trovati:{1}", new Object[]{userResults.size(), userResults});

    }

}
