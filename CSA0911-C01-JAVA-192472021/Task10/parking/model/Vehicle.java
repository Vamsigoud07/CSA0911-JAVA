package parking.model;

import java.util.Objects;

/**
 * Task 10 - Vehicle Model Entity
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class Vehicle {
    // Encapsulation: Strict private fields
    private String registrationNumber;
    private String vehicleType;
    private String ownerName;

    public Vehicle(String registrationNumber, String vehicleType, String ownerName) {
        setRegistrationNumber(registrationNumber);
        setVehicleType(vehicleType);
        setOwnerName(ownerName);
    }

    // Validating Setters ensuring data integrity
    public void setRegistrationNumber(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be null or empty.");
        }
        this.registrationNumber = registrationNumber.trim().toUpperCase();
    }

    public void setVehicleType(String vehicleType) {
        if (vehicleType == null || vehicleType.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle type cannot be null or empty.");
        }
        this.vehicleType = vehicleType.trim();
    }

    public void setOwnerName(String ownerName) {
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be null or empty.");
        }
        this.ownerName = ownerName.trim();
    }

    // Public Getters
    public String getRegistrationNumber() { return registrationNumber; }
    public String getVehicleType() { return vehicleType; }
    public String getOwnerName() { return ownerName; }

    // Protected method accessible within parking.model and subclasses
    protected String getInternalAuditSummary() {
        return "MODEL-AUDIT: " + registrationNumber + " [" + vehicleType + "]";
    }

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
        return String.format("Vehicle [Reg: %-10s | Type: %-12s | Owner: %s]",
                registrationNumber, vehicleType, ownerName);
    }
}
