package Task06.VehicleHierarchy;

/**
 * Task 06 - TwoWheeler Subclass (Hierarchical Inheritance from Vehicle)
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class TwoWheeler extends Vehicle {
    private boolean hasHelmetStorage;

    public TwoWheeler(String registrationNumber, String brand, String model, boolean hasHelmetStorage) {
        super(registrationNumber, brand, model, 10.0);
        System.out.println("      [Level 2: TwoWheeler Constructor] TwoWheeler initialized with helmet storage: " + hasHelmetStorage);
        this.hasHelmetStorage = hasHelmetStorage;
    }

    @Override
    public String getVehicleType() {
        return "Two-Wheeler";
    }

    @Override
    public double calculateParkingFee(long hours) {
        return baseHourlyRate * Math.max(1, hours);
    }

    public boolean isHasHelmetStorage() { return hasHelmetStorage; }
}
