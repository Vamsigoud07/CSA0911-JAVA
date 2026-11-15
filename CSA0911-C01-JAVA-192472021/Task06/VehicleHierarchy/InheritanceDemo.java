package Task06.VehicleHierarchy;

/**
 * Task 06 - Inheritance Demonstration Driver
 * Student Name: K VENKATA VAMSI GOUD | Reg No: 192472021
 */
public class InheritanceDemo {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("          TASK 06: INHERITANCE HIERARCHY & CONSTRUCTOR CHAINING DEMO            ");
        System.out.println("          Student: K VENKATA VAMSI GOUD | Reg No: 192472021                    ");
        System.out.println("================================================================================");

        System.out.println("\n--- Step 1: Demonstrating Hierarchical Inheritance (Vehicle -> TwoWheeler) ---");
        TwoWheeler bike = new TwoWheeler("TN01A1000", "Honda", "Activa 6G", true);

        System.out.println("\n--- Step 2: Demonstrating Single Inheritance (Vehicle -> Car) ---");
        Car sedan = new Car("TN02B2000", "Hyundai", "Verna", 5, "Petrol");

        System.out.println("\n--- Step 3: Demonstrating Multilevel Inheritance (Vehicle -> Car -> ElectricCar) ---");
        ElectricCar ev = new ElectricCar("TN03C3000", "Tata", "Nexon EV", 5, 40.5, 40.0);

        System.out.println("\n--- Step 4: Demonstrating Specialized Subclass (Vehicle -> DeliveryVan) ---");
        DeliveryVan van = new DeliveryVan("TN04D4000", "Mahindra", "Bolero Maxi", 1.5, "Amazon Logistics");

        System.out.println("\n--- Step 5: Polymorphic Processing Across Inheritance Tree ---");
        Vehicle[] fleet = new Vehicle[]{bike, sedan, ev, van};

        System.out.println(String.format("%-14s | %-12s | %-22s | %-10s | %-12s",
                "Registration", "Make/Model", "Category", "Billed (3h)", "Base Rate"));
        System.out.println("--------------------------------------------------------------------------------");
        for (Vehicle v : fleet) {
            double fee = v.calculateParkingFee(3);
            System.out.println(String.format("%-14s | %-12s | %-22s | Rs %-7.1f | Rs %.1f/hr",
                    v.getRegistrationNumber(),
                    v.getBrand() + " " + v.getModel(),
                    v.getVehicleType(),
                    fee,
                    v.getBaseHourlyRate()));
        }
        System.out.println("================================================================================");
    }
}
