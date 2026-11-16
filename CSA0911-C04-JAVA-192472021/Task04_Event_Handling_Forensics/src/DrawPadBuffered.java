package task04;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * DrawPadBuffered - Alternative Drawing Pad using an off-screen BufferedImage.
 * Demonstrates:
 * - Off-screen raster backing buffer
 * - Bit-blit copy in paint(Graphics g): g.drawImage(canvasBuffer, 0, 0, null)
 * - Contrast in time and space complexity with vector Point list.
 */
public class DrawPadBuffered extends Frame {
    private BufferedImage canvasBuffer;
    private Graphics2D bufferGraphics;
    private final Label statusLabel;
    private int clickCount = 0;

    public DrawPadBuffered() {
        setTitle("DrawPad - Off-Screen BufferedImage Alternative");
        setSize(500, 400);
        setLayout(new BorderLayout());

        statusLabel = new Label("Buffered Dots: 0");
        statusLabel.setBackground(new Color(240, 240, 240));
        add(statusLabel, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ensureBufferExists();
                bufferGraphics.setColor(new Color(180, 40, 40));
                bufferGraphics.fillOval(e.getX() - 3, e.getY() - 3, 6, 6);
                clickCount++;
                statusLabel.setText("Buffered Dots: " + clickCount + " | Direct Raster Blit");
                repaint();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (bufferGraphics != null) bufferGraphics.dispose();
                dispose();
            }
        });
    }

    private void ensureBufferExists() {
        if (canvasBuffer == null || canvasBuffer.getWidth() != getWidth() || canvasBuffer.getHeight() != getHeight()) {
            BufferedImage newBuf = new BufferedImage(Math.max(1, getWidth()), Math.max(1, getHeight()), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = newBuf.createGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, newBuf.getWidth(), newBuf.getHeight());
            if (canvasBuffer != null) {
                g2.drawImage(canvasBuffer, 0, 0, null);
                bufferGraphics.dispose();
            }
            canvasBuffer = newBuf;
            bufferGraphics = g2;
            bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (canvasBuffer != null) {
            // Blit entire offscreen raster to native screen in single operation
            g.drawImage(canvasBuffer, 0, 0, null);
        }
    }

    public static void main(String[] args) {
        new DrawPadBuffered().setVisible(true);
    }
}
