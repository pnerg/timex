package org.dmonix.timex.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import org.dmonix.gui.SimpleCheckBoxMenuItem;
import org.dmonix.gui.frames.BaseFrame;
import org.dmonix.gui.models.SortableListModel;
import org.dmonix.io.IOUtil;
import org.dmonix.io.filters.RegExpFilter;
import org.dmonix.timex.list.ActivityListCellRenderer;
import org.dmonix.timex.list.ActivityListModel;
import org.dmonix.timex.list.ActivityListObject;
import org.dmonix.timex.properties.TmxProperties;
import org.dmonix.timex.tmxfilehandler.TimexFileHandler;
import org.dmonix.util.DateHandler;
import org.dmonix.util.print.SimplePrintableObject;

/**
 * The Timex reader/viewer frame
 * 
 * @author Peter Nerg
 */
public class TmxReaderFrame extends BaseFrame {
    private static final long serialVersionUID = 1L;
    private final String FRAME_TITLE = "Timex Reader";
    private final int FRAME_WIDTH = 230;
    private final int FRAME_HEIGHT = 400;

    private final ImageIcon ICON_REML = Resources.getIcon(Resources.pathTimex + "Delete24.gif");
    private final ImageIcon ICON_REMS = Resources.getIcon(Resources.pathTimex + "Delete16.gif");
    private final ImageIcon ICON_PRINTL = Resources.getIcon(Resources.pathTimex + "Print24.gif");
    private final ImageIcon ICON_PRINTS = Resources.getIcon(Resources.pathTimex + "Print16.gif");
    private final ImageIcon ICON_EDITL = Resources.getIcon(Resources.pathTimex + "Edit24.gif");
    private final ImageIcon ICON_EDITS = Resources.getIcon(Resources.pathTimex + "Edit16.gif");
    private final ImageIcon ICON_HELPL = Resources.getIcon(Resources.pathTimex + "Help24.gif");
    private final ImageIcon ICON_THRASHALLL = Resources.getIconLarge(Resources.pathTimex + "thrash_all.gif");
    private final ImageIcon ICON_THRASHALLS = Resources.getIconSmall(Resources.pathTimex + "thrash_all.gif");
    private final ImageIcon ICON_SUML = Resources.getIconLarge(Resources.pathTimex + "sum.gif");
    private final ImageIcon ICON_SUMS = Resources.getIconSmall(Resources.pathTimex + "sum.gif");

    private final String NO_FILTER = ".*";
    private final String ERROR_LOG = "timex.log";

    // //////////////////////////////////
    // Global Variables
    // //////////////////////////////////
    private String YEAR_FILTER = DateHandler.getToday(TimexFileHandler.DATE_FORMAT_YEAR);
    private String MONTH_FILTER = DateHandler.getToday(TimexFileHandler.DATE_FORMAT_MONTH);

    // //////////////////////////////////
    // Panels
    // //////////////////////////////////
    private JPanel jPanelNorth = new JPanel();
    private JPanel jPanelSouth = new JPanel();

    private JList listDayFiles = new JList();
    private JList listActivitiesSum = new JList();

    private transient SortableListModel listModelDayFiles = new SortableListModel(true);
    private transient ActivityListModel listModelActivitiesSum = new ActivityListModel();

    private JScrollPane scrollPaneDayFiles = new JScrollPane();
    private JScrollPane scrollPaneActivitiesSum = new JScrollPane();

    // //////////////////////////////////
    // Declare menu
    // //////////////////////////////////
    private JMenuBar jMenu = new JMenuBar();
    private JMenu fileMenu = new JMenu("File");
    private JMenu viewMenu = new JMenu("View");
    private JMenu helpMenu = new JMenu("Help");
    private JMenu monthSubMenu = new JMenu("Month");
    private JMenu yearSubMenu = new JMenu("Year");

    private JMenuItem jmiPrint = new JMenuItem("Print", ICON_PRINTS);
    private JMenuItem jmiRem = new JMenuItem("Remove", ICON_REMS);
    private JMenuItem jmiRemAll = new JMenuItem("Remove All", ICON_THRASHALLS);
    private JMenuItem jmiSum = new JMenuItem("Summarize", ICON_SUMS);
    private JMenuItem jmiExit = new JMenuItem("Exit");
    private JMenuItem jmiEdit = new JMenuItem("Edit", ICON_EDITS);

    private MonthRadioButtonMenuItem jrmAllMonth = new MonthRadioButtonMenuItem("All Months", NO_FILTER);
    private MonthRadioButtonMenuItem jrmJan = new MonthRadioButtonMenuItem("January", "01");
    private MonthRadioButtonMenuItem jrmFeb = new MonthRadioButtonMenuItem("February", "02");
    private MonthRadioButtonMenuItem jrmMar = new MonthRadioButtonMenuItem("March", "03");
    private MonthRadioButtonMenuItem jrmApr = new MonthRadioButtonMenuItem("April", "04");
    private MonthRadioButtonMenuItem jrmMay = new MonthRadioButtonMenuItem("May", "05");
    private MonthRadioButtonMenuItem jrmJun = new MonthRadioButtonMenuItem("June", "06");
    private MonthRadioButtonMenuItem jrmJul = new MonthRadioButtonMenuItem("July", "07");
    private MonthRadioButtonMenuItem jrmAug = new MonthRadioButtonMenuItem("August", "08");
    private MonthRadioButtonMenuItem jrmSep = new MonthRadioButtonMenuItem("September", "09");
    private MonthRadioButtonMenuItem jrmOct = new MonthRadioButtonMenuItem("October", "10");
    private MonthRadioButtonMenuItem jrmNov = new MonthRadioButtonMenuItem("November", "11");
    private MonthRadioButtonMenuItem jrmDec = new MonthRadioButtonMenuItem("December", "12");

    private SimpleCheckBoxMenuItem jmiViewTbox = new SimpleCheckBoxMenuItem("Toolbar", true, null);

    private ButtonGroup btg1 = new ButtonGroup();
    private ButtonGroup btg2 = new ButtonGroup();

    // //////////////////////////////////
    // Declare toolbar
    // //////////////////////////////////
    private JToolBar jtoolBar = new JToolBar();
    private JButton jtbarBtnEdit = new JButton(ICON_EDITL);
    private JButton jtbarBtnRem = new JButton(ICON_REML);
    private JButton jtbarBtnPrint = new JButton(ICON_PRINTL);
    private JButton jtbarBtnRemAll = new JButton(ICON_THRASHALLL);
    private JButton jtbarBtnSum = new JButton(ICON_SUML);
    private JButton jtbarBtnHelp = new JButton(ICON_HELPL);

    private static final Logger log = Logger.getLogger(TmxFrame.class.getName());

    private transient TimexFileHandler fHandler;
    private JPanel panelLabels = new JPanel();
    private GridBagLayout gridBagLayoutLabels = new GridBagLayout();
    private JLabel lblSum = new JLabel("Sum = 00:00");
    private JLabel lblSelectedRecords = new JLabel();

    /**
     * Main method
     */
    public TmxReaderFrame(TimexFileHandler fHandler) throws Exception {
        this.fHandler = fHandler;
        this.setIconImage(new ImageIcon(TmxReaderFrame.class.getResource("/META-INF/img/timex_blue_banner.jpg")).getImage());
        try {
            jbInit();
            this.init();
        } catch (Exception ex) {
            super.exitError(ERROR_LOG, ex);
        }
    }

    private void jbInit() throws Exception {
        log.fine("Starting Timex Reader");

        // //////////////////////////////////
        // Set toolbar
        // //////////////////////////////////
        jtoolBar.setFloatable(false);
        listDayFiles.addListSelectionListener(new TmxReaderFrame_listDayFiles_listSelectionAdapter());
        jmiExit.addActionListener(new TmxReaderFrame_jmiExit_actionAdapter());
        jmiViewTbox.addActionListener(new TmxReaderFrame_jmiViewTbox_actionAdapter());
        panelLabels.setLayout(gridBagLayoutLabels);
        lblSelectedRecords.setFont(new java.awt.Font("Dialog", 0, 10));
        lblSelectedRecords.setText("Records = 0");
        listDayFiles.setFont(new java.awt.Font("Dialog", 0, 10));
        listActivitiesSum.setEnabled(false);
        listActivitiesSum.setFont(new java.awt.Font("Dialog", 0, 10));
        lblSum.setFont(new java.awt.Font("Dialog", 0, 10));
        jtoolBar.add(jtbarBtnEdit);
        jtoolBar.add(jtbarBtnRem);
        jtoolBar.add(jtbarBtnRemAll);
        jtoolBar.add(jtbarBtnSum);
        jtoolBar.add(jtbarBtnPrint);
        jtoolBar.add(jtbarBtnHelp);
        Insets insets = new Insets(3, 3, 3, 3);
        jtbarBtnEdit.setMargin(insets);
        jtbarBtnRem.setMargin(insets);
        jtbarBtnRemAll.setMargin(insets);
        jtbarBtnSum.setMargin(insets);
        jtbarBtnPrint.setMargin(insets);
        jtbarBtnHelp.setMargin(insets);

        // //////////////////////////////////
        // Set event listeners for the month check boxes
        // //////////////////////////////////
        jrmAllMonth.addActionListener(new TmxReaderFrame_monthRadioButtonMenuItem_actionAdapter());

        // //////////////////////////////////
        // Create the JLists and fill them with default info
        // //////////////////////////////////
        listDayFiles.setModel(listModelDayFiles);
        scrollPaneDayFiles.getViewport().add(listDayFiles);

        listActivitiesSum.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listActivitiesSum.setModel(listModelActivitiesSum);
        listActivitiesSum.setCellRenderer(new ActivityListCellRenderer());
        scrollPaneActivitiesSum.getViewport().add(listActivitiesSum);

        // //////////////////////////////////
        // jPanel1
        // //////////////////////////////////
        jPanelNorth.setLayout(new BorderLayout(5, 10));
        jPanelNorth.add(jtoolBar, BorderLayout.NORTH);
        jPanelNorth.add(scrollPaneDayFiles, BorderLayout.CENTER);

        // //////////////////////////////////
        // jPanel2
        // //////////////////////////////////
        jPanelSouth.setLayout(new BorderLayout(5, 10));
        jPanelSouth.add(scrollPaneActivitiesSum, BorderLayout.CENTER);
        jPanelSouth.add(panelLabels, BorderLayout.NORTH);

        // //////////////////////////////////
        // Menubar
        // //////////////////////////////////
        jMenu.add(fileMenu);
        fileMenu.add(jmiEdit);
        fileMenu.add(jmiRem);
        fileMenu.add(jmiRemAll);
        fileMenu.addSeparator();
        fileMenu.add(jmiSum);
        fileMenu.addSeparator();
        fileMenu.add(jmiPrint);
        fileMenu.addSeparator();
        fileMenu.add(jmiExit);
        jMenu.add(viewMenu);
        viewMenu.add(monthSubMenu);
        monthSubMenu.add(jrmAllMonth);
        monthSubMenu.addSeparator();
        monthSubMenu.add(jrmJan);
        monthSubMenu.add(jrmFeb);
        monthSubMenu.add(jrmMar);
        monthSubMenu.add(jrmApr);
        monthSubMenu.add(jrmMay);
        monthSubMenu.add(jrmJun);
        monthSubMenu.add(jrmJul);
        monthSubMenu.add(jrmAug);
        monthSubMenu.add(jrmSep);
        monthSubMenu.add(jrmOct);
        monthSubMenu.add(jrmNov);
        monthSubMenu.add(jrmDec);
        viewMenu.add(yearSubMenu);
        yearSubMenu.addSeparator();
        jMenu.add(helpMenu);
        viewMenu.add(jmiViewTbox);
        helpMenu.add(super.getAboutMenuItem());

        // Create button group for month radio buttons
        btg1.add(jrmAllMonth);
        btg1.add(jrmJan);
        btg1.add(jrmFeb);
        btg1.add(jrmMar);
        btg1.add(jrmApr);
        btg1.add(jrmMay);
        btg1.add(jrmJun);
        btg1.add(jrmJul);
        btg1.add(jrmAug);
        btg1.add(jrmSep);
        btg1.add(jrmOct);
        btg1.add(jrmNov);
        btg1.add(jrmDec);

        // Register listeners
        addWindowListener(this);

        // Set tool tips text
        jmiExit.setToolTipText("Quit Timex Reader");
        jrmAllMonth.setToolTipText("View all months");
        jtbarBtnHelp.setToolTipText("Help");

        // Add the panels and the menu to the frame
        this.getContentPane().setLayout(new GridLayout(2, 1, 2, 3));
        this.getContentPane().add(jPanelNorth);
        this.getContentPane().add(jPanelSouth);
        panelLabels.add(lblSelectedRecords, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 5, 0,
                5), 0, 0));
        panelLabels.add(lblSum, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        this.setJMenuBar(jMenu);
    }

    private void init() {
        this.setActions();
        jmiExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

        // Sets the radio button for the current month
        setThisMonth();

        this.preBufferFiles(false);
        TmxReaderFrame_yearRadioButtonMenuItem_actionAdapter tya = new TmxReaderFrame_yearRadioButtonMenuItem_actionAdapter();

        // setup the year radio buttons
        int currentYear = Integer.parseInt(DateHandler.getToday(TimexFileHandler.DATE_FORMAT_YEAR));
        for (int i = 2000; i < currentYear; i++) {
            YearRadioButtonMenuItem yrb = new YearRadioButtonMenuItem("" + i, "" + i);
            yrb.addActionListener(tya);
            yearSubMenu.add(yrb);
            btg2.add(yrb);
        }

        YearRadioButtonMenuItem yrb = new YearRadioButtonMenuItem("" + currentYear, "" + currentYear);
        yrb.addActionListener(tya);
        yearSubMenu.add(yrb);
        btg2.add(yrb);
        yrb.setSelected(true);

        YearRadioButtonMenuItem jrmAllYear = new YearRadioButtonMenuItem("All Years", ".*");
        jrmAllYear.addActionListener(tya);
        yearSubMenu.add(jrmAllYear);
        btg2.add(jrmAllYear);
        jrmAllYear.setToolTipText("View all years");

        lblSelectedRecords.setText("Selected records = 0");

        super.setFont(this.jMenu, new Font("Tahoma", 0, 10), new Font("Tahoma", 0, 10));
        // Set size of the frame and display the frame
        this.setTitle(FRAME_TITLE);
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setVisible(true);
        this.setResizable(false);
    }

    private void setActions() {
        this.jtbarBtnHelp.setAction(new ActionHelp("", ICON_HELPL));

        this.jmiEdit.setAction(new ActionEdit("Edit", ICON_EDITS, this));
        this.jtbarBtnEdit.setAction(new ActionEdit("", ICON_EDITL, this));

        this.jmiPrint.setAction(new ActionPrint("Print", ICON_PRINTS));
        this.jtbarBtnPrint.setAction(new ActionPrint("", ICON_PRINTL));

        this.jmiRem.setAction(new ActionRemove("Remove", ICON_REMS, this));
        this.jtbarBtnRem.setAction(new ActionRemove("", ICON_REML, this));

        this.jmiRemAll.setAction(new ActionRemoveAll("Remove all", ICON_THRASHALLS, this));
        this.jtbarBtnRemAll.setAction(new ActionRemoveAll("", ICON_THRASHALLL, this));

        this.jmiSum.setAction(new ActionSum("Sum", ICON_SUMS));
        this.jtbarBtnSum.setAction(new ActionSum("", ICON_SUML));

        jrmJan.setAction(new ActionMonth("January"));
        jrmFeb.setAction(new ActionMonth("February"));
        jrmMar.setAction(new ActionMonth("March"));
        jrmApr.setAction(new ActionMonth("April"));
        jrmMay.setAction(new ActionMonth("May"));
        jrmJun.setAction(new ActionMonth("June"));
        jrmJul.setAction(new ActionMonth("July"));
        jrmAug.setAction(new ActionMonth("August"));
        jrmSep.setAction(new ActionMonth("September"));
        jrmOct.setAction(new ActionMonth("October"));
        jrmNov.setAction(new ActionMonth("November"));
        jrmDec.setAction(new ActionMonth("December"));
    }

    /**
     * Handle window closing event
     */
    public void windowClosing(WindowEvent e) {
        super.setVisible(false);
        this.cleanup();
    }

    /**
     * Display the list of files in jList1 It is possible to filter on both year and month.
     * 
     * @param keepSelection
     *            states if the previous list selection is to be kept
     */
    void preBufferFiles(boolean keepSelection) {
        int[] selected = this.listDayFiles.getSelectedIndices();
        this.listModelDayFiles.clear();
        this.listModelActivitiesSum.clear();

        String[] fileNames = new File(TmxProperties.DATA_DIR).list(new RegExpFilter(".*" + YEAR_FILTER + "\\x2D" + MONTH_FILTER + "\\x2D.*\\x2Etmx"));

        if (fileNames.length < 1)
            return;

        String fileName;
        for (int i = 0; i < fileNames.length; i++) {
            fileName = fileNames[i];
            try {
                DayFileListObject listObj = new DayFileListObject(fileName.substring(0, fileName.indexOf(".")), this.fHandler.readTmxFile(fileName));
                this.listModelDayFiles.addElement(listObj);
                this.listModelDayFiles.sort();
            } catch (IOException ex) {
                super.exitError(ERROR_LOG, ex);
            }
        }
        int prevSelected = 0;
        if (keepSelection) {
            this.listDayFiles.setSelectedIndices(selected);
            prevSelected = selected.length;
        }

        this.lblSum.setText("Sum = " + this.listModelActivitiesSum.sumTimes());
        lblSelectedRecords.setText("Selected records = " + prevSelected);
        repaint();
    }

    private void cleanup() {
        this.dispose();
    }

    /**
     * This will 'arm' the month radio button according to the current month
     */
    private void setThisMonth() {
        btg1.setSelected(btg1.getSelection(), false);
        int selection = Integer.parseInt(DateHandler.getToday(TimexFileHandler.DATE_FORMAT_MONTH));

        switch (selection) {
        case 1:
            jrmJan.setSelected(true);
            break;
        case 2:
            jrmFeb.setSelected(true);
            break;
        case 3:
            jrmMar.setSelected(true);
            break;
        case 4:
            jrmApr.setSelected(true);
            break;
        case 5:
            jrmMay.setSelected(true);
            break;
        case 6:
            jrmJun.setSelected(true);
            break;
        case 7:
            jrmJul.setSelected(true);
            break;
        case 8:
            jrmAug.setSelected(true);
            break;
        case 9:
            jrmSep.setSelected(true);
            break;
        case 10:
            jrmOct.setSelected(true);
            break;
        case 11:
            jrmNov.setSelected(true);
            break;
        case 12:
            jrmDec.setSelected(true);
            break;
        default:
            System.out.println("The date is incorrect");
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
        private TmxReaderFrame owner;

        private ActionEdit(String name, Icon icon, TmxReaderFrame owner) {
            super(name, icon);
            super.putValue(SHORT_DESCRIPTION, "Edit selected file");
            super.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
            this.owner = owner;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Object obj = listDayFiles.getSelectedValue();
                if (obj == null) {
                    return;
                }

                TmxEditFrame tmxEditFrame = new TmxEditFrame(obj.toString() + ".tmx");
                tmxEditFrame.setOwner(owner);
                repaint();
            } catch (Exception ex) {
                exitError(ERROR_LOG, ex);
            }
        }
    }

    /**
     * The "print" action class.
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

            DayFileListObject dayFileListObject;
            Object[] selected = listDayFiles.getSelectedValues();
            for (int i = 0; i < selected.length; i++) {
                dayFileListObject = (DayFileListObject) selected[i];
                printObj.addHeading(dayFileListObject.getDate());
                for (ActivityListObject activityListObject : dayFileListObject.getActivities()) {
                    printObj.addRow(activityListObject.getName(), activityListObject.getTimeString());
                }
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
        private TmxReaderFrame owner;

        private ActionRemove(String name, Icon icon, TmxReaderFrame owner) {
            super(name, icon);
            super.putValue(SHORT_DESCRIPTION, "Remove selected file");
            super.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
            this.owner = owner;
        }

        public void actionPerformed(ActionEvent e) {
            Object[] selected = listDayFiles.getSelectedValues();
            if (selected.length < 1)
                return;

            int answer = JOptionPane.showConfirmDialog(owner, "Delete Selected File(s) Permanently?", "Confirm Delete", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (answer != JOptionPane.YES_OPTION)
                return;

            DayFileListObject listObj;
            for (int i = 0; i < selected.length; i++) {
                listObj = (DayFileListObject) selected[i];
                if (IOUtil.deleteFile(TmxProperties.DATA_DIR + listObj.getFileName())) {
                    listModelDayFiles.removeElement(listObj);
                    listModelDayFiles.sort();
                }
            }
            listModelActivitiesSum.clear();
            lblSum.setText("Sum = " + listModelActivitiesSum.sumTimes());
            lblSelectedRecords.setText("Selected records = 0");
            repaint();
        }
    }

    /**
     * The "remove all" action class.
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class ActionRemoveAll extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private TmxReaderFrame owner;

        private ActionRemoveAll(String name, Icon icon, TmxReaderFrame owner) {
            super(name, icon);
            super.putValue(SHORT_DESCRIPTION, "Remove all displayed files");
            super.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
            this.owner = owner;
        }

        public void actionPerformed(ActionEvent e) {
            int answer = JOptionPane.showConfirmDialog(owner, "Delete All Displayed Files Permanently?", "Confirm Delete", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (answer != JOptionPane.YES_OPTION)
                return;

            Enumeration dayFiles = listModelDayFiles.elements();
            String file;
            while (dayFiles.hasMoreElements()) {
                file = ((DayFileListObject) dayFiles.nextElement()).getFileName();
                IOUtil.deleteFile(TmxProperties.DATA_DIR + file);
            }
            listModelDayFiles.clear();
            listModelActivitiesSum.clear();
            lblSum.setText("Sum = " + listModelActivitiesSum.sumTimes());
            lblSelectedRecords.setText("Selected records = 0");
            repaint();
        }
    }

    /**
     * The "sum" action class.
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class ActionSum extends AbstractAction {
        private static final long serialVersionUID = 1L;

        private ActionSum(String name, Icon icon) {
            super(name, icon);
            super.putValue(SHORT_DESCRIPTION, "Sum all displayed files");
            super.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            listDayFiles.setSelectionInterval(0, listModelDayFiles.getSize() - 1);
            repaint();
        }
    }

    /**
     * The "month" action class.
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class ActionMonth extends AbstractAction {
        private static final long serialVersionUID = 1L;

        private ActionMonth(String name) {
            super(name);
            super.putValue(SHORT_DESCRIPTION, "View this month");
        }

        public void actionPerformed(ActionEvent e) {
            MONTH_FILTER = ((MonthRadioButtonMenuItem) e.getSource()).getMonthValue();
            preBufferFiles(false);
        }
    }

    /**
     * Class for the month radio buttons in the menu.
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class MonthRadioButtonMenuItem extends JRadioButtonMenuItem {
        private static final long serialVersionUID = 1L;
        private String monthValue;

        MonthRadioButtonMenuItem(String text, String monthValue) {
            super(text);
            this.monthValue = monthValue;
        }

        private String getMonthValue() {
            return monthValue;
        }
    }

    /**
     * Class for the year radio buttons in the menu.
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class YearRadioButtonMenuItem extends JRadioButtonMenuItem {
        private static final long serialVersionUID = 1L;
        private String yearValue;

        YearRadioButtonMenuItem(String text, String yearValue) {
            super(text);
            this.yearValue = yearValue;
        }

        private String getYearValue() {
            return yearValue;
        }
    }

    /**
     * The object used in the list of activity files.
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class DayFileListObject implements Comparable<DayFileListObject> {
        private static final long serialVersionUID = 1L;
        private String fileName;
        private List<ActivityListObject> activities;

        private DayFileListObject(String fileName, java.util.List<ActivityListObject> activities) {
            this.fileName = fileName;
            this.activities = activities;
        }

        private java.util.List<ActivityListObject> getActivities() {
            return this.activities;
        }

        private String getDate() {
            return this.fileName;
        }

        private String getFileName() {
            return this.fileName + ".tmx";
        }

        public String toString() {
            return this.fileName;
        }

        public int compareTo(DayFileListObject o) {
            return this.toString().compareToIgnoreCase(o.toString());
        }
    }

    /**
     * Internal action listener
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxReaderFrame_monthRadioButtonMenuItem_actionAdapter implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            MONTH_FILTER = ((MonthRadioButtonMenuItem) e.getSource()).getMonthValue();
            preBufferFiles(false);
        }
    }

    /**
     * Internal action listener
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxReaderFrame_yearRadioButtonMenuItem_actionAdapter implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            YEAR_FILTER = ((YearRadioButtonMenuItem) e.getSource()).getYearValue();
            preBufferFiles(false);
        }
    }

    /**
     * Internal action listener
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxReaderFrame_listDayFiles_listSelectionAdapter implements javax.swing.event.ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            Object[] dayFileItems = listDayFiles.getSelectedValues();

            if (dayFileItems.length < 1)
                return;

            listModelActivitiesSum.clear();

            for (int i = 0; i < dayFileItems.length; i++) {
                for (ActivityListObject activityListObject : ((DayFileListObject) dayFileItems[i]).getActivities()) {
                    listModelActivitiesSum.appendActivity(activityListObject);
                }
            }
            lblSum.setText("Sum = " + listModelActivitiesSum.sumTimes());
            lblSelectedRecords.setText("Selected records = " + dayFileItems.length);
            repaint();
        }
    }

    /**
     * Internal action listener
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxReaderFrame_jmiExit_actionAdapter implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            cleanup();
        }
    }

    /**
     * Internal action listener
     * 
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxReaderFrame_jmiViewTbox_actionAdapter implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            jtoolBar.setVisible(jmiViewTbox.isSelected());
            TmxProperties.setProperty(TmxProperties.PROP_TOOLBAR_READER, jmiViewTbox.isSelected(), false);
        }
    }
}
