package EJB.interfaces;

import bakingBeans.Credentials;
import model.User;
import utility.ControlMessages;

/**
 *
 * @author Umberto
 */
public interface LoginManager {
    
    /**
     * Find a user with the specified credentials in the db
     * @param credentials user credentials
     * @return an User if found, null if not
     */
    public User findUser(Credentials credentials);
    
    /**
     * Retrives last error in the loginManager
     * @return an error, null if none
     */
    public ControlMessages getLastError();
    
}