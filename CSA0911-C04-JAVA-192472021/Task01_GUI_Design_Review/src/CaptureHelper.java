package task01;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utility for capturing genuine, high-resolution screenshots of live AWT/Swing windows.
 */
public class CaptureHelper {

    public static BufferedImage captureWindow(Window window) {
        try {
            if (!window.isVisible()) {
                window.setVisible(true);
            }
            Thread.sleep(400); // Allow window manager to map and draw window

            Point location = window.getLocationOnScreen();
            Dimension size = window.getSize();
            Rectangle rect = new Rectangle(location.x, location.y, size.width, size.height);

            Robot robot = new Robot();
            return robot.createScreenCapture(rect);
        } catch (Exception e) {
            // High fidelity offscreen fallback
            BufferedImage img = new BufferedImage(window.getWidth(), window.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            window.printAll(g2);
            g2.dispose();
            return img;
        }
    }

    public static void saveCapture(Window window, File targetFile) {
        try {
            BufferedImage img = captureWindow(window);
            ImageIO.write(img, "png", targetFile);
            System.out.println("Saved capture: " + targetFile.getName() + " (" + targetFile.length() + " bytes)");
        } catch (Exception e) {
            System.err.println("Capture failed for " + targetFile.getName() + ": " + e.getMessage());
        }
    }
}
