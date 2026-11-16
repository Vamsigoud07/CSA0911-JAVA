package com.campus;

import com.campus.ui.MainFrame;

import javax.swing.*;

/**
 * Main application launcher for Campus Event Registration System.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set native look and feel if available
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MainFrame().setVisible(true);
        });
    }
}
