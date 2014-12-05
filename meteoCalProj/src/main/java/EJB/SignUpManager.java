/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB;

import java.util.List;
import model.User;

/**
 *
 * @author Umberto
 */
public interface SignUpManager {

    public List<User> getUsers();

    public boolean addUser();

    public User getNewUser();
}
