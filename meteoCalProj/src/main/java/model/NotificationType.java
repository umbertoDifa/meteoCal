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

    INVITATION("Invito all''evento {0}",
            "Ciao {0},\n hai ricevuto un invito per l''evento {1} da {2}.\n "
            + "Guarda la pagina dell''evento al seguente link:\n{3}\nMeteoCalendarTeam"), //    WEATHER_CHANGED,
    EVENT_CHANGED("L''evento {0} è stato modficato",
            "Ciao {0},\n ti informiamo che l''evento {1} è stato modificato.\nLink:{3}"),
    EVENT_CANCELLED("L''evento {0} è stato cancellato",
            "Ciao {0},\n ti informiamo che l''evento {1} è stato cancellato.\n");

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
