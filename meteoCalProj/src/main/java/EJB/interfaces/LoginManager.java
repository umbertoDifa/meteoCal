package EJB.interfaces;

import model.User;

/**
 *
 * @author Umberto
 */
public interface LoginManager {

    public User findUser(String username, String password);

}
