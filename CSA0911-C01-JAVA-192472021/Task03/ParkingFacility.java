package Task03;

import java.util.Arrays;

/**
 * Task 03 - Parking Facility Control Logic Implementation
 * Student Name: K VENKATA VAMSI GOUD
 * Register Number: 192472021
 * Course: Programming in Java (CSA09)
 * Institution: SIMATS - Saveetha School of Engineering
 */
public class ParkingFacility {

    // 2D Jagged array representing parking slots across floors
    // occupied[f][s] = true indicates slot 's' on floor 'f' is occupied
    private final boolean[][] occupied;

    /**
     * Default constructor creating a facility matching the test configuration:
     * Floor 0 has 2 slots, Floor 1 has 1 slot (total capacity = 3 slots).
     */
    public ParkingFacility() {
        this(new int[]{2, 1});
    }

    /**
     * Parameterised constructor initialising jagged floor grid based on capacities.
     * @param floorCapacities Array where index represents floor and value represents slot count.
     */
    public ParkingFacility(int[] floorCapacities) {
        if (floorCapacities == null || floorCapacities.length == 0) {
            this.occupied = new boolean[][]{new boolean[2], new boolean[1]};
        } else {
            this.occupied = new boolean[floorCapacities.length][];
            for (int i = 0; i < floorCapacities.length; i++) {
                this.occupied[i] = new boolean[floorCapacities[i]];
            }
        }
    }

    /**
     * Allocates the first available free slot in the facility using linear floor scan.
     * @return int array containing [floorIndex, slotIndex], or null if facility is completely full.
     */
    public int[] allocateSlot() {
        for (int floor = 0; floor < occupied.length; floor++) {
            for (int slot = 0; slot < occupied[floor].length; slot++) {
                // Control statement checking availability
                if (!occupied[floor][slot]) {
                    occupied[floor][slot] = true; // Mark as occupied
                    return new int[]{floor, slot}; // Immediate exit upon allocation
                }
            }
        }
        return null; // Entire facility is full
    }

    /**
     * Releases an occupied parking slot.
     * @param floor Floor index
     * @param slot Slot index
     * @return true if slot was occupied and successfully released; false if slot is invalid or already vacant.
     */
    public boolean releaseSlot(int floor, int slot) {
        // Boundary validation checks
        if (floor < 0 || floor >= occupied.length) {
            return false;
        }
        if (slot < 0 || slot >= occupied[floor].length) {
            return false;
        }

        // Check current status
        if (occupied[floor][slot]) {
            occupied[floor][slot] = false; // Release the bay
            return true;
        }
        return false; // Slot was already vacant
    }

    /**
     * Calculates parking fee based on vehicle type and duration in hours.
     * Demonstrates switch, if-else, and arithmetic control flow.
     * @param vehicleType Type of vehicle ("TwoWheeler", "Car", "ElectricCar", "DeliveryVan")
     * @param hours Parked duration in hours
     * @return Total calculated fee in INR
     */
    public double calculateFee(String vehicleType, int hours) {
        if (hours <= 0) {
            return 0.0;
        }

        double ratePerHour;
        switch (vehicleType.toLowerCase()) {
            case "twowheeler":
                ratePerHour = 10.0;
                break;
            case "car":
                ratePerHour = 30.0;
                break;
            case "electriccar":
                ratePerHour = 25.0; // Green concession
                break;
            case "deliveryvan":
                ratePerHour = 50.0;
                break;
            default:
                ratePerHour = 20.0;
                break;
        }

        double total = ratePerHour * hours;
        // Discount control logic for long stays (> 8 hours)
        if (hours >= 8) {
            total = total * 0.90; // 10% daily discount
        }
        return total;
    }

    /**
     * Checks if a specific slot is occupied.
     */
    public boolean isOccupied(int floor, int slot) {
        if (floor >= 0 && floor < occupied.length && slot >= 0 && slot < occupied[floor].length) {
            return occupied[floor][slot];
        }
        return false;
    }

    /**
     * Mandatory provided main method.
     * DO NOT MODIFY THIS METHOD.
     */
    public static void main(String[] args) {
        ParkingFacility facility = new ParkingFacility(new int[]{2, 1});
        System.out.println(Arrays.toString(facility.allocateSlot()));
        System.out.println(Arrays.toString(facility.allocateSlot()));
        System.out.println(Arrays.toString(facility.allocateSlot()));
        System.out.println(Arrays.toString(facility.allocateSlot()));
        System.out.println(facility.releaseSlot(0, 1) + " " + facility.releaseSlot(0, 1) + " " + facility.releaseSlot(0, 5));
        System.out.println(Arrays.toString(facility.allocateSlot()));
    }
}
