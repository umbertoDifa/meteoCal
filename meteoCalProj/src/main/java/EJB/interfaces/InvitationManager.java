/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB.interfaces;

import java.util.List;
import model.Event;
import model.Invitation;
import model.InvitationAnswer;
import model.UserModel;

/**
 *
 * @author Umberto
 */
public interface InvitationManager {
    /**
     * Create an invite for all the users in the list for the specified event
     * @param userToInvite users to invite
     * @param event event for which the invitation is created
     * @return true if success
     */
    public void createInvitations(List<UserModel> userToInvite, Event event);
    
    boolean setAnswer(Invitation invitation, InvitationAnswer answer);
    

}
