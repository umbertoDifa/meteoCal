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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import model.CalendarModel;
import model.UserModel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import utility.LoggerLevel;
import utility.LoggerProducer;

/**
 *
 * @author Francesco
 */
@Named(value = "settings")
@ViewScoped
public class SettingsBacking implements Serializable {

    private UserModel user;

    private StreamedContent streamedContent;
    private boolean disableDownloadButton = true;

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
        login = (LoginBacking) facesContext.getApplication().evaluateExpressionGet(
                facesContext, "#{login}", LoginBacking.class);

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

    public boolean isDisableDownloadButton() {
        return disableDownloadButton;
    }

    public void setDisableDownloadButton(boolean showDownloadButton) {
        this.disableDownloadButton = showDownloadButton;
    }

    public StreamedContent getStreamedContent() {
        return streamedContent;
    }

    public void setStreamedContent(StreamedContent streamedContent) {
        this.streamedContent = streamedContent;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setCalendarTitles(List<String> c) {
        this.calendarTitles = c;
    }

    public void setCalendarToExport(String calendarToExport) {
        logger.log(LoggerLevel.DEBUG, "DENTRO il setCalendarTOExport vale: "
                + calendarToExport);
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

    public void saveCredentials() {
        //TODO
        // if(settingManager.changeCredentials(login.getCurrentUser(), name, surname, email))
        // showMessage("credential updated");
        //else
        // showMessage("credential not updated");
    }

    public void savePassword() {
        //TODO
        // if(settingManager.changePassword(login.getCurrentUser(), password))
        // showMessage("password updated");
        //else
        // showMessage("password not updated");
    }

    public void importCalendar(FileUploadEvent event) {
        settingManager.importCalendar(user, event.getFile());
        //TODO gestire le liste di ritorno

        FacesMessage msg = new FacesMessage("Success! ",
                event.getFile().getFileName() + " is uploaded.");
    }

    public void exportCalendar() {
        //disabilito pulasante download se era attivo
        RequestContext context = RequestContext.getCurrentInstance();
        setDisableDownloadButton(true);
        context.update("downloadForm:downloadButton");

        CalendarModel calToExp = findCalendarByName(calendars, calendarToExport);
        InputStream stream;
        if (calToExp != null) {
            new File(SettingManagerImpl.COMMON_PATH + "export" + File.separator
                    + login.getCurrentUser().getId()).mkdirs();

            if (settingManager.exportCalendar(calToExp)) {
                String path = getGlassfishDomainPath();

                logger.log(LoggerLevel.DEBUG, "PATH: " + path);

                try {
                    stream = new FileInputStream(new File(path + "config"
                            + File.separator + "export" + File.separator
                            + login.getCurrentUser().getId() + File.separator
                            + "exportCal.ics"));

                    //create file to be streamed
                    streamedContent = new DefaultStreamedContent(stream,
                            "text/calendar", "downloaded_calendar.ics");

                    //abilito il pulsante download
                    setDisableDownloadButton(
                            false);
                    context = RequestContext.getCurrentInstance();

                    context.update("downloadForm:donwloadButton");

                    logger.log(LoggerLevel.DEBUG,
                            "Calendario pronto per essere scaricato!");
                } catch (FileNotFoundException ex) {
                    logger.log(LoggerLevel.DEBUG,
                            "Cal to export not found for stream");
                }

            } else {
                //msg nessun cal trovato
                logger.log(LoggerLevel.DEBUG,
                        "Non è stato possibile creare il calenario to export");
                //TODO showmessage
            }

        } else {
            //msg nessun cal trovato
            logger.log(LoggerLevel.DEBUG, "Nessun calendario trovato");
            //TODO showmessage
        }

    }

    private String getGlassfishDomainPath() {
        //let the user download it

        File docroot1 = new File("../");
        try {
            docroot1 = docroot1.getCanonicalFile();
        } catch (IOException e) {
            docroot1 = docroot1.getAbsoluteFile();
        }
        String path = docroot1.getPath() + File.separator;
        //path prima di config
        return path;
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
