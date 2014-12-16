/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.text.MessageFormat;
import java.util.logging.Logger;
import utility.LoggerLevel;
import utility.LoggerProducer;

/**
 *
 * @author Luckyna
 */
public enum NotificationType {

    INVITATION("Invitation to event {0}",
            "Hello {0},\n you've received an invitation for event {1} from {2}.\n "
            + "Have a look to the event page on the following link:\n{3}\nMeteoCalendarTeam"), //    WEATHER_CHANGED,
    EVENT_CHANGED("Event {0} has been modified",
            "Hello {0},\n we inform you that event {1} has been modified\nLink:{3}"),
    EVENT_CANCELLED("Event {0} has been cancelled",
            "Hello {0},\n we inform you that event {1} has been cancelled\n"),
    EVENT_PUBLIC("Event {0} has changed privacy from Private to Public",
            "Hello {0},\n we inform you that event {1} has changed its privacy from Private to Public.\n");

    private String subject;
    private String bodyMessage;
    private String eventOwner;
    private String inviteeName;
    private String eventName;
    private String link;

    Object[] subjParams;
    Object[] bodyParams;

    private Logger logger = LoggerProducer.debugLogger(NotificationType.class);

    private NotificationType(String subject, String body) {
        this.subject = subject;
        this.bodyMessage = body;
    }

    public NotificationType setEventOwner(String eventOwner) {
        this.eventOwner = eventOwner;
        return this;
    }

    public NotificationType setInviteeName(String inviteeName) {
        this.inviteeName = inviteeName;
        return this;
    }

    public NotificationType setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public String getBodyMessage() {
        return bodyMessage;
    }

    public NotificationType buildEmail() {
        link = "llink temporaneo";

        subjParams = new Object[]{eventName};
        bodyParams = new Object[]{inviteeName, eventName, eventOwner, link};

        subject = MessageFormat.format(subject, subjParams);
        bodyMessage = MessageFormat.format(bodyMessage, bodyParams);
        logger.log(LoggerLevel.DEBUG,
                "Email formattata:\nsubject: " + subject + "\nbody: "
                + bodyMessage);
        return this;
    }

}
