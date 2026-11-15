package Task07;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Task 07 - Method Overriding, Object Contracts, and Final Mechanics
 * Student Name: K VENKATA VAMSI GOUD
 * Register Number: 192472021
 * Course: Programming in Java (CSA09)
 * Institution: SIMATS - Saveetha School of Engineering
 */
public class MethodOverridingDemo {

    // Base Class
    public static class SmartVehicle {
        // final variable: Immutable license plate once assigned
        protected final String registrationNumber;
        protected final String ownerName;
        protected double baseRate;

        public SmartVehicle(String registrationNumber, String ownerName, double baseRate) {
            this.registrationNumber = registrationNumber;
            this.ownerName = ownerName;
            this.baseRate = baseRate;
        }

        // Method intended for polymorphic overriding
        public double calculateFee(long hours) {
            return baseRate * Math.max(1, hours);
        }

        // final method: Cannot be overridden by any subclass, enforcing core security policy
        public final String getSecurityAuditStamp() {
            return "SEC-AUDIT-" + Math.abs(Objects.hash(registrationNumber, ownerName));
        }

        // 1. Overriding toString() for human-readable state representation
        @Override
        public String toString() {
            return String.format("SmartVehicle [Reg: %s | Owner: %s | Rate: Rs %.1f/hr]",
                    registrationNumber, ownerName, baseRate);
        }

        // 2. Overriding equals() adhering strictly to the Java Equivalence Contract
        @Override
        public boolean equals(Object o) {
            // Reflexivity check: o == this
            if (this == o) return true;
            // Null check and exact class identity verification
            if (o == null || getClass() != o.getClass()) return false;
            SmartVehicle that = (SmartVehicle) o;
            // Logical equivalence based on unique vehicular registration number
            return Objects.equals(registrationNumber, that.registrationNumber);
        }

        // 3. Overriding hashCode() consistent with equals()
        @Override
        public int hashCode() {
            return Objects.hash(registrationNumber);
        }

        public String getRegistrationNumber() { return registrationNumber; }
    }

    // Subclass 1: Specialised Fee Overriding
    public static class CompactTwoWheeler extends SmartVehicle {
        public CompactTwoWheeler(String registrationNumber, String ownerName) {
            super(registrationNumber, ownerName, 10.0);
        }

        // @Override annotation validates compile-time signature compliance
        @Override
        public double calculateFee(long hours) {
            double fee = super.calculateFee(hours);
            // Campus student discount: 20% concession on two-wheelers
            return fee * 0.80;
        }

        @Override
        public String toString() {
            return String.format("CompactTwoWheeler [Reg: %s | Owner: %s | Net Rate: Rs %.1f/hr (with 20%% Campus Concession)]",
                    registrationNumber, ownerName, baseRate * 0.80);
        }
    }

    // Subclass 2: Premium SUV Overriding
    public static class HeavySuv extends SmartVehicle {
        private final double oversizedSurcharge;

        public HeavySuv(String registrationNumber, String ownerName, double oversizedSurcharge) {
            super(registrationNumber, ownerName, 35.0);
            this.oversizedSurcharge = oversizedSurcharge;
        }

        @Override
        public double calculateFee(long hours) {
            return (baseRate + oversizedSurcharge) * Math.max(1, hours);
        }

        @Override
        public String toString() {
            return String.format("HeavySuv [Reg: %s | Owner: %s | Base: Rs %.1f | Surcharge: Rs %.1f]",
                    registrationNumber, ownerName, baseRate, oversizedSurcharge);
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("       TASK 07: METHOD OVERRIDING, OBJECT CONTRACTS & FINAL MECHANICS           ");
        System.out.println("       Student: K VENKATA VAMSI GOUD | Reg No: 192472021                       ");
        System.out.println("================================================================================");

        System.out.println("\n--- Step 1: Demonstrating Polymorphic calculateFee() Overriding ---");
        SmartVehicle v1 = new SmartVehicle("TN01A1111", "Faculty Member", 25.0);
        SmartVehicle v2 = new CompactTwoWheeler("TN02B2222", "Student Scholar");
        SmartVehicle v3 = new HeavySuv("TN03C3333", "Campus Visitor", 15.0);

        SmartVehicle[] vehicles = new SmartVehicle[]{v1, v2, v3};
        for (SmartVehicle v : vehicles) {
            System.out.println(v.toString());
            System.out.println("    --> Billed Fee for 4 hours: Rs " + v.calculateFee(4));
            System.out.println("    --> Security Stamp (final method): " + v.getSecurityAuditStamp());
        }

        System.out.println("\n--- Step 2: Testing the equals() and hashCode() Contract ---");
        // Creating two distinct object references with identical business key (registrationNumber)
        SmartVehicle bikeA = new CompactTwoWheeler("TN09Z9999", "Driver Alpha");
        SmartVehicle bikeB = new CompactTwoWheeler("TN09Z9999", "Driver Alpha Duplicate Entry");
        SmartVehicle bikeC = new CompactTwoWheeler("TN08X8888", "Driver Charlie");

        System.out.println("Object A: " + bikeA);
        System.out.println("Object B: " + bikeB);
        System.out.println("Object C: " + bikeC);

        System.out.println("\nVerification of Contract Rules:");
        System.out.println("  1. Reference Equality (bikeA == bikeB): " + (bikeA == bikeB) + " (Different heap addresses)");
        System.out.println("  2. Logical Equivalence (bikeA.equals(bikeB)): " + bikeA.equals(bikeB) + " (Equal reg numbers)");
        System.out.println("  3. Distinct Object Equivalence (bikeA.equals(bikeC)): " + bikeA.equals(bikeC));
        System.out.println("  4. HashCode Match: bikeA=" + bikeA.hashCode() + " | bikeB=" + bikeB.hashCode() +
                " -> Hash Equal? " + (bikeA.hashCode() == bikeB.hashCode()));

        System.out.println("\n--- Step 3: HashSet Behavior (Contract Compliance in Collections) ---");
        Set<SmartVehicle> vehicleRegistry = new HashSet<>();
        boolean addedA = vehicleRegistry.add(bikeA);
        boolean addedB = vehicleRegistry.add(bikeB); // Must be rejected because equals & hashCode match bikeA!
        boolean addedC = vehicleRegistry.add(bikeC);

        System.out.println("  Added bikeA to Set: " + addedA);
        System.out.println("  Added duplicate bikeB to Set: " + addedB + " (Properly rejected duplicate!)");
        System.out.println("  Added distinct bikeC to Set: " + addedC);
        System.out.println("  Registry Set Size: " + vehicleRegistry.size() + " (Expected: 2)");

        System.out.println("\n--- Step 4: Comparison: Overriding vs Switch-Based Calculation ---");
        System.out.println("  * Method Overriding adheres to SOLID (Open-Closed Principle): New vehicle categories");
        System.out.println("    are added by creating a new subclass without modifying existing, tested code.");
        System.out.println("  * Switch-based design violates OCP: Every new category forces modifications to the");
        System.out.println("    central switch statement, increasing regression bug risk and cyclomatic complexity.");
        System.out.println("================================================================================");
    }
}
