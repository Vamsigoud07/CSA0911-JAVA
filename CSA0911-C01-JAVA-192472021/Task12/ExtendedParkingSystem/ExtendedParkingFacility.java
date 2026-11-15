package Task12.ExtendedParkingSystem;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Task 12 - Extended Multi-Storey 5-Floor Facility using Jagged Array Architecture
 * Demonstrates 5 floors with variable capacity per floor.
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class ExtendedParkingFacility {

    public static class ExtendedSlot {
        private final int floor;
        private final int slotNumber;
        private final String supportedCategory;
        private boolean occupied;
        private ExtendedVehicle currentVehicle;

        public ExtendedSlot(int floor, int slotNumber, String supportedCategory) {
            this.floor = floor;
            this.slotNumber = slotNumber;
            this.supportedCategory = supportedCategory;
            this.occupied = false;
            this.currentVehicle = null;
        }

        public boolean isOccupied() { return occupied; }
        public int getFloor() { return floor; }
        public int getSlotNumber() { return slotNumber; }
        public String getSupportedCategory() { return supportedCategory; }
        public ExtendedVehicle getCurrentVehicle() { return currentVehicle; }

        public void occupy(ExtendedVehicle vehicle) {
            this.currentVehicle = vehicle;
            this.occupied = true;
        }

        public ExtendedVehicle vacate() {
            ExtendedVehicle v = this.currentVehicle;
            this.currentVehicle = null;
            this.occupied = false;
            return v;
        }
    }

    public static class ExtendedTicket {
        private final String ticketId;
        private final ExtendedVehicle vehicle;
        private final ExtendedSlot slot;
        private final LocalDateTime entryTime;
        private final ParkingPass associatedPass;

        public ExtendedTicket(String ticketId, ExtendedVehicle vehicle, ExtendedSlot slot,
                              LocalDateTime entryTime, ParkingPass associatedPass) {
            this.ticketId = ticketId;
            this.vehicle = vehicle;
            this.slot = slot;
            this.entryTime = entryTime;
            this.associatedPass = associatedPass;
        }

        public String getTicketId() { return ticketId; }
        public ExtendedVehicle getVehicle() { return vehicle; }
        public ExtendedSlot getSlot() { return slot; }
        public LocalDateTime getEntryTime() { return entryTime; }
        public ParkingPass getAssociatedPass() { return associatedPass; }
    }

    // 5-Floor Jagged Array Structure: each row represents a floor with independent slot count
    private final ExtendedSlot[][] facilityGrid;
    private final Map<String, ExtendedTicket> activeTickets;
    private final Map<String, ParkingPass> registeredPasses;
    private int ticketSequence;
    private double totalRevenue;

    /**
     * Default constructor implementing Requirement 5 & 6:
     * 5 Floors with asymmetric jagged capacities:
     * Floor 0 (Ground): 6 bays (Delivery vans & heavy visitor vehicles)
     * Floor 1: 5 bays
     * Floor 2: 4 bays (Compact bays for E-Bikes and two-wheelers)
     * Floor 3: 7 bays (General car parking)
     * Floor 4 (Rooftop): 3 bays (Dedicated EV & Faculty bays)
     */
    public ExtendedParkingFacility() {
        this(new int[]{6, 5, 4, 7, 3});
    }

    public ExtendedParkingFacility(int[] floorCapacities) {
        if (floorCapacities == null || floorCapacities.length != 5) {
            floorCapacities = new int[]{6, 5, 4, 7, 3}; // 5-floor requirement enforcement
        }

        this.facilityGrid = new ExtendedSlot[floorCapacities.length][];
        this.activeTickets = new HashMap<>();
        this.registeredPasses = new HashMap<>();
        this.ticketSequence = 2000;
        this.totalRevenue = 0.0;

        for (int f = 0; f < floorCapacities.length; f++) {
            this.facilityGrid[f] = new ExtendedSlot[floorCapacities[f]];
            for (int s = 0; s < floorCapacities[f]; s++) {
                String cat;
                if (f == 2) cat = "EBike";
                else if (f == 0 && s < 2) cat = "DeliveryVan";
                else cat = "General";

                this.facilityGrid[f][s] = new ExtendedSlot(f, s, cat);
            }
        }
    }

    public void registerVisitorPass(ParkingPass pass) {
        if (pass != null) {
            registeredPasses.put(pass.getRegisteredVehiclePlate(), pass);
            totalRevenue += pass.getPassCost(); // Collect upfront pass fee
            System.out.println("  [Visitor Pass Activated] " + pass);
        }
    }

    public ExtendedTicket parkVehicle(ExtendedVehicle vehicle, LocalDateTime entryTime) {
        if (vehicle == null) throw new IllegalArgumentException("Cannot park null vehicle.");
        if (activeTickets.containsKey(vehicle.getRegistrationNumber())) {
            throw new IllegalStateException("Duplicate entry: Vehicle " + vehicle.getRegistrationNumber() + " is already inside.");
        }

        // Check if an active visitor pass exists for this plate
        ParkingPass pass = registeredPasses.get(vehicle.getRegistrationNumber());

        // Allocate slot across 5 jagged floors
        ExtendedSlot targetSlot = null;
        for (ExtendedSlot[] floor : facilityGrid) {
            for (ExtendedSlot slot : floor) {
                if (!slot.isOccupied()) {
                    targetSlot = slot;
                    break;
                }
            }
            if (targetSlot != null) break;
        }

        if (targetSlot == null) {
            System.out.println("  [FACILITY FULL] No slots available across all 5 floors.");
            return null;
        }

        targetSlot.occupy(vehicle);
        String tktId = "EXT-TKT-" + (++ticketSequence);
        ExtendedTicket ticket = new ExtendedTicket(tktId, vehicle, targetSlot, entryTime, pass);
        activeTickets.put(vehicle.getRegistrationNumber(), ticket);

        return ticket;
    }

    public double processExit(String regNumber, LocalDateTime exitTime) {
        String cleanReg = regNumber.trim().toUpperCase();
        if (!activeTickets.containsKey(cleanReg)) {
            throw new NoSuchElementException("Vehicle not found: " + cleanReg);
        }

        ExtendedTicket ticket = activeTickets.remove(cleanReg);
        ticket.getSlot().vacate();

        long durationMinutes = Duration.between(ticket.getEntryTime(), exitTime).toMinutes();
        if (durationMinutes < 0) durationMinutes = 0;

        // Dynamic fee calculation via Strategy Pattern
        double fee = ticket.getVehicle().getPricingStrategy().calculateFee(
                ticket.getVehicle(), durationMinutes, ticket.getEntryTime(), ticket.getAssociatedPass());

        totalRevenue += fee;
        return fee;
    }

    public void display5FloorStatus() {
        System.out.println("\n----------------- 5-FLOOR JAGGED ARRAY CAPACITY TELEMETRY -----------------");
        int totalCap = 0;
        int totalOccupied = 0;
        for (int f = 0; f < facilityGrid.length; f++) {
            int floorOcc = 0;
            for (int s = 0; s < facilityGrid[f].length; s++) {
                if (facilityGrid[f][s].isOccupied()) floorOcc++;
            }
            totalCap += facilityGrid[f].length;
            totalOccupied += floorOcc;
            System.out.println(String.format("  Floor %d: %2d / %2d slots occupied (Available: %2d slots)",
                    f, floorOcc, facilityGrid[f].length, (facilityGrid[f].length - floorOcc)));
        }
        System.out.println(String.format("  TOTAL FACILITY: %d / %d slots occupied across 5 Floors.",
                totalOccupied, totalCap));
        System.out.println("---------------------------------------------------------------------------");
    }

    public double getTotalRevenue() { return totalRevenue; }
}
