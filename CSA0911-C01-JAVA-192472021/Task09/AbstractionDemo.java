package Task09;

import java.time.LocalDateTime;

/**
 * Task 09 - Abstraction: Abstract Classes, Interfaces, Default & Static Methods
 * Student Name: K VENKATA VAMSI GOUD
 * Register Number: 192472021
 * Course: Programming in Java (CSA09)
 * Institution: SIMATS - Saveetha School of Engineering
 */
public class AbstractionDemo {

    // 1. Interface 1: Chargeable with default and static methods
    public interface Chargeable {
        double CHARGING_STATION_FLAT_FEE = 25.0;

        // Abstract method
        double getChargingFee(int hours);

        // Interface Default Method (introduced in Java 8): Provides reusable default behavior
        default double applyGreenEnergyDiscount(double totalFee) {
            System.out.println("      [Chargeable Interface Default Method] Applying 10% SIMATS Green Rebate.");
            return totalFee * 0.90;
        }

        // Interface Static Method (introduced in Java 8): Utility method bound to interface type
        static boolean isValidChargingBayId(String bayId) {
            return bayId != null && bayId.startsWith("EV-BAY-");
        }
    }

    // 2. Interface 2: Trackable demonstrating multiple interface implementation
    public interface Trackable {
        String getGpsCoordinates();

        default void logTelemetryPing() {
            System.out.println("      [Trackable Interface Default Method] Telemetry timestamp: " + LocalDateTime.now());
        }
    }

    // 3. Abstract Class: Captures state (fields), constructor, and partial implementation
    public static abstract class Vehicle {
        protected final String registrationNumber;
        protected final String make;
        protected final String model;
        protected double baseRatePerHour;

        public Vehicle(String registrationNumber, String make, String model, double baseRatePerHour) {
            this.registrationNumber = registrationNumber;
            this.make = make;
            this.model = model;
            this.baseRatePerHour = baseRatePerHour;
        }

        // Pure Abstract Methods: Subclasses MUST implement these contracts
        public abstract String getCategoryDescription();
        public abstract double calculateParkingFee(int hours);

        // Concrete Method inherited by all subclasses
        public void displayManifest() {
            System.out.println(String.format("Manifest Entry: [%-10s | %s %s | Base Rate: Rs %.1f/hr]",
                    registrationNumber, make, model, baseRatePerHour));
        }

        public String getRegistrationNumber() { return registrationNumber; }
    }

    // 4. Concrete Subclass implementing MULTIPLE interfaces
    public static class SmartAutonomousElectricCar extends Vehicle implements Chargeable, Trackable {
        private final String chargingBayId;
        private final String gpsLocation;

        public SmartAutonomousElectricCar(String registrationNumber, String make, String model,
                                          String chargingBayId, String gpsLocation) {
            super(registrationNumber, make, model, 30.0);
            if (!Chargeable.isValidChargingBayId(chargingBayId)) {
                throw new IllegalArgumentException("Invalid Charging Bay Identifier: " + chargingBayId);
            }
            this.chargingBayId = chargingBayId;
            this.gpsLocation = gpsLocation;
        }

        @Override
        public String getCategoryDescription() {
            return "Autonomous Smart EV (Multi-Interface: Chargeable + Trackable)";
        }

        @Override
        public double calculateParkingFee(int hours) {
            double parkingFee = baseRatePerHour * hours;
            double chargingFee = getChargingFee(hours);
            double total = parkingFee + chargingFee;
            return applyGreenEnergyDiscount(total); // Calling default method from Chargeable interface
        }

        @Override
        public double getChargingFee(int hours) {
            return CHARGING_STATION_FLAT_FEE + (35.0 * hours);
        }

        @Override
        public String getGpsCoordinates() {
            return gpsLocation;
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("       TASK 09: ABSTRACTION, INTERFACES & MULTIPLE IMPLEMENTATION DEMO          ");
        System.out.println("       Student: K VENKATA VAMSI GOUD | Reg No: 192472021                       ");
        System.out.println("================================================================================");

        System.out.println("\n--- Step 1: Testing Interface Static Method Validation ---");
        String testBay1 = "EV-BAY-104";
        String testBay2 = "REGULAR-SLOT-02";
        System.out.println("  Validating '" + testBay1 + "': " + Chargeable.isValidChargingBayId(testBay1));
        System.out.println("  Validating '" + testBay2 + "': " + Chargeable.isValidChargingBayId(testBay2));

        System.out.println("\n--- Step 2: Instantiating Multi-Interface Concrete Subclass ---");
        SmartAutonomousElectricCar smartCar = new SmartAutonomousElectricCar(
                "TN07EV7777", "Tesla", "Model 3", "EV-BAY-104", "13.0827 N, 80.2707 E");

        smartCar.displayManifest();
        System.out.println("  Vehicle Type: " + smartCar.getCategoryDescription());
        System.out.println("  GPS Location: " + smartCar.getGpsCoordinates());
        smartCar.logTelemetryPing();

        System.out.println("\n--- Step 3: Polymorphic Fee Calculation via Default Interface Logic ---");
        int parkedDuration = 3; // 3 hours
        double finalFee = smartCar.calculateParkingFee(parkedDuration);
        System.out.println(String.format("  Total Billed Fee for %d hours (with green rebate): Rs %.2f", parkedDuration, finalFee));

        System.out.println("\n--- Step 4: Upcasting to Interface Types (Interface References) ---");
        Chargeable chargingRef = smartCar;
        System.out.println("  Interface Reference [Chargeable]: Charging portion for 3 hrs = Rs " + chargingRef.getChargingFee(3));

        Trackable trackableRef = smartCar;
        System.out.println("  Interface Reference [Trackable]: Current coords = " + trackableRef.getGpsCoordinates());

        System.out.println("\n--- Step 5: Abstract Class vs Interface Architectural Comparison ---");
        System.out.println("  1. State: Abstract classes can maintain instance state (fields), interfaces define behavior.");
        System.out.println("  2. Constructors: Abstract classes have constructors for initialization; interfaces do not.");
        System.out.println("  3. Multiple Inheritance: A class can extend only ONE class, but can implement MANY interfaces.");
        System.out.println("================================================================================");
    }
}
