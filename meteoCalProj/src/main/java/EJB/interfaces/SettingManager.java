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

    public List<Pair<String, String>> importCalendar(UserModel user, UploadedFile uploadedFile);

    public void deleteExportFolder(UserModel user);

    public void deleteAccount(UserModel userToDelete);

    public void changePassword(UserModel user, String oldPassword, String newPassword);

    public void changeCredentials(UserModel user, String name, String surname, String email);

}
