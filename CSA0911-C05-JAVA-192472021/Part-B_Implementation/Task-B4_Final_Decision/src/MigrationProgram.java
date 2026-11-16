package task_b4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import task_b3.PassLoader;

/**
 * Task B4: Enterprise Migration Program & Database Metadata Inspector
 * Migrates legacy flat-file bus-pass data into a relational database using PreparedStatement batching
 * within an atomic transaction, validating all fields and quarantining corrupt rows into a failure report.
 *
 * Student: K VENKATA VAMSI GOUD | Reg No: 192472021 | Course: CSA09
 */
public class MigrationProgram {

    public static class RejectionEntry {
        public int lineNo;
        public String rawData;
        public String reason;

        public RejectionEntry(int lineNo, String rawData, String reason) {
            this.lineNo = lineNo;
            this.rawData = rawData;
            this.reason = reason;
        }
    }

    public static class ValidRecord {
        public String passId;
        public String regNo;
        public String name;
        public int routeNo;
        public double amount;

        public ValidRecord(String passId, String regNo, String name, int routeNo, double amount) {
            this.passId = passId;
            this.regNo = regNo;
            this.name = name;
            this.routeNo = routeNo;
            this.amount = amount;
        }
    }

    public static void runMigration(String inputFilePath, String failureReportPath) throws Exception {
        System.out.println("\n>>> Starting Migration Pipeline from Flat File to Relational Store...");
        File inputFile = new File(inputFilePath);
        if (!inputFile.exists()) {
            throw new IOException("Source file not found: " + inputFilePath);
        }

        List<ValidRecord> validRecords = new ArrayList<>();
        List<RejectionEntry> rejectedRecords = new ArrayList<>();

        int totalLines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                totalLines++;
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    rejectedRecords.add(new RejectionEntry(totalLines, line, "BLANK_ROW"));
                    continue;
                }

                String[] tokens = trimmed.split(",", -1);
                if (tokens.length != 5) {
                    rejectedRecords.add(new RejectionEntry(totalLines, line, "INVALID_FIELD_COUNT (Found " + tokens.length + ", Expected 5)"));
                    continue;
                }

                String passId = tokens[0].trim();
                String regNo = tokens[1].trim();
                String name = tokens[2].trim();
                String routeStr = tokens[3].trim();
                String amtStr = tokens[4].trim();

                if (passId.isEmpty() || regNo.isEmpty() || name.isEmpty() || routeStr.isEmpty() || amtStr.isEmpty()) {
                    rejectedRecords.add(new RejectionEntry(totalLines, line, "NULL_OR_EMPTY_MANDATORY_VALUE"));
                    continue;
                }

                int routeNo;
                try {
                    routeNo = Integer.parseInt(routeStr);
                    if (routeNo <= 0 || routeNo > 100) {
                        rejectedRecords.add(new RejectionEntry(totalLines, line, "ROUTE_OUT_OF_RANGE (" + routeNo + ")"));
                        continue;
                    }
                } catch (NumberFormatException e) {
                    rejectedRecords.add(new RejectionEntry(totalLines, line, "NON_NUMERIC_ROUTE (" + routeStr + ")"));
                    continue;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amtStr);
                    if (amount < 0) {
                        rejectedRecords.add(new RejectionEntry(totalLines, line, "NEGATIVE_AMOUNT (" + amtStr + ")"));
                        continue;
                    }
                } catch (NumberFormatException e) {
                    rejectedRecords.add(new RejectionEntry(totalLines, line, "NON_NUMERIC_AMOUNT (" + amtStr + ")"));
                    continue;
                }

                validRecords.add(new ValidRecord(passId, regNo, name, routeNo, amount));
            }
        }

        // Save Failure Report
        File repFile = new File(failureReportPath);
        repFile.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(repFile))) {
            bw.write("LineNumber,RawRecord,RejectionReason\n");
            for (RejectionEntry r : rejectedRecords) {
                bw.write(String.format("%d,\"%s\",\"%s\"\n", r.lineNo, r.rawData.replace("\"", "\"\""), r.reason));
            }
        }

        // Load Valid Records into Database with Transaction and Batching
        int insertedCount = 0;
        try (Connection conn = PassLoader.getConnection()) {
            PassLoader.initializeSchema(conn);
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false); // Atomic Transaction

            String sql = "INSERT INTO bus_pass (pass_id, reg_no, name, route_no, amount) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                int batchCount = 0;
                for (ValidRecord rec : validRecords) {
                    pstmt.setString(1, rec.passId);
                    pstmt.setString(2, rec.regNo);
                    pstmt.setString(3, rec.name);
                    pstmt.setInt(4, rec.routeNo);
                    pstmt.setDouble(5, rec.amount);
                    pstmt.addBatch();
                    batchCount++;

                    if (batchCount % 1000 == 0) {
                        int[] results = pstmt.executeBatch();
                        insertedCount += results.length;
                        pstmt.clearBatch();
                    }
                }
                if (batchCount % 1000 != 0) {
                    int[] remaining = pstmt.executeBatch();
                    insertedCount += remaining.length;
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        }

        System.out.println("------------------------------------------------------------------");
        System.out.printf("Total Lines Processed : %d\n", totalLines);
        System.out.printf("Valid Records Migrated: %d\n", insertedCount);
        System.out.printf("Rejected Records      : %d (Logged to %s)\n", rejectedRecords.size(), failureReportPath);
        System.out.println("Migration Status      : SUCCESS (Transaction Committed Atomically)");
        System.out.println("------------------------------------------------------------------");
    }

    public static void printDatabaseMetadata() {
        System.out.println("\n==========================================================================");
        System.out.println("  Task B4.2: Inspecting DatabaseMetaData for Portability & Migration");
        System.out.println("==========================================================================");

        try (Connection conn = PassLoader.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("Database Product Name    : " + meta.getDatabaseProductName());
            System.out.println("Database Product Version : " + meta.getDatabaseProductVersion());
            System.out.println("JDBC Driver Name         : " + meta.getDriverName());
            System.out.println("JDBC Driver Version      : " + meta.getDriverVersion());
            System.out.println("JDBC Major/Minor Version : " + meta.getJDBCMajorVersion() + "." + meta.getJDBCMinorVersion());
            System.out.println("Database URL             : " + meta.getURL());
            System.out.println("User Name                : " + meta.getUserName());
            System.out.println("Supports Batch Updates   : " + meta.supportsBatchUpdates());
            System.out.println("Supports Transactions    : " + meta.supportsTransactions());

            System.out.println("\nAvailable Tables in Catalog/Schema:");
            try (ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"})) {
                int tblCount = 0;
                while (rs.next()) {
                    tblCount++;
                    System.out.println("  [" + tblCount + "] " + rs.getString("TABLE_NAME") + " (Type: " + rs.getString("TABLE_TYPE") + ")");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading metadata: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("==========================================================================");
        System.out.println("  Programming in Java - Assignment 5 (CO5)");
        System.out.println("  Student: K VENKATA VAMSI GOUD | Reg No: 192472021 | Course: CSA09");
        System.out.println("  Task B4: Final Engineering Decision & Migration Program");
        System.out.println("==========================================================================");

        // 1. Generate test migration input file with mixed valid and corrupt data
        String inputPath = "Part-B_Implementation/Task-B4_Final_Decision/input/legacy_passes_mixed.txt";
        String failureReportPath = "Part-B_Implementation/Task-B4_Final_Decision/output/migration_rejections.csv";

        File inputF = new File(inputPath);
        inputF.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(inputF))) {
            // Write 50 valid records
            for (int i = 1; i <= 50; i++) {
                bw.write(String.format("P%04d,192472%03d,Student_%d,%d,%.2f\n",
                    i, i, i, (i % 20) + 1, 3500.0 + (i * 10)));
            }
            // Inject damaged records
            bw.write("P9001,DAMAGED_CORRUPTED_LINE_MISSING_FIELDS\n");
            bw.write("P9002,192472902,InvalidRoute,NOT_A_NUMBER,4000.00\n");
            bw.write("\n"); // Blank line
            bw.write("P9003,192472903,NegativeFee,7,-1200.00\n");
            bw.write("P9004,,EmptyRegNo,8,3900.00\n");
            // Write 20 more valid records
            for (int i = 51; i <= 70; i++) {
                bw.write(String.format("P%04d,192472%03d,Student_%d,%d,%.2f\n",
                    i, i, i, (i % 20) + 1, 3500.0 + (i * 10)));
            }
        }

        // 2. Execute migration
        runMigration(inputPath, failureReportPath);

        // 3. Inspect DatabaseMetaData
        printDatabaseMetadata();

        System.out.println("\n==========================================================================");
        System.out.println("  Task B4 Migration & Metadata Verification Completed.");
        System.out.println("==========================================================================");
    }
}
