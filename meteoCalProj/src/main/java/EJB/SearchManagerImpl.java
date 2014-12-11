package EJB;

import EJB.interfaces.SearchManager;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Event;
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
    

}
