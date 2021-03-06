/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

/**
 *
 * @author Francesco
 */
public enum ControlMessages {

    USER_NOT_FOUND(0, "Incorrect Username"),
    WRONG_PASSWORD(1, "Incorrect Password"),
    EVENT_ALREADY_IN_CALENDARS(2, "The event is already in a user calendar"),
    EVENT_ADDED(3, "The event has been added to the user calendar"),
    LOGIN_SUCCESSFUL(4, "The user has been logged in"),
    ERROR_ADDING_EVENT_TO_CAL(5, "The event cannot be added to the calendar"),
    NO_USER_WITH_CALENDAR(6,
            "The user doesn't own calendar with the requested name"),
    BAD_WEATHER_FORECAST(7, "The weather for the event day is bad."),
    CALENDAR_CONFLICTS(8,
            "There are events already scheduled for the selected date in some of your calendars."),
    NO_PROBLEM(9,
            "The weather is ok and there are no conflicts for the selected timeslots."),
    MULTIPLE_USERS_FOUND(10,"There are multiple user correspoding to an ID");

    //static?
    private final int index;
    private final String message;

    ControlMessages(int index, String message) {
        this.index = index;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getIndex() {
        return index;
    }

}
