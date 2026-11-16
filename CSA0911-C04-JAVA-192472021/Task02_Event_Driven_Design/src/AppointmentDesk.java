package task02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Hospital Outpatient Appointment Desk Application.
 * Demonstrates:
 * - Event-Driven Architecture with JComboBox ItemListener
 * - Dynamic cascade-refresh of doctor list upon department selection
 * - Form validation and booking confirmation
 * - Custom WindowClosing confirmation dialog when unsaved input exists
 */
public class AppointmentDesk extends JFrame {
    private JTextField patientIdField;
    private JTextField patientNameField;
    private JComboBox<String> departmentCombo;
    private JComboBox<Doctor> doctorCombo;
    private JComboBox<String> timeSlotCombo;
    private JButton bookButton;
    private JButton clearButton;
    private JLabel statusLabel;

    // In-memory catalog mapping department to doctor roster
    private final Map<String, List<Doctor>> doctorCatalog = new LinkedHashMap<>();

    public AppointmentDesk() {
        initDoctorCatalog();
        initUI();
    }

    private void initDoctorCatalog() {
        doctorCatalog.put("Cardiology", Arrays.asList(
                new Doctor("DOC-C1", "Dr. A. Sharma", "Cardiology", "MD, DM"),
                new Doctor("DOC-C2", "Dr. B. Raman", "Cardiology", "MD, FACC")
        ));
        doctorCatalog.put("Neurology", Arrays.asList(
                new Doctor("DOC-N1", "Dr. C. Iyer", "Neurology", "MD, DM (Neuro)"),
                new Doctor("DOC-N2", "Dr. D. Varma", "Neurology", "MS, MCh")
        ));
        doctorCatalog.put("Orthopedics", Arrays.asList(
                new Doctor("DOC-O1", "Dr. E. Menon", "Orthopedics", "MS (Ortho)"),
                new Doctor("DOC-O2", "Dr. F. Das", "Orthopedics", "DNB, FRCS")
        ));
        doctorCatalog.put("Pediatrics", Arrays.asList(
                new Doctor("DOC-P1", "Dr. G. Nair", "Pediatrics", "MD, DCH"),
                new Doctor("DOC-P2", "Dr. H. Kulkarni", "Pediatrics", "MD, FIAP")
        ));
        doctorCatalog.put("General Medicine", Arrays.asList(
                new Doctor("DOC-G1", "Dr. I. Reddy", "General Medicine", "MD (Gen Med)"),
                new Doctor("DOC-G2", "Dr. J. Patil", "General Medicine", "MBBS, DNB")
        ));
    }

    private void initUI() {
        setTitle("SIMATS Hospital - Outpatient Appointment Desk");
        setSize(520, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 1. Header Panel
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(18, 90, 80));
        JLabel title = new JLabel("OUTPATIENT APPOINTMENT BOOKING");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // 2. Form Panel (GridBagLayout)
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Patient ID
        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Patient ID:"), g);
        g.gridx = 1; g.gridy = 0;
        patientIdField = new JTextField(18);
        form.add(patientIdField, g);

        // Row 1: Patient Name
        g.gridx = 0; g.gridy = 1;
        form.add(new JLabel("Patient Name:"), g);
        g.gridx = 1; g.gridy = 1;
        patientNameField = new JTextField(18);
        form.add(patientNameField, g);

        // Row 2: Department
        g.gridx = 0; g.gridy = 2;
        form.add(new JLabel("Department:"), g);
        g.gridx = 1; g.gridy = 2;
        departmentCombo = new JComboBox<>(doctorCatalog.keySet().toArray(new String[0]));
        form.add(departmentCombo, g);

        // Row 3: Doctor Selection
        g.gridx = 0; g.gridy = 3;
        form.add(new JLabel("Doctor:"), g);
        g.gridx = 1; g.gridy = 3;
        doctorCombo = new JComboBox<>();
        form.add(doctorCombo, g);

        // Row 4: Time Slot
        g.gridx = 0; g.gridy = 4;
        form.add(new JLabel("Time Slot:"), g);
        g.gridx = 1; g.gridy = 4;
        String[] slots = {"09:00 AM - 10:00 AM", "10:30 AM - 11:30 AM", "02:00 PM - 03:00 PM", "04:00 PM - 05:00 PM"};
        timeSlotCombo = new JComboBox<>(slots);
        form.add(timeSlotCombo, g);

        // Row 5: Status Label
        g.gridx = 0; g.gridy = 5;
        g.gridwidth = 2;
        statusLabel = new JLabel("Please enter patient details and choose slot.", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(60, 60, 60));
        form.add(statusLabel, g);

        add(form, BorderLayout.CENTER);

        // 3. Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        bookButton = new JButton("Book Appointment");
        clearButton = new JButton("Clear Form");

        bookButton.setBackground(new Color(30, 130, 70));
        bookButton.setForeground(Color.WHITE);
        clearButton.setBackground(new Color(160, 50, 50));
        clearButton.setForeground(Color.WHITE);

        buttonPanel.add(bookButton);
        buttonPanel.add(clearButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 4. Register Event Listeners
        departmentCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                refreshDoctorList((String) departmentCombo.getSelectedItem());
            }
        });

        bookButton.addActionListener(e -> validateAndBookAppointment());
        clearButton.addActionListener(e -> clearForm());

        // Keyboard Shortcut: Enter on Patient Name triggers booking
        patientNameField.addActionListener(e -> validateAndBookAppointment());

        // Window Closing Handler with Confirmation Dialog
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });

        // Initialize doctor list for default department
        refreshDoctorList((String) departmentCombo.getSelectedItem());
    }

    public void refreshDoctorList(String department) {
        doctorCombo.removeAllItems();
        List<Doctor> doctors = doctorCatalog.get(department);
        if (doctors != null) {
            for (Doctor d : doctors) {
                doctorCombo.addItem(d);
            }
        }
        statusLabel.setText("Doctors loaded for " + department + " (" + (doctors != null ? doctors.size() : 0) + " available).");
    }

    public void validateAndBookAppointment() {
        String pid = patientIdField.getText().trim();
        String name = patientNameField.getText().trim();

        if (pid.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Validation Error: Patient ID is required.");
            JOptionPane.showMessageDialog(this, "Please enter Patient ID.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            patientIdField.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Validation Error: Patient Name is required.");
            JOptionPane.showMessageDialog(this, "Please enter Patient Name.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            patientNameField.requestFocus();
            return;
        }

        Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
        String selectedSlot = (String) timeSlotCombo.getSelectedItem();
        String dept = (String) departmentCombo.getSelectedItem();

        String appointmentToken = "APP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        statusLabel.setForeground(new Color(0, 120, 40));
        statusLabel.setText("Booked: Token " + appointmentToken);

        String message = "========================================\n"
                       + "     APPOINTMENT CONFIRMATION\n"
                       + "========================================\n"
                       + " Booking Token  : " + appointmentToken + "\n"
                       + " Patient ID     : " + pid + "\n"
                       + " Patient Name   : " + name + "\n"
                       + " Department     : " + dept + "\n"
                       + " Doctor         : " + selectedDoctor + "\n"
                       + " Time Slot      : " + selectedSlot + "\n"
                       + " Status         : CONFIRMED\n"
                       + "========================================";

        JOptionPane.showMessageDialog(this, message, "Appointment Confirmed", JOptionPane.INFORMATION_MESSAGE);
    }

    public void handleWindowClosing() {
        boolean hasUnsavedData = !patientIdField.getText().trim().isEmpty() ||
                                 !patientNameField.getText().trim().isEmpty();

        if (hasUnsavedData) {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "An appointment booking is currently in progress.\nAre you sure you want to exit without booking?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (option == JOptionPane.YES_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    private void clearForm() {
        patientIdField.setText("");
        patientNameField.setText("");
        departmentCombo.setSelectedIndex(0);
        timeSlotCombo.setSelectedIndex(0);
        statusLabel.setForeground(new Color(60, 60, 60));
        statusLabel.setText("Form cleared. Ready for new booking.");
    }

    // Accessors for testing
    public JTextField getPatientIdField() { return patientIdField; }
    public JTextField getPatientNameField() { return patientNameField; }
    public JComboBox<String> getDepartmentCombo() { return departmentCombo; }
    public JComboBox<Doctor> getDoctorCombo() { return doctorCombo; }
    public JComboBox<String> getTimeSlotCombo() { return timeSlotCombo; }
    public JButton getBookButton() { return bookButton; }
    public JLabel getStatusLabel() { return statusLabel; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppointmentDesk().setVisible(true));
    }
}
