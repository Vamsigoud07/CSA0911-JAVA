package Task12.ExtendedParkingSystem;

import java.time.LocalDateTime;

/**
 * Task 12 - Standard Extended Car Entity
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class ExtendedCar extends ExtendedVehicle {
    public static final double CAR_RATE = 30.0;

    public ExtendedCar(String registrationNumber, String ownerName) {
        super(registrationNumber, ownerName, new CarPricingStrategy());
    }

    @Override
    public String getCategoryName() {
        return "Standard Car";
    }

    public static class CarPricingStrategy implements PricingStrategy {
        @Override
        public double calculateFee(ExtendedVehicle vehicle, long durationMinutes, LocalDateTime entryTime, ParkingPass activePass) {
            if (activePass != null && activePass.isValidFor(vehicle.getRegistrationNumber(), entryTime)) {
                return 0.0;
            }
            if (durationMinutes <= 15) {
                return 0.0;
            }
            long billedHours = (long) Math.ceil(durationMinutes / 60.0);
            return CAR_RATE * billedHours;
        }
    }
}
