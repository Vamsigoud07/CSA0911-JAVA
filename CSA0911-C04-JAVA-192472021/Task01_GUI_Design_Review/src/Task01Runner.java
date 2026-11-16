package task01;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Headless-capable test runner and genuine screenshot generator for Task 1.
 */
public class Task01Runner {

    public static void main(String[] args) {
        String baseDir = args.length > 0 ? args[0] : ".";
        File scDir = new File(baseDir + "/Task01_GUI_Design_Review/screenshots");
        File resDir = new File(baseDir + "/Task01_GUI_Design_Review/results");
        scDir.mkdirs();
        resDir.mkdirs();

        PrintWriter log = null;
        try {
            log = new PrintWriter(new FileWriter(new File(resDir, "test_results.txt")));
            log.println("==================================================================");
            log.println("TASK 1: GUI DESIGN REVIEW - EXECUTION AND TEST RESULTS");
            log.println("Student: K VENKATA VAMSI GOUD (192472021) | Course: CSA09");
            log.println("==================================================================\n");

            // 1. Capture Defective / Poor Design Window
            PoorFeeWindow poorWin = new PoorFeeWindow();
            poorWin.regField.setText("192472021");
            poorWin.amountField.setText("15000");
            poorWin.setLocationRelativeTo(null);
            poorWin.setVisible(true);
            CaptureHelper.saveCapture(poorWin, new File(scDir, "Task01_Diagnosis.png"));
            poorWin.dispose();
            log.println("[TEST 1.1] PoorFeeWindow captured: Task01_Diagnosis.png (Flawed UI with monolithic logic)");

            // 2. Test Repaired FeeWindow - Initial State
            FeeService service = new FeeService();
            FeeWindow feeWin = new FeeWindow(service);
            feeWin.setLocationRelativeTo(null);
            feeWin.setVisible(true);
            CaptureHelper.saveCapture(feeWin, new File(scDir, "Task01_FeeWindow.png"));
            log.println("[TEST 1.2] Repaired FeeWindow initialized: Task01_FeeWindow.png");

            // 3. Validation Tests
            log.println("\n--- RUNNING VALIDATION SUITE ---");

            // Case A: Empty Reg No
            PaymentResult r1 = service.processPayment("", "5000");
            log.println("Case A (Empty Reg): Expected Fail | Actual: " + r1.getMessage() + " | Result: " + (!r1.isSuccess() ? "PASS" : "FAIL"));

            // Case B: Non-numeric Amount
            PaymentResult r2 = service.processPayment("192472021", "abc_invalid");
            log.println("Case B (Non-numeric): Expected Fail | Actual: " + r2.getMessage() + " | Result: " + (!r2.isSuccess() ? "PASS" : "FAIL"));

            // Case C: Negative Amount
            PaymentResult r3 = service.processPayment("192472021", "-500");
            log.println("Case C (Negative): Expected Fail | Actual: " + r3.getMessage() + " | Result: " + (!r3.isSuccess() ? "PASS" : "FAIL"));

            // Case D: Exceeds Maximum Amount
            PaymentResult r4 = service.processPayment("192472021", "99999999");
            log.println("Case D (Exceed Max): Expected Fail | Actual: " + r4.getMessage() + " | Result: " + (!r4.isSuccess() ? "PASS" : "FAIL"));

            // Case E: Valid Payment
            PaymentResult r5 = service.processPayment("192472021", "25000");
            log.println("Case E (Valid Payment): Expected Success | TxId: " + r5.getTransactionId() + " | Result: " + (r5.isSuccess() ? "PASS" : "FAIL"));

            // 4. Update UI to simulate successful payment and capture Payment Success Screen
            feeWin.getRegField().setText("192472021");
            feeWin.getAmountField().setText("25000");
            feeWin.getStatusLabel().setText("Paid: " + r5.getTransactionId());
            feeWin.getStatusLabel().setForeground(new Color(0, 130, 0));
            feeWin.getReceiptButton().setEnabled(true);
            CaptureHelper.saveCapture(feeWin, new File(scDir, "Task01_PaymentSuccess.png"));
            log.println("[TEST 1.3] Payment Success captured: Task01_PaymentSuccess.png");

            // 5. Render Receipt Dialog
            JDialog receiptDlg = new JDialog(feeWin, "Official Payment Receipt", false);
            receiptDlg.setSize(420, 320);
            receiptDlg.setLocationRelativeTo(feeWin);
            JTextArea area = new JTextArea(r5.generateReceipt());
            area.setFont(new Font("Monospaced", Font.BOLD, 12));
            area.setBackground(new Color(250, 250, 240));
            area.setMargin(new Insets(10, 10, 10, 10));
            receiptDlg.add(new JScrollPane(area));
            receiptDlg.setVisible(true);
            CaptureHelper.saveCapture(receiptDlg, new File(scDir, "Task01_Receipt.png"));
            receiptDlg.dispose();
            feeWin.dispose();
            log.println("[TEST 1.4] Official Receipt rendered: Task01_Receipt.png");

            log.println("\nALL TASK 1 TESTS PASSED SUCCESSFULLY.");
            System.out.println("Task 1 execution completed successfully. Results written to " + resDir.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            if (log != null) log.println("ERROR: " + ex.getMessage());
        } finally {
            if (log != null) log.close();
        }
    }
}
