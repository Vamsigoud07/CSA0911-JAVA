package parking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Task 10 - ParkingTicket Model Entity
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class ParkingTicket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSlot allocatedSlot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Double feeCharged; // Wrapper class Double (allows null prior to exit)
    private Boolean isSettled; // Wrapper class Boolean

    public ParkingTicket(String ticketId, Vehicle vehicle, ParkingSlot allocatedSlot, LocalDateTime entryTime) {
        if (ticketId == null || vehicle == null || allocatedSlot == null || entryTime == null) {
            throw new IllegalArgumentException("Ticket parameters cannot be null.");
        }
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.allocatedSlot = allocatedSlot;
        this.entryTime = entryTime;
        this.exitTime = null;
        this.feeCharged = null;
        this.isSettled = false; // Autoboxing primitive false -> Boolean
    }

    public void closeTicket(LocalDateTime exitTime, double fee) {
        if (exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Exit time cannot precede entry time.");
        }
        this.exitTime = exitTime;
        this.feeCharged = fee; // Autoboxing primitive double -> Double
        this.isSettled = true;  // Autoboxing primitive boolean -> Boolean
    }

    public String getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSlot getAllocatedSlot() { return allocatedSlot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public Double getFeeCharged() { return feeCharged; }
    public Boolean getIsSettled() { return isSettled; }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Ticket [%-12s | Reg: %-10s | Floor: %d, Slot: %d | Entry: %s | Settled: %s | Fee: Rs %s]",
                ticketId, vehicle.getRegistrationNumber(),
                allocatedSlot.getFloor(), allocatedSlot.getSlotNumber(),
                entryTime.format(dtf), isSettled,
                (feeCharged != null ? String.format("%.2f", feeCharged) : "PENDING"));
    }
}
