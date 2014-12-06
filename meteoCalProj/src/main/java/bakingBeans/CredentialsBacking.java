package bakingBeans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author Umberto
 */
@RequestScoped
@Named(value = "credentials")
public class CredentialsBacking {

    private String email;

    private String password;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {

        return password;

    }

    public void setPassword(String password) {

        this.password = password;

    }

}
