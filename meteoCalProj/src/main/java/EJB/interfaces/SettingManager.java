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

    public boolean exportCalendar(CalendarModel calendar, String destionationPath);

    public List<Pair<String, String>> importCalendar(UserModel user, UploadedFile uploadedFile);
    
    public void deleteExportFolder(UserModel user);
 
}
