/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.EventManager;
import EJB.interfaces.CalendarManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import model.UserModel;
import javax.inject.Named;

/**
 *
 * @author Francesco
 */
@Named(value = "calendar")
@RequestScoped
public class CalendarBacking implements Serializable {

    @Inject
    EventManager eventManager;

    @Inject
    CalendarManager calendarManager;

    @Inject
    private LoginBacking login;

    private List<model.CalendarModel> calendars;
    private String userId;
    private boolean external;

    /**
     * Creates a new instance of CalendarBacking
     */
    public CalendarBacking() {
    }

    public void load(UserModel user) {

        //chiedere quale è di deafult
    }

    public void getCurrent() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public List<String> getCalendarNames() {
        if (calendars != null) {
            List<String> r = titlesCalendar(this.calendars);
            return r;
        }
        return null;
    } 

    @PostConstruct
    private void init() {

        if (login.getCurrentUser() != null) {
            calendars = calendarManager.getCalendars(login.getCurrentUser());
        }
    }

    /**
     * da spostare!!
     *
     * @param c
     * @return
     */
    private List<String> titlesCalendar(List<model.CalendarModel> c) {
        List<String> result = new ArrayList<>();
        if (c != null) {
            for (model.CalendarModel b : c) {
                result.add(b.getTitle());
            }
        } else {
            System.out.println("Lista calendari null");
        }
        return result;
    }

    public void setExternalView() {
        external = true;
    }

}
