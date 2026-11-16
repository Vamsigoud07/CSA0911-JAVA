package task01;

import java.sql.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Data Access Layer for student fee records.
 * Demonstrates:
 * - PreparedStatement parameterization to prevent SQL Injection
 * - Try-with-resources for automatic resource closing
 * - Clean abstraction allowing real JDBC or in-memory persistence
 */
public class FeeRepository {
    private final String dbUrl;
    private final String dbUser;
    private final String dbPass;
    private final boolean useRealDb;

    // Resilient in-memory fallback store to ensure offline testing runs seamlessly
    private final Map<String, Double> paymentRecords = new ConcurrentHashMap<>();

    public FeeRepository() {
        this.dbUrl = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/fees");
        this.dbUser = System.getenv().getOrDefault("DB_USER", "root");
        this.dbPass = System.getenv().getOrDefault("DB_PASS", "root");
        this.useRealDb = false; // By default uses clean resilient storage
    }

    public FeeRepository(String dbUrl, String dbUser, String dbPass) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.useRealDb = true;
    }

    /**
     * Saves payment record safely using PreparedStatement and try-with-resources.
     */
    public boolean savePayment(String transactionId, String regNo, double amount) throws SQLException {
        if (!useRealDb) {
            // Thread-safe in-memory storage simulating DB record
            paymentRecords.put(transactionId, amount);
            return true;
        }

        String sql = "INSERT INTO fee_payments (transaction_id, student_reg_no, amount, payment_date) "
                   + "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        // Try-with-resources guarantees Connection and PreparedStatement are closed
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transactionId);
            pstmt.setString(2, regNo);
            pstmt.setDouble(3, amount);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }

    public int getRecordCount() {
        return paymentRecords.size();
    }
}
