package task05;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Style C: Anonymous Inner Class Implements ActionListener.
 * - Anonymous inner class instantiated inline at the point of registration.
 */
public class ConverterAnonymousClass extends ConverterFrameListener {

    public ConverterAnonymousClass() {
        super();
        setTitle("Currency Converter - Style C (Anonymous Inner Class)");
    }

    @Override
    protected void initUI() {
        super.initUI();
        convertButton.removeActionListener(this);
        amountField.removeActionListener(this);

        // STYLE C REGISTRATION: Anonymous Inner Class
        ActionListener anonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        };

        convertButton.addActionListener(anonListener);
        // Requirement Change (Task 5.2): Enter key triggers same listener
        amountField.addActionListener(anonListener);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConverterAnonymousClass().setVisible(true));
    }
}
