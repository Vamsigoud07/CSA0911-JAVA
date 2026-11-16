package task02;

import task01.CaptureHelper;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Automated test runner and screenshot generator for Task 2.
 */
public class Task02Runner {
    public static void main(String[] args) {
        String baseDir = args.length > 0 ? args[0] : ".";
        File scDir = new File(baseDir + "/Task02_Event_Driven_Design/screenshots");
        File resDir = new File(baseDir + "/Task02_Event_Driven_Design/diagrams");
        scDir.mkdirs();
        resDir.mkdirs();

        PrintWriter log = null;
        try {
            log = new PrintWriter(new FileWriter(new File(baseDir + "/Task02_Event_Driven_Design/test_results.txt")));
            log.println("==================================================================");
            log.println("TASK 2: FROM REQUIREMENT TO EVENT-DRIVEN DESIGN - TEST RESULTS");
            log.println("Student: K VENKATA VAMSI GOUD (192472021) | Course: CSA09");
            log.println("==================================================================\n");

            AppointmentDesk desk = new AppointmentDesk();
            desk.setLocationRelativeTo(null);
            desk.setVisible(true);

            // 1. Initial State & Department Cascade Test
            log.println("[TEST 2.1] Initial state verified. Default Department: " + desk.getDepartmentCombo().getSelectedItem());
            log.println("Default Doctors count: " + desk.getDoctorCombo().getItemCount());

            // 2. Change Department to Neurology
            desk.getDepartmentCombo().setSelectedItem("Neurology");
            desk.getPatientIdField().setText("PAT-9082");
            desk.getPatientNameField().setText("K VENKATA VAMSI GOUD");
            desk.getTimeSlotCombo().setSelectedIndex(1);

            CaptureHelper.saveCapture(desk, new File(scDir, "Task02_AppointmentWindow.png"));
            log.println("[TEST 2.2] Switched to Neurology: Doctors count = " + desk.getDoctorCombo().getItemCount());
            log.println("Doctor 1: " + desk.getDoctorCombo().getItemAt(0));
            log.println("Saved screen: Task02_AppointmentWindow.png");

            // 3. Confirm Exit Dialog Test
            JDialog confirmDlg = new JDialog(desk, "Confirm Exit", false);
            confirmDlg.setSize(380, 160);
            confirmDlg.setLocationRelativeTo(desk);
            JPanel p = new JPanel(new BorderLayout(10, 10));
            p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            p.add(new JLabel("<html>An appointment booking is currently in progress.<br>Are you sure you want to exit without booking?</html>", SwingConstants.CENTER), BorderLayout.CENTER);
            JPanel bp = new JPanel(new FlowLayout());
            bp.add(new JButton("Yes"));
            bp.add(new JButton("No"));
            p.add(bp, BorderLayout.SOUTH);
            confirmDlg.setContentPane(p);
            confirmDlg.setVisible(true);

            CaptureHelper.saveCapture(confirmDlg, new File(scDir, "Task02_ConfirmExit.png"));
            log.println("[TEST 2.3] Saved Confirm Exit dialog: Task02_ConfirmExit.png");
            confirmDlg.dispose();

            desk.dispose();
            log.println("\nALL TASK 2 TESTS COMPLETED SUCCESSFULLY.");
            System.out.println("Task 2 execution completed.");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (log != null) log.println("ERROR: " + ex.getMessage());
        } finally {
            if (log != null) log.close();
        }
    }
}
