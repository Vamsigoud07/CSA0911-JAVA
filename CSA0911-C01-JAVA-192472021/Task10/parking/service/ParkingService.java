package parking.service;

import parking.model.ParkingSlot;
import parking.model.ParkingTicket;
import parking.model.Vehicle;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Task 10 - Core Parking Management Service
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class ParkingService {
    private final List<ParkingSlot> slots;
    private final Map<String, ParkingTicket> activeTicketsByReg;
    private final FeeCalculationService feeService;
    private int ticketSequence;
    private double dailyRevenueTotal;

    public ParkingService(int floorCount, int slotsPerFloor) {
        this.slots = new ArrayList<>();
        this.activeTicketsByReg = new HashMap<>();
        this.feeService = new FeeCalculationService();
        this.ticketSequence = 5000;
        this.dailyRevenueTotal = 0.0;

        // Initialize slots
        for (int f = 0; f < floorCount; f++) {
            for (int s = 0; s < slotsPerFloor; s++) {
                String cat = (s == 0) ? "TwoWheeler" : (s == 1 ? "ElectricCar" : "Car");
                slots.add(new ParkingSlot(f, s, cat));
            }
        }
    }

    public ParkingTicket parkVehicle(Vehicle vehicle, LocalDateTime entryTime) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Cannot park null vehicle.");
        }

        // Prevent duplicate registration number entry
        if (activeTicketsByReg.containsKey(vehicle.getRegistrationNumber())) {
            throw new IllegalStateException("Vehicle " + vehicle.getRegistrationNumber() + " is already parked in facility!");
        }

        // Find available compatible slot
        ParkingSlot compatibleSlot = null;
        for (ParkingSlot slot : slots) {
            if (!slot.isOccupied() && isCompatible(slot.getSupportedCategory(), vehicle.getVehicleType())) {
                compatibleSlot = slot;
                break;
            }
        }

        // Fallback: If exact match full, check any general vacant slot
        if (compatibleSlot == null) {
            for (ParkingSlot slot : slots) {
                if (!slot.isOccupied()) {
                    compatibleSlot = slot;
                    break;
                }
            }
        }

        if (compatibleSlot == null) {
            System.out.println("  [Full Reject] No available slot for " + vehicle.getRegistrationNumber());
            return null; // Facility full
        }

        compatibleSlot.assignVehicle(vehicle);
        String ticketId = "TKT-" + (++ticketSequence);
        ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, compatibleSlot, entryTime);
        activeTicketsByReg.put(vehicle.getRegistrationNumber(), ticket);
        return ticket;
    }

    public double processExit(String regNumber, LocalDateTime exitTime) {
        if (!activeTicketsByReg.containsKey(regNumber)) {
            throw new NoSuchElementException("No active parking record found for registration: " + regNumber);
        }

        ParkingTicket ticket = activeTicketsByReg.remove(regNumber);
        ticket.getAllocatedSlot().releaseVehicle();

        long durationMinutes = Duration.between(ticket.getEntryTime(), exitTime).toMinutes();
        double fee = feeService.calculateFee(ticket.getVehicle(), durationMinutes);
        ticket.closeTicket(exitTime, fee);
        dailyRevenueTotal += fee;

        System.out.println(String.format("  [Exit Processed] Ticket: %s | Reg: %s | Parked: %d min | Fee: Rs %.2f",
                ticket.getTicketId(), regNumber, durationMinutes, fee));
        return fee;
    }

    private boolean isCompatible(String slotCategory, String vehicleCategory) {
        return slotCategory.equalsIgnoreCase(vehicleCategory);
    }

    public int getAvailableSlotCount() {
        int count = 0;
        for (ParkingSlot s : slots) {
            if (!s.isOccupied()) count++;
        }
        return count;
    }

    public double getDailyRevenueTotal() {
        return dailyRevenueTotal;
    }

    public Collection<ParkingTicket> getActiveTickets() {
        return Collections.unmodifiableCollection(activeTicketsByReg.values());
    }
}
