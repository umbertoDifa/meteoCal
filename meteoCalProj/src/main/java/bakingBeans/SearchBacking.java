/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.SearchManager;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import model.Event;
import model.UserModel;
import utility.SearchResult;

/**
 *
 * @author Francesco
 */
@Named(value = "search")
@RequestScoped
public class SearchBacking {

    private String searchKey;
    private List<SearchResult> results;
    private boolean searchForUsers;
    private boolean searchForEvents;

    @Inject
    private SearchManager searchManager;

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

    public List<SearchResult> getResults() {
        return results;
    }

    public void setResults(List<SearchResult> results) {
        this.results = results;
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

        if (searchForUsers) {
            for (UserModel u : users) {
                results.add(new SearchResult(u.getName() + u.getSurname(), u.getEmail(), u.getAvatarPath()));
            }
        }

        if (searchForEvents) {
            for (Event ev : events) {
                results.add(new SearchResult(ev.getTitle(), ev.getDescription(), ev.getImgPath()));
            }
        }

    }

}
