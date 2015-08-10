package org.dmonix.timex.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;

import org.dmonix.gui.SimpleCheckBoxMenuItem;
import org.dmonix.gui.frames.BaseFrame;
import org.dmonix.io.IOUtil;
import org.dmonix.timex.list.ActivityListCellRenderer;
import org.dmonix.timex.list.ActivityListModel;
import org.dmonix.timex.list.ActivityListObject;
import org.dmonix.timex.properties.TmxProperties;
import org.dmonix.timex.tmxfilehandler.TimexFileHandler;
import org.dmonix.util.ClassUtil;
import org.dmonix.util.DateHandler;
import org.dmonix.util.print.SimplePrintableObject;
import org.dmonix.util.zip.ZipReader;

/**
 * Main frame for the Timex program
 * 
 * @author Peter Nerg
 */
public class TmxFrame extends BaseFrame {
    private static final long serialVersionUID = 1L;

    // //////////////////////////////////
    // Constants
    // //////////////////////////////////
    private static final String ERROR_LOG = "timex.log";

    private final ImageIcon ICON_ADDL = Resources.getIcon(Resources.pathTimex + "Add24.gif");
    private final ImageIcon ICON_ADDS = Resources.getIcon(Resources.pathTimex + "Add16.gif");
    private final ImageIcon ICON_REML = Resources.getIcon(Resources.pathTimex + "Delete24.gif");
    private final ImageIcon ICON_REMS = Resources.getIcon(Resources.pathTimex + "Delete16.gif");
    private final ImageIcon ICON_PRINTS = Resources.getIcon(Resources.pathTimex + "Print16.gif");
    private final ImageIcon ICON_STOPL = Resources.getIcon(Resources.pathTimex + "Pause24.gif");
    private final ImageIcon ICON_STOPS = Resources.getIcon(Resources.pathTimex + "Pause16.gif");
    private final ImageIcon ICON_EDITL = Resources.getIcon(Resources.pathTimex + "Edit24.gif");
    private final ImageIcon ICON_EDITS = Resources.getIcon(Resources.pathTimex + "Edit16.gif");
    private final ImageIcon ICON_HELPL = Resources.getIcon(Resources.pathTimex + "Help24.gif");
    private final ImageIcon ICON_HELPS = Resources.getIcon(Resources.pathTimex + "Help16.gif");
    private final ImageIcon ICON_TMXVIEWL = Resources.getIconLarge(Resources.pathTimex + "calendar.gif");
    private final ImageIcon ICON_TMXVIEWS = Resources.getIconSmall(Resources.pathTimex + "calendar.gif");
    private transient final Logger log = Logger.getLogger(TmxFrame.class.getName());

    // //////////////////////////////////
    // Global variables
    // //////////////////////////////////
    private String today = DateHandler.getToday(TimexFileHandler.DATE_FORMAT);

    private transient ActivityListModel listModel = new ActivityListModel();
    private JScrollPane scrollPane = new JScrollPane();

    // //////////////////////////////////
    // Declare menu items
    // //////////////////////////////////
    private JMenuBar jMenu = new JMenuBar();
    private JMenu fileMenu = new JMenu("File");
    private JMenu viewMenu = new JMenu("View");
    private JMenu toolMenu = new JMenu("Tools");
    private JMenu helpMenu = new JMenu("Help");
    private JMenuItem jmiAdd = new JMenuItem("Add new");
    private JMenuItem jmiRem = new JMenuItem("Remove");
    private JMenuItem jmiPrint = new JMenuItem("Print");
    private JMenuItem jmiExit = new JMenuItem("Exit");
    private JMenuItem jmiPause = new JMenuItem("Pause");
    private JMenuItem jmiTmxReader = new JMenuItem("Timex Reader");
    private JMenuItem jmiEdit = new JMenuItem("Edit");
    private JMenuItem jmiHelp = new JMenuItem("Help");
    private SimpleCheckBoxMenuItem jmiViewTbox = new SimpleCheckBoxMenuItem("Toolbar", true, null);
    private SimpleCheckBoxMenuItem jmiViewSum = new SimpleCheckBoxMenuItem("Sum Label", true, null);

    // //////////////////////////////////
    // Declare panelActivities
    // //////////////////////////////////
    private JPanel panelActivities = new JPanel();
    private JList listActivities = new JList();

    // //////////////////////////////////
    // Declare panelAddSub
    // //////////////////////////////////
    private JPanel panelAddSub = new JPanel();
    private JButton btnAdd = new JButton("+");
    private JButton btnSub = new JButton("-");

    // //////////////////////////////////
    // Declare panelBtnBar
    // //////////////////////////////////
    private JPanel panelBtnBar = new JPanel();
    private JLabel sumLabel = new JLabel();

    // //////////////////////////////////
    // Declare toolbar
    // //////////////////////////////////
    private JToolBar jtoolBar = new JToolBar();
    private JButton jtbarBtnAdd = new JButton();
    private JButton jtbarBtnEdit = new JButton(ICON_EDITL);
    private JButton jtbarBtnRem = new JButton(ICON_REML);
    private JButton jtbarBtnPause = new JButton(ICON_STOPL);
    private JButton jtbarBtnTmxReader = new JButton(ICON_TMXVIEWL);
    private JButton jtbarBtnHelp = new JButton(ICON_HELPL);

    private transient TimexFileHandler fHandler = new TimexFileHandler();

    private Timer timer = new Timer(60000, null);

    public TmxFrame() throws Exception {
        super();
        try {
            this.initialize();
            super.configure("/org/dmonix/timex/config/config.xml");
            this.init();
        } catch (Exception ex) {
            super.exitError(TmxFrame.ERROR_LOG, ex);
        }
    }

    private void initialize() throws Exception {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new TmxFrame_this_windowAdapter());

        listActivities.setCellRenderer(new ActivityListCellRenderer());
        listActivities.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listActivities.setFont(new java.awt.Font("Dialog", 0, 10));
        listActivities.setAlignmentX((float) 0.5);
        listActivities.setModel(listModel);

        sumLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        scrollPane.getViewport().add(listActivities);

        // //////////////////////////////////
        // Set toolbar
        // //////////////////////////////////
        jtoolBar.setFloatable(false);

        btnAdd.addActionListener(new TmxFrame_btnAdd_actionAdapter());
        btnSub.addActionListener(new TmxFrame_btnSub_actionAdapter());
        jmiExit.addActionListener(new TmxFrame_jmiExit_actionAdapter());
        timer.addActionListener(new TmxFrame_timer1_actionAdapter());

        jtoolBar.add(jtbarBtnAdd);
        jtoolBar.add(jtbarBtnEdit);
        jtoolBar.add(jtbarBtnRem);
        jtoolBar.add(jtbarBtnPause);
        jtoolBar.add(jtbarBtnTmxReader);
        jtoolBar.add(jtbarBtnHelp);
        Insets insets = new Insets(3, 3, 3, 3);
        jtbarBtnAdd.setMargin(insets);
        jtbarBtnEdit.setMargin(insets);
        jtbarBtnRem.setMargin(insets);
        jtbarBtnPause.setMargin(insets);
        jtbarBtnTmxReader.setMargin(insets);
        jtbarBtnHelp.setMargin(insets);

        // Panel1
        panelActivities.setLayout(new BorderLayout(5, 10));
        panelActivities.add(scrollPane, BorderLayout.CENTER);

        // Panel2
        panelAddSub.setLayout(new FlowLayout());
        panelAddSub.add(btnAdd);
        panelAddSub.add(btnSub);

        // Panel3
        panelBtnBar.setLayout(new BorderLayout(5, 10));
        panelBtnBar.add(jtoolBar, BorderLayout.NORTH);
        panelBtnBar.add(sumLabel, BorderLayout.CENTER);

        // Add panels to frame
        this.getContentPane().add(panelBtnBar, BorderLayout.NORTH);
        this.getContentPane().add(panelActivities, BorderLayout.CENTER);
        this.getContentPane().add(panelAddSub, BorderLayout.SOUTH);
        this.setJMenuBar(jMenu);

        // Menubar
        jMenu.add(fileMenu);
        fileMenu.add(jmiAdd);
        fileMenu.add(jmiEdit);
        fileMenu.add(jmiRem);
        fileMenu.addSeparator();
        fileMenu.add(jmiPause);
        fileMenu.addSeparator();
        fileMenu.add(jmiPrint);
        fileMenu.addSeparator();
        fileMenu.add(jmiExit);
        jMenu.add(viewMenu);
        // Add the look 'n feel menu from the super class
        viewMenu.add(super.getLooknFeelMenu());
        viewMenu.add(jmiViewTbox);
        viewMenu.add(jmiViewSum);
        jMenu.add(toolMenu);
        toolMenu.add(jmiTmxReader);
        jMenu.add(helpMenu);
        helpMenu.add(super.getAboutMenuItem());
        helpMenu.add(jmiHelp);

        // Set tooltips
        jmiExit.setToolTipText("Quit Timex");
        btnAdd.setToolTipText("Add 5min");
        btnSub.setToolTipText("Remove 5min");

        this.validate();
    }

    private void init() {
        // =====================
        // Check if the file path exists, create new path if neccessary
        // =====================
        File path = new File(TmxProperties.DATA_DIR);
        if (!path.exists())
            path.mkdirs();

        // =====================
        // Check that Timex is not already running in the same
        // folder as this instance
        // =====================
        File lockFile = new File(TmxProperties.PATH + "timex.lck");
        if (lockFile.exists()) {
            int answer = JOptionPane.showConfirmDialog(this, "<html>Timex has detected the existens of a lock file.<br>"
                    + "This means that either there is already an instance of the program running<br>"
                    + "or that the program has previously been closed in a unorderly fashion thus leaving the lock file.<br>"
                    + "For more information please refer to the online manual at:<br>" + "http://www.dmonix.org<br><br>"
                    + "Do you wish to remove the lock file?</html>", "Lock file detected", JOptionPane.YES_NO_OPTION);

            if (answer == JOptionPane.NO_OPTION) {
                super.dispose();
                System.exit(-1);
            } else {
                lockFile.delete();
            }
        }

        try {
            lockFile.createNewFile();
            lockFile.deleteOnExit();
        } catch (IOException ex) {
            super.exitError(TmxFrame.ERROR_LOG, ex);
        }

        this.setActions();
        this.parsePropertyFile();
        this.unzipFiles();

        // Show splashscreen and sleep until the screen has disappeared
        super.showSplashScreen(false);

        this.log.fine("Starting Timex");

        // Create the list of activities and merge any existing files for today
        for (ActivityListObject activityListObject : fHandler.openTimexFiles()) {
            listModel.appendActivity(activityListObject);
        }

        // Sum label
        this.sumLabel.setText("Time = " + this.listModel.sumTimes());

        // =====================
        // Set accelerators
        // =====================
        jmiExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));

        super.setFont(this.jMenu, new Font("Tahoma", 0, 10), new Font("Tahoma", 0, 10));
        this.timer.start();
        this.validate();
        this.setVisible(true);
    }

    /**
     * Read the property file
     */
    private void parsePropertyFile() {
        TmxProperties.parseTimexPropertyFile();
        super.setLooknFeel(TmxProperties.getProperty(TmxProperties.PROP_LOOKNFEEL));
        this.jmiViewSum.setSelected(TmxProperties.getBooleanProperty(TmxProperties.PROP_SUMLABEL, true));
        this.jmiViewTbox.setSelected(TmxProperties.getBooleanProperty(TmxProperties.PROP_TOOLBAR_TMX, true));
    }

    /**
     * Init the actions
     */
    private void setActions() {
        this.jmiAdd.setAction(new ActionAddNew("New", ICON_ADDS, this));
        this.jtbarBtnAdd.setAction(new ActionAddNew("", ICON_ADDL, this));

        this.jmiEdit.setAction(new ActionEdit("Edit", ICON_EDITS, this));
        this.jtbarBtnEdit.setAction(new ActionEdit("", ICON_EDITL, this));

        this.jmiHelp.setAction(new ActionHelp("Help", ICON_HELPS));
        this.jtbarBtnHelp.setAction(new ActionHelp("", ICON_HELPL));

        this.jmiPause.setAction(new ActionPause("Pause", ICON_STOPS));
        this.jtbarBtnPause.setAction(new ActionPause("", ICON_STOPL));

        this.jmiPrint.setAction(new ActionPrint("Print", ICON_PRINTS));

        this.jmiRem.setAction(new ActionRemove("Remove", ICON_REMS, this));
        this.jtbarBtnRem.setAction(new ActionRemove("", ICON_REML, this));

        this.jmiTmxReader.setAction(new ActionTmxReader("Timex Reader", ICON_TMXVIEWS));
        this.jtbarBtnTmxReader.setAction(new ActionTmxReader("", ICON_TMXVIEWL));

        this.jmiViewSum.addActionListener(new TmxFrame_jmiViewSum_actionAdapter());
        this.jmiViewTbox.addActionListener(new TmxFrame_jmiViewTbox_actionAdapter());
    }

    /**
     * Exit Timex.
     */
    private void exitTimex() {
        int ans = JOptionPane.showConfirmDialog(this, "Exit Timex", "Exit Timex", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (ans != JOptionPane.YES_OPTION)
            return;

        super.setVisible(false);

        try {
            TmxProperties.setProperty(TmxProperties.PROP_LOOKNFEEL, super.getLooknFeel(), false);
            TmxProperties.setProperty(TmxProperties.PROP_TOOLBAR_TMX, this.jmiViewTbox.isSelected(), false);
            TmxProperties.setProperty(TmxProperties.PROP_SUMLABEL, this.jmiViewSum.isSelected(), false);

            TmxProperties.saveTimexPropertyFile();
            this.fHandler.saveTimexFiles(this.listModel.elements());

            // this.zipFiles();
        } catch (Exception ex) {
            super.exitError(TmxFrame.ERROR_LOG, ex);
        }
        dispose();
        System.exit(0);
    }

    /**
     * unzip the date/time files
     */
    private void unzipFiles() {
        File zipFile = new File(TmxProperties.PATH + "tmx.zip");
        if (!zipFile.exists())
            return;

        try {
            ZipReader zipReader = ZipReader.openZipFile(zipFile);

            boolean more = true;
            ZipEntry zipEntry;
            while (more) {
                zipEntry = zipReader.nextEntry();
                if (zipEntry != null)
                    zipReader.unzip(zipEntry, TmxProperties.DATA_DIR);
                else
                    more = false;
            }
            zipReader.close();

            IOUtil.deleteFile(zipFile);
        } catch (Exception ex) {
            exitError(TmxFrame.ERROR_LOG, ex);
        }
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        try {
            Locale.setDefault(Locale.US);
            System.setProperty("user.country", Locale.US.getCountry());
            System.setProperty("user.language", Locale.US.getLanguage());
            System.setProperty("user.variant", Locale.US.getVariant());

            File propFile = new File(TmxProperties.getHome() + File.separator + "logging.properties");
            if (propFile.exists()) {
                LogManager.getLogManager().readConfiguration(new FileInputStream(propFile));
            } else {
                LogManager.getLogManager().readConfiguration(TmxFrame.class.getClassLoader().getResourceAsStream("logging.properties"));
            }

            new TmxFrame();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * The "add new" action class.
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class ActionAddNew extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private TmxFrame owner;

        private ActionAddNew(String name, Icon icon, TmxFrame owner) {
            super(name, icon);
            this.owner = owner;
            super.putValue(SHORT_DESCRIPTION, "New Activity");
            super.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            Object selectedObject = listActivities.getSelectedValue();

            ActivityListObject listObj = listModel.newActivity(owner);
            if (listObj == null)
                return;

            if (selectedObject != null)
                listActivities.setSelectedValue(selectedObject, true);

            fHandler.addActivity(listObj.getName(), listObj.getDescription());
            validate();
        }
    }

    /**
     * The "edit" action class.
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class ActionEdit extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private TmxFrame owner;

        private ActionEdit(String name, Icon icon, TmxFrame owner) {
            super(name, icon);
            this.owner = owner;
            super.putValue(SHORT_DESCRIPTION, "Edit Time");
            super.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            int selected = listActivities.getSelectedIndex();
            if (selected < 0)
                return;

            listModel.editActivity(owner, selected);
            sumLabel.setText("Time = " + listModel.sumTimes());
            repaint();
        }
    }

    /**
     * The "pause" action class.
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class ActionPause extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private ActionPause(String name, Icon icon) {
            super(name, icon);
            super.putValue(SHORT_DESCRIPTION, "Pause Activity");
            super.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            listActivities.clearSelection();
            listActivities.setToolTipText("");
            repaint();
        }
    }

    /**
     * The "pause" action class.
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class ActionPrint extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private ActionPrint(String name, Icon icon) {
            super(name, icon);
            super.putValue(SHORT_DESCRIPTION, "Print");
            super.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            SimplePrintableObject printObj = new SimplePrintableObject("Timex");
            printObj.addHeading(DateHandler.getToday(TimexFileHandler.DATE_FORMAT));

            ActivityListObject listObj;
            for (int i = 0; i < listModel.getSize(); i++) {
                listObj = listModel.getActivity(i);
                printObj.addRow(listObj.getName(), listObj.getTimeString());
            }
            printObj.finish();
        }
    }

    /**
     * The "remove" action class.
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class ActionRemove extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private TmxFrame owner;

        private ActionRemove(String name, Icon icon, TmxFrame owner) {
            super(name, icon);
            this.owner = owner;
            super.putValue(SHORT_DESCRIPTION, "Remove Activity");
            super.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            int selected = listActivities.getSelectedIndex();
            if (selected < 0)
                return;

            ActivityListObject listObj = listModel.removeActivity(owner, selected);
            if (listObj == null)
                return;

            fHandler.removeActitivity(listObj.getName());
            sumLabel.setText("Time = " + listModel.sumTimes());
            listActivities.clearSelection();
            validate();
            repaint();
        }
    }

    /**
     * The "Timex Reader" action class.
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class ActionTmxReader extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private ActionTmxReader(String name, Icon icon) {
            super(name, icon);
            super.putValue(SHORT_DESCRIPTION, "Start Timex Reader");
            super.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            try {
                new TmxReaderFrame(fHandler);
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Could not start the Timex reader", ex.getMessage());
            }
        }
    }

    private class TmxFrame_jmiExit_actionAdapter implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            exitTimex();
        }
    }

    /**
     * Internal action listener
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxFrame_btnAdd_actionAdapter implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selected = listActivities.getSelectedIndex();
            if (selected < 0)
                return;

            listModel.getActivity(selected).addMinutes(5);

            sumLabel.setText("Time = " + listModel.sumTimes());

            validate();
            repaint();
        }
    }

    /**
     * Internal action listener
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxFrame_btnSub_actionAdapter implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selected = listActivities.getSelectedIndex();
            if (selected < 0)
                return;

            ((ActivityListObject) listModel.getElementAt(selected)).subtract5Minutes();

            sumLabel.setText("Time = " + listModel.sumTimes());

            validate();
            repaint();
        }
    }

    /**
     * Internal action listener
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxFrame_timer1_actionAdapter implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            String dateNow = DateHandler.getToday(TimexFileHandler.DATE_FORMAT);

            // If the clock has passed midnight, reset all times in the Timex
            // Loop through the vector with all Timex activities and reset
            // the times with '00:00'
            if (!today.equals(dateNow)) {
                today = dateNow;
                listModel.resetTimes();
            }

            // Increase time if there is a selected activity
            int selected = listActivities.getSelectedIndex();
            if (selected >= 0)
                listModel.getActivity(selected).addMinutes(1);

            try {
                fHandler.saveTimexFiles(listModel.elements());
            } catch (Exception ex) {
                exitError(TmxFrame.ERROR_LOG, ex);
            }

            sumLabel.setText("Time = " + listModel.sumTimes());
            validate();
            repaint();
        }
    }

    /**
     * Internal action listener
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxFrame_jmiViewTbox_actionAdapter implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            jtoolBar.setVisible(jmiViewTbox.isSelected());
        }
    }

    /**
     * Internal action listener
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxFrame_jmiViewSum_actionAdapter implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            sumLabel.setVisible(jmiViewSum.isSelected());
        }
    }

    /**
     * Internal action listener
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxFrame_this_windowAdapter extends java.awt.event.WindowAdapter {
        public void windowClosing(WindowEvent e) {
            exitTimex();
        }
    }
}

/**
 * The "help" action class.
 * 
 * @author Peter Nerg
 * @version 1.0
 */
class ActionHelp extends AbstractAction {
    private static final long serialVersionUID = 1L;
    ActionHelp(String name, Icon icon) {
        super(name, icon);
        super.putValue(SHORT_DESCRIPTION, "Help");
    }

    public void actionPerformed(ActionEvent e) {
        try {
            ClassUtil.openBrowser("http://www.dmonix.org/programs/timex/manual.jsp");
        } catch (Exception ex) {
            Component component = null;
            Object o = e.getSource();
            if (o != null && o instanceof Component) {
                component = ((Component) o).getParent();
            }

            JOptionPane.showMessageDialog(component, "Could not open help documentation", "Error with help documentation", JOptionPane.ERROR_MESSAGE);
        }
    }
}
