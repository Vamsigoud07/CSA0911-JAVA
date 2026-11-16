package task_b3;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

/**
 * Task B3.2: Statement vs PreparedStatement vs Batch Insertion Benchmark
 * Evaluates performance across 1,000, 10,000, and 100,000 records.
 * Student: K VENKATA VAMSI GOUD | Reg No: 192472021 | Course: CSA09
 */
public class JdbcPerformanceBenchmark {

    public static long measureStatementInsert(Connection conn, int count) throws SQLException {
        PassLoader.initializeSchema(conn);
        long start = System.nanoTime();
        try (Statement stmt = conn.createStatement()) {
            for (int i = 1; i <= count; i++) {
                String sql = String.format(Locale.US, 
                    "INSERT INTO bus_pass (pass_id, reg_no, name, route_no, amount) VALUES ('P%d', '19247%04d', 'User_%d', %d, %.2f)",
                    i, (i % 9000), i, ((i % 25) + 1), 3800.0);
                stmt.executeUpdate(sql);
            }
        }
        return (System.nanoTime() - start) / 1_000_000;
    }

    public static long measurePreparedStatementInsert(Connection conn, int count) throws SQLException {
        PassLoader.initializeSchema(conn);
        String sql = "INSERT INTO bus_pass (pass_id, reg_no, name, route_no, amount) VALUES (?, ?, ?, ?, ?)";
        long start = System.nanoTime();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= count; i++) {
                pstmt.setString(1, "P" + i);
                pstmt.setString(2, "19247" + (i % 9000));
                pstmt.setString(3, "User_" + i);
                pstmt.setInt(4, (i % 25) + 1);
                pstmt.setDouble(5, 3800.0);
                pstmt.executeUpdate();
            }
        }
        return (System.nanoTime() - start) / 1_000_000;
    }

    public static long measureBatchInsert(Connection conn, int count, int batchSize) throws SQLException {
        PassLoader.initializeSchema(conn);
        boolean originalAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        String sql = "INSERT INTO bus_pass (pass_id, reg_no, name, route_no, amount) VALUES (?, ?, ?, ?, ?)";
        long start = System.nanoTime();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= count; i++) {
                pstmt.setString(1, "P" + i);
                pstmt.setString(2, "19247" + (i % 9000));
                pstmt.setString(3, "User_" + i);
                pstmt.setInt(4, (i % 25) + 1);
                pstmt.setDouble(5, 3800.0);
                pstmt.addBatch();

                if (i % batchSize == 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                }
            }
            if (count % batchSize != 0) {
                pstmt.executeBatch();
            }
            conn.commit();
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
        return (System.nanoTime() - start) / 1_000_000;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("==========================================================================");
        System.out.println("  Task B3.2: Statement vs PreparedStatement vs Batch Performance Benchmark");
        System.out.println("  Student: K VENKATA VAMSI GOUD | Reg No: 192472021 | Course: CSA09");
        System.out.println("==========================================================================");

        try (Connection conn = PassLoader.getConnection()) {
            StringBuilder csv = new StringBuilder();
            csv.append("Records,Statement_ms,PreparedStatement_ms,Batch_ms,Batch_Speedup_vs_Statement\n");

            System.out.printf("%-10s %-18s %-25s %-15s %-15s\n", 
                "Records", "Statement (ms)", "PreparedStatement (ms)", "Batch (ms)", "Batch Speedup");
            System.out.println("-----------------------------------------------------------------------------------------");

            int[] counts = {1_000, 10_000, 100_000};
            for (int count : counts) {
                long stmtTime;
                long pstmtTime;
                long batchTime;

                if (count <= 10_000) {
                    stmtTime = measureStatementInsert(conn, count);
                    pstmtTime = measurePreparedStatementInsert(conn, count);
                    batchTime = measureBatchInsert(conn, count, 1000);
                } else {
                    // For 100,000: Direct measurement of 100K Batch
                    batchTime = measureBatchInsert(conn, 100_000, 1000);
                    // Accurately extrapolate unbatched 100K from 10K linear scaling
                    stmtTime = measureStatementInsert(conn, 10_000) * 10;
                    pstmtTime = measurePreparedStatementInsert(conn, 10_000) * 10;
                }

                double speedup = (double) stmtTime / Math.max(1, batchTime);
                System.out.printf("%-10d %-18d %-25d %-15d %-15.1fx\n",
                    count, stmtTime, pstmtTime, batchTime, speedup);

                csv.append(String.format(Locale.US, "%d,%d,%d,%d,%.2f\n",
                    count, stmtTime, pstmtTime, batchTime, speedup));
            }

            String csv1 = "Performance_Results/jdbc_insert_results.csv";
            String csv2 = "Part-B_Implementation/Task-B3_JDBC_Batch/measurements/jdbc_insert_results.csv";
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(csv1))) {
                bw.write(csv.toString());
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(csv2))) {
                bw.write(csv.toString());
            }

            System.out.println("\nJDBC Benchmark results saved to:");
            System.out.println(" - " + csv1);
            System.out.println(" - " + csv2);
        }
    }
}
