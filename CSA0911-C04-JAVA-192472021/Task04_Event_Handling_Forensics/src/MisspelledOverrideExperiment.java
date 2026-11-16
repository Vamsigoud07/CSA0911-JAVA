package task04;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Deliberate experiment for Task 4.4:
 * Demonstrates that adding @Override to a misspelled callback method
 * forces the Java compiler to reject the file at compile time,
 * completely preventing silent runtime bugs.
 */
public class MisspelledOverrideExperiment extends MouseAdapter {

    // When @Override is added, compiler errors because 'mouseclicked' is NOT in MouseAdapter
    @Override
    public void mouseclicked(MouseEvent e) {
        System.out.println("This should trigger compiler error");
    }
}
