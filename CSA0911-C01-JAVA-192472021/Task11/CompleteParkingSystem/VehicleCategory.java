package Task11.CompleteParkingSystem;

/**
 * Task 11 - Vehicle Category Specification
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public enum VehicleCategory {
    TWO_WHEELER("Two-Wheeler", 10.0, false),
    CAR("Car (Sedan/Hatchback)", 30.0, false),
    ELECTRIC_CAR("Electric Car", 25.0, true),
    DELIVERY_VAN("Delivery Van", 50.0, false);

    private final String displayName;
    private final double hourlyRate;
    private final boolean requiresChargingBay;

    VehicleCategory(String displayName, double hourlyRate, boolean requiresChargingBay) {
        this.displayName = displayName;
        this.hourlyRate = hourlyRate;
        this.requiresChargingBay = requiresChargingBay;
    }

    public String getDisplayName() { return displayName; }
    public double getHourlyRate() { return hourlyRate; }
    public boolean isRequiresChargingBay() { return requiresChargingBay; }

    public static VehicleCategory parseCategory(String input) {
        if (input == null) return CAR;
        String normalized = input.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        for (VehicleCategory cat : values()) {
            if (cat.name().equalsIgnoreCase(normalized) || cat.displayName.equalsIgnoreCase(input.trim())) {
                return cat;
            }
        }
        return CAR; // Default fallback
    }
}
