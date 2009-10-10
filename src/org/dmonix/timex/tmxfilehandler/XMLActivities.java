package org.dmonix.timex.tmxfilehandler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dmonix.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The activity file
 * @author Peter Nerg
 * @version 1.0
 */
public class XMLActivities
{
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ELEMENT_ACTIVITIES = "activities";
    private static final String ELEMENT_ACTIVITY = "activity";
    private static final String ELEMENT_DESC = "description";

    private static final Logger log = Logger.getLogger(XMLActivities.class.getName());

    private String fileName;
    private Document doc = null;
    private Element root = null;


    public XMLActivities(String fileName) {
        try {
            if(log.isLoggable(Level.FINE))
                log.fine("Reading activities from file ["+fileName+"]");
            this.fileName = fileName;
            this.parseXMLFile(new FileInputStream(fileName));            
        }
        catch (Exception ex) {
            this.doc = XMLUtil.newDocument();
            this.root = this.doc.createElement(XMLActivities.ELEMENT_ACTIVITIES);
            this.doc.appendChild(root);
        }
    }

    /**
     * Add an activity.
     * @param name Name of the activity
     * @param description Description of the activity
     */
    public void addActivity(String name, String description) {
        Element eActivity = this.doc.createElement(XMLActivities.ELEMENT_ACTIVITY);
        eActivity.setAttribute(XMLActivities.ATTRIBUTE_NAME, name);

        Element eDescription = this.doc.createElement(XMLActivities.ELEMENT_DESC);
        eDescription.appendChild(this.doc.createTextNode(description));

        eActivity.appendChild(eDescription);

        this.root.appendChild(eActivity);

        if(log.isLoggable(Level.FINE))
            log.fine("Added activity - "+name+" : "+description);
    }


    public List<String> getActivities(){
	NodeList list = this.root.getElementsByTagName(XMLActivities.ELEMENT_ACTIVITY);
        List<String> activities = new ArrayList<String>();
        
        for(int i=0; i < list.getLength(); i++) {
            activities.add(((Element)list.item(i)).getAttribute(XMLActivities.ATTRIBUTE_NAME));
        }

        return activities;
    }

    public String getActivityDescription(String name) {
        Element act = this.getActivityElement(name);

        if(act == null)
            return null;

        NodeList list = act.getElementsByTagName(XMLActivities.ELEMENT_DESC);
        if(list.getLength() < 1)
            return null;

        Element desc = (Element)list.item(0);

        if(!desc.hasChildNodes())
            return null;

        return desc.getFirstChild().getNodeValue();
    }

    public int getActivityCount() {
        return this.root.getElementsByTagName(XMLActivities.ELEMENT_ACTIVITY).getLength();
    }

    /**
     * Remove an activity.
     * @param name The name of the activity
     */
    public void removeActivity(String name) {
        Element activity = this.getActivityElement(name);
        if(activity != null) {
            this.root.removeChild(activity);

            if(log.isLoggable(Level.FINE))
                log.fine("Removed activity : "+name);
        }
    }


    public void toFile() throws Exception {
        XMLUtil.documentToFile(this.doc, this.fileName);

        if(log.isLoggable(Level.FINE))
            log.fine("Activities written to file ["+this.fileName+"]");
    }

    public String toString() {
        return doc.getDocumentElement().toString();
    }


    /**
     * Find an activity element.
     * The method expects there to be only one activity element with an
     * attribute of the given value.
     * @param name The name of the activity
     * @return
     */
    private Element getActivityElement(String name) {
        NodeList list = this.root.getElementsByTagName(XMLActivities.ELEMENT_ACTIVITY);
        for (int i = 0; i < list.getLength(); i++) {
            if (((Element) list.item(i)).getAttribute(XMLActivities.ATTRIBUTE_NAME).equals(name)) {
                if(log.isLoggable(Level.FINE))
                    log.log(Level.FINE, "Activity element found for name = "+name);
                return (Element) list.item(i);
            }
        }

        if(log.isLoggable(Level.CONFIG))
            log.log(Level.CONFIG, "NO activity element found for name = "+name);

        return null;
    }

    private void parseXMLFile(InputStream istream) throws Exception {
        this.doc = XMLUtil.getDocument(istream);

        if(this.doc.hasChildNodes()) {
            this.root = (Element)this.doc.getFirstChild();
        }
        else {
            if(log.isLoggable(Level.FINE))
                log.fine("Activity file is empty");

            this.root = doc.createElement(XMLActivities.ELEMENT_ACTIVITIES);
            this.doc.appendChild(this.root);
        }
    }
}