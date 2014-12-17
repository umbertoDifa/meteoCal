package EJB.interfaces;

import model.UserModel;

/**
 *
 * @author Umberto
 */
public interface SignUpManager {
    /**
     * Aggiunge un utente al database e crea un calendario di default 
     * @param newUser user to add
     * @return true se l'utente è creato, false se l'utente esiste già(ovvero stesso userName)
     */
    public boolean addUser(UserModel newUser);
}
