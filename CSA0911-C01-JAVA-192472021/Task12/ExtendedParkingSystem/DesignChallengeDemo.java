package Task12.ExtendedParkingSystem;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Task 12 - Day-4 Design Challenge Verification Driver
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class DesignChallengeDemo {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("          TASK 12: DAY-4 DESIGN CHALLENGE - EXTENDED PARKING SYSTEM             ");
        System.out.println("          Student: K VENKATA VAMSI GOUD | Reg No: 192472021                    ");
        System.out.println("================================================================================");

        ExtendedParkingFacility facility = new ExtendedParkingFacility(new int[]{6, 5, 4, 7, 3});

        System.out.println("\n--- Step 1: Initial 5-Floor Jagged Array Configuration ---");
        facility.display5FloorStatus();

        System.out.println("\n--- Step 2: Test Requirement 1 - E-Bike Flat Parking Fee ---");
        ExtendedVehicle ebike = new EBike("TN01EB1111", "Student E-Rider");
        LocalDateTime ebikeIn = LocalDateTime.of(2026, 11, 15, 8, 30);
        facility.parkVehicle(ebike, ebikeIn);

        // Parked for 7 hours (420 minutes)
        LocalDateTime ebikeOut = ebikeIn.plusHours(7);
        double ebikeFee = facility.processExit("TN01EB1111", ebikeOut);
        System.out.println(String.format("  E-Bike TN01EB1111 checked out after 7 hours. Billed Fee: Rs %.2f (Expected flat: Rs 15.00)", ebikeFee));

        System.out.println("\n--- Step 3: Test Requirements 2 & 3 - Delivery Van Peak vs Off-Peak Pricing ---");
        // Test 3A: Peak Delivery Van (Entry at 11:00 AM - inside 09:00 to 17:00 window)
        ExtendedVehicle peakVan = new DeliveryVan("TN02DV2222", "BlueDart Express");
        LocalDateTime peakIn = LocalDateTime.of(2026, 11, 15, 11, 0); // 11 AM
        facility.parkVehicle(peakVan, peakIn);
        LocalDateTime peakOut = peakIn.plusHours(2); // 2 hours
        double peakFee = facility.processExit("TN02DV2222", peakOut);
        // Rate = Double car rate (30 * 2 = 60/hr) * 2 hours = Rs 120.00
        System.out.println(String.format("  Peak Delivery Van (11 AM to 1 PM, 2h): Rs %.2f (Expected: Rs 120.00 [2h x Rs 60/hr])", peakFee));

        // Test 3B: Off-Peak Delivery Van (Entry at 7:00 PM - outside peak window)
        ExtendedVehicle offPeakVan = new DeliveryVan("TN03DV3333", "Amazon Night Delivery");
        LocalDateTime offPeakIn = LocalDateTime.of(2026, 11, 15, 19, 0); // 7 PM
        facility.parkVehicle(offPeakVan, offPeakIn);
        LocalDateTime offPeakOut = offPeakIn.plusHours(2); // 2 hours
        double offPeakFee = facility.processExit("TN03DV3333", offPeakOut);
        // Rate = Standard car rate (30/hr) * 2 hours = Rs 60.00
        System.out.println(String.format("  Off-Peak Delivery Van (7 PM to 9 PM, 2h): Rs %.2f (Expected: Rs 60.00 [2h x Rs 30/hr])", offPeakFee));

        System.out.println("\n--- Step 4: Test Requirement 4 - One-Day Visitor Pass Waiver ---");
        LocalDate today = LocalDate.of(2026, 11, 15);
        ParkingPass visitorPass = new ParkingPass("PASS-2026-001", "Conference Delegate", "TN04VIP4444", today);
        facility.registerVisitorPass(visitorPass); // Collects Rs 100 upfront pass fee

        ExtendedVehicle visitorCar = new ExtendedCar("TN04VIP4444", "Conference Delegate");
        LocalDateTime passIn = LocalDateTime.of(2026, 11, 15, 9, 0);
        facility.parkVehicle(visitorCar, passIn);

        // Parked for full 9 hours
        LocalDateTime passOut = passIn.plusHours(9);
        double passExitFee = facility.processExit("TN04VIP4444", passOut);
        System.out.println(String.format("  Pass Holder Vehicle checked out after 9 hours. Billed Exit Fee: Rs %.2f (Expected: Rs 0.00 - Waived!)", passExitFee));

        System.out.println("\n--- Step 5: Test Requirements 5 & 6 - Multi-Floor Jagged Allocation Verification ---");
        System.out.println("  Simulating burst ingress across 5 floors with variable capacity...");
        for (int i = 1; i <= 15; i++) {
            ExtendedVehicle temp = new ExtendedCar("TN10CC" + (1000 + i), "Visitor " + i);
            facility.parkVehicle(temp, LocalDateTime.now());
        }
        facility.display5FloorStatus();

        System.out.println("\n--- Step 6: Total System Revenue Audit ---");
        // Total = E-Bike (15) + Peak Van (120) + Off-Peak Van (60) + Pass Upfront (100) + Pass Exit (0) = Rs 295.00
        System.out.println(String.format("  Total Extended Facility Revenue Collected: Rs %.2f (Expected: Rs 295.00)",
                facility.getTotalRevenue()));
        System.out.println("================================================================================");
    }
}
