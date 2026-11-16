package com.campus.ui;

import com.campus.model.Event;
import com.campus.model.Registration;
import com.campus.service.RegistrationService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * RegistrationTablePanel - Table view, event filtering, and CSV export.
 * Uses SwingWorker for all database lookups to guarantee smooth EDT execution.
 */
public class RegistrationTablePanel extends JPanel {
    private final RegistrationService service;
    private JTable table;
    private RegistrationTableModel tableModel;
    private JComboBox<String> filterCombo;
    private JButton filterButton;
    private JButton refreshButton;
    private JButton exportButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    public RegistrationTablePanel(RegistrationService service) {
        this.service = service;
        initUI();
        loadAllRegistrations();
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Top Toolbar Panel (FlowLayout)
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        toolbar.setBorder(BorderFactory.createTitledBorder("Filter Registrations by Event"));

        toolbar.add(new JLabel("Select Event:"));
        filterCombo = new JComboBox<>();
        filterCombo.addItem("All Events");
        toolbar.add(filterCombo);

        filterButton = new JButton("Filter");
        filterButton.setBackground(new Color(40, 110, 180));
        filterButton.setForeground(Color.WHITE);
        toolbar.add(filterButton);

        refreshButton = new JButton("Refresh All");
        toolbar.add(refreshButton);

        exportButton = new JButton("Export to CSV");
        exportButton.setBackground(new Color(40, 140, 80));
        exportButton.setForeground(Color.WHITE);
        toolbar.add(exportButton);

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(100, 18));
        progressBar.setVisible(false);
        toolbar.add(progressBar);

        add(toolbar, BorderLayout.NORTH);

        // Center Table with ScrollPane
        tableModel = new RegistrationTableModel();
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(230, 235, 245));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Status Bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        statusLabel = new JLabel("Total Registrations: 0");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        // Listeners
        filterButton.addActionListener(e -> applyFilter());
        refreshButton.addActionListener(e -> {
            filterCombo.setSelectedIndex(0);
            loadAllRegistrations();
        });
        exportButton.addActionListener(e -> handleExportCSV());

        populateFilterDropdown();
    }

    public void populateFilterDropdown() {
        SwingWorker<List<Event>, Void> worker = new SwingWorker<List<Event>, Void>() {
            @Override
            protected List<Event> doInBackground() throws Exception {
                return service.getEvents();
            }

            @Override
            protected void done() {
                try {
                    List<Event> events = get();
                    filterCombo.removeAllItems();
                    filterCombo.addItem("All Events");
                    for (Event ev : events) {
                        filterCombo.addItem(ev.getEventId() + " - " + ev.getEventName());
                    }
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    public void loadAllRegistrations() {
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        statusLabel.setText("Querying registrations from database...");

        SwingWorker<List<Registration>, Void> worker = new SwingWorker<List<Registration>, Void>() {
            @Override
            protected List<Registration> doInBackground() throws Exception {
                return service.getAllRegistrations();
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                try {
                    List<Registration> list = get();
                    tableModel.setRegistrations(list);
                    statusLabel.setText("Total Registrations: " + list.size() + " records loaded.");
                } catch (Exception ex) {
                    statusLabel.setText("Error loading registrations: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void applyFilter() {
        String selected = (String) filterCombo.getSelectedItem();
        final String eventId;
        if (selected == null || "All Events".equals(selected)) {
            loadAllRegistrations();
            return;
        } else {
            eventId = selected.split(" - ")[0];
        }

        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        statusLabel.setText("Filtering registrations for " + eventId + "...");

        SwingWorker<List<Registration>, Void> worker = new SwingWorker<List<Registration>, Void>() {
            @Override
            protected List<Registration> doInBackground() throws Exception {
                return service.filterRegistrationsByEvent(eventId);
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                try {
                    List<Registration> list = get();
                    tableModel.setRegistrations(list);
                    statusLabel.setText("Filtered by " + eventId + ": " + list.size() + " matching records found.");
                } catch (Exception ex) {
                    statusLabel.setText("Filter failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void handleExportCSV() {
        List<Registration> data = tableModel.getData();
        if (data == null || data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No records to export.", "Export Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("registrations_export.csv"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            try {
                int count = service.exportRegistrationsToCSV(data, target);
                JOptionPane.showMessageDialog(this,
                        "Successfully exported " + count + " records to:\n" + target.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("Exported " + count + " records to " + target.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to export CSV: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Accessors for testing
    public JTable getTable() { return table; }
    public RegistrationTableModel getTableModel() { return tableModel; }
    public JComboBox<String> getFilterCombo() { return filterCombo; }
    public JButton getFilterButton() { return filterButton; }
    public JButton getExportButton() { return exportButton; }
    public JLabel getStatusLabel() { return statusLabel; }
}
