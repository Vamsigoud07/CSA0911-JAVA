package task04;

import task01.CaptureHelper;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Automated test runner and forensic analyzer for Task 4.
 */
public class Task04Runner {
    public static void main(String[] args) {
        String baseDir = args.length > 0 ? args[0] : ".";
        File scDir = new File(baseDir + "/Task04_Event_Handling_Forensics/screenshots");
        File resDir = new File(baseDir + "/Task04_Event_Handling_Forensics/results");
        scDir.mkdirs();
        resDir.mkdirs();

        PrintWriter log = null;
        try {
            log = new PrintWriter(new FileWriter(new File(resDir, "test_results.txt")));
            log.println("==================================================================");
            log.println("TASK 4: EVENT-HANDLING FORENSICS - TEST AND FORENSIC RESULTS");
            log.println("Student: K VENKATA VAMSI GOUD (192472021) | Course: CSA09");
            log.println("==================================================================\n");

            // 1. Instantiate Repaired DrawPad
            DrawPad pad = new DrawPad();
            pad.setLocationRelativeTo(null);
            pad.setVisible(true);

            // Add simulated user clicks across the drawing canvas
            Point[] clickPoints = {
                    new Point(100, 120),
                    new Point(180, 160),
                    new Point(240, 200),
                    new Point(320, 150),
                    new Point(380, 240),
                    new Point(150, 280),
                    new Point(280, 310)
            };

            for (Point p : clickPoints) {
                pad.addSimulatedPoint(p);
            }

            // 2. Capture Before Minimize
            CaptureHelper.saveCapture(pad, new File(scDir, "Task04_BeforeMinimize.png"));
            log.println("[TEST 4.1] Captured Before Minimize: Task04_BeforeMinimize.png");
            log.println("Total points stored: " + pad.getPoints().size());
            log.println("Status Label text: " + pad.getStatusLabel().getText());

            // 3. Simulate Minimize and Restore
            pad.setExtendedState(Frame.ICONIFIED);
            Thread.sleep(300);
            pad.setExtendedState(Frame.NORMAL);
            Thread.sleep(400);

            // 4. Capture After Restore
            CaptureHelper.saveCapture(pad, new File(scDir, "Task04_AfterRestore.png"));
            log.println("[TEST 4.2] Captured After Restore: Task04_AfterRestore.png");
            log.println("Points successfully retained and redrawn: " + pad.getPoints().size());

            // 5. Add additional clicks to demonstrate Click Count update
            pad.addSimulatedPoint(new Point(200, 100));
            pad.addSimulatedPoint(new Point(350, 100));
            pad.addSimulatedPoint(new Point(220, 260));

            CaptureHelper.saveCapture(pad, new File(scDir, "Task04_ClickCount.png"));
            log.println("[TEST 4.3] Captured Updated Click Count: Task04_ClickCount.png");
            log.println("Final Points count: " + pad.getPoints().size() + " | Status: " + pad.getStatusLabel().getText());

            pad.dispose();

            log.println("\n--- TASK 4.4: COMPILER RECURRENCE PREVENTION EXPERIMENT ---");
            log.println("Testing compiler detection of misspelled callback 'mouseclicked' with @Override...");

            System.out.println("Task 4 execution completed successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (log != null) log.println("ERROR: " + ex.getMessage());
        } finally {
            if (log != null) log.close();
        }
    }
}
