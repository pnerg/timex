package org.dmonix.timex.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
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
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import org.dmonix.gui.frames.BaseFrame;
import org.dmonix.timex.list.ActivityListCellRenderer;
import org.dmonix.timex.list.ActivityListModel;
import org.dmonix.timex.list.ActivityListObject;
import org.dmonix.timex.tmxfilehandler.TimexFileHandler;

/**
 * The Timex edit frame
 * @author Peter Nerg
 * @version 1.3.1
 */
public class TmxEditFrame extends BaseFrame {
    private static final long serialVersionUID = 1L;
    private final String ERROR_LOG = "timex.log";
    private final boolean FRAME_RESIZABLE = false;
    private final boolean FRAME_VISIBLE = true;
    private final String FRAME_TITLE = "";
    private final int FRAME_WIDTH = 230;
    private final int FRAME_HEIGHT = 350;
    private final ImageIcon ICON_ADDL = Resources.getIcon(Resources.pathTimex + "Add24.gif");
    private final ImageIcon ICON_ADDS = Resources.getIcon(Resources.pathTimex + "Add16.gif");
    private final ImageIcon ICON_REML = Resources.getIcon(Resources.pathTimex + "Delete24.gif");
    private final ImageIcon ICON_REMS = Resources.getIcon(Resources.pathTimex + "Delete16.gif");
    private final ImageIcon ICON_EDITL = Resources.getIcon(Resources.pathTimex + "Edit24.gif");
    private final ImageIcon ICON_EDITS = Resources.getIcon(Resources.pathTimex + "Edit16.gif");
    private final ImageIcon ICON_HELPL = Resources.getIcon(Resources.pathTimex + "Help24.gif");
    private final ImageIcon ICON_PLUS = Resources.getIconLarge(Resources.pathTimex + "plus.gif");
    private final ImageIcon ICON_MINUS = Resources.getIconLarge(Resources.pathTimex + "minus.gif");

    private TmxReaderFrame owner;
    private transient ActivityListModel listModel = new ActivityListModel();

    // //////////////////////////////////
    // Declare menu items
    // //////////////////////////////////
    private JMenuBar jMenu = new JMenuBar();
    private JMenu fileMenu = new JMenu("File");
    private JMenuItem jmiAdd = new JMenuItem("New");
    private JMenuItem jmiRem = new JMenuItem("Remove");
    private JMenuItem jmiEdit = new JMenuItem("Edit");

    // //////////////////////////////////
    // Declare jPanel1
    // //////////////////////////////////
    private JLabel lblSum = new JLabel();
    private JPanel jPanel1 = new JPanel();

    // //////////////////////////////////
    // Declare jPanel2
    // //////////////////////////////////
    private JPanel jPanel2 = new JPanel();
    private JList listActivities = new JList();

    // //////////////////////////////////
    // Declare jPanel2
    // //////////////////////////////////
    private JPanel jPanel3 = new JPanel();
    private JButton jbtnOk = new JButton("Ok");
    private JButton jbtnCancel = new JButton("Cancel");

    // //////////////////////////////////
    // Declare toolbar
    // //////////////////////////////////
    private JToolBar jtoolBar = new JToolBar();
    private JButton jtbarBtnAdd = new JButton(ICON_ADDL);
    private JButton jtbarBtnEdit = new JButton(ICON_EDITL);
    private JButton jtbarBtnRem = new JButton(ICON_REML);
    private JButton jtbarBtnPlus = new JButton(ICON_PLUS);
    private JButton jtbarBtnMinus = new JButton(ICON_MINUS);
    private JButton jtbarBtnHelp = new JButton(ICON_HELPL);
    private JScrollPane scrollPane = new JScrollPane();
    private String fileName;

    private transient TimexFileHandler fHandler = new TimexFileHandler();
    private transient final Logger log = Logger.getLogger(TmxEditFrame.class.getName());

    public TmxEditFrame(String fileName) throws Exception {
        this.fileName = fileName;
        try {
            this.setIconImage(new ImageIcon(TmxEditFrame.class.getResource("/org/dmonix/timex/img/timex_blue_banner.jpg")).getImage());
            jbInit();
            this.init();
            this.setActions();
        } catch (Exception ex) {
            super.exitError(ERROR_LOG, ex);
        }
    }

    private void jbInit() throws Exception {
        this.log.fine("Starting Timex Editor");

        listActivities.setFont(new java.awt.Font("Dialog", 0, 10));
        listActivities.setCellRenderer(new ActivityListCellRenderer());
        listActivities.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lblSum.setFont(new java.awt.Font("Dialog", 0, 10));
        jbtnOk.setFont(new java.awt.Font("Dialog", 0, 10));
        jbtnCancel.setFont(new java.awt.Font("Dialog", 0, 10));
        scrollPane.getViewport().add(listActivities);
        listActivities.setModel(listModel);

        // //////////////////////////////////
        // Set toolbar
        // //////////////////////////////////
        jtoolBar.setFloatable(false);
        jtbarBtnPlus.addActionListener(new TmxEditFrame_jtbarBtnPlus_actionAdapter());
        jtbarBtnMinus.addActionListener(new TmxEditFrame_jtbarBtnMinus_actionAdapter());
        jbtnOk.addActionListener(new TmxEditFrame_jbtnOk_actionAdapter());
        jbtnCancel.addActionListener(new TmxEditFrame_jbtnCancel_actionAdapter());
        jtoolBar.add(jtbarBtnAdd);
        jtoolBar.add(jtbarBtnEdit);
        jtoolBar.add(jtbarBtnRem);
        jtoolBar.add(jtbarBtnPlus);
        jtoolBar.add(jtbarBtnMinus);
        jtoolBar.add(jtbarBtnHelp);
        Insets insets = new Insets(3, 3, 3, 3);
        jtbarBtnAdd.setMargin(insets);
        jtbarBtnEdit.setMargin(insets);
        jtbarBtnRem.setMargin(insets);
        jtbarBtnPlus.setMargin(insets);
        jtbarBtnMinus.setMargin(insets);
        jtbarBtnHelp.setMargin(insets);

        // //////////////////////////////////
        // Panel1
        // //////////////////////////////////
        jPanel1.setLayout(new BorderLayout(5, 10));
        jPanel1.add(lblSum, BorderLayout.NORTH);
        jPanel1.add(jtoolBar, BorderLayout.CENTER);

        // //////////////////////////////////
        // Panel2
        // //////////////////////////////////
        jPanel2.setLayout(new BorderLayout(5, 10));
        jPanel2.add(scrollPane, BorderLayout.CENTER);

        // //////////////////////////////////
        // Panel2
        // //////////////////////////////////
        jPanel3.setLayout(new FlowLayout());
        jPanel3.add(jbtnOk);
        jPanel3.add(jbtnCancel);

        // //////////////////////////////////
        // Add panels to frame
        // //////////////////////////////////
        this.getContentPane().add(jPanel1, BorderLayout.NORTH);
        this.getContentPane().add(jPanel2, BorderLayout.CENTER);
        this.getContentPane().add(jPanel3, BorderLayout.SOUTH);
        this.setJMenuBar(jMenu);

        // //////////////////////////////////
        // Menubar
        // //////////////////////////////////
        jMenu.add(fileMenu);
        fileMenu.add(jmiAdd);
        fileMenu.add(jmiEdit);
        fileMenu.add(jmiRem);
        fileMenu.addSeparator();

        // //////////////////////////////////
        // Register listeners
        // //////////////////////////////////
        addWindowListener(this);

        // //////////////////////////////////
        // Set tooltips
        // //////////////////////////////////
        jtbarBtnPlus.setToolTipText("Add 5 minutes");
        jtbarBtnMinus.setToolTipText("Remove 5 minutes");
    }

    private void init() {
        super.setFont(this.jMenu, new Font("Tahoma", 0, 10), new Font("Tahoma", 0, 10));
        this.dispActData();
        lblSum.setText("Time = " + this.listModel.sumTimes());
        this.setTitle(FRAME_TITLE + " : " + fileName.substring(0, fileName.indexOf(".")));
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setVisible(FRAME_VISIBLE);
        this.setResizable(FRAME_RESIZABLE);
    }

    private void setActions() {
        this.jtbarBtnHelp.setAction(new ActionHelp("", ICON_HELPL));

        this.jmiAdd.setAction(new ActionAdd("New", ICON_ADDS, this));
        this.jtbarBtnAdd.setAction(new ActionAdd("", ICON_ADDL, this));

        this.jmiEdit.setAction(new ActionEdit("Edit", ICON_EDITS, this));
        this.jtbarBtnEdit.setAction(new ActionEdit("", ICON_EDITL, this));

        this.jmiRem.setAction(new ActionRemove("Remove", ICON_REMS, this));
        this.jtbarBtnRem.setAction(new ActionRemove("", ICON_REML, this));
    }

    /**
     * Handle window closing event
     */
    public void windowClosing(WindowEvent e) {
        int choice = JOptionPane.showConfirmDialog(this, "Save changes?", "Save changes", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.OK_OPTION) {
            try {
                this.fHandler.saveTimexFile(this.fileName.substring(0, fileName.indexOf(".")), this.listModel.elements());
                // Fake an event in order to re-load the list of files in the
                // Timex Reader
                this.owner.preBufferFiles(true);

            } catch (IOException ex) {
                super.exitError(ERROR_LOG, ex);
            }
        }
        dispose();
    }

    void setOwner(TmxReaderFrame owner) {
        this.owner = owner;
    }

    /**
     * Display the existing activities and their current elapsed time
     */
    private void dispActData() {
        this.listModel.clear();
        try {
            for (ActivityListObject activityListObject : fHandler.readTmxFile(this.fileName)) {
                this.listModel.addElement(activityListObject);
            }
        } catch (IOException ex) {
            super.exitError(ERROR_LOG, ex);
        }

    }

    /**
     * The "add" action class.
     * @author Peter Nerg
     * @version 1.0
     */
    private class ActionAdd extends AbstractAction {
        private TmxEditFrame owner;

        private ActionAdd(String name, Icon icon, TmxEditFrame owner) {
            super(name, icon);
            super.putValue(SHORT_DESCRIPTION, "New Activity");
            super.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
            this.owner = owner;
        }

        public void actionPerformed(ActionEvent e) {
            ActivityListObject listObj = listModel.newEditorActivity(owner, fHandler);
            if (listObj == null)
                return;

            repaint();
        }
    }

    /**
     * The "edit" action class.
     * @author Peter Nerg
     * @version 1.0
     */
    private class ActionEdit extends AbstractAction {
        private TmxEditFrame owner;

        private ActionEdit(String name, Icon icon, TmxEditFrame owner) {
            super(name, icon);
            super.putValue(SHORT_DESCRIPTION, "Edit Time");
            super.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
            this.owner = owner;
        }

        public void actionPerformed(ActionEvent e) {
            int selected = listActivities.getSelectedIndex();
            if (selected < 0)
                return;

            listModel.editActivity(owner, selected);
            lblSum.setText("Time = " + listModel.sumTimes());
            repaint();
        }
    }

    /**
     * The "remove" action class.
     * @author Peter Nerg
     * @version 1.0
     */
    private class ActionRemove extends AbstractAction {
        private TmxEditFrame owner;

        private ActionRemove(String name, Icon icon, TmxEditFrame owner) {
            super(name, icon);
            super.putValue(SHORT_DESCRIPTION, "Remove Activity");
            super.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
            this.owner = owner;
        }

        public void actionPerformed(ActionEvent e) {
            int selected = listActivities.getSelectedIndex();
            if (selected < 0)
                return;

            ActivityListObject listObj = listModel.removeActivity(owner, selected);
            if (listObj == null)
                return;

            fHandler.removeActitivity(listObj.getName());
            lblSum.setText("Time = " + listModel.sumTimes());
            repaint();

        }
    }

    /**
     * Internal action listener
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxEditFrame_jtbarBtnPlus_actionAdapter implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selected = listActivities.getSelectedIndex();
            if (selected < 0)
                return;

            listModel.getActivity(selected).addMinutes(5);

            lblSum.setText("Time = " + listModel.sumTimes());

            validate();
            repaint();
        }
    }

    /**
     * Internal action listener
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxEditFrame_jtbarBtnMinus_actionAdapter implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selected = listActivities.getSelectedIndex();
            if (selected < 0)
                return;

            ((ActivityListObject) listModel.getElementAt(selected)).subtract5Minutes();

            lblSum.setText("Time = " + listModel.sumTimes());

            validate();
            repaint();
        }
    }

    /**
     * Internal action listener
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxEditFrame_jbtnOk_actionAdapter implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                fHandler.saveTimexFile(fileName.substring(0, fileName.indexOf(".")), listModel.elements());
                // force the Timex reader to re-load the view
                owner.preBufferFiles(true);
            } catch (IOException ex) {
                exitError(ERROR_LOG, ex);
            }
            dispose();
        }
    }

    /**
     * Internal action listener
     * @author Peter Nerg
     * @version 1.0
     */
    private class TmxEditFrame_jbtnCancel_actionAdapter implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }
}