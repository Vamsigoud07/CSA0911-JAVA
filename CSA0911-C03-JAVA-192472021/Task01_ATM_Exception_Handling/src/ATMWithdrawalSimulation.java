package task01;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

// Custom exception for negative or zero withdrawal amount
class InvalidAmountException extends IllegalArgumentException {
    public InvalidAmountException(String message) {
        super(message);
    }
}

// Custom exception for exceeding current account balance
class InsufficientBalanceException extends Exception {
    private final double balance;
    private final double requested;

    public InsufficientBalanceException(String message, double balance, double requested) {
        super(message);
        this.balance = balance;
        this.requested = requested;
    }

    public double getBalance() {
        return balance;
    }

    public double getRequested() {
        return requested;
    }
}

/**
 * Task 01: Bank ATM Withdrawal Simulation
 * Demonstrates try, catch, multi-catch, finally, checked and unchecked exceptions.
 */
public class ATMWithdrawalSimulation {
    private static double currentBalance = 10000.00;
    private static final String CARD_READER_CONFIG = "card_reader.cfg";

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SIMATS SSE BANKING ATM SIMULATION SYSTEM       ");
        System.out.println("   Student: K VENKATA VAMSI GOUD (192472021)      ");
        System.out.println("==================================================");

        if (args.length >= 2) {
            // Run non-interactively with arguments: <accNo> <amount> [cardConfigPath]
            String cardFile = args.length >= 3 ? args[2] : CARD_READER_CONFIG;
            processWithdrawal(args[0], args[1], cardFile);
        } else {
            // Interactive mode
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Account Number: ");
            String accStr = scanner.nextLine();
            System.out.print("Enter Withdrawal Amount: ");
            String amtStr = scanner.nextLine();
            processWithdrawal(accStr, amtStr, CARD_READER_CONFIG);
        }
    }

    public static void processWithdrawal(String accStr, String amtStr, String cardConfigFile) {
        System.out.println("\n[ATM Session Initiated]");
        System.out.println("Input Account Number : " + accStr);
        System.out.println("Input Amount         : " + amtStr);
        System.out.println("Card Config Path     : " + cardConfigFile);
        System.out.printf("Current Balance      : INR %.2f\n", currentBalance);

        boolean isSuccess = false;

        try {
            System.out.println("\n--- Step 1: Validating Card Reader Hardware (Checked Exception Check) ---");
            verifyCardReader(cardConfigFile);
            System.out.println("[SUCCESS] Card reader hardware verified.");

            System.out.println("\n--- Step 2: Parsing Inputs (Multi-Catch / Unchecked Check) ---");
            long accNo = Long.parseLong(accStr.trim());
            double amount = Double.parseDouble(amtStr.trim());
            System.out.println("[SUCCESS] Account number " + accNo + " and amount parsed successfully.");

            System.out.println("\n--- Step 3: Validating Business Rules ---");
            if (amount <= 0) {
                throw new InvalidAmountException("Withdrawal amount must be strictly greater than zero. Provided: " + amount);
            }

            if (amount > currentBalance) {
                throw new InsufficientBalanceException(
                        "Transaction Declined: Insufficient balance.", currentBalance, amount);
            }

            // Perform withdrawal
            currentBalance -= amount;
            isSuccess = true;
            System.out.printf("[SUCCESS] Withdrawal completed: INR %.2f dispensed.\n", amount);
            System.out.printf("New Remaining Balance: INR %.2f\n", currentBalance);

        } catch (FileNotFoundException e) {
            // Specific Checked Exception
            System.err.println("[CATCH - FileNotFoundException]: Hardware fault detected.");
            System.err.println("Details: " + e.getMessage());
            System.err.println("Action: Card reader configuration offline. Transaction aborted.");

        } catch (NumberFormatException e) {
            // Specific Unchecked Exception (Multi-catch capability demonstration)
            System.err.println("[CATCH - NumberFormatException]: Invalid numeric formatting.");
            System.err.println("Details: Non-numeric input encountered: " + e.getMessage());
            System.err.println("Action: Please enter valid digits for account number and withdrawal amount.");

        } catch (InvalidAmountException e) {
            // Specific Custom Unchecked Exception
            System.err.println("[CATCH - InvalidAmountException]: Business rule violation.");
            System.err.println("Details: " + e.getMessage());
            System.err.println("Action: Transaction rejected. Positive denomination required.");

        } catch (InsufficientBalanceException e) {
            // Specific Custom Checked Exception
            System.err.println("[CATCH - InsufficientBalanceException]: Balance limit exceeded.");
            System.err.println("Details: " + e.getMessage());
            System.err.printf("Available Balance: INR %.2f | Requested: INR %.2f\n", e.getBalance(), e.getRequested());
            System.err.println("Action: Please enter an amount within your available balance.");

        } catch (Exception e) {
            // Generic fallback
            System.err.println("[CATCH - Generic Exception]: Unexpected failure occurred.");
            System.err.println("Details: " + e.getMessage());

        } finally {
            // Finally block always executes
            System.out.println("\n[FINALLY BLOCK EXECUTED]");
            System.out.println("1. Card hardware session closed and card ejected safely.");
            System.out.println("2. Security audit log updated (Transaction status: " + (isSuccess ? "APPROVED" : "FAILED") + ").");
            System.out.println("3. ATM ready for next user.");
            System.out.println("==================================================");
        }
    }

    private static void verifyCardReader(String configPath) throws FileNotFoundException {
        File file = new File(configPath);
        if (!file.exists()) {
            throw new FileNotFoundException("ATM Card reader configuration file '" + configPath + "' not found.");
        }
    }
}
