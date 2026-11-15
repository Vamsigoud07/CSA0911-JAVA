package Task11.CompleteParkingSystem;

/**
 * Task 11 - Parking Slot Representation
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class ParkingSlot {
    private final int floorIndex;
    private final int slotIndex;
    private final VehicleCategory supportedCategory;
    private boolean isOccupied;
    private Vehicle currentVehicle;

    public ParkingSlot(int floorIndex, int slotIndex, VehicleCategory supportedCategory) {
        this.floorIndex = floorIndex;
        this.slotIndex = slotIndex;
        this.supportedCategory = supportedCategory;
        this.isOccupied = false;
        this.currentVehicle = null;
    }

    public boolean canAccommodate(VehicleCategory cat) {
        if (isOccupied) return false;
        // Dedicated EV bays only accept Electric Cars
        if (this.supportedCategory == VehicleCategory.ELECTRIC_CAR) {
            return cat == VehicleCategory.ELECTRIC_CAR;
        }
        // Dedicated Two-Wheeler bays only accept Two-Wheelers
        if (this.supportedCategory == VehicleCategory.TWO_WHEELER) {
            return cat == VehicleCategory.TWO_WHEELER;
        }
        // General car / multi-use slots accept Cars, Delivery Vans, etc.
        return true;
    }

    public void occupy(Vehicle vehicle) {
        this.currentVehicle = vehicle;
        this.isOccupied = true;
    }

    public Vehicle vacate() {
        Vehicle departing = this.currentVehicle;
        this.currentVehicle = null;
        this.isOccupied = false;
        return departing;
    }

    public int getFloorIndex() { return floorIndex; }
    public int getSlotIndex() { return slotIndex; }
    public VehicleCategory getSupportedCategory() { return supportedCategory; }
    public boolean isOccupied() { return isOccupied; }
    public Vehicle getCurrentVehicle() { return currentVehicle; }

    @Override
    public String toString() {
        return String.format("Floor %d, Bay %02d [%-12s] -> %s",
                floorIndex, slotIndex, supportedCategory.getDisplayName(),
                (isOccupied ? "OCCUPIED (" + currentVehicle.getRegistrationNumber() + ")" : "VACANT"));
    }
}
