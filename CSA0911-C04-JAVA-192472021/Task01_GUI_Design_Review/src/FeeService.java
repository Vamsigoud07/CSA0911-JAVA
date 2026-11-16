package task01;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Service Layer for Fee Payment.
 * Encapsulates input validation, business rules, and transaction coordination.
 * Separated cleanly from GUI to allow unit testing and reuse.
 */
public class FeeService {
    private final FeeRepository repository;
    private static final Pattern REG_PATTERN = Pattern.compile("^[A-Za-z0-9]{6,15}$");
    private static final double MIN_FEE = 100.0;
    private static final double MAX_FEE = 500000.0;

    public FeeService(FeeRepository repository) {
        this.repository = repository;
    }

    public FeeService() {
        this(new FeeRepository());
    }

    /**
     * Validates input fields and executes payment transaction.
     */
    public PaymentResult processPayment(String regNo, String amountStr) {
        // 1. Validation: Registration Number
        if (regNo == null || regNo.trim().isEmpty()) {
            return PaymentResult.failure("Validation Error: Registration Number cannot be empty.");
        }
        regNo = regNo.trim().toUpperCase();
        if (!REG_PATTERN.matcher(regNo).matches()) {
            return PaymentResult.failure("Validation Error: Invalid Registration Number format (alphanumeric, 6-15 chars).");
        }

        // 2. Validation: Amount String
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return PaymentResult.failure("Validation Error: Amount cannot be empty.");
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr.trim());
        } catch (NumberFormatException nfe) {
            return PaymentResult.failure("Validation Error: Amount must be a valid numeric value.");
        }

        // 3. Validation: Amount Bounds & Finite Checks
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            return PaymentResult.failure("Validation Error: Amount cannot be NaN or Infinite.");
        }
        if (amount < MIN_FEE) {
            return PaymentResult.failure(String.format("Validation Error: Minimum payable fee is INR %.2f.", MIN_FEE));
        }
        if (amount > MAX_FEE) {
            return PaymentResult.failure(String.format("Validation Error: Maximum single fee payment is INR %.2f.", MAX_FEE));
        }

        // 4. Execute Payment & Save via Repository
        String txId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        try {
            boolean saved = repository.savePayment(txId, regNo, amount);
            if (saved) {
                return PaymentResult.success(txId, regNo, amount, "Payment processed and confirmed successfully.");
            } else {
                return PaymentResult.failure("Transaction Failed: Record could not be saved to repository.");
            }
        } catch (Exception ex) {
            return PaymentResult.failure("Database Error: " + ex.getMessage());
        }
    }
}
