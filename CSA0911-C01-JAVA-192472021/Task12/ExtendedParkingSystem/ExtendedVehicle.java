package Task12.ExtendedParkingSystem;

/**
 * Task 12 - Extended Vehicle Abstract Base Class
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public abstract class ExtendedVehicle {
    protected final String registrationNumber;
    protected final String ownerName;
    protected final PricingStrategy pricingStrategy;

    public ExtendedVehicle(String registrationNumber, String ownerName, PricingStrategy pricingStrategy) {
        this.registrationNumber = registrationNumber.trim().toUpperCase();
        this.ownerName = ownerName;
        this.pricingStrategy = pricingStrategy;
    }

    public abstract String getCategoryName();

    public String getRegistrationNumber() { return registrationNumber; }
    public String getOwnerName() { return ownerName; }
    public PricingStrategy getPricingStrategy() { return pricingStrategy; }
}
