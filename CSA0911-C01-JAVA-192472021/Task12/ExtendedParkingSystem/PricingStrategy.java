package Task12.ExtendedParkingSystem;

import java.time.LocalDateTime;

/**
 * Task 12 - Extensible Pricing Strategy Contract (Open-Closed Principle)
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public interface PricingStrategy {
    double calculateFee(ExtendedVehicle vehicle, long durationMinutes, LocalDateTime entryTime, ParkingPass activePass);
}
