package org.dmonix.timex.list;

/**
 * The object to use in the activity list. Each object will hold information for
 * one activity, i.e. name and time
 * 
 * @author Peter Nerg
 * @version 1.0
 */
public class ActivityListObject implements Cloneable, Comparable {

    private String name;
    private String description;
    private int hour;
    private int minute;

    private String activityString = "";

    public ActivityListObject(String name, String description, int hour, int minute) {
        this.name = name;
        this.description = description;
        this.hour = hour;
        this.minute = minute;
        this.setStringRepresentation();
    }

    public void addMinutes(int minutes) {
        this.minute += minutes;
        if (this.minute > 59) {
            this.hour += (this.minute - (this.minute) % 60) / 60;
            this.minute = this.minute % 60;
        }
        this.setStringRepresentation();
    }

    public int compareTo(Object o) {
        return name.compareToIgnoreCase(((ActivityListObject) o).name);
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public void subtract5Minutes() {
        this.minute -= 5;
        if (this.minute < 0) {
            this.minute = this.minute + 60;
            this.hour--;
        }

        if (this.hour < 0) {
            this.hour = 0;
            this.minute = 0;
        }
        this.setStringRepresentation();
    }

    public String getDescription() {
        return this.description;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public int getMinutes() {
        return (this.hour * 60) + this.minute;
    }

    public String getName() {
        return this.name;
    }

    public String getTimeString() {
        StringBuffer sb = new StringBuffer();
        if (this.hour < 10)
            sb.append("0");

        sb.append(hour);
        sb.append(":");

        if (this.minute < 10)
            sb.append("0");

        sb.append(this.minute);

        return sb.toString();
    }

    public void resetTimes() {
        this.hour = 0;
        this.minute = 0;
        this.setStringRepresentation();
    }

    private void setStringRepresentation() {
        StringBuffer sb = new StringBuffer();
        if (this.hour < 10)
            sb.append("0");

        sb.append(hour);
        sb.append(":");

        if (this.minute < 10)
            sb.append("0");

        sb.append(this.minute);
        sb.append(" ");
        sb.append(this.name);

        activityString = sb.toString();
    }

    public String toString() {
        return activityString;
    }
}