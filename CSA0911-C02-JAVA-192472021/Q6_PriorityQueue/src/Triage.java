package src;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Q6: Hospital Emergency Triage System
 * Completed code producing the mandatory expected output:
 * Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
 *
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class Triage {

    private final PriorityQueue<Patient> queue;
    private long sequenceCounter = 0;

    public Triage() {
        // Comparator: Priority to lower severity number; tie-break by arrival counter
        Comparator<Patient> triageComparator = Comparator
            .comparingInt(Patient::getSeverity)
            .thenComparingLong(Patient::getArrivalOrder);

        this.queue = new PriorityQueue<>(triageComparator);
    }

    public void admit(String name, int severity) {
        queue.offer(new Patient(name, severity, ++sequenceCounter));
    }

    public Patient treatNext() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public static void main(String[] args) {
        Triage triage = new Triage();

        // Admitting patients in arrival sequence
        triage.admit("Ravi", 3);
        triage.admit("Meena", 1);
        triage.admit("Asha", 5);
        triage.admit("Kumar", 1);
        triage.admit("Lata", 2);
        triage.admit("John", 3);

        // Discharging/treating patients in priority order
        StringBuilder sb = new StringBuilder();
        while (!triage.isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(triage.treatNext());
        }

        // Mandatory expected output:
        // Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
        System.out.println(sb.toString());
    }
}
