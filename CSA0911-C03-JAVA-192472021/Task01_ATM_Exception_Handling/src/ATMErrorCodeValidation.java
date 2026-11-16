package task01;

import java.io.File;
import java.util.Scanner;

/**
 * Task 01 - Alternative Implementation:
 * Validation using Scanner.hasNextInt() / hasNextDouble() and Return Error Codes.
 * Demonstrates the non-exception based approach and allows comparative analysis.
 */
public class ATMErrorCodeValidation {

    // Return status codes
    public static final int SUCCESS = 0;
    public static final int ERR_CARD_READER_MISSING = 1;
    public static final int ERR_INVALID_NUMERIC_FORMAT = 2;
    public static final int ERR_NEGATIVE_AMOUNT = 3;
    public static final int ERR_INSUFFICIENT_BALANCE = 4;

    private static double currentBalance = 10000.00;
    private static final String CARD_READER_CONFIG = "card_reader.cfg";

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   ATM SIMULATION: ERROR CODE & SCANNER VALIDATION");
        System.out.println("   Student: K VENKATA VAMSI GOUD (192472021)      ");
        System.out.println("==================================================");

        String cardFile = args.length >= 3 ? args[2] : CARD_READER_CONFIG;
        String accInput = args.length >= 1 ? args[0] : "9876543210";
        String amtInput = args.length >= 2 ? args[1] : "1500.00";

        int statusCode = processWithdrawalWithCodes(accInput, amtInput, cardFile);
        System.out.println("\n[RESULT STATUS CODE]: " + statusCode);
        System.out.println("Status Interpretation: " + interpretStatusCode(statusCode));
        System.out.printf("Final Account Balance: INR %.2f\n", currentBalance);
        System.out.println("==================================================");
    }

    public static int processWithdrawalWithCodes(String accStr, String amtStr, String cardConfigFile) {
        // Step 1: Check card reader configuration file presence
        File cfg = new File(cardConfigFile);
        if (!cfg.exists()) {
            return ERR_CARD_READER_MISSING;
        }

        // Step 2: Validate Account Number format using Scanner
        Scanner accScanner = new Scanner(accStr.trim());
        if (!accScanner.hasNextLong()) {
            accScanner.close();
            return ERR_INVALID_NUMERIC_FORMAT;
        }
        long accNo = accScanner.nextLong();
        accScanner.close();

        // Step 3: Validate Amount format using Scanner
        Scanner amtScanner = new Scanner(amtStr.trim());
        if (!amtScanner.hasNextDouble()) {
            amtScanner.close();
            return ERR_INVALID_NUMERIC_FORMAT;
        }
        double amount = amtScanner.nextDouble();
        amtScanner.close();

        // Step 4: Business Rule - Positive amount check
        if (amount <= 0.0) {
            return ERR_NEGATIVE_AMOUNT;
        }

        // Step 5: Business Rule - Balance check
        if (amount > currentBalance) {
            return ERR_INSUFFICIENT_BALANCE;
        }

        // State update
        currentBalance -= amount;
        System.out.printf("[DISPENSED] Cash dispensed: INR %.2f to Account: %d\n", amount, accNo);
        return SUCCESS;
    }

    public static String interpretStatusCode(int code) {
        switch (code) {
            case SUCCESS:
                return "SUCCESS (Code 0): Withdrawal processed successfully.";
            case ERR_CARD_READER_MISSING:
                return "ERROR (Code 1): Card reader hardware config missing.";
            case ERR_INVALID_NUMERIC_FORMAT:
                return "ERROR (Code 2): Non-numeric string provided.";
            case ERR_NEGATIVE_AMOUNT:
                return "ERROR (Code 3): Withdrawal amount must be greater than zero.";
            case ERR_INSUFFICIENT_BALANCE:
                return "ERROR (Code 4): Withdrawal amount exceeds available balance.";
            default:
                return "UNKNOWN ERROR (Code " + code + ")";
        }
    }
}
