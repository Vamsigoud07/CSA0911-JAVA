package Task11.CompleteParkingSystem;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Task 11 - Robust Encapsulated Vehicle Entity
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class Vehicle {
    private static final Pattern REG_PATTERN = Pattern.compile("^[A-Z]{2}[0-9]{1,2}[A-Z]{0,3}[0-9]{1,4}$");

    private final String registrationNumber;
    private final VehicleCategory category;
    private final String ownerName;

    public Vehicle(String registrationNumber, VehicleCategory category, String ownerName) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration plate cannot be blank.");
        }
        String cleanReg = registrationNumber.trim().toUpperCase().replace(" ", "").replace("-", "");
        if (cleanReg.length() < 4 || cleanReg.length() > 12) {
            throw new IllegalArgumentException("Invalid registration plate length: " + registrationNumber);
        }
        this.registrationNumber = cleanReg;
        this.category = (category != null) ? category : VehicleCategory.CAR;
        this.ownerName = (ownerName != null && !ownerName.trim().isEmpty()) ? ownerName.trim() : "Campus Visitor";
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public VehicleCategory getCategory() { return category; }
    public String getOwnerName() { return ownerName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(registrationNumber, vehicle.registrationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationNumber);
    }

    @Override
    public String toString() {
        return String.format("[%s | %-12s | %s]", registrationNumber, category.getDisplayName(), ownerName);
    }
}
