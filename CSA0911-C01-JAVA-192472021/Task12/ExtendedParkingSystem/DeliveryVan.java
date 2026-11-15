package Task12.ExtendedParkingSystem;

import java.time.LocalDateTime;

/**
 * Task 12 - DeliveryVan Vehicle Category with Peak-Hour Dynamic Pricing
 * Requirement: Delivery vans pay double the car rate between 9 AM and 5 PM.
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class DeliveryVan extends ExtendedVehicle {
    public static final double STANDARD_CAR_HOURLY_RATE = 30.0;

    public DeliveryVan(String registrationNumber, String ownerName) {
        super(registrationNumber, ownerName, new DeliveryVanPricingStrategy());
    }

    @Override
    public String getCategoryName() {
        return "Delivery Van";
    }

    public static class DeliveryVanPricingStrategy implements PricingStrategy {
        @Override
        public double calculateFee(ExtendedVehicle vehicle, long durationMinutes, LocalDateTime entryTime, ParkingPass activePass) {
            // One-Day Pass waiver
            if (activePass != null && activePass.isValidFor(vehicle.getRegistrationNumber(), entryTime)) {
                return 0.0;
            }
            if (durationMinutes <= 15) {
                return 0.0;
            }

            long billedHours = (long) Math.ceil(durationMinutes / 60.0);
            int entryHour = entryTime.getHour();

            // Business Rule: Delivery vans pay double the car rate between 9 AM (09:00) and 5 PM (17:00)
            boolean isPeakHours = (entryHour >= 9 && entryHour < 17);
            double appliedHourlyRate = isPeakHours ? (STANDARD_CAR_HOURLY_RATE * 2.0) : STANDARD_CAR_HOURLY_RATE;

            return appliedHourlyRate * billedHours;
        }
    }
}
