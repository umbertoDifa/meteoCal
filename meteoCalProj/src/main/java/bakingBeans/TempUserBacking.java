/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author Francesco
 */
@RequestScoped
@Named
public class TempUserBacking {

    private String email;
    private String name;
    private String surname;
    private boolean gender;
    private String password;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public boolean isGender() {
        return gender;
    }

    public String getPassword() {
        return password;
    }
    
    
    /**
     * Creates a new instance of User
     */
    public TempUserBacking() {
    }
    
}
