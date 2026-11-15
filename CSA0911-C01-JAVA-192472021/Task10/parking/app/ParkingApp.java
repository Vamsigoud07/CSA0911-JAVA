package parking.app;

import parking.model.Vehicle;
import parking.model.ParkingTicket;
import parking.service.ParkingService;

import java.time.LocalDateTime;

/**
 * Task 10 - Package Integration, Encapsulation, Wrappers & Autoboxing
 * Student Name: K VENKATA VAMSI GOUD
 * Register Number: 192472021
 * Course: Programming in Java (CSA09)
 * Institution: SIMATS - Saveetha School of Engineering
 */
public class ParkingApp {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("         TASK 10: PACKAGES, ENCAPSULATION, WRAPPERS & AUTOBOXING                ");
        System.out.println("         Student: K VENKATA VAMSI GOUD | Reg No: 192472021                     ");
        System.out.println("================================================================================");

        System.out.println("\n--- Step 1: Encapsulation & Validating Setters Verification ---");
        try {
            Vehicle validVehicle = new Vehicle("TN01A1001", "Car", "Dr. Rajesh");
            System.out.println("  Successfully instantiated: " + validVehicle);

            System.out.println("  Testing defensive setter with empty registration string...");
            validVehicle.setRegistrationNumber("   "); // Throws IllegalArgumentException
        } catch (IllegalArgumentException ex) {
            System.out.println("  [Validation Caught Expected Exception] " + ex.getMessage());
        }

        System.out.println("\n--- Step 2: Multi-Package Service Coordination ---");
        ParkingService service = new ParkingService(2, 2); // 2 floors, 2 slots per floor = 4 slots
        System.out.println("  Initial Free Slots: " + service.getAvailableSlotCount());

        LocalDateTime now = LocalDateTime.now();
        Vehicle v1 = new Vehicle("TN01AA1111", "TwoWheeler", "Vamsi Goud");
        Vehicle v2 = new Vehicle("TN02BB2222", "Car", "Prof. Kumar");
        Vehicle v3 = new Vehicle("TN03CC3333", "ElectricCar", "Dean Office");

        ParkingTicket t1 = service.parkVehicle(v1, now.minusMinutes(125)); // 2h 5m
        ParkingTicket t2 = service.parkVehicle(v2, now.minusMinutes(45));  // 45m
        ParkingTicket t3 = service.parkVehicle(v3, now.minusMinutes(10));  // 10m (grace period)

        System.out.println("\n  Active Tickets In Facility:");
        for (ParkingTicket t : service.getActiveTickets()) {
            System.out.println("    " + t);
        }

        System.out.println("\n--- Step 3: Vehicle Egress & Fee Collection ---");
        service.processExit("TN01AA1111", now); // 125 min -> 3 billed hours * Rs 10 = Rs 30
        service.processExit("TN02BB2222", now); // 45 min -> 1 billed hour * Rs 30 = Rs 30
        service.processExit("TN03CC3333", now); // 10 min -> Grace period = Rs 0.0

        System.out.println("  Total Daily Collection: Rs " + service.getDailyRevenueTotal());
        System.out.println("  Remaining Free Slots: " + service.getAvailableSlotCount());

        System.out.println("\n--- Step 4: Wrapper Classes, Autoboxing & The Integer Cache Trap ---");
        // Autoboxing: primitive int converted to java.lang.Integer object automatically
        Integer slotWrapper1 = 100; // Value inside Byte/Integer Cache (-128 to 127)
        Integer slotWrapper2 = 100;

        Integer slotWrapper3 = 500; // Value OUTSIDE Integer Cache
        Integer slotWrapper4 = 500;

        System.out.println("  Testing Integer Cache (-128 to 127):");
        System.out.println("    Integer(100) == Integer(100)       : " + (slotWrapper1 == slotWrapper2) + "  (Cached reference match!)");
        System.out.println("    Integer(100).equals(Integer(100))  : " + slotWrapper1.equals(slotWrapper2) + "  (Value match)");

        System.out.println("\n  Testing Values Above 127 (Outside Cache):");
        System.out.println("    Integer(500) == Integer(500)       : " + (slotWrapper3 == slotWrapper4) + " (FALSE! Different heap references!)");
        System.out.println("    Integer(500).equals(Integer(500))  : " + slotWrapper3.equals(slotWrapper4) + "  (Value match - ALWAYS USE .equals())");

        // Unboxing: Wrapper Double -> primitive double
        Double boxedRate = Double.valueOf(45.50);
        double primitiveRate = boxedRate; // Implicit unboxing
        System.out.println(String.format("\n  Autoboxing/Unboxing: Boxed Double=%.2f, Unboxed double=%.2f",
                boxedRate, primitiveRate));
        System.out.println("================================================================================");
    }
}
