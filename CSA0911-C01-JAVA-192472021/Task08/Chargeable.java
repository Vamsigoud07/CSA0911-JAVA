package Task08;

/**
 * Task 08 - Chargeable Interface
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public interface Chargeable {
    double CHARGING_RATE_PER_HOUR = 40.0;

    double getChargingFee(int hours);

    default double applyGreenRebate(double totalFee) {
        return totalFee * 0.95; // 5% green incentive rebate
    }
}
