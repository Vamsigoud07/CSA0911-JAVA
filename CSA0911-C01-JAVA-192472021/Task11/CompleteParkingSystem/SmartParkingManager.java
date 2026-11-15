package Task11.CompleteParkingSystem;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Task 11 - Smart Parking Engine Orchestrator
 * Implements all 14 Functional Core Requirements.
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class SmartParkingManager {
    // 2D Jagged Grid representing Floors and Slots
    private final ParkingSlot[][] floorSlots;
    // Fast O(1) active ticket lookup by Registration Number (duplicate prevention)
    private final Map<String, ParkingTicket> activeTicketsByReg;
    // Archive of closed tickets for daily auditing
    private final List<ParkingTicket> closedTickets;
    private int ticketSequenceCounter;
    private double dailyTotalRevenue;

    public SmartParkingManager() {
        this(new int[]{4, 4, 2});
    }

    public SmartParkingManager(int[] capacities) {
        this.floorSlots = new ParkingSlot[capacities.length][];
        this.activeTicketsByReg = new HashMap<>();
        this.closedTickets = new ArrayList<>();
        this.ticketSequenceCounter = 1000;
        this.dailyTotalRevenue = 0.0;

        for (int f = 0; f < capacities.length; f++) {
            floorSlots[f] = new ParkingSlot[capacities[f]];
            for (int s = 0; s < capacities[f]; s++) {
                VehicleCategory cat;
                if (s == 0 && capacities[f] > 1) {
                    cat = VehicleCategory.TWO_WHEELER;
                } else if (s == 1 && f > 0) {
                    cat = VehicleCategory.ELECTRIC_CAR;
                } else if (f == 0 && s == 2) {
                    cat = VehicleCategory.DELIVERY_VAN;
                } else {
                    cat = VehicleCategory.CAR;
                }

                floorSlots[f][s] = new ParkingSlot(f, s, cat);
            }
        }
    }

    public ParkingTicket parkVehicle(String regNumber, VehicleCategory category, String ownerName, LocalDateTime entryTime) {
        if (regNumber == null || regNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration plate cannot be empty.");
        }
        Vehicle vehicle = new Vehicle(regNumber, category, ownerName);

        if (activeTicketsByReg.containsKey(vehicle.getRegistrationNumber())) {
            throw new IllegalStateException("Duplicate entry rejected: Vehicle " + vehicle.getRegistrationNumber() + " is currently parked inside.");
        }

        ParkingSlot targetSlot = null;
        // Priority 1: Exact category match
        for (ParkingSlot[] floor : floorSlots) {
            for (ParkingSlot slot : floor) {
                if (slot.canAccommodate(vehicle.getCategory()) && slot.getSupportedCategory() == vehicle.getCategory()) {
                    targetSlot = slot;
                    break;
                }
            }
            if (targetSlot != null) break;
        }

        // Priority 2: General compatible slot
        if (targetSlot == null) {
            for (ParkingSlot[] floor : floorSlots) {
                for (ParkingSlot slot : floor) {
                    if (slot.canAccommodate(vehicle.getCategory())) {
                        targetSlot = slot;
                        break;
                    }
                }
                if (targetSlot != null) break;
            }
        }

        if (targetSlot == null) {
            System.out.println("  [FACILITY FULL] No compatible vacant slot available for category: " + vehicle.getCategory().getDisplayName());
            return null;
        }

        targetSlot.occupy(vehicle);
        String ticketId = String.format("TKT-%05d", ++ticketSequenceCounter);
        ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, targetSlot, entryTime);
        activeTicketsByReg.put(vehicle.getRegistrationNumber(), ticket);

        return ticket;
    }

    public double processExit(String regNumber, LocalDateTime exitTime) {
        if (regNumber == null || regNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration plate must be supplied.");
        }
        String cleanReg = regNumber.trim().toUpperCase().replace(" ", "").replace("-", "");

        if (!activeTicketsByReg.containsKey(cleanReg)) {
            throw new NoSuchElementException("No active parking record found for vehicle: " + cleanReg);
        }

        ParkingTicket ticket = activeTicketsByReg.remove(cleanReg);
        ticket.getSlot().vacate();

        long durationMinutes = Duration.between(ticket.getEntryTime(), exitTime).toMinutes();
        if (durationMinutes < 0) durationMinutes = 0;

        double fee = computeParkingFee(ticket.getVehicle().getCategory(), durationMinutes, ticket.getEntryTime());
        ticket.settle(exitTime, fee);

        dailyTotalRevenue += fee;
        closedTickets.add(ticket);

        return fee;
    }

    public double computeParkingFee(VehicleCategory category, long durationMinutes, LocalDateTime entryTime) {
        if (durationMinutes <= 15) {
            return 0.0;
        }

        long billedHours = (long) Math.ceil(durationMinutes / 60.0);
        double rate = category.getHourlyRate();

        if (category == VehicleCategory.DELIVERY_VAN) {
            int entryHour = entryTime.getHour();
            if (entryHour >= 9 && entryHour < 17) {
                rate = rate * 2.0;
            }
        }

        if (category == VehicleCategory.ELECTRIC_CAR) {
            rate = rate + 45.0;
        }

        return rate * billedHours;
    }

    public void displayActiveTickets() {
        System.out.println("\n--------------------------- ACTIVE PARKING TICKETS ---------------------------");
        if (activeTicketsByReg.isEmpty()) {
            System.out.println("  No vehicles currently parked.");
        } else {
            for (ParkingTicket t : activeTicketsByReg.values()) {
                System.out.println("  " + t);
            }
        }
        System.out.println("------------------------------------------------------------------------------");
    }

    public void displayOccupancyGrid() {
        System.out.println("\n======================== REAL-TIME FACILITY GRID ========================");
        for (int f = 0; f < floorSlots.length; f++) {
            System.out.println(" Floor " + f + ":");
            for (int s = 0; s < floorSlots[f].length; s++) {
                ParkingSlot slot = floorSlots[f][s];
                String status = slot.isOccupied() ? "[X] " + slot.getCurrentVehicle().getRegistrationNumber() : "[ ] VACANT";
                System.out.println(String.format("   Slot %02d (%-14s): %s", s, slot.getSupportedCategory().getDisplayName(), status));
            }
        }
        System.out.println(" Total Free Slots: " + getAvailableSlotCount() + " / " + getTotalCapacity());
        System.out.println("=========================================================================");
    }

    public int getAvailableSlotCount() {
        int count = 0;
        for (ParkingSlot[] floor : floorSlots) {
            for (ParkingSlot slot : floor) {
                if (!slot.isOccupied()) count++;
            }
        }
        return count;
    }

    public int getTotalCapacity() {
        int total = 0;
        for (ParkingSlot[] floor : floorSlots) {
            total += floor.length;
        }
        return total;
    }

    public double getDailyTotalRevenue() { return dailyTotalRevenue; }
    public Map<String, ParkingTicket> getActiveTicketsByReg() { return Collections.unmodifiableMap(activeTicketsByReg); }
    public List<ParkingTicket> getClosedTickets() { return Collections.unmodifiableList(closedTickets); }
}
