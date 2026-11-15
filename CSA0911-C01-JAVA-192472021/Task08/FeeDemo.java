package Task08;

/**
 * Task 08 - Polymorphism, Dynamic Method Dispatch & Interfaces
 * Student Name: K VENKATA VAMSI GOUD
 * Register Number: 192472021
 * Course: Programming in Java (CSA09)
 * Institution: SIMATS - Saveetha School of Engineering
 */

// Abstract Vehicle Base Class
abstract class Vehicle {
    protected String regNumber;
    protected int hours;

    public Vehicle(String regNumber) {
        this(regNumber, 3);
    }

    public Vehicle(String regNumber, int hours) {
        this.regNumber = regNumber;
        this.hours = hours;
    }

    // Method Overloading: calculateFee() with and without parameters
    public double calculateFee() {
        return calculateFee(this.hours > 0 ? this.hours : 3);
    }

    // Abstract method to be overridden polymorphically
    public abstract double calculateFee(int hours);

    public abstract String getVehicleType();

    public String getRegistrationNumber() {
        return regNumber;
    }
}

// TwoWheeler Subclass
class TwoWheeler extends Vehicle {
    private static final double RATE_PER_HOUR = 10.0;

    public TwoWheeler(String regNumber) {
        super(regNumber);
    }

    public TwoWheeler(String regNumber, int hours) {
        super(regNumber, hours);
    }

    @Override
    public double calculateFee(int hours) {
        return RATE_PER_HOUR * hours;
    }

    @Override
    public String getVehicleType() {
        return "TwoWheeler";
    }
}

// Car Subclass
class Car extends Vehicle {
    protected static final double BASE_CAR_RATE = 30.0;

    public Car(String regNumber) {
        super(regNumber);
    }

    public Car(String regNumber, int hours) {
        super(regNumber, hours);
    }

    @Override
    public double calculateFee(int hours) {
        return BASE_CAR_RATE * hours;
    }

    @Override
    public String getVehicleType() {
        return "Car";
    }
}

// ElectricCar Subclass implementing Chargeable interface
class ElectricCar extends Car implements Chargeable {
    public ElectricCar(String regNumber) {
        super(regNumber);
    }

    public ElectricCar(String regNumber, int hours) {
        super(regNumber, hours);
    }

    @Override
    public double getChargingFee(int hours) {
        return CHARGING_RATE_PER_HOUR * hours;
    }

    @Override
    public double calculateFee(int hours) {
        // Base car parking rate (30/hr) + EV fast-charging surcharge (40/hr) = 70/hr
        return super.calculateFee(hours) + getChargingFee(hours);
    }

    @Override
    public String getVehicleType() {
        return "ElectricCar";
    }
}

public class FeeDemo {

    /**
     * Mandatory provided main method.
     * DO NOT MODIFY THIS METHOD.
     */
    public static void main(String[] args) {
        Vehicle v1 = new TwoWheeler("TN01A1", 3);
        Vehicle v2 = new Car("TN02B2", 3);
        Vehicle v3 = new ElectricCar("TN03C3", 3);

        System.out.println(v1.getVehicleType() + " " + v1.getRegistrationNumber() + " -> Rs " + v1.calculateFee(3));
        System.out.println(v2.getVehicleType() + " " + v2.getRegistrationNumber() + " -> Rs " + v2.calculateFee(3));
        System.out.println(v3.getVehicleType() + " " + v3.getRegistrationNumber() + " -> Rs " + v3.calculateFee(3));
        System.out.println(v3 instanceof Chargeable);
    }
}
