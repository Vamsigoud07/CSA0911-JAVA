package src;

import java.util.Objects;

/**
 * Medicine item model for Pharmacy Inventory Management.
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class Medicine implements Comparable<Medicine> {
    private final String code;
    private final String name;
    private final String category;
    private final double price;
    private final int stockQuantity;

    public Medicine(String code, String name, String category, double price, int stockQuantity) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicine medicine = (Medicine) o;
        return Objects.equals(code, medicine.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public int compareTo(Medicine o) {
        return this.code.compareTo(o.code);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | $%.2f | Stock: %d", code, name, category, price, stockQuantity);
    }
}
