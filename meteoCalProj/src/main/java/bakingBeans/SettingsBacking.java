/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.SettingManagerImpl;
import EJB.interfaces.CalendarManager;
import EJB.interfaces.SettingManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import model.CalendarModel;
import model.UserModel;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import utility.LoggerLevel;
import utility.LoggerProducer;
import utility.TimeTool;

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
    private CalendarManager calendarManager;

    @Inject
    private SettingManager settingManager;

    private LoginBacking login;

    private Logger logger = LoggerProducer.debugLogger(SettingsBacking.class);

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

    public void importCalendar(FileUploadEvent event) {
        FacesMessage msg = new FacesMessage("Success! ", event.getFile().getFileName() + " is uploaded.");
        
        settingManager.importCalendar(user, event.getFile());
        //TODO gestire le liste di ritorno
    }

    private void copyFile(String fileName, InputStream in) {
        try {

            // write the inputStream to a FileOutputStream
            new File(SettingManagerImpl.COMMON_PATH + "import").mkdirs();
            File importFile = new File(SettingManagerImpl.COMMON_PATH + "import" + File.separator + fileName);
            logger.log(LoggerLevel.DEBUG, "path: " + importFile.getAbsolutePath());
            OutputStream out = new FileOutputStream(importFile);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            in.close();
            out.flush();
            out.close();

            System.out.println("New file created!");

//            settingManager.importCalendar(login.getCurrentUser(), importFile.getAbsolutePath());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void exportCalendar() {
        CalendarModel c = findCalendarByName(calendars, calendarToExport);
        InputStream stream;
        if (c != null) {
            new File(SettingManagerImpl.COMMON_PATH + "export" + File.separator + login.getCurrentUser().getId()).mkdirs();
            File exportFile = new File(SettingManagerImpl.COMMON_PATH + "export" + File.separator + login.getCurrentUser().getId() + File.separator
                    + TimeTool.dateToTextDay(Calendar.getInstance().getTime(),
                            "yyyy-MM-dd-hh-mm-ss") + ".ics");
            if (settingManager.exportCalendar(c, exportFile.getAbsolutePath())) {
             //   stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getR getResourceAsStream(exportFile.getAbsolutePath());

            }
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
