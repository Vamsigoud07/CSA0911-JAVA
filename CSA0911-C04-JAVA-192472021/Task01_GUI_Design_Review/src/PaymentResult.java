package task01;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Encapsulates the immutable result of a fee payment transaction.
 */
public class PaymentResult implements Serializable {
    private final boolean success;
    private final String transactionId;
    private final String registrationNumber;
    private final double amount;
    private final String message;
    private final LocalDateTime timestamp;

    public PaymentResult(boolean success, String transactionId, String registrationNumber,
                         double amount, String message) {
        this.success = success;
        this.transactionId = transactionId;
        this.registrationNumber = registrationNumber;
        this.amount = amount;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static PaymentResult failure(String message) {
        return new PaymentResult(false, null, null, 0.0, message);
    }

    public static PaymentResult success(String txId, String regNo, double amt, String msg) {
        return new PaymentResult(true, txId, regNo, amt, msg);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public double getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String generateReceipt() {
        if (!success) {
            return "No successful transaction available for receipt generation.";
        }
        return "==========================================\n"
             + "        SIMATS UNIVERSITY - FEE RECEIPT\n"
             + "==========================================\n"
             + " Transaction ID : " + transactionId + "\n"
             + " Register No    : " + registrationNumber + "\n"
             + " Amount Paid    : INR " + String.format("%.2f", amount) + "\n"
             + " Date & Time    : " + getFormattedTimestamp() + "\n"
             + " Status         : SUCCESS / CONFIRMED\n"
             + "==========================================\n"
             + " Thank you for your payment!\n"
             + "==========================================\n";
    }
}
