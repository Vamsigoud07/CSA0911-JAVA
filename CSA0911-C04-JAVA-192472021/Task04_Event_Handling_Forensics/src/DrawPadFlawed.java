package task04;

import java.awt.*;
import java.awt.event.*;

/**
 * DrawPadFlawed - Demonstrates the defects diagnosed in Task 4.1 and 4.2.
 *
 * DEFECT 1: Misspelled method 'public void mouseclicked(MouseEvent e)'.
 *           Because Java is case-sensitive, this does NOT override
 *           MouseAdapter.mouseClicked(MouseEvent e). Thus, clicks are ignored.
 *
 * DEFECT 2: Direct drawing via getGraphics() without persistent model.
 *           When the window is minimized and restored, the OS redraws the window
 *           by calling paint(Graphics g), wiping away all transient drawings.
 */
public class DrawPadFlawed extends Frame {
    private Label statusLabel;

    public DrawPadFlawed() {
        setTitle("DrawPad - Flawed Version");
        setSize(500, 400);
        setLayout(new BorderLayout());

        statusLabel = new Label("Clicks: 0 | DrawPad Flawed (Direct getGraphics)");
        statusLabel.setBackground(Color.LIGHT_GRAY);
        add(statusLabel, BorderLayout.SOUTH);

        // Attaching listener with misspelled callback
        addMouseListener(new MouseAdapter() {
            // DEFECT 1: Case typo! 'mouseclicked' instead of 'mouseClicked'
            public void mouseclicked(MouseEvent e) {
                // This method is NEVER invoked by the AWT event dispatcher!
                statusLabel.setText("Clicked at: " + e.getPoint());
            }

            // Demonstrating transient getGraphics drawing (Defect 2)
            @Override
            public void mousePressed(MouseEvent e) {
                Graphics g = getGraphics();
                if (g != null) {
                    g.setColor(Color.RED);
                    g.fillOval(e.getX() - 3, e.getY() - 3, 6, 6);
                    g.dispose();
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        new DrawPadFlawed().setVisible(true);
    }
}
