package parking.model;

/**
 * Task 10 - ParkingSlot Model Entity
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class ParkingSlot {
    private final int floor;
    private final int slotNumber;
    private final String supportedCategory;
    private boolean isOccupied;
    private Vehicle parkedVehicle;

    public ParkingSlot(int floor, int slotNumber, String supportedCategory) {
        if (floor < 0) throw new IllegalArgumentException("Floor cannot be negative.");
        if (slotNumber < 0) throw new IllegalArgumentException("Slot number cannot be negative.");
        this.floor = floor;
        this.slotNumber = slotNumber;
        this.supportedCategory = (supportedCategory == null || supportedCategory.trim().isEmpty()) ? "Car" : supportedCategory;
        this.isOccupied = false;
        this.parkedVehicle = null;
    }

    public boolean assignVehicle(Vehicle vehicle) {
        if (isOccupied || vehicle == null) {
            return false;
        }
        this.parkedVehicle = vehicle;
        this.isOccupied = true;
        return true;
    }

    public Vehicle releaseVehicle() {
        if (!isOccupied) {
            return null;
        }
        Vehicle departing = this.parkedVehicle;
        this.parkedVehicle = null;
        this.isOccupied = false;
        return departing;
    }

    public int getFloor() { return floor; }
    public int getSlotNumber() { return slotNumber; }
    public String getSupportedCategory() { return supportedCategory; }
    public boolean isOccupied() { return isOccupied; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }

    @Override
    public String toString() {
        return String.format("Slot [F:%d, S:%d | Type: %-10s | Occupied: %s | Vehicle: %s]",
                floor, slotNumber, supportedCategory, isOccupied,
                (parkedVehicle != null ? parkedVehicle.getRegistrationNumber() : "NONE"));
    }
}
