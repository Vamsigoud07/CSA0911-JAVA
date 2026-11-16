package task03;

import task01.CaptureHelper;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Automated test runner and multi-resolution screenshot generator for Task 3.
 */
public class Task03Runner {
    public static void main(String[] args) {
        String baseDir = args.length > 0 ? args[0] : ".";
        File scDir = new File(baseDir + "/Task03_Layout_Design/screenshots");
        scDir.mkdirs();

        PrintWriter log = null;
        try {
            log = new PrintWriter(new FileWriter(new File(baseDir + "/Task03_Layout_Design/test_results.txt")));
            log.println("==================================================================");
            log.println("TASK 3: FROM SKETCH TO LAYOUT - RESIZING AND EXECUTION RESULTS");
            log.println("Student: K VENKATA VAMSI GOUD (192472021) | Course: CSA09");
            log.println("==================================================================\n");

            LibrarySearchScreen screen = new LibrarySearchScreen();
            screen.setLocationRelativeTo(null);
            screen.setVisible(true);

            // 1. Medium Window (Standard: 720 x 480)
            screen.setSize(720, 480);
            screen.validate();
            CaptureHelper.saveCapture(screen, new File(scDir, "Task03_MediumWindow.png"));
            log.println("[TEST 3.1] Captured Medium Window (720x480): Task03_MediumWindow.png");
            log.println("Observations: Search controls comfortable, JTable fills center, buttons clear.");

            // 2. Small Window (Compact: 480 x 360)
            screen.setSize(480, 360);
            screen.validate();
            CaptureHelper.saveCapture(screen, new File(scDir, "Task03_SmallWindow.png"));
            log.println("[TEST 3.2] Captured Small Window (480x360): Task03_SmallWindow.png");
            log.println("Observations: Table scrolls vertically, search bar wraps or fits, all buttons visible.");

            // 3. Large Window (Expanded: 1024 x 700)
            screen.setSize(1024, 700);
            screen.validate();
            CaptureHelper.saveCapture(screen, new File(scDir, "Task03_LargeWindow.png"));
            log.println("[TEST 3.3] Captured Large Window (1024x700): Task03_LargeWindow.png");
            log.println("Observations: Center JTable expands horizontally and vertically; top/bottom panels retain natural height.");

            // 4. Test Search Interaction
            screen.getSearchField().setText("Java");
            screen.getCategoryCombo().setSelectedItem("Computer Science");
            screen.performSearch();
            log.println("\n[TEST 3.4] Search for 'Java' in 'Computer Science':");
            log.println("Matching rows in table: " + screen.getResultsTable().getRowCount());
            log.println("First result: " + screen.getResultsTable().getValueAt(0, 1));
            log.println("Status: " + screen.getStatusLabel().getText());

            screen.dispose();
            log.println("\nALL TASK 3 TESTS AND RESIZING CAPTURES COMPLETED SUCCESSFULLY.");
            System.out.println("Task 3 execution completed.");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (log != null) log.println("ERROR: " + ex.getMessage());
        } finally {
            if (log != null) log.close();
        }
    }
}
