package Task11.CompleteParkingSystem;

import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Task 11 - Interactive Menu-Driven Console Application
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class SmartParkingConsoleApp {

    public static void main(String[] args) {
        SmartParkingManager manager = new SmartParkingManager();
        Scanner scanner = new Scanner(System.in);
        boolean exitRequested = false;

        System.out.println("================================================================================");
        System.out.println("             CAMPUS SMART-PARKING MANAGEMENT SYSTEM - SIMATS                    ");
        System.out.println("             Student: K VENKATA VAMSI GOUD | Reg No: 192472021                 ");
        System.out.println("================================================================================");

        while (!exitRequested) {
            System.out.println("\n-------------------------------- MAIN MENU --------------------------------");
            System.out.println(" 1. Park Vehicle (Ingress & Ticket Generation)");
            System.out.println(" 2. Process Vehicle Exit (Egress, Billing & Release)");
            System.out.println(" 3. View Active Parking Tickets");
            System.out.println(" 4. View Facility Occupancy Grid");
            System.out.println(" 5. View Daily Revenue & Audit Telemetry");
            System.out.println(" 6. Run Comprehensive Automated Test Suite (All 11 Test Cases)");
            System.out.println(" 7. Exit System");
            System.out.print(" Enter your choice (1-7): ");

            String input = scanner.nextLine().trim();
            int choice = -1;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(" Invalid numerical choice. Please enter a digit between 1 and 7.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print(" Enter Vehicle Registration Number (e.g. TN01A1000): ");
                    String reg = scanner.nextLine().trim();
                    System.out.println(" Available Categories: 1. Two-Wheeler | 2. Car | 3. Electric Car | 4. Delivery Van");
                    System.out.print(" Choose category (1-4): ");
                    String catChoice = scanner.nextLine().trim();
                    VehicleCategory cat;
                    switch (catChoice) {
                        case "1": cat = VehicleCategory.TWO_WHEELER; break;
                        case "2": cat = VehicleCategory.CAR; break;
                        case "3": cat = VehicleCategory.ELECTRIC_CAR; break;
                        case "4": cat = VehicleCategory.DELIVERY_VAN; break;
                        default: cat = VehicleCategory.CAR; break;
                    }
                    System.out.print(" Enter Driver / Owner Name: ");
                    String owner = scanner.nextLine().trim();
                    try {
                        ParkingTicket ticket = manager.parkVehicle(reg, cat, owner, LocalDateTime.now());
                        if (ticket != null) {
                            System.out.println(" >> SUCCESS! Parking Ticket Issued:");
                            System.out.println("    " + ticket);
                        }
                    } catch (Exception ex) {
                        System.out.println(" >> REGISTRATION FAILED: " + ex.getMessage());
                    }
                    break;

                case 2:
                    System.out.print(" Enter Vehicle Registration to Exit: ");
                    String exitReg = scanner.nextLine().trim();
                    try {
                        double fee = manager.processExit(exitReg, LocalDateTime.now());
                        System.out.println(String.format(" >> SUCCESS! Vehicle %s checked out. Total Fee Collected: Rs %.2f",
                                exitReg, fee));
                    } catch (Exception ex) {
                        System.out.println(" >> EXIT FAILED: " + ex.getMessage());
                    }
                    break;

                case 3:
                    manager.displayActiveTickets();
                    break;

                case 4:
                    manager.displayOccupancyGrid();
                    break;

                case 5:
                    System.out.println("\n----------------------- DAILY REVENUE & TELEMETRY -----------------------");
                    System.out.println(" Total Vehicles Processed Today: " + manager.getClosedTickets().size());
                    System.out.println(" Currently Parked Vehicles:      " + manager.getActiveTicketsByReg().size());
                    System.out.println(String.format(" Total Revenue Collected:        Rs %.2f", manager.getDailyTotalRevenue()));
                    System.out.println(" Total Vacant Bays:              " + manager.getAvailableSlotCount() + " / " + manager.getTotalCapacity());
                    System.out.println("-------------------------------------------------------------------------");
                    break;

                case 6:
                    System.out.println("\n Executing Automated Test Suite...");
                    AutomatedTestDriver.runAllTests();
                    break;

                case 7:
                    System.out.println(" Exiting Campus Smart-Parking System. Have a pleasant day!");
                    exitRequested = true;
                    break;

                default:
                    System.out.println(" Invalid option selected. Please choose between 1 and 7.");
                    break;
            }
        }
    }
}
