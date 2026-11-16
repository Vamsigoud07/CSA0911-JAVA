package task_b2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Task B2: Adversarial Input File Validation
 * Demonstrates resilient file ingestion across malformed records:
 * - Wrong number of fields
 * - Non-numeric amount
 * - Empty line
 * - Invalid route number
 * - Missing values
 * Student: K VENKATA VAMSI GOUD | Reg No: 192472021 | Course: CSA09
 */
public class AdversarialFileValidator {

    public static class ValidationReport {
        public int lineNumber;
        public String rawLine;
        public boolean isValid;
        public String statusMessage;

        public ValidationReport(int lineNumber, String rawLine, boolean isValid, String statusMessage) {
            this.lineNumber = lineNumber;
            this.rawLine = rawLine;
            this.isValid = isValid;
            this.statusMessage = statusMessage;
        }
    }

    public static List<ValidationReport> validateAndIngest(String filePath) {
        List<ValidationReport> reports = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            reports.add(new ValidationReport(0, "", false, "FILE_NOT_FOUND: Specified path does not exist."));
            return reports;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                String trimmed = line.trim();

                // 1. Empty Line Check
                if (trimmed.isEmpty()) {
                    reports.add(new ValidationReport(lineNum, line, false, "REJECTED: Empty / blank line detected."));
                    continue;
                }

                // 2. Field Count Check (Standard CSV record requires exactly 5 fields)
                String[] tokens = trimmed.split(",", -1);
                if (tokens.length != 5) {
                    reports.add(new ValidationReport(lineNum, line, false, 
                        "REJECTED: Incorrect field count (Expected 5, Found " + tokens.length + ")."));
                    continue;
                }

                String passId = tokens[0].trim();
                String regNo = tokens[1].trim();
                String name = tokens[2].trim();
                String routeStr = tokens[3].trim();
                String amountStr = tokens[4].trim();

                // 3. Missing Value Check
                if (passId.isEmpty() || regNo.isEmpty() || name.isEmpty() || routeStr.isEmpty() || amountStr.isEmpty()) {
                    reports.add(new ValidationReport(lineNum, line, false, 
                        "REJECTED: Record contains missing/empty mandatory values."));
                    continue;
                }

                // 4. Route Number Validation (Must be positive integer between 1 and 100)
                int routeNo;
                try {
                    routeNo = Integer.parseInt(routeStr);
                    if (routeNo <= 0 || routeNo > 100) {
                        reports.add(new ValidationReport(lineNum, line, false, 
                            "REJECTED: Route number out of allowable range (1-100): " + routeNo));
                        continue;
                    }
                } catch (NumberFormatException e) {
                    reports.add(new ValidationReport(lineNum, line, false, 
                        "REJECTED: Route number is non-numeric: \"" + routeStr + "\""));
                    continue;
                }

                // 5. Amount Validation (Must be positive numeric decimal)
                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                    if (amount < 0.0) {
                        reports.add(new ValidationReport(lineNum, line, false, 
                            "REJECTED: Amount cannot be negative: " + amount));
                        continue;
                    }
                } catch (NumberFormatException e) {
                    reports.add(new ValidationReport(lineNum, line, false, 
                        "REJECTED: Amount contains non-numeric characters: \"" + amountStr + "\""));
                    continue;
                }

                // Valid record accepted
                reports.add(new ValidationReport(lineNum, line, true, 
                    String.format("ACCEPTED: Valid Bus Pass Record [ID=%s, Reg=%s, Route=%d, Amt=Rs.%.2f]",
                        passId, regNo, routeNo, amount)));
            }
        } catch (IOException e) {
            reports.add(new ValidationReport(0, "", false, "I/O Error: " + e.getMessage()));
        }

        return reports;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("==========================================================================");
        System.out.println("  Task B2: Adversarial File Ingestion & Damaged Data Resilience");
        System.out.println("  Student: K VENKATA VAMSI GOUD | Reg No: 192472021 | Course: CSA09");
        System.out.println("==========================================================================");

        String adversarialFilePath = "Part-B_Implementation/Task-B2_Adversarial_Input/input/adversarial_passes.txt";
        File advFile = new File(adversarialFilePath);
        advFile.getParentFile().mkdirs();

        // Construct adversarial test cases
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(advFile))) {
            bw.write("P101,192472001,Karthik,5,3500.00\n");               // TC1: Valid record
            bw.write("P102,192472002,DAMAGED_RECORD_TRUNCATED\n");         // TC2: Wrong number of fields (3 instead of 5)
            bw.write("P103,192472003,Deepa,12,FOUR_THOUSAND\n");           // TC3: Non-numeric amount
            bw.write("   \n");                                             // TC4: Empty line
            bw.write("P104,192472004,Suresh,999,4200.00\n");              // TC5: Invalid route number (out of range)
            bw.write("P105,,Priya,8,3800.00\n");                           // TC6: Missing reg_no
            bw.write("P106,192472006,Vamsi Goud,15,4500.00\n");            // TC7: Valid record with space in name
            bw.write("P107,192472007,Manoj,-3,3000.00\n");                 // TC8: Negative route number
            bw.write("P108,192472008,Sneha,10,-500.00\n");                 // TC9: Negative amount
            bw.write("P109,192472009,Rahul,11,4000.00,ExtraField\n");      // TC10: Too many fields (6 instead of 5)
        }

        List<ValidationReport> results = validateAndIngest(adversarialFilePath);

        System.out.printf("\n%-6s %-45s %-10s %-40s\n", "Line", "Raw Input", "Verdict", "Reason / Diagnostic Message");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");

        int validCount = 0;
        int rejectedCount = 0;

        for (ValidationReport r : results) {
            String shortRaw = r.rawLine.length() > 42 ? r.rawLine.substring(0, 39) + "..." : r.rawLine;
            String verdict = r.isValid ? "PASS" : "REJECT";
            if (r.isValid) validCount++; else rejectedCount++;
            System.out.printf("%-6d %-45s %-10s %-40s\n", r.lineNumber, shortRaw, verdict, r.statusMessage);
        }

        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("Summary: Total Records Ingested = %d | Accepted Valid = %d | Safely Rejected = %d\n",
            results.size(), validCount, rejectedCount);
        System.out.println("Result: Zero uncaught exceptions. Malformed rows were safely quarantined without crashing.");
        System.out.println("==========================================================================");
    }
}
