package task_b3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Task B3: PassLoader Implementation with Batch Ingestion & Transaction Integrity
 * Completes TODO 1, TODO 2, and TODO 3.
 * Student: K VENKATA VAMSI GOUD | Reg No: 192472021 | Course: CSA09
 */
public class PassLoader {

    public static class BusPassRecord {
        public String passId;
        public String regNo;
        public String name;
        public String route;
        public String amount;

        public BusPassRecord(String passId, String regNo, String name, String route, String amount) {
            this.passId = passId;
            this.regNo = regNo;
            this.name = name;
            this.route = route;
            this.amount = amount;
        }
    }

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        File propFile = new File("db.properties");
        if (propFile.exists()) {
            try (FileInputStream fis = new FileInputStream(propFile)) {
                props.load(fis);
            } catch (IOException ignored) {}
        }

        String driver = props.getProperty("db.driver", "org.h2.Driver");
        String url = props.getProperty("db.url", "jdbc:h2:file:./Part-B_Implementation/Task-B3_JDBC_Batch/output/transport_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;FILE_LOCK=NO;DB_CLOSE_DELAY=-1");
        String user = props.getProperty("db.user", "sa");
        String pwd = props.getProperty("db.password", "");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found: " + e.getMessage());
        }

        return DriverManager.getConnection(url, user, pwd);
    }

    public static void initializeSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS bus_pass");
            stmt.executeUpdate("CREATE TABLE bus_pass (" +
                "pass_id VARCHAR(30) PRIMARY KEY, " +
                "reg_no VARCHAR(30) NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "route_no INT NOT NULL, " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "boarding_stop VARCHAR(100) DEFAULT 'Main Campus Gate', " +
                "validity_date DATE DEFAULT '2026-12-31')");
        }
    }

    /**
     * Loads a list of BusPassRecords using batching and explicit transaction control.
     * Completes TODO 1, TODO 2, and TODO 3.
     */
    public static int loadAll(Connection conn, List<BusPassRecord> records) throws SQLException {
        if (records == null || records.isEmpty()) {
            return 0;
        }

        boolean originalAutoCommit = conn.getAutoCommit();
        int totalInserted = 0;
        String insertSql = "INSERT INTO bus_pass (pass_id, reg_no, name, route_no, amount) VALUES (?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false); // Begin Transaction

            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                int batchCount = 0;

                // ==================== TODO 1 ====================
                for (BusPassRecord rec : records) {
                    // Set all five parameters
                    pstmt.setString(1, rec.passId);
                    pstmt.setString(2, rec.regNo);
                    pstmt.setString(3, rec.name);

                    // Convert route to int and amount to double
                    int routeNo;
                    double amount;
                    try {
                        routeNo = Integer.parseInt(rec.route.trim());
                        amount = Double.parseDouble(rec.amount.trim());
                    } catch (NumberFormatException nfe) {
                        throw new IllegalArgumentException("Data parse error in record " + rec.passId + ": " + nfe.getMessage(), nfe);
                    }

                    pstmt.setInt(4, routeNo);
                    pstmt.setDouble(5, amount);

                    // Call addBatch()
                    pstmt.addBatch();
                    batchCount++;

                    // Execute the batch after every 1,000 rows
                    if (batchCount % 1000 == 0) {
                        int[] counts = pstmt.executeBatch();
                        totalInserted += counts.length;
                        pstmt.clearBatch(); // Clear batch after successful execution
                    }
                }

                // ==================== TODO 2 ====================
                // Execute any remaining batch
                if (batchCount % 1000 != 0) {
                    int[] remainingCounts = pstmt.executeBatch();
                    totalInserted += remainingCounts.length;
                    pstmt.clearBatch();
                }

                // Commit the transaction
                conn.commit();
                return totalInserted;
            }

        } catch (SQLException | RuntimeException e) {
            // ==================== TODO 3 ====================
            // Roll back on SQLException or RuntimeException
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                // Preserve original exception and suppress rollback exception
                e.addSuppressed(rollbackEx);
            }
            // Re-throw original exception
            throw e;
        } finally {
            // Restore original auto-commit state
            try {
                conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException restoreEx) {
                System.err.println("Failed to restore original auto-commit state: " + restoreEx.getMessage());
            }
        }
    }

    public static void displayMetadata(Connection conn) throws SQLException {
        String query = "SELECT * FROM bus_pass LIMIT 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            System.out.println("\n--- ResultSetMetaData Inspection ---");
            System.out.println("Total Column Count: " + colCount);
            System.out.printf("%-6s %-18s %-16s %-12s\n", "Index", "Column Name", "Data Type", "Column Size");
            System.out.println("---------------------------------------------------------");
            for (int i = 1; i <= colCount; i++) {
                System.out.printf("%-6d %-18s %-16s %-12d\n",
                    i, meta.getColumnName(i), meta.getColumnTypeName(i), meta.getColumnDisplaySize(i));
            }
        }
    }

    public static long getRowCount(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM bus_pass")) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("==========================================================================");
        System.out.println("  Task B3: JDBC Batch Processing & Transaction Integrity (PassLoader)");
        System.out.println("  Student: K VENKATA VAMSI GOUD | Reg No: 192472021 | Course: CSA09");
        System.out.println("==========================================================================");

        try (Connection conn = getConnection()) {
            initializeSchema(conn);
            System.out.println("[Database Schema] 'bus_pass' table initialized successfully.");

            // B3.1: Small dataset (10 records)
            System.out.println("\n--- B3.1: Inserting 10 Sample Records using Batch ---");
            List<BusPassRecord> sample10 = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                sample10.add(new BusPassRecord("P" + (1000 + i), "19247" + (2000 + i), "Student_" + i, "" + ((i % 10) + 1), "4200.00"));
            }
            int loaded = loadAll(conn, sample10);
            System.out.println("Successfully inserted records: " + loaded + " / Current Table Count: " + getRowCount(conn));

            displayMetadata(conn);

            // B3.3: Transaction Rollback Test
            System.out.println("\n--- B3.3: Transaction Rollback Verification (Forced Corrupted Row) ---");
            initializeSchema(conn);
            List<BusPassRecord> corruptBatch = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                if (i == 11) {
                    // Poison pill at row 11: Non-numeric amount
                    corruptBatch.add(new BusPassRecord("P9999", "192479999", "CorruptStudent", "5", "INVALID_AMOUNT"));
                } else {
                    corruptBatch.add(new BusPassRecord("P" + (2000 + i), "19247" + (2000 + i), "BatchStudent_" + i, "4", "3900.00"));
                }
            }

            System.out.println("Table count BEFORE transaction: " + getRowCount(conn));
            System.out.println("Submitting batch with intentional corruption halfway at record #11...");
            try {
                loadAll(conn, corruptBatch);
            } catch (Exception ex) {
                System.out.println("-> Transaction failed as expected: " + ex.getMessage());
            }

            long countAfterRollback = getRowCount(conn);
            System.out.println("Table count AFTER rollback: " + countAfterRollback);
            if (countAfterRollback == 0) {
                System.out.println(">>> ACID ATOMICITY VERIFIED: Complete batch was rolled back. Zero dirty rows persisted!");
            }

            // Benchmarking 1,000, 10,000, and 100,000 rows
            System.out.println("\n--- Performance Verification: Batch Loading Scalability ---");
            int[] testSizes = {1_000, 10_000, 100_000};
            for (int size : testSizes) {
                initializeSchema(conn);
                List<BusPassRecord> benchBatch = new ArrayList<>(size);
                for (int i = 1; i <= size; i++) {
                    benchBatch.add(new BusPassRecord("P" + i, "19247" + (10000 + (i % 90000)), "Student_" + i, "" + ((i % 25) + 1), "4500.00"));
                }
                long tStart = System.nanoTime();
                int inserted = loadAll(conn, benchBatch);
                long tElapsed = (System.nanoTime() - tStart) / 1_000_000;
                System.out.printf("Loaded %d records in %d ms (Throughput: %.1f rows/sec)\n", 
                    inserted, tElapsed, (inserted * 1000.0 / Math.max(1, tElapsed)));
            }
        }
        System.out.println("\n==========================================================================");
        System.out.println("  PassLoader TODO 1, 2, 3 Implementation Verified.");
        System.out.println("==========================================================================");
    }
}
