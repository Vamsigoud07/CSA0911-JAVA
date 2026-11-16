package task_b2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Task B2: Adversarial Input Testing - SQL Injection Demonstration
 * Compares Unsafe Statement Concatenation vs Safe Parameterized PreparedStatement.
 * Student: K VENKATA VAMSI GOUD | Reg No: 192472021 | CSA09
 */
public class AdversarialAuthTest {

    private static final String DB_URL = "jdbc:h2:./Part-B_Implementation/Task-B2_Adversarial_Input/output/auth_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver error: " + e.getMessage());
        }
        return DriverManager.getConnection(DB_URL, "sa", "");
    }

    public static void setupDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS users");
            stmt.executeUpdate("CREATE TABLE users (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "pwd VARCHAR(50), " +
                    "role VARCHAR(20))");

            // Seed legitimate administrative user
            stmt.executeUpdate("INSERT INTO users VALUES ('admin', 'SuperSecret#2026', 'ADMINISTRATOR')");
            stmt.executeUpdate("INSERT INTO users VALUES ('student1', 'Pass@123', 'STUDENT')");
            System.out.println("[Database Setup] Created 'users' table and seeded legitimate accounts.");
        } catch (SQLException e) {
            System.err.println("Database setup error: " + e.getMessage());
        }
    }

    /**
     * VULNERABLE: Direct SQL string concatenation allows SQL Injection.
     */
    public static boolean unsafeLogin(String id, String pwd) {
        String query = "SELECT * FROM users WHERE id='" + id + "' AND pwd='" + pwd + "'";
        System.out.println("  [Vulnerable Query Executed]: " + query);
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                System.out.println("  -> AUTHENTICATION SUCCESSFUL! Logged in as: " + rs.getString("id") + " (" + rs.getString("role") + ")");
                return true;
            } else {
                System.out.println("  -> AUTHENTICATION FAILED: Invalid credentials.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("  -> SQL ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * SECURE: PreparedStatement with bind variables prevents code-data mixing.
     */
    public static boolean safeLogin(String id, String pwd) {
        String query = "SELECT * FROM users WHERE id=? AND pwd=?";
        System.out.println("  [Parameterized Query]: " + query);
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            pstmt.setString(2, pwd);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("  -> AUTHENTICATION SUCCESSFUL! Logged in as: " + rs.getString("id") + " (" + rs.getString("role") + ")");
                    return true;
                } else {
                    System.out.println("  -> AUTHENTICATION REJECTED: User input treated strictly as literal data.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("  -> SQL ERROR: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("  Task B2: Adversarial Security Testing - SQL Injection");
        System.out.println("  Student: K VENKATA VAMSI GOUD | Reg No: 192472021 | Course: CSA09");
        System.out.println("======================================================================");

        setupDatabase();

        System.out.println("\n--- TEST CASE 1: Normal Legitimate Login (Valid User & Password) ---");
        System.out.println("[Unsafe Login Method]:");
        unsafeLogin("admin", "SuperSecret#2026");
        System.out.println("[Safe Login Method]:");
        safeLogin("admin", "SuperSecret#2026");

        System.out.println("\n--- TEST CASE 2: Invalid Password Attempt ---");
        System.out.println("[Unsafe Login Method]:");
        unsafeLogin("admin", "WrongPassword");
        System.out.println("[Safe Login Method]:");
        safeLogin("admin", "WrongPassword");

        System.out.println("\n--- TEST CASE 3: ADVERSARIAL ATTACK (SQL Injection Bypass) ---");
        String maliciousId = "admin' --";
        String maliciousPwd = "irrelevant_password";
        System.out.println("Payload: ID = \"" + maliciousId + "\", PWD = \"" + maliciousPwd + "\"");

        System.out.println("\n[Executing Attack on Vulnerable Statement Method]:");
        boolean breach = unsafeLogin(maliciousId, maliciousPwd);
        if (breach) {
            System.out.println("  >>> CRITICAL SECURITY VULNERABILITY CONFIRMED: Attacker bypassed authentication without valid password!");
        }

        System.out.println("\n[Executing Same Attack on Secure PreparedStatement Method]:");
        boolean blocked = !safeLogin(maliciousId, maliciousPwd);
        if (blocked) {
            System.out.println("  >>> ATTACK THWARTED: PreparedStatement prevented SQL Injection by escaping payload as literal text.");
        }

        System.out.println("\n--- TEST CASE 4: Second Injection Variant (' OR '1'='1) ---");
        String payload2Id = "anyone' OR '1'='1";
        String payload2Pwd = "anyone' OR '1'='1";
        System.out.println("Payload: ID = \"" + payload2Id + "\", PWD = \"" + payload2Pwd + "\"");
        System.out.println("[Unsafe Statement]:");
        unsafeLogin(payload2Id, payload2Pwd);
        System.out.println("[Safe PreparedStatement]:");
        safeLogin(payload2Id, payload2Pwd);

        System.out.println("\n======================================================================");
        System.out.println("  Adversarial Security Evaluation Complete.");
        System.out.println("======================================================================");
    }
}
