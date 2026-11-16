package task05;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Style D: Java 8 Lambda Expression.
 * - Concise functional programming syntax treating ActionListener as a Single Abstract Method (SAM) type.
 */
public class ConverterLambda extends ConverterFrameListener {

    public ConverterLambda() {
        super();
        setTitle("Currency Converter - Style D (Lambda Expression)");
    }

    @Override
    protected void initUI() {
        super.initUI();
        convertButton.removeActionListener(this);
        amountField.removeActionListener(this);

        // STYLE D REGISTRATION: Single Lambda instance registered with both controls
        ActionListener lambdaHandler = e -> performConversion();

        convertButton.addActionListener(lambdaHandler);
        // Requirement Change (Task 5.2): Enter key triggers same lambda handler
        amountField.addActionListener(lambdaHandler);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConverterLambda().setVisible(true));
    }
}
