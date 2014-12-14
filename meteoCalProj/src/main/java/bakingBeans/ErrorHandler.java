/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Francesco
 */
@Named(value = "errorHandler")
@RequestScoped
public class ErrorHandler {

    /**
     * Creates a new instance of ErrorHandler
     */
    public ErrorHandler() {
    }

    public String getMessage() {
        String msg = (String) FacesContext.getCurrentInstance().
                getExternalContext().getRequestMap().get("javax.servlet.error.message");
        return msg;
    }

    public String getCode() {
        String val = String.valueOf((Integer) FacesContext.getCurrentInstance().
                getExternalContext().getRequestMap().get("javax.servlet.error.status_code"));
        return val;
    }

}
