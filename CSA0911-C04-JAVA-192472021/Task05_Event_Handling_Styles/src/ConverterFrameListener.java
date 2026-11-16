package task05;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Style A: Frame Implements ActionListener.
 * - The top-level JFrame implements the ActionListener interface directly.
 * - Single actionPerformed() method handles all actions via e.getSource().
 */
public class ConverterFrameListener extends JFrame implements ActionListener {
    protected JTextField amountField;
    protected JComboBox<String> currencyCombo;
    protected JButton convertButton;
    protected JLabel resultLabel;

    public ConverterFrameListener() {
        initUI();
    }

    protected void initUI() {
        setTitle("Currency Converter - Style A (Frame Listener)");
        setSize(440, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel header = new JPanel(new FlowLayout());
        header.setBackground(new Color(40, 70, 110));
        JLabel title = new JLabel("INR CURRENCY CONVERTER");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(3, 2, 8, 8));
        center.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        center.add(new JLabel("Amount in INR:"));
        amountField = new JTextField(12);
        center.add(amountField);

        center.add(new JLabel("Target Currency:"));
        String[] currencies = {"USD (US Dollar)", "EUR (Euro)", "GBP (British Pound)", "JPY (Japanese Yen)", "CAD (Canadian Dollar)"};
        currencyCombo = new JComboBox<>(currencies);
        center.add(currencyCombo);

        center.add(new JLabel("Conversion Result:"));
        resultLabel = new JLabel("Enter amount and convert");
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        center.add(resultLabel);

        add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel(new FlowLayout());
        convertButton = new JButton("Convert");
        btnPanel.add(convertButton);
        south.add(btnPanel, BorderLayout.NORTH);

        JLabel note = new JLabel("Note: Conversion rates are illustrative and fixed.", SwingConstants.CENTER);
        note.setFont(new Font("SansSerif", Font.ITALIC, 10));
        note.setForeground(Color.GRAY);
        south.add(note, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        // STYLE A REGISTRATION: Passing 'this' frame instance as ActionListener
        convertButton.addActionListener(this);
        // Requirement Change (Task 5.2): Enter key triggers conversion
        amountField.addActionListener(this);
    }

    public static double rate(String currency) {
        if (currency.startsWith("USD")) return 0.012;
        if (currency.startsWith("EUR")) return 0.011;
        if (currency.startsWith("GBP")) return 0.0095;
        if (currency.startsWith("JPY")) return 1.80;
        if (currency.startsWith("CAD")) return 0.016;
        return 1.0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Dispatches based on event source
        if (e.getSource() == convertButton || e.getSource() == amountField) {
            performConversion();
        }
    }

    protected void performConversion() {
        String input = amountField.getText().trim();
        try {
            if (input.isEmpty()) {
                throw new IllegalArgumentException("Amount cannot be empty.");
            }
            double inr = Double.parseDouble(input);
            if (Double.isNaN(inr) || Double.isInfinite(inr) || inr < 0) {
                throw new IllegalArgumentException("Amount must be a non-negative finite number.");
            }

            String target = (String) currencyCombo.getSelectedItem();
            double r = rate(target);
            double converted = inr * r;
            String outText = String.format("%.2f INR = %.2f %s", inr, converted, target.substring(0, 3));
            resultLabel.setText(outText);

            // Requirement Change (Task 5.2): Display result in dialog box
            JOptionPane.showMessageDialog(this, outText, "Conversion Result", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            resultLabel.setText("Invalid amount");
            JOptionPane.showMessageDialog(this, "Invalid amount: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JTextField getAmountField() { return amountField; }
    public JComboBox<String> getCurrencyCombo() { return currencyCombo; }
    public JButton getConvertButton() { return convertButton; }
    public JLabel getResultLabel() { return resultLabel; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConverterFrameListener().setVisible(true));
    }
}
