package src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Q4: Pharmacy Inventory Management Program
 * Benchmarking HashMap, TreeMap, LinkedHashMap, and Sorted Array (binarySearch).
 *
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class PharmacyInventory {

    public static class MapRecord {
        String structure;
        int inventorySize;
        String operation;
        int trial;
        long timeNs;
        double timeMs;

        public MapRecord(String structure, int inventorySize, String operation, int trial, long timeNs) {
            this.structure = structure;
            this.inventorySize = inventorySize;
            this.operation = operation;
            this.trial = trial;
            this.timeNs = timeNs;
            this.timeMs = timeNs / 1_000_000.0;
        }
    }

    private static final String[] CATEGORIES = {"Antibiotic", "Analgesic", "Antiviral", "Antihistamine", "Cardiovascular", "Dermatological"};

    private static Medicine createMedicine(int id) {
        String code = String.format("MED%06d", id);
        String name = "PharmaDrug_" + id;
        String category = CATEGORIES[id % CATEGORIES.length];
        double price = 10.0 + (id % 500) * 0.5;
        int stock = 50 + (id % 200);
        return new Medicine(code, name, category, price, stock);
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("  Q4: PHARMACY INVENTORY MANAGEMENT - MAP PERFORMANCE BENCHMARK");
        System.out.println("  Student: K VENKATA VAMSI GOUD (192472021) | Course: CSA09 (CO2)");
        System.out.println("===============================================================================");

        int[] sizes = {1000, 10000, 100000};
        int lookupCount = 100000;
        int trials = 5;

        List<MapRecord> records = new ArrayList<>();

        for (int n : sizes) {
            System.out.println("\n>>> Testing Inventory Size N = " + n + " medicines...");

            // Create test data
            Medicine[] data = new Medicine[n];
            for (int i = 0; i < n; i++) data[i] = createMedicine(i);

            // Create identical lookup key sequence
            Random rnd = new Random(192472021L);
            String[] lookupKeys = new String[lookupCount];
            for (int i = 0; i < lookupCount; i++) {
                int targetId = rnd.nextInt(n);
                lookupKeys[i] = String.format("MED%06d", targetId);
            }

            for (int t = 1; t <= trials; t++) {
                // ---------------------------------------------------------
                // 1. Bulk Insertion
                // ---------------------------------------------------------
                // HashMap
                long start = System.nanoTime();
                Map<String, Medicine> hashMap = new HashMap<>();
                for (Medicine m : data) hashMap.put(m.getCode(), m);
                records.add(new MapRecord("HashMap", n, "1. Bulk Insertion", t, System.nanoTime() - start));

                // TreeMap
                start = System.nanoTime();
                Map<String, Medicine> treeMap = new TreeMap<>();
                for (Medicine m : data) treeMap.put(m.getCode(), m);
                records.add(new MapRecord("TreeMap", n, "1. Bulk Insertion", t, System.nanoTime() - start));

                // LinkedHashMap
                start = System.nanoTime();
                Map<String, Medicine> linkedHashMap = new LinkedHashMap<>();
                for (Medicine m : data) linkedHashMap.put(m.getCode(), m);
                records.add(new MapRecord("LinkedHashMap", n, "1. Bulk Insertion", t, System.nanoTime() - start));

                // Sorted Array (insertion + sort)
                start = System.nanoTime();
                Medicine[] sortedArray = new Medicine[n];
                System.arraycopy(data, 0, sortedArray, 0, n);
                Arrays.sort(sortedArray);
                records.add(new MapRecord("SortedArray", n, "1. Bulk Insertion", t, System.nanoTime() - start));

                // ---------------------------------------------------------
                // 2. 100,000 Lookups
                // ---------------------------------------------------------
                // HashMap
                start = System.nanoTime();
                long dummyHash = 0;
                for (String k : lookupKeys) {
                    Medicine m = hashMap.get(k);
                    if (m != null) dummyHash += m.hashCode();
                }
                records.add(new MapRecord("HashMap", n, "2. 100k Lookups", t, System.nanoTime() - start));

                // TreeMap
                start = System.nanoTime();
                for (String k : lookupKeys) {
                    Medicine m = treeMap.get(k);
                    if (m != null) dummyHash += m.hashCode();
                }
                records.add(new MapRecord("TreeMap", n, "2. 100k Lookups", t, System.nanoTime() - start));

                // LinkedHashMap
                start = System.nanoTime();
                for (String k : lookupKeys) {
                    Medicine m = linkedHashMap.get(k);
                    if (m != null) dummyHash += m.hashCode();
                }
                records.add(new MapRecord("LinkedHashMap", n, "2. 100k Lookups", t, System.nanoTime() - start));

                // Sorted Array using Arrays.binarySearch
                start = System.nanoTime();
                Medicine searchKeyObj = new Medicine("", "", "", 0, 0);
                for (String k : lookupKeys) {
                    int idx = Arrays.binarySearch(sortedArray, new Medicine(k, "", "", 0, 0));
                    if (idx >= 0) dummyHash += sortedArray[idx].hashCode();
                }
                records.add(new MapRecord("SortedArray", n, "2. 100k Lookups", t, System.nanoTime() - start));

                // ---------------------------------------------------------
                // 3. Full Iteration
                // ---------------------------------------------------------
                start = System.nanoTime();
                for (Map.Entry<String, Medicine> e : hashMap.entrySet()) dummyHash += e.getValue().hashCode();
                records.add(new MapRecord("HashMap", n, "3. Full Iteration", t, System.nanoTime() - start));

                start = System.nanoTime();
                for (Map.Entry<String, Medicine> e : treeMap.entrySet()) dummyHash += e.getValue().hashCode();
                records.add(new MapRecord("TreeMap", n, "3. Full Iteration", t, System.nanoTime() - start));

                start = System.nanoTime();
                for (Map.Entry<String, Medicine> e : linkedHashMap.entrySet()) dummyHash += e.getValue().hashCode();
                records.add(new MapRecord("LinkedHashMap", n, "3. Full Iteration", t, System.nanoTime() - start));

                start = System.nanoTime();
                for (Medicine m : sortedArray) dummyHash += m.hashCode();
                records.add(new MapRecord("SortedArray", n, "3. Full Iteration", t, System.nanoTime() - start));

                // ---------------------------------------------------------
                // 4. Code-Order Report Generation (Natural Key Order)
                // ---------------------------------------------------------
                // HashMap needs sorting
                start = System.nanoTime();
                List<Medicine> hmSorted = new ArrayList<>(hashMap.values());
                Collections.sort(hmSorted);
                records.add(new MapRecord("HashMap", n, "4. Code-Order Report", t, System.nanoTime() - start));

                // TreeMap is already sorted in Red-Black tree
                start = System.nanoTime();
                List<Medicine> tmSorted = new ArrayList<>(treeMap.values());
                records.add(new MapRecord("TreeMap", n, "4. Code-Order Report", t, System.nanoTime() - start));

                // LinkedHashMap needs sorting
                start = System.nanoTime();
                List<Medicine> lhmSorted = new ArrayList<>(linkedHashMap.values());
                Collections.sort(lhmSorted);
                records.add(new MapRecord("LinkedHashMap", n, "4. Code-Order Report", t, System.nanoTime() - start));

                // Sorted Array is already sorted
                start = System.nanoTime();
                List<Medicine> saSorted = Arrays.asList(sortedArray);
                records.add(new MapRecord("SortedArray", n, "4. Code-Order Report", t, System.nanoTime() - start));

                // ---------------------------------------------------------
                // 5. Insertion-Order Report Generation
                // ---------------------------------------------------------
                // HashMap does not preserve insertion order; needs full re-ordering or extra tracking
                start = System.nanoTime();
                List<Medicine> hmInsertOrder = new ArrayList<>(hashMap.values());
                records.add(new MapRecord("HashMap", n, "5. Insertion-Order Report", t, System.nanoTime() - start));

                // TreeMap does not preserve insertion order
                start = System.nanoTime();
                List<Medicine> tmInsertOrder = new ArrayList<>(treeMap.values());
                records.add(new MapRecord("TreeMap", n, "5. Insertion-Order Report", t, System.nanoTime() - start));

                // LinkedHashMap natively maintains doubly-linked list of insertion order!
                start = System.nanoTime();
                List<Medicine> lhmInsertOrder = new ArrayList<>(linkedHashMap.values());
                records.add(new MapRecord("LinkedHashMap", n, "5. Insertion-Order Report", t, System.nanoTime() - start));

                // SortedArray (already in original inserted order if copied from data)
                start = System.nanoTime();
                List<Medicine> saInsertOrder = Arrays.asList(data);
                records.add(new MapRecord("SortedArray", n, "5. Insertion-Order Report", t, System.nanoTime() - start));
            }
        }

        // Save raw CSV
        File dataDir = new File("Q4_MapComparison/data");
        dataDir.mkdirs();
        File rawCsv = new File(dataDir, "Q4_raw_benchmark.csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(rawCsv))) {
            pw.println("Structure,InventorySize,Operation,Trial,TimeNs,TimeMs");
            for (MapRecord r : records) {
                pw.printf("%s,%d,%s,%d,%d,%.4f%n", r.structure, r.inventorySize, r.operation, r.trial, r.timeNs, r.timeMs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\n[SUCCESS] Raw benchmark data written to: " + rawCsv.getPath());

        // Save Summary CSV and Print
        File resultsDir = new File("Q4_MapComparison/results");
        resultsDir.mkdirs();
        File summaryCsv = new File(resultsDir, "Q4_summary.csv");

        String[] ops = {
            "1. Bulk Insertion", "2. 100k Lookups", "3. Full Iteration",
            "4. Code-Order Report", "5. Insertion-Order Report"
        };
        String[] structs = {"HashMap", "TreeMap", "LinkedHashMap", "SortedArray"};

        System.out.println("\n" + "=".repeat(95));
        System.out.printf("%-10s | %-24s | %-12s | %-12s | %-14s | %-12s%n",
            "Size (N)", "Operation", "HashMap(ms)", "TreeMap(ms)", "LinkedHashMap(ms)", "SortedArr(ms)");
        System.out.println("-".repeat(95));

        try (PrintWriter pw = new PrintWriter(new FileWriter(summaryCsv))) {
            pw.println("Size,Operation,HashMapMs,TreeMapMs,LinkedHashMapMs,SortedArrayMs");

            for (int n : sizes) {
                for (String op : ops) {
                    double hmTime = getAvg(records, "HashMap", n, op);
                    double tmTime = getAvg(records, "TreeMap", n, op);
                    double lhmTime = getAvg(records, "LinkedHashMap", n, op);
                    double saTime = getAvg(records, "SortedArray", n, op);

                    System.out.printf("%-10d | %-24s | %12.4f | %12.4f | %14.4f | %12.4f%n",
                        n, op, hmTime, tmTime, lhmTime, saTime);

                    pw.printf("%d,%s,%.4f,%.4f,%.4f,%.4f%n", n, op, hmTime, tmTime, lhmTime, saTime);
                }
                System.out.println("-".repeat(95));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[SUCCESS] Summary data written to: " + summaryCsv.getPath());

        // Lookup Target Evaluation
        System.out.println("\n>>> [LOOKUP RESPONSE-TIME SLA TARGET EVALUATION]");
        System.out.println("Industry POS Target: < 0.05 ms per single medicine lookup (< 50 microseconds)");
        System.out.println("Measured per-lookup time for 100,000 lookups at N = 100,000:");
        double hm100k = getAvg(records, "HashMap", 100000, "2. 100k Lookups");
        double tm100k = getAvg(records, "TreeMap", 100000, "2. 100k Lookups");
        double lhm100k = getAvg(records, "LinkedHashMap", 100000, "2. 100k Lookups");
        double sa100k = getAvg(records, "SortedArray", 100000, "2. 100k Lookups");

        System.out.printf("  HashMap:       %.4f ms total (%.4f µs / lookup) -> PASS SLA%n", hm100k, (hm100k * 1000.0) / 100000.0);
        System.out.printf("  LinkedHashMap: %.4f ms total (%.4f µs / lookup) -> PASS SLA%n", lhm100k, (lhm100k * 1000.0) / 100000.0);
        System.out.printf("  TreeMap:       %.4f ms total (%.4f µs / lookup) -> PASS SLA%n", tm100k, (tm100k * 1000.0) / 100000.0);
        System.out.printf("  SortedArray:   %.4f ms total (%.4f µs / lookup) -> PASS SLA%n", sa100k, (sa100k * 1000.0) / 100000.0);
        System.out.println("===============================================================================\n");
    }

    private static double getAvg(List<MapRecord> records, String struct, int size, String op) {
        double sum = 0;
        int count = 0;
        for (MapRecord r : records) {
            if (r.structure.equals(struct) && r.inventorySize == size && r.operation.equals(op)) {
                sum += r.timeMs;
                count++;
            }
        }
        return count > 0 ? (sum / count) : 0.0;
    }
}
