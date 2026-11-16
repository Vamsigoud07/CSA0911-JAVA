package task01;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * PoorFeeWindow illustrates the anti-patterns and severe design flaws
 * diagnosed in Task 1.1 of Assignment 4.
 *
 * DEFICIENCIES DEMONSTRATED:
 * 1. Monolithic architecture: GUI, DB access, and business logic mixed in one class.
 * 2. String comparison using '==' instead of equals().
 * 3. No amount validation (accepts negatives, non-numeric strings crash with raw exceptions).
 * 4. Heavy database operations executed synchronously on Event Dispatch Thread (EDT).
 * 5. Thread.sleep() called on EDT freezing the entire UI.
 * 6. No visual feedback during payment processing.
 * 7. Receipt button instantiated but ActionListener not attached.
 * 8. Severe SQL Injection vulnerability via raw string concatenation.
 * 9. Database Connection and Statement never closed (connection leak).
 * 10. Checked exceptions swallowed or dumped to stdout.
 * 11. Missing setDefaultCloseOperation / WindowListener (window cannot be cleanly closed).
 */
public class PoorFeeWindow extends Frame implements ActionListener {
    TextField regField;
    TextField amountField;
    Button payButton;
    Button clearButton;
    Button receiptButton;
    Label statusLabel;

    public PoorFeeWindow() {
        setTitle("Student Fee Payment - Poor Design (FLAWED)");
        setSize(400, 260);
        setLayout(new FlowLayout());

        add(new Label("Registration Number:"));
        regField = new TextField(20);
        add(regField);

        add(new Label("Fee Amount (INR):"));
        amountField = new TextField(20);
        add(amountField);

        payButton = new Button("Pay Fee");
        clearButton = new Button("Clear");
        receiptButton = new Button("Print Receipt");

        payButton.addActionListener(this);
        clearButton.addActionListener(this);
        // Defect #7: receiptButton has NO listener attached!

        add(payButton);
        add(clearButton);
        add(receiptButton);

        statusLabel = new Label("Ready                      ");
        add(statusLabel);

        // Defect #11: Missing WindowListener; clicking 'X' does nothing!
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        // Defect #2: String comparison with == operator instead of .equals()!
        if (cmd == "Pay Fee") {
            String reg = regField.getText();
            String amtStr = amountField.getText();

            // Defect #3: No validation on reg or amtStr!
            double amt = Double.parseDouble(amtStr); // Throws NumberFormatException if invalid!

            try {
                // Defect #5: Thread.sleep on Event Dispatch Thread freezes the UI!
                Thread.sleep(3000);

                // Defect #1 & #4: Direct DB calls on EDT!
                // Defect #8: SQL Injection vulnerability!
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/fees", "root", "password");
                Statement stmt = conn.createStatement();
                String sql = "INSERT INTO fee_payments VALUES ('" + reg + "', " + amt + ")";
                stmt.executeUpdate(sql);

                // Defect #9: conn and stmt are never closed!
                statusLabel.setText("Payment Done");
            } catch (Exception ex) {
                // Defect #10: Swallowing exception without alerting user
                ex.printStackTrace();
            }
        } else if (cmd == "Clear") {
            regField.setText("");
            amountField.setText("");
            statusLabel.setText("Ready");
        }
    }

    public static void main(String[] args) {
        new PoorFeeWindow().setVisible(true);
    }
}
