package com.campus.ui;

import com.campus.model.Registration;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom TableModel for displaying Campus Event Registrations in JTable.
 */
public class RegistrationTableModel extends AbstractTableModel {
    private final String[] columnNames = {
            "Reg ID", "Student ID", "Student Name", "Email", "Department", "Event ID", "Event Name", "Registered At"
    };
    private List<Registration> data = new ArrayList<>();

    public void setRegistrations(List<Registration> registrations) {
        this.data = (registrations != null) ? new ArrayList<>(registrations) : new ArrayList<>();
        fireTableDataChanged();
    }

    public List<Registration> getData() {
        return data;
    }

    public Registration getRegistrationAt(int row) {
        if (row >= 0 && row < data.size()) {
            return data.get(row);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Registration r = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return r.getRegistrationId();
            case 1: return r.getStudentId();
            case 2: return r.getStudentName();
            case 3: return r.getEmail();
            case 4: return r.getDepartment();
            case 5: return r.getEventId();
            case 6: return r.getEventName();
            case 7: return r.getFormattedDate();
            default: return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
