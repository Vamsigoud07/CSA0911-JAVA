package com.campus.ui;

import com.campus.model.Event;
import com.campus.model.Registration;
import com.campus.service.RegistrationService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * RegistrationPanel - Form for student event registration.
 * Executes database registration asynchronously inside SwingWorker to keep EDT responsive.
 */
public class RegistrationPanel extends JPanel {
    private final RegistrationService service;
    private final Runnable onRegistrationSuccess;

    private JTextField studentIdField;
    private JTextField studentNameField;
    private JTextField emailField;
    private JComboBox<String> departmentCombo;
    private JComboBox<Event> eventCombo;
    private JButton registerButton;
    private JButton clearButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    public RegistrationPanel(RegistrationService service, Runnable onRegistrationSuccess) {
        this.service = service;
        this.onRegistrationSuccess = onRegistrationSuccess;
        initUI();
        loadEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Form Panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Student Event Registration Form"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Student ID
        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Student Register No:"), g);
        g.gridx = 1; g.gridy = 0;
        studentIdField = new JTextField(18);
        form.add(studentIdField, g);

        // Row 1: Student Name
        g.gridx = 0; g.gridy = 1;
        form.add(new JLabel("Student Full Name:"), g);
        g.gridx = 1; g.gridy = 1;
        studentNameField = new JTextField(18);
        form.add(studentNameField, g);

        // Row 2: Email
        g.gridx = 0; g.gridy = 2;
        form.add(new JLabel("University Email:"), g);
        g.gridx = 1; g.gridy = 2;
        emailField = new JTextField(18);
        form.add(emailField, g);

        // Row 3: Department
        g.gridx = 0; g.gridy = 3;
        form.add(new JLabel("Academic Department:"), g);
        g.gridx = 1; g.gridy = 3;
        String[] depts = {
                "Computer Science & Engineering",
                "Information Technology",
                "Electronics & Communication",
                "Electrical & Electronics",
                "Mechanical Engineering",
                "Biotechnology"
        };
        departmentCombo = new JComboBox<>(depts);
        form.add(departmentCombo, g);

        // Row 4: Event
        g.gridx = 0; g.gridy = 4;
        form.add(new JLabel("Select Campus Event:"), g);
        g.gridx = 1; g.gridy = 4;
        eventCombo = new JComboBox<>();
        form.add(eventCombo, g);

        // Row 5: Progress Bar
        g.gridx = 0; g.gridy = 5;
        g.gridwidth = 2;
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        form.add(progressBar, g);

        // Row 6: Status Message
        g.gridx = 0; g.gridy = 6;
        g.gridwidth = 2;
        statusLabel = new JLabel("Fill out all fields and click Register.", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(60, 60, 60));
        form.add(statusLabel, g);

        add(form, BorderLayout.CENTER);

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        registerButton = new JButton("Register for Event");
        registerButton.setBackground(new Color(30, 130, 70));
        registerButton.setForeground(Color.WHITE);

        clearButton = new JButton("Clear Form");
        clearButton.setBackground(new Color(160, 50, 50));
        clearButton.setForeground(Color.WHITE);

        btnPanel.add(registerButton);
        btnPanel.add(clearButton);
        add(btnPanel, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> handleRegister());
        clearButton.addActionListener(e -> clearForm());
    }

    public void loadEvents() {
        SwingWorker<List<Event>, Void> worker = new SwingWorker<List<Event>, Void>() {
            @Override
            protected List<Event> doInBackground() throws Exception {
                return service.getEvents();
            }

            @Override
            protected void done() {
                try {
                    List<Event> list = get();
                    eventCombo.removeAllItems();
                    for (Event ev : list) {
                        eventCombo.addItem(ev);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Failed to load events: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void handleRegister() {
        final String sid = studentIdField.getText().trim();
        final String name = studentNameField.getText().trim();
        final String email = emailField.getText().trim();
        final String dept = (String) departmentCombo.getSelectedItem();
        final Event event = (Event) eventCombo.getSelectedItem();

        if (event == null) {
            JOptionPane.showMessageDialog(this, "Please select an event.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Disable controls and show progress
        setControlsEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setText("Processing registration with database...");

        SwingWorker<Registration, Void> worker = new SwingWorker<Registration, Void>() {
            @Override
            protected Registration doInBackground() throws Exception {
                // Background execution off the EDT
                return service.registerStudent(sid, name, email, dept, event.getEventId());
            }

            @Override
            protected void done() {
                setControlsEnabled(true);
                progressBar.setVisible(false);
                try {
                    Registration reg = get();
                    statusLabel.setForeground(new Color(0, 130, 0));
                    statusLabel.setText("Registered! ID: " + reg.getRegistrationId());

                    String msg = "========================================\n"
                               + "     EVENT REGISTRATION SUCCESSFUL\n"
                               + "========================================\n"
                               + " Registration ID : " + reg.getRegistrationId() + "\n"
                               + " Student ID      : " + reg.getStudentId() + "\n"
                               + " Student Name    : " + reg.getStudentName() + "\n"
                               + " Event           : " + reg.getEventName() + "\n"
                               + " Department      : " + reg.getDepartment() + "\n"
                               + " Registered Date : " + reg.getFormattedDate() + "\n"
                               + "========================================";

                    JOptionPane.showMessageDialog(RegistrationPanel.this, msg,
                            "Registration Confirmed", JOptionPane.INFORMATION_MESSAGE);

                    clearForm();
                    loadEvents(); // Refresh seat counts
                    if (onRegistrationSuccess != null) {
                        onRegistrationSuccess.run();
                    }
                } catch (Exception ex) {
                    statusLabel.setForeground(Color.RED);
                    String errorMsg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    statusLabel.setText("Failed: " + errorMsg);
                    JOptionPane.showMessageDialog(RegistrationPanel.this,
                            errorMsg, "Registration Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    public void clearForm() {
        studentIdField.setText("");
        studentNameField.setText("");
        emailField.setText("");
        departmentCombo.setSelectedIndex(0);
        if (eventCombo.getItemCount() > 0) eventCombo.setSelectedIndex(0);
        statusLabel.setForeground(new Color(60, 60, 60));
        statusLabel.setText("Ready for next student registration.");
    }

    private void setControlsEnabled(boolean enabled) {
        studentIdField.setEnabled(enabled);
        studentNameField.setEnabled(enabled);
        emailField.setEnabled(enabled);
        departmentCombo.setEnabled(enabled);
        eventCombo.setEnabled(enabled);
        registerButton.setEnabled(enabled);
        clearButton.setEnabled(enabled);
    }

    // Accessors for automation
    public JTextField getStudentIdField() { return studentIdField; }
    public JTextField getStudentNameField() { return studentNameField; }
    public JTextField getEmailField() { return emailField; }
    public JComboBox<String> getDepartmentCombo() { return departmentCombo; }
    public JComboBox<Event> getEventCombo() { return eventCombo; }
    public JButton getRegisterButton() { return registerButton; }
    public JLabel getStatusLabel() { return statusLabel; }
}
