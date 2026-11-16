package task04;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * DrawPad - Repaired Drawing Pad Application.
 * Follows the mandatory "Complete the Code" specifications:
 *
 * TODO 1: Correct @Override mouseClicked(MouseEvent e):
 *         - points.add(e.getPoint());
 *         - Update status text using points.size();
 *         - Call repaint();
 *
 * TODO 2: In paint(Graphics g):
 *         - Iterate over all stored Point instances in 'points';
 *         - Draw each point as a filled 6x6 circle centred at (p.x, p.y):
 *           g.fillOval(p.x - 3, p.y - 3, 6, 6);
 */
public class DrawPad extends Frame {
    private final ArrayList<Point> points = new ArrayList<>();
    private final Label statusLabel;

    public DrawPad() {
        setTitle("DrawPad - Persistent Vector Rendering");
        setSize(500, 400);
        setLayout(new BorderLayout());

        statusLabel = new Label("Total Dots Placed: 0");
        statusLabel.setBackground(new Color(230, 230, 230));
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        add(statusLabel, BorderLayout.SOUTH);

        // TODO 1 COMPLETED: Proper MouseAdapter registration with correct mouseClicked()
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Ignore clicks that land on the status bar or window title bar insets
                Insets insets = getInsets();
                if (e.getY() >= insets.top && e.getY() <= getHeight() - statusLabel.getHeight()) {
                    points.add(e.getPoint());
                    statusLabel.setText("Total Dots Placed: " + points.size() + " | Last: (" + e.getX() + ", " + e.getY() + ")");
                    repaint();
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    // TODO 2 COMPLETED: Deterministic, persistent rendering of all recorded points
    @Override
    public void paint(Graphics g) {
        super.paint(g); // Paint standard frame components (like statusLabel)

        // Set rendering color for persistent dots
        g.setColor(new Color(20, 80, 180));

        // Iterate through all stored points and draw filled 6x6 circles centred on the coordinate
        for (Point p : points) {
            g.fillOval(p.x - 3, p.y - 3, 6, 6);
        }
    }

    // Accessors for testing and automation
    public ArrayList<Point> getPoints() {
        return points;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public void addSimulatedPoint(Point p) {
        points.add(p);
        statusLabel.setText("Total Dots Placed: " + points.size() + " | Last: (" + p.x + ", " + p.y + ")");
        repaint();
    }

    public static void main(String[] args) {
        new DrawPad().setVisible(true);
    }
}
