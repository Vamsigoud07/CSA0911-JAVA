package Task06.VehicleHierarchy;

/**
 * Task 06 - Car Subclass (Hierarchical Inheritance from Vehicle)
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class Car extends Vehicle {
    protected int seatingCapacity;
    protected String fuelType;

    public Car(String registrationNumber, String brand, String model, int seatingCapacity, String fuelType) {
        super(registrationNumber, brand, model, 30.0);
        System.out.println("      [Level 2: Car Constructor] Car initialized with seating: " + seatingCapacity + ", fuel: " + fuelType);
        this.seatingCapacity = seatingCapacity;
        this.fuelType = fuelType;
    }

    @Override
    public String getVehicleType() {
        return "Car (" + fuelType + ")";
    }

    @Override
    public double calculateParkingFee(long hours) {
        return baseHourlyRate * Math.max(1, hours);
    }

    public int getSeatingCapacity() { return seatingCapacity; }
    public String getFuelType() { return fuelType; }
}
