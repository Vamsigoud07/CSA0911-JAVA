package src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Q2: Music Playlist Editor Benchmark - ArrayList vs LinkedList vs ArrayDeque
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class ListBenchmark {

    public static class MetricRecord {
        String structure;
        int playlistSize;
        String operation;
        int trial;
        long timeNs;
        double timeMs;

        public MetricRecord(String structure, int playlistSize, String operation, int trial, long timeNs) {
            this.structure = structure;
            this.playlistSize = playlistSize;
            this.operation = operation;
            this.trial = trial;
            this.timeNs = timeNs;
            this.timeMs = timeNs / 1_000_000.0;
        }
    }

    private static Song createDummySong(int id) {
        return new Song("TRACK_" + id, "Song Title " + id, "Artist " + (id % 100), 180 + (id % 120));
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("  Q2: MUSIC PLAYLIST EDITOR - ARRAYLIST vs LINKEDLIST vs ARRAYDEQUE");
        System.out.println("  Student: K VENKATA VAMSI GOUD (192472021) | Course: CSA09 (CO2)");
        System.out.println("===============================================================================");

        int[] sizes = {1000, 10000, 100000};
        int trials = 5;
        List<MetricRecord> records = new ArrayList<>();

        for (int n : sizes) {
            System.out.println("\n>>> Testing Playlist Size N = " + n + " elements...");

            for (int t = 1; t <= trials; t++) {
                // Pre-generate songs
                Song[] pool = new Song[n];
                for (int i = 0; i < n; i++) pool[i] = createDummySong(i);

                // --- 1. Append (Add at End) ---
                // ArrayList
                long start = System.nanoTime();
                ArrayList<Song> arrayList = new ArrayList<>();
                for (Song s : pool) arrayList.add(s);
                records.add(new MetricRecord("ArrayList", n, "1. Add End", t, System.nanoTime() - start));

                // LinkedList
                start = System.nanoTime();
                LinkedList<Song> linkedList = new LinkedList<>();
                for (Song s : pool) linkedList.add(s);
                records.add(new MetricRecord("LinkedList", n, "1. Add End", t, System.nanoTime() - start));

                // ArrayDeque
                start = System.nanoTime();
                ArrayDeque<Song> arrayDeque = new ArrayDeque<>();
                for (Song s : pool) arrayDeque.addLast(s);
                records.add(new MetricRecord("ArrayDeque", n, "1. Add End", t, System.nanoTime() - start));

                // --- 2. Insert at Front (500 ops) ---
                int frontOps = Math.min(500, n / 2);
                ArrayList<Song> alFront = new ArrayList<>(arrayList);
                start = System.nanoTime();
                for (int i = 0; i < frontOps; i++) alFront.add(0, pool[i % n]);
                records.add(new MetricRecord("ArrayList", n, "2. Insert Front", t, System.nanoTime() - start));

                LinkedList<Song> llFront = new LinkedList<>(linkedList);
                start = System.nanoTime();
                for (int i = 0; i < frontOps; i++) llFront.addFirst(pool[i % n]);
                records.add(new MetricRecord("LinkedList", n, "2. Insert Front", t, System.nanoTime() - start));

                ArrayDeque<Song> adFront = new ArrayDeque<>(arrayDeque);
                start = System.nanoTime();
                for (int i = 0; i < frontOps; i++) adFront.addFirst(pool[i % n]);
                records.add(new MetricRecord("ArrayDeque", n, "2. Insert Front", t, System.nanoTime() - start));

                // --- 3. Insert Middle (300 ops) ---
                int midOps = Math.min(300, n / 4);
                ArrayList<Song> alMid = new ArrayList<>(arrayList);
                start = System.nanoTime();
                for (int i = 0; i < midOps; i++) alMid.add(alMid.size() / 2, pool[i % n]);
                records.add(new MetricRecord("ArrayList", n, "3. Insert Middle", t, System.nanoTime() - start));

                LinkedList<Song> llMid = new LinkedList<>(linkedList);
                start = System.nanoTime();
                for (int i = 0; i < midOps; i++) llMid.add(llMid.size() / 2, pool[i % n]);
                records.add(new MetricRecord("LinkedList", n, "3. Insert Middle", t, System.nanoTime() - start));

                // ArrayDeque does NOT support arbitrary indexed middle insertion.
                // We record 0 ns (unsupported) and clearly document it in the report.
                records.add(new MetricRecord("ArrayDeque", n, "3. Insert Middle", t, 0));

                // --- 4. Remove at Front (500 ops) ---
                ArrayList<Song> alRemFront = new ArrayList<>(arrayList);
                start = System.nanoTime();
                for (int i = 0; i < frontOps; i++) alRemFront.remove(0);
                records.add(new MetricRecord("ArrayList", n, "4. Remove Front", t, System.nanoTime() - start));

                LinkedList<Song> llRemFront = new LinkedList<>(linkedList);
                start = System.nanoTime();
                for (int i = 0; i < frontOps; i++) llRemFront.removeFirst();
                records.add(new MetricRecord("LinkedList", n, "4. Remove Front", t, System.nanoTime() - start));

                ArrayDeque<Song> adRemFront = new ArrayDeque<>(arrayDeque);
                start = System.nanoTime();
                for (int i = 0; i < frontOps; i++) adRemFront.removeFirst();
                records.add(new MetricRecord("ArrayDeque", n, "4. Remove Front", t, System.nanoTime() - start));

                // --- 5. Remove Middle (300 ops) ---
                ArrayList<Song> alRemMid = new ArrayList<>(arrayList);
                start = System.nanoTime();
                for (int i = 0; i < midOps; i++) alRemMid.remove(alRemMid.size() / 2);
                records.add(new MetricRecord("ArrayList", n, "5. Remove Middle", t, System.nanoTime() - start));

                LinkedList<Song> llRemMid = new LinkedList<>(linkedList);
                start = System.nanoTime();
                for (int i = 0; i < midOps; i++) llRemMid.remove(llRemMid.size() / 2);
                records.add(new MetricRecord("LinkedList", n, "5. Remove Middle", t, System.nanoTime() - start));

                records.add(new MetricRecord("ArrayDeque", n, "5. Remove Middle", t, 0)); // Unsupported

                // --- 6. Access k-th element (10,000 random accesses) ---
                int accessCount = 10000;
                Random rnd = new Random(42);
                int[] accessIndices = new int[accessCount];
                for (int i = 0; i < accessCount; i++) accessIndices[i] = rnd.nextInt(n);

                start = System.nanoTime();
                long dummyHash = 0;
                for (int idx : accessIndices) dummyHash += arrayList.get(idx).hashCode();
                records.add(new MetricRecord("ArrayList", n, "6. Access k-th", t, System.nanoTime() - start));

                // For LinkedList, doing 10,000 get(k) on 100k would take minutes (O(n*m)),
                // so we do 200 lookups and scale proportionally to 10,000.
                int llAccessCount = Math.min(200, accessCount);
                start = System.nanoTime();
                for (int i = 0; i < llAccessCount; i++) dummyHash += linkedList.get(accessIndices[i]).hashCode();
                long llElapsed = (System.nanoTime() - start) * (accessCount / llAccessCount);
                records.add(new MetricRecord("LinkedList", n, "6. Access k-th", t, llElapsed));

                // ArrayDeque has no direct get(k); marked unsupported (0)
                records.add(new MetricRecord("ArrayDeque", n, "6. Access k-th", t, 0));

                // --- 7. Full Iteration ---
                start = System.nanoTime();
                for (Song s : arrayList) dummyHash += s.hashCode();
                records.add(new MetricRecord("ArrayList", n, "7. Full Iteration", t, System.nanoTime() - start));

                start = System.nanoTime();
                for (Song s : linkedList) dummyHash += s.hashCode();
                records.add(new MetricRecord("LinkedList", n, "7. Full Iteration", t, System.nanoTime() - start));

                start = System.nanoTime();
                for (Song s : arrayDeque) dummyHash += s.hashCode();
                records.add(new MetricRecord("ArrayDeque", n, "7. Full Iteration", t, System.nanoTime() - start));

                // --- 8. Double-ended Ops (1,000 addFirst/removeLast cycles) ---
                int deqOps = 1000;
                ArrayList<Song> alDeq = new ArrayList<>(arrayList);
                start = System.nanoTime();
                for (int i = 0; i < deqOps; i++) {
                    alDeq.add(0, pool[i % n]);
                    alDeq.remove(alDeq.size() - 1);
                }
                records.add(new MetricRecord("ArrayList", n, "8. Both Ends", t, System.nanoTime() - start));

                LinkedList<Song> llDeq = new LinkedList<>(linkedList);
                start = System.nanoTime();
                for (int i = 0; i < deqOps; i++) {
                    llDeq.addFirst(pool[i % n]);
                    llDeq.removeLast();
                }
                records.add(new MetricRecord("LinkedList", n, "8. Both Ends", t, System.nanoTime() - start));

                ArrayDeque<Song> adDeq = new ArrayDeque<>(arrayDeque);
                start = System.nanoTime();
                for (int i = 0; i < deqOps; i++) {
                    adDeq.addFirst(pool[i % n]);
                    adDeq.removeLast();
                }
                records.add(new MetricRecord("ArrayDeque", n, "8. Both Ends", t, System.nanoTime() - start));
            }
        }

        // Save raw CSV
        File dataDir = new File("Q2_ListComparison/data");
        dataDir.mkdirs();
        File rawCsv = new File(dataDir, "Q2_raw_benchmark.csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(rawCsv))) {
            pw.println("Structure,PlaylistSize,Operation,Trial,TimeNs,TimeMs");
            for (MetricRecord r : records) {
                pw.printf("%s,%d,%s,%d,%d,%.4f%n", r.structure, r.playlistSize, r.operation, r.trial, r.timeNs, r.timeMs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\n[SUCCESS] Raw benchmark data written to: " + rawCsv.getPath());

        // Save Summary CSV and Print
        File resultsDir = new File("Q2_ListComparison/results");
        resultsDir.mkdirs();
        File summaryCsv = new File(resultsDir, "Q2_summary.csv");

        String[] ops = {
            "1. Add End", "2. Insert Front", "3. Insert Middle", "4. Remove Front",
            "5. Remove Middle", "6. Access k-th", "7. Full Iteration", "8. Both Ends"
        };
        String[] structs = {"ArrayList", "LinkedList", "ArrayDeque"};

        System.out.println("\n" + "=".repeat(85));
        System.out.printf("%-10s | %-18s | %-12s | %-12s | %-12s%n",
            "Size (N)", "Operation", "ArrayList(ms)", "LinkedList(ms)", "ArrayDeque(ms)");
        System.out.println("-".repeat(85));

        try (PrintWriter pw = new PrintWriter(new FileWriter(summaryCsv))) {
            pw.println("Size,Operation,ArrayListMs,LinkedListMs,ArrayDequeMs");

            for (int n : sizes) {
                for (String op : ops) {
                    double alTime = getAvg(records, "ArrayList", n, op);
                    double llTime = getAvg(records, "LinkedList", n, op);
                    double adTime = getAvg(records, "ArrayDeque", n, op);

                    String adStr = (op.contains("Middle") || op.contains("Access")) ? "N/A (O(n))" : String.format("%.4f", adTime);

                    System.out.printf("%-10d | %-18s | %12.4f | %12.4f | %12s%n",
                        n, op, alTime, llTime, adStr);

                    pw.printf("%d,%s,%.4f,%.4f,%s%n", n, op, alTime, llTime, adStr);
                }
                System.out.println("-".repeat(85));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[SUCCESS] Summary data written to: " + summaryCsv.getPath());
        System.out.println("===============================================================================\n");
    }

    private static double getAvg(List<MetricRecord> records, String struct, int size, String op) {
        double sum = 0;
        int count = 0;
        for (MetricRecord r : records) {
            if (r.structure.equals(struct) && r.playlistSize == size && r.operation.equals(op)) {
                sum += r.timeMs;
                count++;
            }
        }
        return count > 0 ? (sum / count) : 0.0;
    }
}
