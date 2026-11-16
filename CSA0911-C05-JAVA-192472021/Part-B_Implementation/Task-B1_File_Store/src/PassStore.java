package task_b1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Programming in Java - Assignment 5 (CO5)
 * Student Name: K VENKATA VAMSI GOUD
 * Register Number: 192472021
 * Course Code: CSA09
 * College: Saveetha School of Engineering, SIMATS
 *
 * Task B1: Bus-Pass Text-File Store Implementation
 */
public class PassStore {

    /**
     * Finds a pass record by Pass ID.
     * Completes TODO 1:
     * - Uses BufferedReader with try-with-resources.
     * - Reads one line at a time.
     * - Splits line using comma delimiter.
     * - Skips damaged lines that do not contain exactly five fields.
     * - Returns the matching record as List<Object> [passId, regNo, name, routeNo, amount].
     * - Returns null if not found.
     */
    public static List<Object> findPass(String filePath, String targetPassId) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Error: File not found -> " + filePath);
            return null;
        }

        // TODO 1 Implementation
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // Skip empty lines
                }

                String[] parts = line.split(",");
                if (parts.length != 5) {
                    // Damaged line: skip without crashing
                    continue;
                }

                String passId = parts[0].trim();
                if (passId.equalsIgnoreCase(targetPassId)) {
                    String regNo = parts[1].trim();
                    String name = parts[2].trim();
                    int routeNo;
                    double amount;
                    try {
                        routeNo = Integer.parseInt(parts[3].trim());
                        amount = Double.parseDouble(parts[4].trim());
                    } catch (NumberFormatException e) {
                        // Data corruption in numeric fields
                        continue;
                    }
                    return Arrays.asList(passId, regNo, name, routeNo, amount);
                }
            }
        } catch (IOException e) {
            System.err.println("I/O Error while reading pass store: " + e.getMessage());
        }

        return null; // Not found
    }

    /**
     * Appends a new pass record to the file.
     */
    public static boolean addPass(String filePath, String passId, String regNo, String name, int routeNo, double amount) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(String.format("%s,%s,%s,%d,%.2f", passId, regNo, name, routeNo, amount));
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Error appending pass: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lists all passes on a particular route.
     */
    public static List<List<Object>> listPassesByRoute(String filePath, int targetRoute) {
        List<List<Object>> matchingPasses = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return matchingPasses;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length != 5) continue;

                try {
                    int routeNo = Integer.parseInt(parts[3].trim());
                    if (routeNo == targetRoute) {
                        matchingPasses.add(Arrays.asList(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            routeNo,
                            Double.parseDouble(parts[4].trim())
                        ));
                    }
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
        }
        return matchingPasses;
    }

    /**
     * Fast line counter using BufferedReader.
     */
    public static long countLinesBuffered(String filePath) {
        long count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            System.err.println("Error counting lines: " + e.getMessage());
        }
        return count;
    }

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  Programming in Java - Assignment 5 (CO5)");
        System.out.println("  Student: K VENKATA VAMSI GOUD | Reg No: 192472021");
        System.out.println("  Task B1: Bus-Pass Text Store & TODO 1 Verification");
        System.out.println("============================================================");

        String sampleFilePath = "Part-B_Implementation/Task-B1_File_Store/input/sample_passes.txt";
        
        // Ensure input directory and sample test data exists
        File sampleFile = new File(sampleFilePath);
        sampleFile.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sampleFile))) {
            writer.write("P1001,192311001,Arun,12,4500\n");
            writer.write("P1002,DAMAGED LINE\n");
            writer.write("P1003,192311003,Divya,7,3800\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Test with supplied test data
        System.out.println("\n--- Testing findPass for 'P1003' ---");
        List<Object> pass1003 = findPass(sampleFilePath, "P1003");
        System.out.println(pass1003);

        System.out.println("\n--- Testing findPass for non-existent 'P9999' ---");
        List<Object> pass9999 = findPass(sampleFilePath, "P9999");
        System.out.println(pass9999);

        System.out.println("\n--- Route Query: Listing all passes for Route 7 ---");
        List<List<Object>> route7Passes = listPassesByRoute(sampleFilePath, 7);
        for (List<Object> p : route7Passes) {
            System.out.println("Found: " + p);
        }

        System.out.println("\n============================================================");
        System.out.println("  TODO 1 Verification Completed Successfully!");
        System.out.println("============================================================");
    }
}
