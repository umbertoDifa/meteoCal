/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

/**
 *
 * @author Luckyna
 */
public enum NotificationCategories {
    INVITATIONS("Invitations"),
    EVENTS_CHANGES("Events");
    
    private String title;

    private NotificationCategories(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    
    
}
