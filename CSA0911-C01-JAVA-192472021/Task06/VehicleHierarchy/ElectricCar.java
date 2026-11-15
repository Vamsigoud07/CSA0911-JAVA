package Task06.VehicleHierarchy;

/**
 * Task 06 - ElectricCar Subclass (Multilevel Inheritance: Vehicle -> Car -> ElectricCar)
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class ElectricCar extends Car {
    private double batteryCapacityKWh;
    private double chargingRatePerHour;

    public ElectricCar(String registrationNumber, String brand, String model, int seatingCapacity,
                       double batteryCapacityKWh, double chargingRatePerHour) {
        super(registrationNumber, brand, model, seatingCapacity, "Electric");
        System.out.println("        [Level 3: ElectricCar Constructor] ElectricCar initialized. Battery: " + batteryCapacityKWh + " kWh");
        this.batteryCapacityKWh = batteryCapacityKWh;
        this.chargingRatePerHour = chargingRatePerHour;
    }

    @Override
    public String getVehicleType() {
        return "Electric Car";
    }

    @Override
    public double calculateParkingFee(long hours) {
        // Parking base rate + EV fast charging station rate
        long billedHours = Math.max(1, hours);
        double parkingPortion = baseHourlyRate * billedHours;
        double chargingPortion = chargingRatePerHour * billedHours;
        return parkingPortion + chargingPortion;
    }

    public double getBatteryCapacityKWh() { return batteryCapacityKWh; }
    public double getChargingRatePerHour() { return chargingRatePerHour; }
}
