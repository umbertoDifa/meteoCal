/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Objects;

/**
 *
 * @author Luckyna
 */
public class InvitationId {
    private Long invitee;
    private Long event;

    public Long getInvitee() {
        return invitee;
    }

    public void setInvitee(Long invitee) {
        this.invitee = invitee;
    }

    public Long getEvent() {
        return event;
    }

    public void setEvent(Long event) {
        this.event = event;
    }



    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + Objects.hashCode(this.invitee);
        hash = 73 * hash + Objects.hashCode(this.event);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InvitationId other = (InvitationId) obj;
        if (!Objects.equals(this.invitee, other.invitee)) {
            return false;
        }
        if (!Objects.equals(this.event, other.event)) {
            return false;
        }
        return true;
    }
    
    
    
}
