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

    /**
     * SE AGGIUNGETE ENTITA' A QUESTA CLASSE, DOVETE AGGIUNGERLA ANCHE ALL'ENUM
     * DEL DATABASE CHE TROVATE NELL'ENTITA' NOTIFICATION NEL CAMPO TYPE
     */
    /**
     * SE AGGIUNGETE ENTITA' A QUESTA CLASSE, DOVETE AGGIUNGERLA ANCHE ALL'ENUM
     * DEL DATABASE CHE TROVATE NELL'ENTITA' NOTIFICATION NEL CAMPO TYPE
     */
    INVITATION("Invitation to event {0}",
            "Hello {0},\n you''ve received an invitation for event {1} from {2}.\n "
            + "Have a look to the event page on the following link:\n{3}\nMeteoCalendarTeam"),
    WEATHER_CHANGED("Weather changed for event {0}",
            "Hello {0},\n we inform you that the weather for"
            + "the event {1} has changed. Check it at:\nLink:{3}"),
    BAD_WEATHER_TOMORROW("Bad weather forecast for tomorrow event {0}",
            "Hello {0},\nwe are sorry to inform you that the weather forecast for"
            + "the tomorrow event {1} is bad.\nLink:{3}"),
    BAD_WEATHER_IN_THREE_DAYS("Bad weather forecast for event {0}",
            "Hello {0},\nwe are sorry to inform you that the weather forecast for"
            + "the event {1} is bad. If you want you can reschedule your event.\nLink:{3}"),
    EVENT_CHANGED("Event {0} has been modified",
            "Hello {0},\n we inform you that event {1} has been modified\nLink:{3}"),
    EVENT_CANCELLED("Event {0} has been cancelled",
            "Hello {0},\n we inform you that event {1} has been cancelled\n"),
    EVENT_CHANGED_TO_PUBLIC(
            "Event {0} has changed privacy from Private to Public",
            "Hello {0},\n we inform you that event {1} has changed its privacy from Private to Public.\nLink:{3}"),
    EVENT_CHANGED_TO_PRIVATE(
            "Event {0} has changed privacy from Public to Private",
            "Hello {0},\n we inform you that event {1} has changed its privacy from Public to Private.\n");

    private String subject;
    private String bodyMessage;
    private String eventOwner;
    private String inviteeName;
    private String eventName;
    private String link;

    private final String STATIC_LINK = "localhost:8080/meteoCalProj/s/eventPage.xhtml?id=";

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

    public NotificationType setLink(Long id) {
        this.link = STATIC_LINK + id;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public String getBodyMessage() {
        return bodyMessage;
    }

    public NotificationType buildEmail() {
        //link = "llink temporaneo";

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
