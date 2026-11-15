package Task12.ExtendedParkingSystem;

import java.time.LocalDateTime;

/**
 * Task 12 - EBike Vehicle Category with Flat-Fee Pricing Strategy
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class EBike extends ExtendedVehicle {
    public static final double FLAT_EBIKE_FEE = 15.0;

    public EBike(String registrationNumber, String ownerName) {
        super(registrationNumber, ownerName, new EBikePricingStrategy());
    }

    @Override
    public String getCategoryName() {
        return "E-Bike (Flat Fee)";
    }

    // Concrete Inner Pricing Strategy for E-Bike
    public static class EBikePricingStrategy implements PricingStrategy {
        @Override
        public double calculateFee(ExtendedVehicle vehicle, long durationMinutes, LocalDateTime entryTime, ParkingPass activePass) {
            // One-day visitor pass waives all fees
            if (activePass != null && activePass.isValidFor(vehicle.getRegistrationNumber(), entryTime)) {
                return 0.0;
            }
            // Grace period: <= 15 minutes is free
            if (durationMinutes <= 15) {
                return 0.0;
            }
            // Flat fee irrespective of duration up to 24 hours
            return FLAT_EBIKE_FEE;
        }
    }
}
