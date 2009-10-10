package org.dmonix.timex.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.dmonix.gui.models.SortableComboBoxModel;
import org.dmonix.timex.tmxfilehandler.TimexFileHandler;

/**
 * Dialog for editing acitivities.
 * 
 * @author Peter Nerg
 * @version 1.0
 */
public class ActivityDialogEditor extends JDialog {
    private boolean okPressed = false;

    private transient SortableComboBoxModel modelCombo = new SortableComboBoxModel();

    private transient TimexFileHandler timexFileHandler;

    private JPanel panelMain = new JPanel();

    private GridBagLayout gridBagLayout1 = new GridBagLayout();

    private JTextField txtName = new JTextField();

    private JPanel jPanel2 = new JPanel();

    private FlowLayout flowLayout1 = new FlowLayout();

    private JButton btnCancel = new JButton();

    private JButton btnOk = new JButton();

    private JRadioButton rdBtnNew = new JRadioButton();

    private JComboBox comboActivites = new JComboBox();

    private JRadioButton rdBtnExisting = new JRadioButton();

    private ButtonGroup buttonGroup = new ButtonGroup();

    public ActivityDialogEditor(Frame frame, TimexFileHandler timexFileHandler) {
        super(frame, "New Activity", true);
        this.timexFileHandler = timexFileHandler;
        try {
            jbInit();
            init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getDescription() {
        String text;
        if (this.rdBtnNew.isSelected())
            text = "";
        else
            text = ((InternalListObject) this.comboActivites.getSelectedItem()).getDescription();

        return text;
    }

    public String getNameTxt() {
        String text;
        if (this.rdBtnNew.isSelected())
            text = this.txtName.getText();
        else
            text = this.comboActivites.getSelectedItem().toString();

        return text;
    }

    private void jbInit() throws Exception {
        modelCombo.clear();
        comboActivites.setEnabled(true);
        comboActivites.setFont(new java.awt.Font("Dialog", 0, 10));
        comboActivites.setModel(modelCombo);
        comboActivites.setRenderer(new InternalListCellRenderer());
        panelMain.setLayout(gridBagLayout1);
        txtName.setFont(new java.awt.Font("Dialog", 0, 10));
        txtName.setText("");
        jPanel2.setLayout(flowLayout1);
        btnCancel.setFont(new java.awt.Font("Dialog", 0, 10));
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new ActivityDialogEditor_btnCancel_actionAdapter(this));
        btnOk.setFont(new java.awt.Font("Dialog", 0, 10));
        btnOk.setText("Ok");
        btnOk.addActionListener(new ActivityDialogEditor_btnOk_actionAdapter(this));
        rdBtnNew.setFont(new java.awt.Font("Dialog", 0, 10));
        rdBtnNew.setSelected(true);
        rdBtnNew.setText("New");
        rdBtnNew.addActionListener(new ActivityDialogEditor_rdBtnNew_actionAdapter(this));
        rdBtnExisting.setFont(new java.awt.Font("Dialog", 0, 10));
        rdBtnExisting.setText("Existing");
        rdBtnExisting.addActionListener(new ActivityDialogEditor_rdBtnExisting_actionAdapter(this));
        getContentPane().add(panelMain);
        panelMain.add(txtName, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5,
                10), 0, 0));
        panelMain.add(rdBtnNew, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5),
                0, 0));
        this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
        jPanel2.add(btnOk, null);
        jPanel2.add(btnCancel, null);
        panelMain.add(comboActivites, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,
                5, 10, 10), 0, 0));
        panelMain.add(rdBtnExisting, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 10, 10,
                5), 0, 0));

        super.setDefaultCloseOperation(super.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setSize(new Dimension(300, 130));
        this.validate();
        this.setVisible(false);
        buttonGroup.add(rdBtnNew);
        buttonGroup.add(rdBtnExisting);
    }

    private void init() {
        this.rdBtn_actionPerformed(null);
        for (String activity : this.timexFileHandler.getActivites()) {
            InternalListObject listObj = new InternalListObject(activity, this.timexFileHandler.getActivityDescription(activity));

            this.modelCombo.addElement(listObj);
        }
        this.modelCombo.sort();

        if (this.timexFileHandler.getActivites().size() > 0)
            this.comboActivites.setSelectedIndex(0);
        else
            this.rdBtnExisting.setEnabled(false);
    }

    public boolean showDialog() {
        this.setVisible(true);
        return this.okPressed;
    }

    void btnOk_actionPerformed(ActionEvent e) {
        super.setVisible(false);
        this.okPressed = true;
    }

    void btnCancel_actionPerformed(ActionEvent e) {
        super.setVisible(false);
    }

    void rdBtn_actionPerformed(ActionEvent e) {
        this.txtName.setEditable(!this.rdBtnExisting.isSelected());
        this.comboActivites.setEnabled(this.rdBtnExisting.isSelected());
    }

    /**
     * The internal cell renderer for the combo box with defined activities.
     * @author Peter Nerg
     * @version 1.0
     */
    private class InternalListCellRenderer extends DefaultListCellRenderer {
        private InternalListCellRenderer() {
            super();
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            String desc = ((InternalListObject) value).getDescription();
            if (desc != null)
                super.setToolTipText(desc);

            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }

    }

    /**
     * The object to use in the combo box with defined activities.
     * @author Peter Nerg
     * @version 1.0
     */
    private class InternalListObject implements Comparable {
        private String name;

        private String description;

        private InternalListObject(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String toString() {
            return this.name;
        }

        String getDescription() {
            return this.description;
        }

        public int compareTo(Object o) {
            return this.toString().compareToIgnoreCase(o.toString());
        }
    }

    private class ActivityDialogEditor_btnOk_actionAdapter implements java.awt.event.ActionListener {
        ActivityDialogEditor adaptee;

        ActivityDialogEditor_btnOk_actionAdapter(ActivityDialogEditor adaptee) {
            this.adaptee = adaptee;
        }

        public void actionPerformed(ActionEvent e) {
            adaptee.btnOk_actionPerformed(e);
        }
    }

    private class ActivityDialogEditor_btnCancel_actionAdapter implements java.awt.event.ActionListener {
        ActivityDialogEditor adaptee;

        ActivityDialogEditor_btnCancel_actionAdapter(ActivityDialogEditor adaptee) {
            this.adaptee = adaptee;
        }

        public void actionPerformed(ActionEvent e) {
            adaptee.btnCancel_actionPerformed(e);
        }
    }

    private class ActivityDialogEditor_rdBtnNew_actionAdapter implements java.awt.event.ActionListener {
        ActivityDialogEditor adaptee;

        ActivityDialogEditor_rdBtnNew_actionAdapter(ActivityDialogEditor adaptee) {
            this.adaptee = adaptee;
        }

        public void actionPerformed(ActionEvent e) {
            adaptee.rdBtn_actionPerformed(e);
        }
    }

    private class ActivityDialogEditor_rdBtnExisting_actionAdapter implements java.awt.event.ActionListener {
        ActivityDialogEditor adaptee;

        ActivityDialogEditor_rdBtnExisting_actionAdapter(ActivityDialogEditor adaptee) {
            this.adaptee = adaptee;
        }

        public void actionPerformed(ActionEvent e) {
            adaptee.rdBtn_actionPerformed(e);
        }
    }
}
