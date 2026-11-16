package task05;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * ConverterActionDemo - Demonstrates Swing Action (AbstractAction)
 * Alternative Concept for Task 5.
 *
 * Shows how a single Action object can be bound simultaneously to:
 * 1. A JButton
 * 2. A JMenuItem in the menu bar
 * 3. A KeyStroke shortcut accelerator (Ctrl+Enter)
 *
 * Provides maximum reusability, centralized enabling/disabling, and DRY code.
 */
public class ConverterActionDemo extends JFrame {
    private JTextField amountField;
    private JComboBox<String> currencyCombo;
    private JLabel resultLabel;

    public ConverterActionDemo() {
        setTitle("Currency Converter - Swing Action Alternative");
        setSize(450, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 8, 8));

        // Shared AbstractAction
        Action convertAction = new AbstractAction("Convert Currency") {
            {
                putValue(Action.SHORT_DESCRIPTION, "Converts INR to selected target currency");
                putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
                putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.CTRL_MASK));
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        };

        // 1. Menu Bar binding
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Operations");
        menu.add(new JMenuItem(convertAction));
        menuBar.add(menu);
        setJMenuBar(menuBar);

        add(new JLabel(" Amount in INR:"));
        amountField = new JTextField();
        add(amountField);

        add(new JLabel(" Target Currency:"));
        String[] targets = {"USD", "EUR", "GBP", "JPY", "CAD"};
        currencyCombo = new JComboBox<>(targets);
        add(currencyCombo);

        // 2. Button binding
        JButton btn = new JButton(convertAction);
        add(btn);

        resultLabel = new JLabel(" Result: -");
        add(resultLabel);

        // 3. Enter key on text field binding
        amountField.addActionListener(convertAction);
    }

    private void performConversion() {
        try {
            String txt = amountField.getText().trim();
            if (txt.isEmpty()) throw new IllegalArgumentException("Amount cannot be empty.");
            double inr = Double.parseDouble(txt);
            if (inr < 0) throw new IllegalArgumentException("Amount cannot be negative.");
            String cur = (String) currencyCombo.getSelectedItem();
            double res = inr * Converter.rate(cur);
            String msg = String.format("Result: %.2f INR = %.2f %s", inr, res, cur);
            resultLabel.setText(" " + msg);
            JOptionPane.showMessageDialog(this, msg, "Action Conversion", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            resultLabel.setText(" Result: Invalid amount");
            JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConverterActionDemo().setVisible(true));
    }
}
