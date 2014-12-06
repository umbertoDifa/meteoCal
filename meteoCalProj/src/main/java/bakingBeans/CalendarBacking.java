/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.EventManager;
import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import model.User;
import utility.ViewModality;

/**
 *
 * @author Francesco
 */
@Named(value = "calendar")
@SessionScoped
public class CalendarBacking implements Serializable {

    //serialVersionUID?
    @Inject
    EventManager eventManager;

    private List<model.Calendar> calendars;
    private int indexCurrentCalendar;
    private ViewModality viewModality;
    /**
     * Creates a new instance of CalendarBacking
     */
    public CalendarBacking() {
    }

    public void load(User user) {
        calendars = eventManager.loadCalendars(user);
        //chiedere quale è di deafult

    }
    
    public void getCurrent(){
        
    }

}
