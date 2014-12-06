package EJB.interfaces;

import bakingBeans.CredentialsBacking;
import objectAndString.UserAndMessage;

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
    public UserAndMessage findUser(CredentialsBacking credentials);
    
}
