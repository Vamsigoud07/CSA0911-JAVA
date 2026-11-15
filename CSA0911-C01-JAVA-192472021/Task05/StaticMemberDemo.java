package Task05;

/**
 * Task 05 - Static Members, Object Lifecycle & Multi-Facility Management
 * Student Name: K VENKATA VAMSI GOUD
 * Register Number: 192472021
 * Course: Programming in Java (CSA09)
 * Institution: SIMATS - Saveetha School of Engineering
 */
public class StaticMemberDemo {

    public static class CampusParkingBranch {
        // STATIC VARIABLES: Shared globally across ALL branches in SIMATS campus
        private static int globalTicketCounter;
        private static int globalActiveOccupancy;
        private static double totalCampusRevenue;
        public static final String UNIVERSITY_NAME = "SIMATS - Saveetha School of Engineering";

        // Static Initialisation Block: Executes once when class is loaded into JVM Method Area
        static {
            globalTicketCounter = 1000; // Starting ticket sequence
            globalActiveOccupancy = 0;
            totalCampusRevenue = 0.0;
            System.out.println("[Static Init Block] CampusParkingBranch class loaded. Global sequence initialized to " + globalTicketCounter);
        }

        // INSTANCE VARIABLES: Unique to each independent physical parking building
        private final String branchId;
        private final String branchName;
        private final int localCapacity;
        private int localOccupancy;
        private double localRevenue;

        // Instance Initialisation Block: Runs before constructor
        {
            this.localOccupancy = 0;
            this.localRevenue = 0.0;
        }

        public CampusParkingBranch(String branchId, String branchName, int localCapacity) {
            this.branchId = branchId;
            this.branchName = branchName;
            this.localCapacity = localCapacity;
            System.out.println(String.format("  [Instance Created] Branch: %s (%s) | Max Capacity: %d slots",
                    branchName, branchId, localCapacity));
        }

        /**
         * Instance Method: Parks a vehicle at this specific branch.
         * Updates BOTH instance state (branch occupancy) and static state (global counter & occupancy).
         */
        public String issueTicket(String regNumber, double estimatedFee) {
            if (localOccupancy >= localCapacity) {
                System.out.println("  [Full Warning] " + branchName + " is at maximum capacity!");
                return null;
            }

            // Increment local (instance) state
            localOccupancy++;
            localRevenue += estimatedFee;

            // Increment global (static) state
            globalActiveOccupancy++;
            totalCampusRevenue += estimatedFee;
            int currentTicketNumber = ++globalTicketCounter;

            String ticketId = String.format("%s-TKT-%05d", branchId, currentTicketNumber);
            System.out.println(String.format("    --> Ticket Issued: %-16s | Vehicle: %-10s | At: %s",
                    ticketId, regNumber, branchName));
            return ticketId;
        }

        /**
         * Instance Method: Vehicle exits this specific branch.
         */
        public boolean processExit(String ticketId) {
            if (localOccupancy > 0) {
                localOccupancy--;
                globalActiveOccupancy--;
                System.out.println("    <-- Vehicle Exited: " + ticketId + " from " + branchName);
                return true;
            }
            return false;
        }

        public void printBranchStatus() {
            System.out.println(String.format("  Branch Status [%s - %s]: Occupancy: %d/%d slots | Revenue: Rs %.2f",
                    branchId, branchName, localOccupancy, localCapacity, localRevenue));
        }

        // STATIC METHODS: Can be called without any object instance; operate on static state
        public static int getGlobalTicketCounter() {
            return globalTicketCounter;
        }

        public static int getGlobalActiveOccupancy() {
            return globalActiveOccupancy;
        }

        public static double getTotalCampusRevenue() {
            return totalCampusRevenue;
        }

        public static void printCampusWideAudit() {
            System.out.println("\n----------------- SIMATS CAMPUS-WIDE AUDIT REPORT -----------------");
            System.out.println("  Institution:             " + UNIVERSITY_NAME);
            System.out.println("  Total Tickets Issued:    " + (globalTicketCounter - 1000));
            System.out.println("  Last Ticket Number:      " + globalTicketCounter);
            System.out.println("  Active Parked Vehicles:  " + globalActiveOccupancy);
            System.out.println("  Total Campus Revenue:    Rs " + totalCampusRevenue);
            System.out.println("-------------------------------------------------------------------");
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("        TASK 05: STATIC MEMBERS, OBJECT LIFECYCLE & MULTI-FACILITY SCENARIO     ");
        System.out.println("        Student: K VENKATA VAMSI GOUD | Reg No: 192472021                      ");
        System.out.println("================================================================================");

        System.out.println("\n--- Step 1: Instantiate Multiple Physical Parking Facilities ---");
        // Creating two independent parking facility branches
        CampusParkingBranch northGateBranch = new CampusParkingBranch("NG", "North Gate Facility", 3);
        CampusParkingBranch southGateBranch = new CampusParkingBranch("SG", "South Gate Facility", 2);

        System.out.println("\n--- Step 2: Ingress Activity Across Facilities (Modifying Static & Instance) ---");
        String t1 = northGateBranch.issueTicket("TN01AA1111", 30.0);
        String t2 = northGateBranch.issueTicket("TN01BB2222", 50.0);
        String t3 = southGateBranch.issueTicket("TN02CC3333", 30.0);
        String t4 = northGateBranch.issueTicket("TN01DD4444", 25.0);

        // Attempting to exceed North Gate capacity (3 slots full)
        String t5 = northGateBranch.issueTicket("TN01EE5555", 30.0);

        System.out.println("\n--- Step 3: Local Facility Status (Instance Variables Isolation) ---");
        northGateBranch.printBranchStatus();
        southGateBranch.printBranchStatus();

        System.out.println("\n--- Step 4: Egress Activity ---");
        northGateBranch.processExit(t1);

        System.out.println("\n--- Step 5: Updated Local Status After Exit ---");
        northGateBranch.printBranchStatus();
        southGateBranch.printBranchStatus();

        System.out.println("\n--- Step 6: Global Centralized Telemetry (Static Methods & Variables) ---");
        // Invoking static methods via class name (Best Practice)
        CampusParkingBranch.printCampusWideAudit();

        System.out.println("\n--- Step 7: Object Lifecycle and Garbage Collection Demonstration ---");
        CampusParkingBranch tempFacility = new CampusParkingBranch("TEMP", "Temporary Sports Complex Parking", 50);
        tempFacility.issueTicket("TN99ZZ9999", 100.0);
        
        System.out.println("  Assigning tempFacility reference to null (Eligible for Garbage Collection)...");
        tempFacility = null; // Unreferencing object

        System.out.println("  Requesting JVM Garbage Collection cycle via System.gc()...");
        System.gc(); // Suggestion to JVM Garbage Collector
        System.out.println("  JVM GC triggered. Notice: Static totals remain intact even after object de-referencing.");
        CampusParkingBranch.printCampusWideAudit();
        System.out.println("================================================================================");
    }
}
