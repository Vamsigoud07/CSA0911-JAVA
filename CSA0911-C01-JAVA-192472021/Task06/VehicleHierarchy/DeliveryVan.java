package Task06.VehicleHierarchy;

/**
 * Task 06 - DeliveryVan Subclass (Hierarchical Inheritance from Vehicle)
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class DeliveryVan extends Vehicle {
    private double payloadCapacityTons;
    private String deliveryVendor;

    public DeliveryVan(String registrationNumber, String brand, String model, double payloadCapacityTons, String deliveryVendor) {
        super(registrationNumber, brand, model, 50.0);
        System.out.println("      [Level 2: DeliveryVan Constructor] DeliveryVan initialized. Vendor: " + deliveryVendor);
        this.payloadCapacityTons = payloadCapacityTons;
        this.deliveryVendor = deliveryVendor;
    }

    @Override
    public String getVehicleType() {
        return "Delivery Van (" + deliveryVendor + ")";
    }

    @Override
    public double calculateParkingFee(long hours) {
        return baseHourlyRate * Math.max(1, hours);
    }

    public double getPayloadCapacityTons() { return payloadCapacityTons; }
    public String getDeliveryVendor() { return deliveryVendor; }
}
