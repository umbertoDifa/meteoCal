package EJB;

import bakingBeans.Credentials;

/**
 *
 * @author Umberto
 */
public interface SignUpManager {
    /**
     * Aggiunge un utente al database
     * @param credential i parametri dell'utente --> non saranno credential ma un oggetto più complesso
     * @return true se l'utente è creato, false se l'utente esiste già(ovvero stesso userName)
     */
    public boolean addUser(Credentials credential);
}
