package src;

import java.util.Objects;

/**
 * Patient entity for Emergency Room Triage.
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class Patient implements Comparable<Patient> {
    private final String name;
    private final int severity;       // 1 = Resuscitation/Critical, 5 = Non-urgent
    private final long arrivalOrder;  // Monotonic sequence counter for FIFO tie-breaking

    public Patient(String name, int severity, long arrivalOrder) {
        this.name = name;
        this.severity = severity;
        this.arrivalOrder = arrivalOrder;
    }

    public String getName() {
        return name;
    }

    public int getSeverity() {
        return severity;
    }

    public long getArrivalOrder() {
        return arrivalOrder;
    }

    @Override
    public int compareTo(Patient other) {
        // Primary criterion: Severity (lower number = higher clinical urgency)
        int sev = Integer.compare(this.severity, other.severity);
        if (sev != 0) return sev;
        // Secondary criterion: Arrival order (FIFO tie-breaking)
        return Long.compare(this.arrivalOrder, other.arrivalOrder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return severity == patient.severity &&
               arrivalOrder == patient.arrivalOrder &&
               Objects.equals(name, patient.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, severity, arrivalOrder);
    }

    @Override
    public String toString() {
        return name + "(S" + severity + ")";
    }
}
