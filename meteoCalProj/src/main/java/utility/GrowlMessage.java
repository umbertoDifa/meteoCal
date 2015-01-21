/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import javax.faces.application.FacesMessage;

/**
 *
 * @author Luckyna
 */
public enum GrowlMessage {
    GENERIC_ERROR ("Error", "An Error has occured", FacesMessage.SEVERITY_ERROR),
    ERROR_USER ("Error", "No user found", FacesMessage.SEVERITY_ERROR),
    NOT_DELETE_DEFAULT ("Cannot Delete Default Calendar", "You cannot delete the default calendar. Please make default another calenar and then remove this one.", FacesMessage.SEVERITY_ERROR),
    ERROR_REDIRECT ("Error", "Redirect has failed. An error has occured.", FacesMessage.SEVERITY_ERROR),
    CALENDAR_DELETED ("Calendar Deleted", "Your calendar has been successfully deleted", FacesMessage.SEVERITY_INFO),
    ERROR_DELETE ("Error", "Delete has failed. An error has occured", FacesMessage.SEVERITY_ERROR),
    DEFAULT_CHANGED ("Default Calendar Changed", "The current calendar has been successfully set as Default.", FacesMessage.SEVERITY_INFO),
    CALENDAR_SWITCHED_TO_PRIVATE ("Privacy Changed", "Current calendar is now private", FacesMessage.SEVERITY_INFO),
    CALENDAR_SWITCHED_TO_PUBLIC ("Privacy Changed", "Current calendar is now public", FacesMessage.SEVERITY_INFO),
    CALENDAR_CREATED("Calendar has been created", "The new calendar has been created", FacesMessage.SEVERITY_INFO),
    CALENDAR_EXISTS("Calendar already exists", "A calendar with the same name already exists!", FacesMessage.SEVERITY_WARN),
    EVENT_NOT_ADDED_TO_CALENDAR("Error adding the event", "Impossibile add the event to the selected calendar", FacesMessage.SEVERITY_WARN),
    EVENT_ADDED("Event added","The event has been correctly added to your calendar",FacesMessage.SEVERITY_INFO);
    
    private final String title;
    private final String message;
    private final FacesMessage.Severity severity;

    private GrowlMessage(String title, String message, FacesMessage.Severity severity) {
        this.title = title;
        this.message = message;
        this.severity = severity;
    }

    public FacesMessage.Severity getSeverity() {
        return severity;
    }


    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
    
    
    
}
