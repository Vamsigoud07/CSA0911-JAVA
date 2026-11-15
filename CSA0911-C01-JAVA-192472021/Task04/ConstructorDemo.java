package Task04;

/**
 * Task 04 - Classes, Objects, and Constructors Demonstration
 * Student Name: K VENKATA VAMSI GOUD
 * Register Number: 192472021
 * Course: Programming in Java (CSA09)
 * Institution: SIMATS - Saveetha School of Engineering
 */
public class ConstructorDemo {

    // Base Class demonstrating constructor mechanics
    public static class VehicleTicket {
        private String ticketId;
        private String regNumber;
        private String category;
        private double baseRate;
        private boolean isPaid;

        /**
         * 1. Default Constructor
         * Employs constructor chaining via this() to call the 3-arg constructor.
         */
        public VehicleTicket() {
            this("TKT-DEFAULT", "UNREGISTERED", "TwoWheeler");
            System.out.println("  [Base Ticket] Default constructor invoked via this().");
        }

        /**
         * 2. Parameterised Constructor (3 arguments)
         * Chains to the 4-argument master constructor.
         */
        public VehicleTicket(String ticketId, String regNumber, String category) {
            this(ticketId, regNumber, category, 20.0);
            System.out.println("  [Base Ticket] 3-argument constructor invoked.");
        }

        /**
         * 3. Master Parameterised Constructor (Overloading & Initialization)
         */
        public VehicleTicket(String ticketId, String regNumber, String category, double baseRate) {
            this.ticketId = ticketId;
            this.regNumber = regNumber;
            this.category = category;
            this.baseRate = baseRate;
            this.isPaid = false;
            System.out.println("  [Base Ticket] Master 4-argument constructor executed.");
        }

        /**
         * 4. Copy Constructor
         * Creates an independent deep copy of an existing ticket object.
         */
        public VehicleTicket(VehicleTicket other) {
            if (other == null) {
                throw new IllegalArgumentException("Cannot copy null VehicleTicket instance.");
            }
            this.ticketId = other.ticketId + "-COPY";
            this.regNumber = other.regNumber;
            this.category = other.category;
            this.baseRate = other.baseRate;
            this.isPaid = other.isPaid;
            System.out.println("  [Base Ticket] Copy constructor executed for ticket: " + other.ticketId);
        }

        /**
         * 5. Static Factory Method
         * Encapsulates object creation with expressive naming and validation.
         */
        public static VehicleTicket createCarTicket(String regNumber) {
            System.out.println("  [Static Factory] Generating standard Car ticket...");
            return new VehicleTicket("TKT-CAR-" + System.currentTimeMillis() % 10000, regNumber, "Car", 30.0);
        }

        public void printTicketSummary() {
            System.out.println(String.format("    Ticket ID: %-14s | Reg: %-10s | Category: %-12s | Rate: Rs %.1f | Paid: %s",
                    ticketId, regNumber, category, baseRate, isPaid));
        }

        // Getters and Setters
        public String getTicketId() { return ticketId; }
        public String getRegNumber() { return regNumber; }
        public String getCategory() { return category; }
        public double getBaseRate() { return baseRate; }
        public boolean isPaid() { return isPaid; }
        public void setPaid(boolean paid) { this.isPaid = paid; }
    }

    // Subclass demonstrating super() invocation and constructor hierarchy
    public static class PremiumElectricTicket extends VehicleTicket {
        private int chargingSlotNumber;
        private double chargingFee;

        /**
         * Subclass constructor chaining to parent using super()
         */
        public PremiumElectricTicket(String ticketId, String regNumber, int chargingSlotNumber, double chargingFee) {
            super(ticketId, regNumber, "ElectricCar", 35.0); // Calls parent 4-arg constructor
            this.chargingSlotNumber = chargingSlotNumber;
            this.chargingFee = chargingFee;
            System.out.println("  [Subclass Electric] Subclass constructor executed with charging slot: " + chargingSlotNumber);
        }

        @Override
        public void printTicketSummary() {
            super.printTicketSummary();
            System.out.println(String.format("      └─ EV Charging Bay: Slot #%-3d | Fast-Charging Surcharge: Rs %.1f",
                    chargingSlotNumber, chargingFee));
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("      TASK 04: CONSTRUCTORS, OBJECT LIFECYCLE & MEMORY DEMONSTRATION           ");
        System.out.println("      Student: K VENKATA VAMSI GOUD | Reg No: 192472021                        ");
        System.out.println("================================================================================");

        System.out.println("\n--- Step 1: Default Constructor & Constructor Chaining (this()) ---");
        VehicleTicket t1 = new VehicleTicket();
        t1.printTicketSummary();

        System.out.println("\n--- Step 2: Overloaded Parameterised Constructor ---");
        VehicleTicket t2 = new VehicleTicket("TKT-101", "TN01A1000", "TwoWheeler", 15.0);
        t2.printTicketSummary();

        System.out.println("\n--- Step 3: Copy Constructor (Deep Copy Verification) ---");
        VehicleTicket t3 = new VehicleTicket(t2);
        t3.printTicketSummary();
        System.out.println("  Reference Equality Check (t2 == t3): " + (t2 == t3) + " (Distinct Heap Objects)");

        System.out.println("\n--- Step 4: Subclass Constructor Chaining using super() ---");
        PremiumElectricTicket evTicket = new PremiumElectricTicket("TKT-EV201", "TN03EV999", 5, 50.0);
        evTicket.printTicketSummary();

        System.out.println("\n--- Step 5: Static Factory Method Instantiation ---");
        VehicleTicket factoryTicket = VehicleTicket.createCarTicket("TN02B2026");
        factoryTicket.printTicketSummary();

        System.out.println("\n--- Step 6: Memory Architecture Reflection ---");
        System.out.println("  Stack Allocations: References 't1', 't2', 't3', 'evTicket', 'factoryTicket'");
        System.out.println("  Heap Allocations:  5 distinct object instances with individual field memory blocks.");
        System.out.println("================================================================================");
    }
}
