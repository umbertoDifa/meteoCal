/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.CalendarManager;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import model.CalendarModel;
import model.UserModel;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Francesco
 */
@Named(value = "settings")
@Dependent
public class SettingsBacking {

    private UserModel user;

    private String email;
    private String name;
    private String surname;
    private List<CalendarModel> calendars;
    private String calendarToExport;
    private StreamedContent calendarToExportFile;

    private List<String> calendarTitles;

    @Inject
    CalendarManager calendarManager;

    LoginBacking login;

    /**
     * Creates a new instance of SettingsBacking
     */
    public SettingsBacking() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        //mi salvo il login per ottenere l'info di chi è loggato
        login = (LoginBacking) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{login}", LoginBacking.class);

    }

    @PostConstruct
    public void init() {
        user = login.getCurrentUser();
        email = login.getEmail();
        name = login.getName();
        surname = login.getSurname();
        calendars = calendarManager.getCalendars(user);
        calendarTitles = titlesCalendar(calendars);

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setCalendarTitles(List<String> c) {
        this.calendarTitles = c;
    }

    public void setCalendarToExport(String calendarToExport) {
        this.calendarToExport = calendarToExport;
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

    public String getCalendarToExport() {
        return calendarToExport;
    }

    public List<String> getCalendarTitles() {
        return calendarTitles;
    }

    public StreamedContent getCalendarToExportFile() {
        return calendarToExportFile;
    }

    public void setCalendarToExportFile(StreamedContent calendarToExportFile) {
        this.calendarToExportFile = calendarToExportFile;
    }

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

    public void save() {
        //TODO
    }

    public void clear() {
        init();
    }

    public void importCalendar() {
        //TODO
    }

    public void exportCalendar() {
        CalendarModel c = findCalendarByName(calendars, calendarToExport);
        System.out.println("-dentro exportCal");
        if (c != null) {
            //calendarManager.exportCalendar(c);
            //let the user download it

        } else {
            //msg nessun cal trovato
        }

    }

    private CalendarModel findCalendarByName(List<CalendarModel> calendars, String name) {
        for (CalendarModel cal : calendars) {
            if (cal.getTitle().equals(name)) {
                return cal;
            }
        }
        return null;
    }

}
