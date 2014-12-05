package EJB;

import EJB.interfaces.SearchManager;
import javax.ejb.Stateless;

@Stateless
public class SearchManagerImpl implements SearchManager {

    @Override
    public <List> Object search(Object thingToSearch) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
