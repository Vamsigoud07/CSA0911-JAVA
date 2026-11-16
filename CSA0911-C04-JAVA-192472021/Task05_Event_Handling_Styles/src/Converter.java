package task05;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Converter - Mandatory "Complete the Code" Program for Task 5.
 *
 * TODO 1:
 * - Create ONE lambda ActionListener.
 * - Register the same listener with convertButton.
 * - Register the same listener with amount using addActionListener().
 * - Ensure Enter invokes the same conversion logic.
 *
 * TODO 2:
 * - Parse amount using Double.parseDouble().
 * - Reject invalid, empty, negative or non-finite values.
 * - Convert INR into the selected target currency.
 * - Display the converted value to two decimal places.
 * - Use a JOptionPane message dialog.
 * - Display "Invalid amount" if parsing or validation fails.
 * - Update the result label.
 */
public class Converter extends JFrame {
    private JTextField amount;
    private JComboBox<String> currency;
    private JButton convertButton;
    private JLabel result;

    public Converter() {
        setTitle("Currency Converter - Official Task 5 Implementation");
        setSize(420, 240);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 8, 8));

        add(new JLabel(" Amount in INR:"));
        amount = new JTextField();
        add(amount);

        add(new JLabel(" Target Currency:"));
        String[] targets = {"USD", "EUR", "GBP", "JPY", "CAD"};
        currency = new JComboBox<>(targets);
        add(currency);

        convertButton = new JButton("Convert");
        add(convertButton);

        result = new JLabel(" Result: -");
        add(result);

        // TODO 1 COMPLETED: Single lambda ActionListener attached to both button and text field
        ActionListener convertAction = e -> {
            // TODO 2 COMPLETED: Robust input parsing, bounds checking, conversion & dialog display
            String text = amount.getText().trim();
            try {
                if (text.isEmpty()) {
                    throw new IllegalArgumentException("Field cannot be empty.");
                }

                double inr = Double.parseDouble(text);

                // Validation checks: reject negative, NaN, infinite
                if (Double.isNaN(inr) || Double.isInfinite(inr) || inr < 0) {
                    throw new IllegalArgumentException("Must be a non-negative finite value.");
                }

                String target = (String) currency.getSelectedItem();
                double converted = inr * rate(target);

                String formatted = String.format("Result: %.2f INR = %.2f %s", inr, converted, target);
                result.setText(" " + formatted);

                // Show JOptionPane message dialog
                JOptionPane.showMessageDialog(this, formatted, "Conversion Result", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                result.setText(" Result: Invalid amount");
                JOptionPane.showMessageDialog(this, "Invalid amount", "Conversion Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        // Attach the single lambda to both the button and Enter key on the text field
        convertButton.addActionListener(convertAction);
        amount.addActionListener(convertAction);
    }

    public static double rate(String cur) {
        switch (cur) {
            case "USD": return 0.012;
            case "EUR": return 0.011;
            case "GBP": return 0.0095;
            case "JPY": return 1.80;
            case "CAD": return 0.016;
            default: return 1.0;
        }
    }

    public JTextField getAmount() { return amount; }
    public JComboBox<String> getCurrency() { return currency; }
    public JButton getConvertButton() { return convertButton; }
    public JLabel getResult() { return result; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Converter().setVisible(true));
    }
}
