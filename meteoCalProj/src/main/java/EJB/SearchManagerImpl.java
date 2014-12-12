package EJB;

import EJB.interfaces.SearchManager;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Event;
import model.Invitation;
import model.UserModel;

@Stateless
public class SearchManagerImpl implements SearchManager {

    @PersistenceContext(unitName = "meteoCalDB")
    private EntityManager database;

    @Override
    public List<UserModel> searchUsers(String stringToSearch) {
        return database.createNamedQuery("findUserByString").setParameter(
                "search", "%" + stringToSearch + "%").getResultList();
    }

    @Override
    public List<Event> searchEvents(String stringToSearch) {
        return database.createNamedQuery("findEventbyString").setParameter(
                "search", "%" + stringToSearch + "%").getResultList();
    }

    @Override
    public UserModel findUserbyEmail(String email) {
       UserModel user = (UserModel) database.createNamedQuery("findUserbyEmail").setParameter("email", email).getSingleResult();
       return user;
    }
    
    public List<UserModel> searchUserForInvitation (String stringToSearch) {
        List<UserModel> users = searchUsers(stringToSearch);
        for (UserModel user : users) {
            for (Invitation invitation : user.getInvitations()) {
               // TODO: se esiste una invitation per quell'event
                //ma che succede se l'event non è persistito?
                //come trovare le invitation dello stesso evento non ancora persistite??
            }
        }
        return users;
    }
}
