package Task06.VehicleHierarchy;

/**
 * Task 06 - Vehicle Hierarchy Base Class
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public abstract class Vehicle {
    protected String registrationNumber;
    protected String brand;
    protected String model;
    protected double baseHourlyRate;

    public Vehicle(String registrationNumber, String brand, String model, double baseHourlyRate) {
        System.out.println("    [Level 1: Vehicle Constructor] Initializing base Vehicle: " + registrationNumber);
        this.registrationNumber = registrationNumber;
        this.brand = brand;
        this.model = model;
        this.baseHourlyRate = baseHourlyRate;
    }

    public abstract String getVehicleType();
    public abstract double calculateParkingFee(long hours);

    public void displayBasicInfo() {
        System.out.println(String.format("Vehicle [Reg: %-10s | Make: %-8s | Model: %-10s | Base Rate: Rs %.1f/hr]",
                registrationNumber, brand, model, baseHourlyRate));
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public double getBaseHourlyRate() { return baseHourlyRate; }
}
