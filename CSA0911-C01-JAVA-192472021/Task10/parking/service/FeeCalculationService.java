package parking.service;

import parking.model.Vehicle;

/**
 * Task 10 - Fee Calculation Service
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class FeeCalculationService {

    public double calculateFee(Vehicle vehicle, long durationMinutes) {
        if (vehicle == null || durationMinutes <= 0) {
            return 0.0;
        }

        // Grace period: First 15 minutes free of charge
        if (durationMinutes <= 15) {
            return 0.0;
        }

        // Ceiling hours calculation
        int billedHours = (int) Math.ceil(durationMinutes / 60.0);

        double ratePerHour;
        switch (vehicle.getVehicleType().toLowerCase()) {
            case "twowheeler":
                ratePerHour = 10.0;
                break;
            case "car":
                ratePerHour = 30.0;
                break;
            case "electriccar":
                ratePerHour = 25.0; // Concessionary green parking rate
                break;
            case "deliveryvan":
                ratePerHour = 50.0;
                break;
            default:
                ratePerHour = 20.0;
                break;
        }

        return ratePerHour * billedHours;
    }
}
