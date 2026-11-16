package com.campus.ui;

import com.campus.service.RegistrationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * MainFrame - Main Dashboard Window for Campus Event Registration System.
 * Includes complete JMenuBar, JTabbedPane navigation, and university branding.
 */
public class MainFrame extends JFrame {
    private final RegistrationService service;
    private RegistrationPanel registrationPanel;
    private RegistrationTablePanel tablePanel;
    private JTabbedPane tabbedPane;

    public MainFrame(RegistrationService service) {
        this.service = service;
        initUI();
    }

    public MainFrame() {
        this(new RegistrationService());
    }

    private void initUI() {
        setTitle("Campus Event Registration System - SIMATS University");
        setSize(880, 560);
        setMinimumSize(new Dimension(720, 480));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // University Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(18, 64, 110));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("CAMPUS EVENT REGISTRATION DESKTOP PORTAL");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JLabel studentBadge = new JLabel("Student: K VENKATA VAMSI GOUD (192472021) | CSA09");
        studentBadge.setFont(new Font("SansSerif", Font.PLAIN, 12));
        studentBadge.setForeground(new Color(220, 235, 255));
        header.add(studentBadge, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Child Panels
        tablePanel = new RegistrationTablePanel(service);
        registrationPanel = new RegistrationPanel(service, () -> {
            tablePanel.loadAllRegistrations();
            tablePanel.populateFilterDropdown();
        });

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.addTab("Register Student", registrationPanel);
        tabbedPane.addTab("Registration Records & Filters", tablePanel);
        add(tabbedPane, BorderLayout.CENTER);

        // Menu Bar
        buildMenuBar();
    }

    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 1. File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem exportItem = new JMenuItem("Export Registrations to CSV...");
        exportItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exportItem.addActionListener(e -> {
            tabbedPane.setSelectedComponent(tablePanel);
            tablePanel.handleExportCSV();
        });
        fileMenu.add(exportItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit Application");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(exitItem);

        // 2. Registration Menu
        JMenu regMenu = new JMenu("Registration");
        regMenu.setMnemonic(KeyEvent.VK_R);

        JMenuItem newRegItem = new JMenuItem("Go to Registration Form");
        newRegItem.addActionListener(e -> tabbedPane.setSelectedComponent(registrationPanel));
        regMenu.add(newRegItem);

        JMenuItem clearItem = new JMenuItem("Clear Current Form");
        clearItem.addActionListener(e -> registrationPanel.clearForm());
        regMenu.add(clearItem);

        // 3. View Menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);

        JMenuItem refreshItem = new JMenuItem("Refresh Database Records");
        refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        refreshItem.addActionListener(e -> {
            tablePanel.loadAllRegistrations();
            registrationPanel.loadEvents();
        });
        viewMenu.add(refreshItem);

        // 4. Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem aboutItem = new JMenuItem("About System");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(regMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void showAboutDialog() {
        String info = "========================================================\n"
                    + "   CAMPUS EVENT REGISTRATION DESKTOP SYSTEM\n"
                    + "========================================================\n"
                    + " Course        : CSA09 - Programming in Java\n"
                    + " Assignment    : Assignment 4 (CO4 - Event-Driven GUI Design)\n"
                    + " Topic         : Swing, JDBC, MySQL, SwingWorker\n"
                    + " Student Name  : K VENKATA VAMSI GOUD\n"
                    + " Register No   : 192472021\n"
                    + " Institution   : SIMATS - Saveetha School of Engineering\n"
                    + " Academic Year : 2026-2027\n"
                    + " Architecture  : Layered (GUI -> Service -> DAO -> MySQL)\n"
                    + "========================================================";
        JOptionPane.showMessageDialog(this, info, "About System", JOptionPane.INFORMATION_MESSAGE);
    }

    public RegistrationPanel getRegistrationPanel() { return registrationPanel; }
    public RegistrationTablePanel getTablePanel() { return tablePanel; }
    public JTabbedPane getTabbedPane() { return tabbedPane; }
    public RegistrationService getService() { return service; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
