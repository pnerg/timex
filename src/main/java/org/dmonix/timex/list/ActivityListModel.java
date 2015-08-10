package org.dmonix.timex.list;

import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.dmonix.gui.models.SortableListModel;
import org.dmonix.timex.gui.ActivityDialog;
import org.dmonix.timex.gui.ActivityDialogEditor;
import org.dmonix.timex.tmxfilehandler.TimexFileHandler;

/**
 * The list model for the activity list.
 * @author Peter Nerg
 * @version 1.0
 */
public class ActivityListModel extends SortableListModel {
    private static Logger log = Logger.getLogger(ActivityListModel.class.getName());

    /**
     * Create a default comparator that always performs the compareTo method on
     * the two objects
     */
    private static final Comparator comparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            return ((ActivityListObject) o1).compareTo((ActivityListObject)o2);
        }
    };

    /**
     * Default constructor.
     */
    public ActivityListModel() {
        super(true);
        super.setComparator(comparator);
    }

    /**
     * Adds an object to the model.<br>
     * The object will be added in String order.
     * 
     * @param obj
     */
    public void addElement(ActivityListObject obj) {
        super.addElement(obj);
        super.sort();
    }

    public void appendActivity(ActivityListObject obj) {
        int index = this.indexOfActivity(obj);

        if (this.log.isLoggable(Level.FINEST))
            log.log(Level.FINEST, obj.getName() + " found at index = " + index);

        if (index < 0) {
            this.addElement((ActivityListObject) obj.clone());
            return;
        }

        int minutes = obj.getHour() * 60 + obj.getMinute();
        ((ActivityListObject) super.getElementAt(index)).addMinutes(minutes);
    }

    public boolean contains(String name) {

        for (int i = 0; i < super.getSize(); i++) {
            if (((ActivityListObject) super.getElementAt(i)).getName().equals(name))
                return true;
        }

        return false;
    }

    public int indexOfActivity(ActivityListObject obj) {
        String name = obj.getName();
        for (int i = 0; i < super.getSize(); i++) {
            if (((ActivityListObject) super.getElementAt(i)).getName().equals(name))
                return i;
        }

        return -1;
    }

    public ActivityListObject getActivity(int index) {
        return (ActivityListObject) super.getElementAt(index);
    }

    public void editActivity(JFrame owner, int index) {
        ActivityListObject listObject = (ActivityListObject) super.getElementAt(index);

        Object input = JOptionPane.showInputDialog(owner, "Edit Time", "Edit Time", JOptionPane.PLAIN_MESSAGE, null, null, listObject.getTimeString());

        if (input == null)
            return;

        String newTime = input.toString();
        int hour, minute;

        try {
            hour = Integer.parseInt(newTime.substring(0, newTime.indexOf(":")));
            minute = Integer.parseInt(newTime.substring(newTime.indexOf(":") + 1));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(owner, "Format is [00-23]:[00-59]", "Not Valid Time", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            JOptionPane.showMessageDialog(owner, "Format is [00-23]:[00-59]", "Not Valid Time", JOptionPane.ERROR_MESSAGE);
            return;
        }

        listObject.resetTimes();
        listObject.addMinutes(hour * 60 + minute);
    }

    /**
     * This method is only used by the Timex Editor.
     * 
     * @param owner
     * @param fHandler
     * @return
     */
    public ActivityListObject newEditorActivity(JFrame owner, TimexFileHandler fHandler) {
        ActivityDialogEditor actDialog = new ActivityDialogEditor(owner, fHandler);
        if (!actDialog.showDialog()) {
            actDialog.dispose();
            actDialog = null;
            return null;
        }

        String newActivity = actDialog.getNameTxt();
        String actDesc = actDialog.getDescription();

        actDialog.dispose();
        actDialog = null;

        if (newActivity == null || newActivity.length() == 0) {
            JOptionPane.showMessageDialog(owner, "Not A Valid Name", "Not Valid Activity", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (this.contains(newActivity)) {
            JOptionPane.showMessageDialog(owner, "Activity Already Exist", "Not Valid Activity", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        ActivityListObject listObject = new ActivityListObject(newActivity, actDesc, 0, 0);
        this.addElement(listObject);
        return listObject;
    }

    public ActivityListObject newActivity(JFrame owner, boolean allowDescription) {
        ActivityDialog actDialog = new ActivityDialog(owner, allowDescription, false);
        if (!actDialog.showDialog()) {
            actDialog.dispose();
            return null;
        }

        String newActivity = actDialog.getNameTxt();
        String description = actDialog.getDescriptionTxt();
        actDialog.dispose();

        if (newActivity == null || newActivity.length() == 0 || description == null) {
            JOptionPane.showMessageDialog(owner, "Not A Valid Name", "Not Valid Activity", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (this.contains(newActivity)) {
            JOptionPane.showMessageDialog(owner, "Activity Already Exist", "Not Valid Activity", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        ActivityListObject listObject = new ActivityListObject(newActivity, description, 0, 0);
        this.addElement(listObject);
        return listObject;
    }

    /**
     * Convenience method for adding a new activity.<br>
     * The user will be prompted to type in an new activity.
     * 
     * @param owner
     * @return
     */
    public ActivityListObject newActivity(JFrame owner) {
        return this.newActivity(owner, true);
    }

    /**
     * Convenience method to remove an activity.<br>
     * 
     * @param owner
     * @param index
     * @return
     */
    public ActivityListObject removeActivity(JFrame owner, int index) {
        int answer = JOptionPane.showConfirmDialog(owner, "Delete This Activity?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (answer != JOptionPane.YES_OPTION)
            return null;

        return (ActivityListObject) super.removeElementAt(index);
    }

    public void removeElement(ActivityListObject obj) {
        super.removeElement(obj);
        obj = null;
    }

    /**
     * Reset the times for all activity objects of this list.
     */
    public void resetTimes() {
        for (int i = 0; i < this.getSize(); i++) {
            ((ActivityListObject) super.getElementAt(i)).resetTimes();
        }
    }

    /**
     * Return a string representation of the sum of all times for all items.
     * 
     * @return
     */
    public String sumTimes() {
        if (super.getSize() < 1)
            return "00:00";

        long totalMinutes = 0;
        for (int i = 0; i < super.getSize(); i++) {
            totalMinutes += ((ActivityListObject) super.getElementAt(i)).getMinutes();
        }

        long hour, minute;
        if (totalMinutes > 59) {
            hour = (totalMinutes - (totalMinutes) % 60) / 60;
            minute = totalMinutes % 60;
        } else {
            hour = 0;
            minute = totalMinutes;
        }

        StringBuffer result = new StringBuffer();
        if (hour < 10)
            result.append("0");

        result.append(hour);
        result.append(":");

        if (minute < 10)
            result.append("0");

        result.append(minute);

        return result.toString();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < super.getSize(); i++) {
            buffer.append(super.getElementAt(i).toString());
            if (i < super.getSize() - 1)
                buffer.append("\n");
        }

        return buffer.toString();
    }
}