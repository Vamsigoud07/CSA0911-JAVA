package Task11.CompleteParkingSystem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Task 11 - Parking Ticket Record
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class ParkingTicket {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSlot slot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double billedAmount;
    private boolean isPaid;

    public ParkingTicket(String ticketId, Vehicle vehicle, ParkingSlot slot, LocalDateTime entryTime) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.slot = slot;
        this.entryTime = entryTime;
        this.exitTime = null;
        this.billedAmount = 0.0;
        this.isPaid = false;
    }

    public void settle(LocalDateTime exitTime, double billedAmount) {
        this.exitTime = exitTime;
        this.billedAmount = billedAmount;
        this.isPaid = true;
    }

    public String getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSlot getSlot() { return slot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getBilledAmount() { return billedAmount; }
    public boolean isPaid() { return isPaid; }

    @Override
    public String toString() {
        return String.format("Ticket #%-10s | %-10s | %-14s | Floor %d, Bay %02d | In: %s | Paid: Rs %.2f",
                ticketId, vehicle.getRegistrationNumber(), vehicle.getCategory().getDisplayName(),
                slot.getFloorIndex(), slot.getSlotIndex(),
                entryTime.format(TIME_FMT), billedAmount);
    }
}
