package Task11.CompleteParkingSystem;

import java.time.LocalDateTime;

/**
 * Task 11 - Automated Test Driver executing all 11 required assignment test cases
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class AutomatedTestDriver {

    public static void runAllTests() {
        System.out.println("==========================================================================================");
        System.out.println("                   TASK 11: COMPREHENSIVE AUTOMATED VERIFICATION SUITE                    ");
        System.out.println("                   Student: K VENKATA VAMSI GOUD | Reg No: 192472021                      ");
        System.out.println("==========================================================================================");

        int passed = 0;
        int failed = 0;

        // Create a compact 3-slot test facility: Floor 0 (2 slots), Floor 1 (1 slot)
        SmartParkingManager testFacility = new SmartParkingManager(new int[]{2, 1});
        LocalDateTime baseTime = LocalDateTime.of(2026, 11, 15, 10, 0); // 10:00 AM

        System.out.println(String.format("%-5s | %-28s | %-16s | %-18s | %-8s",
                "TC_ID", "Test Description", "Input / Action", "Observed Output", "Status"));
        System.out.println("------------------------------------------------------------------------------------------");

        // TEST 1: Normal Parking
        try {
            ParkingTicket t1 = testFacility.parkVehicle("TN01A1000", VehicleCategory.CAR, "Student A", baseTime);
            boolean pass = (t1 != null && t1.getSlot().getFloorIndex() == 0 && t1.getSlot().getSlotIndex() == 1);
            printResult("TC01", "Normal Parking (Car Ingress)", "TN01A1000", "Bay [0,1] Allocated", pass);
            if (pass) passed++; else failed++;
        } catch (Exception e) {
            printResult("TC01", "Normal Parking", "TN01A1000", e.getMessage(), false);
            failed++;
        }

        // TEST 2: Different Vehicle Categories (Two-Wheeler)
        try {
            ParkingTicket t2 = testFacility.parkVehicle("TN01B2000", VehicleCategory.TWO_WHEELER, "Faculty B", baseTime);
            boolean pass = (t2 != null && t2.getSlot().getFloorIndex() == 0 && t2.getSlot().getSlotIndex() == 0);
            printResult("TC02", "Category Isolation (Bike)", "TN01B2000", "Bay [0,0] Dedicated", pass);
            if (pass) passed++; else failed++;
        } catch (Exception e) {
            printResult("TC02", "Category Isolation", "TN01B2000", e.getMessage(), false);
            failed++;
        }

        // TEST 3: Last Available Slot Allocation
        try {
            ParkingTicket t3 = testFacility.parkVehicle("TN01C3000", VehicleCategory.CAR, "Visitor C", baseTime);
            boolean pass = (t3 != null && testFacility.getAvailableSlotCount() == 0);
            printResult("TC03", "Last Available Slot", "TN01C3000", "Bay [1,0] (Free: 0)", pass);
            if (pass) passed++; else failed++;
        } catch (Exception e) {
            printResult("TC03", "Last Available Slot", "TN01C3000", e.getMessage(), false);
            failed++;
        }

        // TEST 4: Full Parking Rejection
        try {
            ParkingTicket t4 = testFacility.parkVehicle("TN01D4000", VehicleCategory.CAR, "Visitor D", baseTime);
            boolean pass = (t4 == null && testFacility.getAvailableSlotCount() == 0);
            printResult("TC04", "Full Parking Rejection", "TN01D4000", "Rejected (NULL)", pass);
            if (pass) passed++; else failed++;
        } catch (Exception e) {
            printResult("TC04", "Full Parking Rejection", "TN01D4000", e.getMessage(), false);
            failed++;
        }

        // TEST 5: Zero-Minute / Grace Period Exit (<= 15 min free)
        try {
            double fee = testFacility.processExit("TN01B2000", baseTime.plusMinutes(10));
            boolean pass = (fee == 0.0);
            printResult("TC05", "Grace Period (10 min stay)", "TN01B2000", "Fee = Rs 0.0 (Free)", pass);
            if (pass) passed++; else failed++;
        } catch (Exception e) {
            printResult("TC05", "Grace Period", "TN01B2000", e.getMessage(), false);
            failed++;
        }

        // TEST 6: Exact Fee Slab Boundary (60 min vs 61 min)
        try {
            // Re-park bike in freed slot
            testFacility.parkVehicle("TN01E5000", VehicleCategory.TWO_WHEELER, "Student E", baseTime);
            double fee60 = testFacility.computeParkingFee(VehicleCategory.TWO_WHEELER, 60, baseTime); // exactly 1 hr = 10.0
            double fee61 = testFacility.computeParkingFee(VehicleCategory.TWO_WHEELER, 61, baseTime); // 61 min = 2 hrs ceiling = 20.0
            boolean pass = (fee60 == 10.0 && fee61 == 20.0);
            printResult("TC06", "Fee Boundary (60 vs 61m)", "60m vs 61m", "Rs 10.0 vs Rs 20.0", pass);
            if (pass) passed++; else failed++;
        } catch (Exception e) {
            printResult("TC06", "Fee Boundary", "60m vs 61m", e.getMessage(), false);
            failed++;
        }

        // TEST 7: Duplicate Registration Prevention
        try {
            testFacility.parkVehicle("TN01A1000", VehicleCategory.CAR, "Duplicate Person", baseTime);
            printResult("TC07", "Duplicate Reg Prevention", "TN01A1000", "Allowed (Bug)", false);
            failed++;
        } catch (IllegalStateException e) {
            printResult("TC07", "Duplicate Reg Prevention", "TN01A1000", "Caught Duplicate", true);
            passed++;
        }

        // TEST 8: Invalid Registration Number Validation
        try {
            testFacility.parkVehicle("   ", VehicleCategory.CAR, "Blank Person", baseTime);
            printResult("TC08", "Blank Reg Plate Reject", "Whitespace", "Allowed (Bug)", false);
            failed++;
        } catch (IllegalArgumentException e) {
            printResult("TC08", "Blank Reg Plate Reject", "Whitespace", "Caught Invalid Reg", true);
            passed++;
        }

        // TEST 9: Invalid Slot / Unknown Vehicle Exit
        try {
            testFacility.processExit("AP99ZZ9999", baseTime.plusHours(2));
            printResult("TC09", "Unknown Vehicle Exit", "AP99ZZ9999", "Processed (Bug)", false);
            failed++;
        } catch (Exception e) {
            printResult("TC09", "Unknown Vehicle Exit", "AP99ZZ9999", "Caught NoSuchRecord", true);
            passed++;
        }

        // TEST 10: Vehicle Exit & Slot Deallocation
        try {
            int beforeCount = testFacility.getAvailableSlotCount();
            double feeCar = testFacility.processExit("TN01A1000", baseTime.plusMinutes(125)); // 2h 5m -> 3h * 30 = Rs 90.0
            int afterCount = testFacility.getAvailableSlotCount();
            boolean pass = (feeCar == 90.0 && afterCount == beforeCount + 1);
            printResult("TC10", "Exit & Slot Deallocation", "TN01A1000 (125m)", "Rs 90.0 (Slot freed)", pass);
            if (pass) passed++; else failed++;
        } catch (Exception e) {
            printResult("TC10", "Exit & Deallocation", "TN01A1000", e.getMessage(), false);
            failed++;
        }

        // TEST 11: Total Daily Revenue Verification
        try {
            double feeLast = testFacility.processExit("TN01C3000", baseTime.plusMinutes(60)); // 1h * 30 = Rs 30.0
            double totalRevenue = testFacility.getDailyTotalRevenue();
            // Total = TC05 (Rs 0) + TC10 (Rs 90) + TC11 (Rs 30) = Rs 120.0
            boolean pass = (totalRevenue == 120.0);
            printResult("TC11", "Daily Revenue Accounting", "Sum 3 Checkouts", "Rs 120.0 Verified", pass);
            if (pass) passed++; else failed++;
        } catch (Exception e) {
            printResult("TC11", "Daily Revenue", "Audit Sum", e.getMessage(), false);
            failed++;
        }

        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println(String.format(" Automated Test Suite Result: %d / %d Tests Passed (%.1f%% Success Rate)",
                passed, (passed + failed), (passed * 100.0 / (passed + failed))));
        System.out.println("==========================================================================================");
    }

    private static void printResult(String id, String desc, String input, String output, boolean status) {
        System.out.println(String.format("%-5s | %-28s | %-16s | %-18s | %s",
                id, desc, input, output, (status ? "[PASS]" : "[FAIL]")));
    }

    public static void main(String[] args) {
        runAllTests();
    }
}
