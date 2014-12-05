package EJB;




import model.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Named("userManager")


@Stateless
public class UserManager {

   // @Inject

   // private transient Logger logger;

    @PersistenceContext(unitName = "meteoCalDB")    
    private EntityManager userDatabase;
    
    private User newUser = new User();

    @SuppressWarnings("unchecked")
    @Produces
    @Named(value ="users")
    @RequestScoped

    public List<User> getUsers() throws Exception {
        return userDatabase.createQuery("select u from User u").getResultList();        

    }

    public String addUser() throws Exception {

        userDatabase.persist(newUser);
        
        //logger.info("Added " + newUser);

        //return "userAdded";
        return "users";

    }

    public User findUser(String username, String password) throws Exception {

        @SuppressWarnings("unchecked")

        List<User> results = userDatabase
                .createQuery(
                        "select u from User u where u.username=:username and u.password=:password")
                .setParameter("username", username)
                .setParameter("password", password).getResultList();

        if (results.isEmpty()) {

            return null;

        } else if (results.size() > 1) {

            throw new IllegalStateException(
                    "Cannot have more than one user with the same username!");

        } else {

            return results.get(0);

        }

    }

    @Produces
    @RequestScoped
    @Named

    public User getNewUser() {

        return newUser;

    }

}
