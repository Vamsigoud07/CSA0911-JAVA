package task01;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * FeeWindow - Repaired Student Fee Payment GUI.
 * Adheres to:
 * - Clean layered architecture (Delegates business logic to FeeService)
 * - SwingWorker for asynchronous background execution without freezing EDT
 * - User feedback: Status label, busy cursor, disabling buttons during processing
 * - Separate Pay, Clear, and Receipt handlers
 * - Window closing behavior via setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)
 */
public class FeeWindow extends JFrame {
    private final FeeService feeService;
    private JTextField regField;
    private JTextField amountField;
    private JButton payButton;
    private JButton clearButton;
    private JButton receiptButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private PaymentResult lastResult;

    public FeeWindow(FeeService feeService) {
        this.feeService = feeService;
        initUI();
    }

    public FeeWindow() {
        this(new FeeService());
    }

    private void initUI() {
        setTitle("SIMATS - Student Fee Payment System");
        setSize(480, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(24, 76, 120));
        JLabel titleLabel = new JLabel("STUDENT FEE PAYMENT PORTAL");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Center Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Reg No
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblReg = new JLabel("Register Number:");
        lblReg.setFont(new Font("SansSerif", Font.BOLD, 12));
        formPanel.add(lblReg, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        regField = new JTextField(16);
        regField.setFont(new Font("Monospaced", Font.PLAIN, 13));
        formPanel.add(regField, gbc);

        // Row 1: Amount
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblAmt = new JLabel("Amount (INR):");
        lblAmt.setFont(new Font("SansSerif", Font.BOLD, 12));
        formPanel.add(lblAmt, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        amountField = new JTextField(16);
        amountField.setFont(new Font("Monospaced", Font.PLAIN, 13));
        formPanel.add(amountField, gbc);

        // Row 2: Progress Bar
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setVisible(false);
        formPanel.add(progressBar, gbc);

        // Row 3: Status Label
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        statusLabel = new JLabel("Status: Ready to accept payment", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(50, 50, 50));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        formPanel.add(statusLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // South Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        payButton = new JButton("Pay Fee");
        clearButton = new JButton("Clear");
        receiptButton = new JButton("Print Receipt");
        receiptButton.setEnabled(false); // Enabled only after successful payment

        payButton.setBackground(new Color(40, 140, 60));
        payButton.setForeground(Color.WHITE);
        payButton.setFocusPainted(false);

        clearButton.setBackground(new Color(180, 60, 60));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);

        receiptButton.setBackground(new Color(60, 100, 180));
        receiptButton.setForeground(Color.WHITE);
        receiptButton.setFocusPainted(false);

        // Event Listeners with dedicated handlers
        payButton.addActionListener(e -> handlePayment());
        clearButton.addActionListener(e -> handleClear());
        receiptButton.addActionListener(e -> handlePrintReceipt());

        buttonPanel.add(payButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(receiptButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handlePayment() {
        final String regNo = regField.getText();
        final String amtStr = amountField.getText();

        // Immediate visual feedback and disabling to prevent double-submit
        setControlsEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setText("Processing payment securely... please wait.");

        // SwingWorker keeps the Swing EDT 100% responsive
        SwingWorker<PaymentResult, Void> worker = new SwingWorker<PaymentResult, Void>() {
            @Override
            protected PaymentResult doInBackground() throws Exception {
                // Simulating network latency without freezing EDT
                Thread.sleep(700);
                return feeService.processPayment(regNo, amtStr);
            }

            @Override
            protected void done() {
                try {
                    lastResult = get();
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                    setControlsEnabled(true);

                    if (lastResult.isSuccess()) {
                        statusLabel.setForeground(new Color(0, 130, 0));
                        statusLabel.setText("Paid: " + lastResult.getTransactionId());
                        receiptButton.setEnabled(true);
                        JOptionPane.showMessageDialog(FeeWindow.this,
                                lastResult.getMessage() + "\nTransaction ID: " + lastResult.getTransactionId(),
                                "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("Failed: " + lastResult.getMessage());
                        receiptButton.setEnabled(false);
                        JOptionPane.showMessageDialog(FeeWindow.this,
                                lastResult.getMessage(),
                                "Payment Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    setControlsEnabled(true);
                    progressBar.setVisible(false);
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Unexpected error occurred.");
                    JOptionPane.showMessageDialog(FeeWindow.this,
                            "System Error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void handleClear() {
        regField.setText("");
        amountField.setText("");
        statusLabel.setForeground(new Color(50, 50, 50));
        statusLabel.setText("Status: Ready to accept payment");
        receiptButton.setEnabled(false);
        lastResult = null;
    }

    private void handlePrintReceipt() {
        if (lastResult == null || !lastResult.isSuccess()) {
            JOptionPane.showMessageDialog(this,
                    "No successful transaction to generate a receipt.",
                    "Receipt Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextArea receiptArea = new JTextArea(lastResult.generateReceipt(), 12, 35);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(receiptArea),
                "Official Payment Receipt", JOptionPane.PLAIN_MESSAGE);
    }

    private void setControlsEnabled(boolean enabled) {
        payButton.setEnabled(enabled);
        clearButton.setEnabled(enabled);
        regField.setEnabled(enabled);
        amountField.setEnabled(enabled);
    }

    // Accessors for automated testing and test runner
    public JTextField getRegField() { return regField; }
    public JTextField getAmountField() { return amountField; }
    public JButton getPayButton() { return payButton; }
    public JButton getClearButton() { return clearButton; }
    public JButton getReceiptButton() { return receiptButton; }
    public JLabel getStatusLabel() { return statusLabel; }
    public PaymentResult getLastResult() { return lastResult; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FeeWindow().setVisible(true));
    }
}
