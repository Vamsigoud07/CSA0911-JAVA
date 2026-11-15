package Task12.ExtendedParkingSystem;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Task 12 - One-Day Visitor Parking Pass
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class ParkingPass {
    private final String passId;
    private final String holderName;
    private final String registeredVehiclePlate;
    private final LocalDate validDate;
    private final double passCost;

    public ParkingPass(String passId, String holderName, String registeredVehiclePlate, LocalDate validDate) {
        this.passId = passId;
        this.holderName = holderName;
        this.registeredVehiclePlate = registeredVehiclePlate.trim().toUpperCase();
        this.validDate = validDate;
        this.passCost = 100.0; // Flat one-day pass rate of Rs 100
    }

    public boolean isValidFor(String regNumber, LocalDateTime checkTime) {
        if (regNumber == null || checkTime == null) return false;
        boolean plateMatch = this.registeredVehiclePlate.equalsIgnoreCase(regNumber.trim().toUpperCase());
        boolean dateMatch = this.validDate.equals(checkTime.toLocalDate());
        return plateMatch && dateMatch;
    }

    public String getPassId() { return passId; }
    public String getHolderName() { return holderName; }
    public String getRegisteredVehiclePlate() { return registeredVehiclePlate; }
    public LocalDate getValidDate() { return validDate; }
    public double getPassCost() { return passCost; }

    @Override
    public String toString() {
        return String.format("ParkingPass [%s | Plate: %s | Valid: %s | Cost: Rs %.1f]",
                passId, registeredVehiclePlate, validDate, passCost);
    }
}
