/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

@Named(value = "prova")
@RequestScoped
public class provaBacking {

    public void save(ActionEvent actionEvent) {
        addMessage("Data saved");
    }

    public void update(ActionEvent actionEvent) {
        addMessage("Data updated");
    }

    public void delete(ActionEvent actionEvent) {
        addMessage("Data deleted");
    }

    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
