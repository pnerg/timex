package org.dmonix.timex.gui;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

/**
 * Dialog for creating activities.
 * @author Peter Nerg
 * @version 1.0
 */
public class ActivityDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private final JPanel jPanel1 = new JPanel();
    private final GridBagLayout gridBagLayout1 = new GridBagLayout();
    private final JLabel labelName = new JLabel();
    private final JTextField txtName = new JTextField();
    private final JLabel labelDesc = new JLabel();
    private final JTextArea txtDesc = new JTextArea();
    private final JPanel jPanel2 = new JPanel();
    private final FlowLayout flowLayout1 = new FlowLayout();
    private final JButton btnCancel = new JButton();
    private final JButton btnOk = new JButton();

    private boolean okPressed = false;

    public ActivityDialog(Frame frame, boolean allowDescription,boolean editDescription) {
        super(frame, "New Activity", true);
        try {
            jbInit();

            this.txtDesc.setVisible(allowDescription);
            this.labelDesc.setVisible(allowDescription);
            this.txtName.setEditable(!editDescription);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        jPanel1.setLayout(gridBagLayout1);
        labelName.setFont(new java.awt.Font("Dialog", 0, 10));
        labelName.setText("Name");
        labelDesc.setFont(new java.awt.Font("Dialog", 0, 10));
        labelDesc.setText("Description");
        txtName.setFont(new java.awt.Font("SansSerif", 0, 10));
        txtName.setText("");
        txtDesc.setFont(new java.awt.Font("SansSerif", 0, 10));
        txtDesc.setBorder(BorderFactory.createLoweredBevelBorder());
        txtDesc.setText("");
        txtDesc.setLineWrap(true);
        jPanel2.setLayout(flowLayout1);
        btnCancel.setFont(new java.awt.Font("Dialog", 0, 10));
        btnCancel.setText("Cancel");
        btnCancel
                .addActionListener(new ActivityDialog_btnCancel_actionAdapter());
        btnOk.setFont(new java.awt.Font("Dialog", 0, 10));
        btnOk.setText("Ok");
        btnOk.addActionListener(new ActivityDialog_btnOk_actionAdapter());
        getContentPane().add(jPanel1);
        jPanel1.add(labelName, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(10, 5, 0, 0), 0, 0));
        jPanel1.add(txtName, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 0, 10), 0, 0));
        jPanel1.add(labelDesc, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(10, 5, 0, 0), 0, 0));
        jPanel1.add(txtDesc, new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        10, 5, 0, 10), 0, 0));
        this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
        jPanel2.add(btnOk, null);
        jPanel2.add(btnCancel, null);

        super.setDefaultCloseOperation(super.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setSize(300, 200);
        this.validate();
        this.setVisible(false);
    }

    public boolean showDialog() {
        this.setVisible(true);
        return this.okPressed;
    }

    public String getDescriptionTxt() {
        return this.txtDesc.getText();
    }

    public String getNameTxt() {
        return this.txtName.getText();
    }

    private class ActivityDialog_btnOk_actionAdapter implements
            java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
            okPressed = true;
        }
    }

    private class ActivityDialog_btnCancel_actionAdapter implements
            java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    }

}
