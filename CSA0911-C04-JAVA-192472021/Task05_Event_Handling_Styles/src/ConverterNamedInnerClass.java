package task05;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Style B: Named Inner Class Implements ActionListener.
 * - Dedicated private inner class implements ActionListener.
 * - Encapsulates handling logic while retaining full access to enclosing Frame fields.
 */
public class ConverterNamedInnerClass extends ConverterFrameListener {

    public ConverterNamedInnerClass() {
        super();
        setTitle("Currency Converter - Style B (Named Inner Class)");
    }

    @Override
    protected void initUI() {
        super.initUI();
        // Remove super's 'this' listener
        convertButton.removeActionListener(this);
        amountField.removeActionListener(this);

        // STYLE B REGISTRATION: Instantiate named inner class
        ConvertActionHandler handler = new ConvertActionHandler();
        convertButton.addActionListener(handler);
        // Requirement Change (Task 5.2): Enter key triggers same handler
        amountField.addActionListener(handler);
    }

    // Named Inner Class
    private class ConvertActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            performConversion();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConverterNamedInnerClass().setVisible(true));
    }
}
