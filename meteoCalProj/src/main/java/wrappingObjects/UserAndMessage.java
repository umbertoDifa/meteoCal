package wrappingObjects;

import model.UserModel;
import utility.ControlMessages;

/**
 *
 * @author umboDifa
 */
public class UserAndMessage {
    private UserModel user;
    private ControlMessages controlMessage;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public ControlMessages getControlMessage() {
        return controlMessage;
    }

    public void setControlMessage(ControlMessages message) {
        this.controlMessage = message;
    }

    
    
}
