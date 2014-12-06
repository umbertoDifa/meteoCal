/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

/**
 *
 * @author Luckyna
 */
@Entity
@IdClass (InvitationId.class)
public class Invitation implements Serializable {
    @Id
    @ManyToOne
    private UserModel invitee;
    
    @Id
    @ManyToOne
    private Event event;
    
    private char answer;
    
    //METHODS

    public UserModel getInvitee() {
        return invitee;
    }

    public void setInvitee(UserModel invitee) {
        this.invitee = invitee;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public char getAnswer() {
        return answer;
    }

    public void setAnswer(char answer) {
        this.answer = answer;
    }
    
}
