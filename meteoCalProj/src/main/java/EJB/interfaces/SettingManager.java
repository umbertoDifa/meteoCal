package EJB.interfaces;

import java.util.List;
import model.CalendarModel;
import model.UserModel;
import org.primefaces.model.UploadedFile;
import wrappingObjects.Pair;

/**
 *
 * @author umboDifa
 */
public interface SettingManager {

    public boolean exportCalendar(CalendarModel calendar);
    /**
     * Importa un calendario Costruito con MeteoCal, creando un calendario con
     * gli eventi importati per lo user
     *
     * @param user User che sta importando
     * @param uploadedFile
     * @return La lista delle coppie di eventi non importati (nome,owner), null
     * se c'è stato un errore
     */
    public List<Pair<String, String>> importCalendar(UserModel user, UploadedFile uploadedFile);

    /**
     * Cancella un account se lo userToDelete viene trovato ed e not null
     * @param userToDelete user da eliminare 
     * @return true se lo user è eliminato, false se non è stato possibile eliminarlo
     */
    public boolean deleteAccount(UserModel userToDelete);

    /**
     * 
     * @param user
     * @param oldPassword
     * @param newPassword
     * @return true se la password è stata cambiata, false altrimenti
     */
    public boolean changePassword(UserModel user, String oldPassword, String newPassword);

    /**
     * 
     * @param user
     * @param name
     * @param surname
     * @param email
     * @return true se campi cambiati con successo, false altrimenti
     */
    public boolean changeCredentials(UserModel user, String name, String surname, String email);

}
