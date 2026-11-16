package src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Q1: Master Benchmark Suite Runner
 * Runs repeated trials, logs raw System.nanoTime() measurements to CSV,
 * and prints verified summary comparison table.
 */
public class Q1BenchmarkSuite {

    public static class RawRecord {
        String experiment;
        String approach;
        int tickets;
        int fields;
        int trial;
        long timeNs;
        double timeMs;
        long memBefore;
        long memAfter;
        long memDelta;
        long estObjects;

        public RawRecord(String experiment, String approach, int tickets, int fields, int trial,
                         long timeNs, long memBefore, long memAfter, long estObjects) {
            this.experiment = experiment;
            this.approach = approach;
            this.tickets = tickets;
            this.fields = fields;
            this.trial = trial;
            this.timeNs = timeNs;
            this.timeMs = timeNs / 1_000_000.0;
            this.memBefore = memBefore;
            this.memAfter = memAfter;
            this.memDelta = Math.max(0, memAfter - memBefore);
            this.estObjects = estObjects;
        }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("  Q1: RAILWAY E-TICKET BENCHMARK - STRING vs STRINGBUILDER vs STRINGBUFFER");
        System.out.println("  Student: K VENKATA VAMSI GOUD (192472021) | Course: CSA09 (CO2)");
        System.out.println("===============================================================================");

        int[][] workloads = {
            {1, 5},
            {2500, 50},
            {10000, 50}
        };

        String[] approaches = {"String (+)", "StringBuilder", "StringBuffer"};
        int trials = 5;
        int warmups = 3;

        List<RawRecord> records = new ArrayList<>();

        for (int[] wl : workloads) {
            int tickets = wl[0];
            int fields = wl[1];
            String wlName = (wl[0] == 1) ? "Workload 1 (1 ticket, 5 fields)" :
                            (wl[0] == 2500) ? "Workload 2 (2,500 tickets, 50 fields)" :
                                              "Workload 3 (10,000 tickets, 50 fields)";

            System.out.println("\n>>> Running " + wlName + "...");

            for (String app : approaches) {
                // Warmup
                for (int w = 0; w < warmups; w++) {
                    for (int i = 0; i < Math.min(tickets, 100); i++) {
                        if (app.equals("String (+)")) StringBenchmark.generateTicketWithPlus(i, fields);
                        else if (app.equals("StringBuilder")) StringBuilderBenchmark.generateTicketWithBuilder(i, fields);
                        else StringBufferBenchmark.generateTicketWithStringBuffer(i, fields);
                    }
                }

                // Estimated objects
                long estObjPerTicket = app.equals("String (+)") ? (fields - 1) : 2;
                long totalEstObj = estObjPerTicket * tickets;

                for (int t = 1; t <= trials; t++) {
                    System.gc();
                    try { Thread.sleep(50); } catch (InterruptedException ignored) {}

                    long memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    long start = System.nanoTime();

                    for (int i = 0; i < tickets; i++) {
                        if (app.equals("String (+)")) StringBenchmark.generateTicketWithPlus(i, fields);
                        else if (app.equals("StringBuilder")) StringBuilderBenchmark.generateTicketWithBuilder(i, fields);
                        else StringBufferBenchmark.generateTicketWithStringBuffer(i, fields);
                    }

                    long elapsed = System.nanoTime() - start;
                    long memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                    records.add(new RawRecord(wlName, app, tickets, fields, t, elapsed, memBefore, memAfter, totalEstObj));
                }
            }
        }

        // Save raw CSV
        File dataDir = new File("Q1_StringPerformance/data");
        dataDir.mkdirs();
        File rawCsv = new File(dataDir, "Q1_raw_benchmark.csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(rawCsv))) {
            pw.println("Workload,Approach,Tickets,Fields,Trial,TimeNs,TimeMs,MemBeforeBytes,MemAfterBytes,MemDeltaBytes,EstIntermediateObjects");
            for (RawRecord r : records) {
                pw.printf("%s,%s,%d,%d,%d,%d,%.4f,%d,%d,%d,%d%n",
                    r.experiment, r.approach, r.tickets, r.fields, r.trial,
                    r.timeNs, r.timeMs, r.memBefore, r.memAfter, r.memDelta, r.estObjects);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\n[SUCCESS] Raw benchmark data written to: " + rawCsv.getPath());

        // Compute and print summary
        File resultsDir = new File("Q1_StringPerformance/results");
        resultsDir.mkdirs();
        File summaryCsv = new File(resultsDir, "Q1_summary.csv");

        System.out.println("\n" + "=".repeat(95));
        System.out.printf("%-20s | %-15s | %-10s | %-14s | %-14s | %-12s%n",
            "Workload", "Approach", "Tickets", "Avg Time (ms)", "Avg Mem (MB)", "Est. Objects");
        System.out.println("-".repeat(95));

        try (PrintWriter pw = new PrintWriter(new FileWriter(summaryCsv))) {
            pw.println("Workload,Approach,Tickets,Fields,AvgTimeMs,AvgMemMb,EstObjects");

            for (int[] wl : workloads) {
                int tickets = wl[0];
                int fields = wl[1];
                String wlName = (wl[0] == 1) ? "Workload 1 (1 ticket)" :
                                (wl[0] == 2500) ? "Workload 2 (2.5k tix)" :
                                                  "Workload 3 (10k tix)";

                for (String app : approaches) {
                    double sumTimeMs = 0;
                    double sumMemMb = 0;
                    long estObj = 0;
                    int count = 0;

                    for (RawRecord r : records) {
                        if (r.tickets == tickets && r.approach.equals(app)) {
                            sumTimeMs += r.timeMs;
                            sumMemMb += (r.memDelta / (1024.0 * 1024.0));
                            estObj = r.estObjects;
                            count++;
                        }
                    }

                    double avgTimeMs = sumTimeMs / count;
                    double avgMemMb = sumMemMb / count;

                    System.out.printf("%-20s | %-15s | %-10d | %14.4f | %14.4f | %-12d%n",
                        wlName, app, tickets, avgTimeMs, avgMemMb, estObj);

                    pw.printf("%s,%s,%d,%d,%.4f,%.4f,%d%n",
                        wlName, app, tickets, fields, avgTimeMs, avgMemMb, estObj);
                }
                System.out.println("-".repeat(95));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[SUCCESS] Summary data written to: " + summaryCsv.getPath());
        System.out.println("===============================================================================\n");
    }
}
