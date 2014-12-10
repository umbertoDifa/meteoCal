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

    INVITATION("Invito per il nuovo evento {0}",
            "Ciao utente {0},\n hai ricevuto un invito per evento {1}.\n Guarda la pagina dellevento al seguente link: {2}. ."), //    WEATHER_CHANGED,
    //    EVENT_CHANGED,
    //    EVENT_CANCELLED
    ;

    private String subject;
    private String bodyMessage;
    private String eventOwner;
    private String inviteeName;
    private String eventName;
    private String link;
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
        Object[] subjParams = new Object[]{eventName};
        Object[] bodyParams = new Object[]{inviteeName, eventName, link};

        subject = MessageFormat.format(subject, subjParams);
        bodyMessage = MessageFormat.format(bodyMessage, bodyParams);
        logger.log(LoggerLevel.DEBUG,
                "Email formattata:\nsubject: " + subject + "\nbody: "
                + bodyMessage);
        return this;
    }

}
