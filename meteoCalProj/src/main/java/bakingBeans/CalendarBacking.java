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
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import model.UserModel;
import utility.ViewModality;
import javax.faces.context.FacesContext;
import javax.inject.Named;

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

    @Inject
    CalendarManager calendarManager;

    private LoginBacking login;

    private List<model.CalendarModel> calendars;
    private int indexCurrentCalendar;
    private ViewModality viewModality = ViewModality.DAY;

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

    public String getViewModailty() {
        return viewModality.toString();
    }

    public List<String> getCalendarNames() {
        if (calendars != null) {
            List<String> r = new ArrayList<>();
            return titlesCalendar(this.calendars, r);
        }
        return null;
    }

    @PostConstruct
    private void init() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        login = (LoginBacking) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{login}", LoginBacking.class);
        UserModel u = login.getCurrentUser();
        if (u != null) {
            calendars = calendarManager.getCalendars(u);
        }
    }

    private List<String> titlesCalendar(List<model.CalendarModel> c, List<String> r) {
        if (c.size() == 1) {
            r.add(c.get(0).getTitle());
        } else {
            r.add(c.get(0).getTitle() + ",");
            r.addAll(titlesCalendar(c.subList(1, c.size()), r));
        }
        return r;
    }
}
