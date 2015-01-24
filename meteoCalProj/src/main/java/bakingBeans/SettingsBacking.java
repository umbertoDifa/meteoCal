/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bakingBeans;

import EJB.interfaces.CalendarManager;
import EJB.interfaces.DeleteManager;
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
import utility.Gender;
import utility.LoggerLevel;
import utility.LoggerProducer;
import wrappingObjects.Pair;

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
    private String oldPassword;
    private String newPassword1;
    private String newPassword2;
    private Gender gender;

    private List<String> calendarTitles;

    @Inject
    private CalendarManager calendarManager;

    @Inject
    private SettingManager settingManager;

    @Inject
    private DeleteManager deleteManager;

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
        gender = login.getCurrentUser().getGender();

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
        //disabilito pulasante download se era attivo
        RequestContext context = RequestContext.getCurrentInstance();
        setDisableDownloadButton(true);
        context.update("downloadForm:downloadButton");
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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword1() {
        return newPassword1;
    }

    public void setNewPassword1(String newPassword1) {
        this.newPassword1 = newPassword1;
    }

    public String getNewPassword2() {
        return newPassword2;
    }

    public void setNewPassword2(String newPassword2) {
        this.newPassword2 = newPassword2;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<Gender> getGenders() {
        List<Gender> list = new ArrayList<>();
        list.add(Gender.F);
        list.add(Gender.M);
        return list;
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
        if (settingManager.changeCredentials(login.getCurrentUser(), name,
                surname, email)) {
            showInfoMessage(null, "Credential updated", "");
//            if (settingManager.setGender(gender)) {
//                showInfoMessage(null, "Gender has been set", "");
//            } else {
//                showWarnMessage(null, "Gender not updated", "");
//            }
            login.refreshCurrentUser();
        } else {
            showWarnMessage(null, "Credential not updated", "");
        }
    }

    public void savePassword() {
        if (newPassword1.equals(newPassword2)) {
            if (settingManager.changePassword(login.getCurrentUser(),
                    oldPassword, newPassword1)) {
                showInfoMessage(null, "Password updated", "");
                login.refreshCurrentUser();
            } else {
                showWarnMessage(null, "Password not updated", "");
            }
        } else {
            showWarnMessage(null, "Password wrong", "Check the fields!");
        }
    }

    public void deleteAccount() {

        if (deleteManager.deleteAccount(login.getCurrentUser())) {
            login.forceLogout();
        } else {
            showWarnMessage(null, "Error", "Impossible to delete account");

        }

    }

    public void importCalendar(FileUploadEvent event) {
        List<Pair<String, String>> unimportedEvents = settingManager.importCalendar(
                user, event.getFile());
        //se non ci sono stati errori
        if (unimportedEvents != null) {
            //se tutti gli eventi sono stati importati
            if (unimportedEvents.isEmpty()) {
                showInfoMessage(login.getCurrentUser().getEmail(), "Success!",
                        "Calendar imported");
            } else {
                //se ci sono eventi già in altri calendari
                showInfoMessage(login.getCurrentUser().getEmail(), "Success!",
                        "Calendar imported");
                showWarnMessage(login.getCurrentUser().getEmail(),
                        "Events already in calendars",
                        "Pay attention, some events were already in your calendars or do not exist anymore or you do not have permission to add them");
            }
        } else {
            showWarnMessage(login.getCurrentUser().getEmail(), "Error",
                    "Impossible to import the calendar");
        }

    }

    public void exportCalendar() {
        //disabilito pulasante download se era attivo
        RequestContext context = RequestContext.getCurrentInstance();
        setDisableDownloadButton(true);
        context.update("downloadForm:downloadButton");

        CalendarModel calToExp = findCalendarByName(calendars, calendarToExport);
        InputStream stream;
        if (calToExp != null) {
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
                    showInfoMessage(login.getCurrentUser().getEmail(), "Ready!",
                            "Calendar ready to be downloaded");
                } catch (FileNotFoundException ex) {
                    logger.log(LoggerLevel.DEBUG,
                            "Cal to export not found for stream");
                }

            } else {
                //msg nessun cal trovato
                logger.log(LoggerLevel.DEBUG,
                        "Non è stato possibile creare il calenario to export");
                showWarnMessage(login.getCurrentUser().getEmail(), "Error",
                        "It was not possible to export the selected calendar");
            }

        } else {
            //msg nessun cal trovato
            logger.log(LoggerLevel.DEBUG, "Nessun calendario trovato");
            showWarnMessage(login.getCurrentUser().getEmail(), "Error",
                    "Calendar not found");
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

    private void showInfoMessage(String recipient, String msg, String advice) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(recipient, new FacesMessage(FacesMessage.SEVERITY_INFO,
                msg, advice));
        RequestContext.getCurrentInstance().update("growl");
    }

    private void showWarnMessage(String recipient, String msg, String advice) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(recipient, new FacesMessage(FacesMessage.SEVERITY_WARN,
                msg, advice));
        RequestContext.getCurrentInstance().update("growl");
    }
}
