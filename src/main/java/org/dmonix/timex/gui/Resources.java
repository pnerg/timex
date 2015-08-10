package org.dmonix.timex.gui;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.dmonix.gui.ResourceHandler;

/**
 * Utility for loading resources
 * 
 * @author Peter Nerg
 * @version 1.0
 */
public abstract class Resources extends ResourceHandler {
    public static final String pathTimex = "META-INF/img/";
    public static final String pathDmonix = "img/";

    private static final Logger log = Logger.getLogger(Resources.class.getName());

    public static ImageIcon getIcon(String name) {
        try {
            return ResourceHandler.getIcon(name);
        } catch (FileNotFoundException ex) {
            log.log(Level.CONFIG, "Missing resource : " + name + "\n" + ex.getMessage());
            return null;
        }
    }

    /**
     * Returns a 24 by 24 ImageIcon
     * 
     * @param name
     * @return
     */
    public static ImageIcon getIconLarge(String name) {
        try {
            return ResourceHandler.getIcon(name, 24);
        } catch (FileNotFoundException ex) {
            log.log(Level.CONFIG, "Missing resource : " + name + "\n" + ex.getMessage());
            return null;
        }
    }

    /**
     * Returns a 16 by 16 ImageIcon
     * 
     * @param name
     * @return
     */
    public static ImageIcon getIconSmall(String name) {
        try {
            return ResourceHandler.getIcon(name, 16);
        } catch (FileNotFoundException ex) {
            log.log(Level.CONFIG, "Missing resource : " + name + "\n" + ex.getMessage());
            return null;
        }
    }
}