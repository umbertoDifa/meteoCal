package EJB.interfaces;

import model.User;

/**
 *
 * @author Umberto
 */
public interface SignUpManager {
    /**
     * Aggiunge un utente al database
     * @return true se l'utente è creato, false se l'utente esiste già(ovvero stesso userName)
     */
    public boolean addUser(User newUser);
}
