package org.dmonix.timex.tmxfilehandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dmonix.io.IOUtil;
import org.dmonix.timex.list.ActivityListObject;
import org.dmonix.timex.properties.TmxProperties;
import org.dmonix.util.DateHandler;

/**
 * Mananges the Timex files
 * 
 * @author Peter Nerg
 */
public class TimexFileHandler {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_MONTH = "MM";
    public static final String DATE_FORMAT_YEAR = "yyyy";

    private static Logger log = Logger.getLogger(TimexFileHandler.class.getName());
    private XMLActivities xmlActivities;

    public TimexFileHandler() {
        this.xmlActivities = new XMLActivities(TmxProperties.PATH + "timex.act");
    }

    public String getActivityDescription(String activity) {
        String description = xmlActivities.getActivityDescription(activity);

        if (log.isLoggable(Level.FINE))
            log.fine("Get activity description : " + activity + " - " + description);

        return description;
    }

    public void addActivity(String name, String description) {
        this.xmlActivities.addActivity(name, description);
        if (log.isLoggable(Level.FINE))
            log.fine("Add activity - " + name + " : " + description);
    }

    public List<String> getActivites() {
        return this.xmlActivities.getActivities();
    }

    public void removeActitivity(String name) {
        this.xmlActivities.removeActivity(name);
        if (log.isLoggable(Level.FINE))
            log.fine("Remove activity - " + name);
    }

    public List<ActivityListObject> openTimexFiles() {
        List<ActivityListObject> projData = this.readProjectFile();
        List<String> todayData = this.readTodayTimeFile();

        String activityString;
        int hour, minute;
        for (String tmp : todayData) {
            if(tmp == null || tmp.length() < 6)
                continue;
            
            try {
                activityString = tmp.substring(6);
                hour = Integer.parseInt(tmp.substring(0, 2));
                minute = Integer.parseInt(tmp.substring(3, 5));
                ActivityListObject listObject = new ActivityListObject(activityString, "", hour, minute);
                projData.add(listObject);
            }
            catch(Exception ex) {
                log.severe("Failed to read current activites, the activity ["+tmp+"] is corrupt");
            }
        }
        return projData;
    }

    /**
     * Read the contents of one *.tmx file.
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    public List<ActivityListObject> readTmxFile(String fileName) throws IOException {
        List<ActivityListObject> returnData = new ArrayList<ActivityListObject>();

        String activityString, description;
        int hour, minute;

        List<String> fileData = this.readUTFFile(TmxProperties.DATA_DIR + fileName);

        for (String tmp : fileData) {
            try {
                activityString = tmp.substring(6);
                hour = Integer.parseInt(tmp.substring(0, 2));
                minute = Integer.parseInt(tmp.substring(3, 5));
                description = this.getActivityDescription(activityString);
                returnData.add(new ActivityListObject(activityString, description, hour, minute));
            } catch (StringIndexOutOfBoundsException ex) {
                log.log(Level.WARNING, "Found corrupted data [" + tmp + "] in [" + fileName + "]");
            }
        }

        return returnData;
    }

    public void saveTimexFile(String fileName, Enumeration projData) throws IOException {
        DataOutputStream ostream = new DataOutputStream(new FileOutputStream(TmxProperties.DATA_DIR + fileName + ".tmx"));

        ActivityListObject tmp;
        while (projData.hasMoreElements()) {
            tmp = (ActivityListObject) projData.nextElement();

            if (tmp.getHour() != 0 || tmp.getMinute() != 0)
                ostream.writeUTF(tmp.toString());
        }

        ostream.flush();
        IOUtil.closeException(ostream);
    }

    /**
     * Save Timex files Input Vector with strings in the format 'hh:mm text'
     * Convert to two vectors with strings. 1) All activities, no time data 2)
     * Only those activities with a time != 00:00. Time data included
     */
    public void saveTimexFiles(Enumeration projData) throws Exception {
        this.saveTimexFile(DateHandler.getToday(TimexFileHandler.DATE_FORMAT), projData);

        // Save the currently existing activities to file
        this.xmlActivities.toFile();
    }

    /**
     * Read the project file and write the content in the file to a list
     */
    private List<ActivityListObject> readProjectFile() {
        List<ActivityListObject> projData = new ArrayList<ActivityListObject>();

        for (String activity : xmlActivities.getActivities()) {
            projData.add(new ActivityListObject(activity, this.getActivityDescription(activity), 0, 0));
        }
        return projData;
    }

    /**
     * Read the time file and write the content in the file to a list.
     * 
     * @return The list is empty if todays file does not exist
     */
    private List<String> readTodayTimeFile() {
        try {
            String today = DateHandler.getToday(TimexFileHandler.DATE_FORMAT) + ".tmx";
            return this.readUTFFile(TmxProperties.DATA_DIR + today);
        } catch (IOException iox) {
        }
        return new ArrayList<String>();
    }

    private List<String> readUTFFile(String fileName) throws IOException {
        DataInputStream istream = new DataInputStream(new FileInputStream(fileName));
        ;
        boolean EOFfound = false;
        List<String> data = new ArrayList<String>();

        while (!EOFfound) {
            try {
                data.add(istream.readUTF());
            } catch (EOFException ex) {
                EOFfound = true;
            }
        }

        IOUtil.closeNoException(istream);
        return data;
    }

}