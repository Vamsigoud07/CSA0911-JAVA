package src;

/**
 * Q1: Railway e-Ticket Generation Benchmark - StringBuilder
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class StringBuilderBenchmark {

    private static final String[] FIELD_KEYS = {
        "PNR", "TrainNo", "TrainName", "Source", "Destination",
        "JourneyDate", "ClassType", "Quota", "PassengerName", "Age",
        "Gender", "Coach", "Berth", "BerthType", "BookingStatus",
        "CurrentStatus", "TicketFare", "CateringCharge", "DynamicFare", "GST",
        "TotalFare", "PaymentMode", "TxnId", "IRCTC_Fee", "BankRefNo",
        "BoardingStation", "ReservationUpto", "ScheduledDeparture", "ScheduledArrival", "DistanceKm",
        "TravelTime", "PassengerContact", "IDCardType", "IDCardNo", "FoodChoice",
        "BedrollOpted", "AutoUpgradation", "InsuranceOpted", "InsurancePolicyNo", "EmergencyContact",
        "ConcessionType", "GateNo", "PlatformNo", "TIN", "AgentID",
        "ChartStatus", "QRCodeString", "SecurityHash", "FareRuleCode", "CancellationTerms"
    };

    public static String generateTicketWithBuilder(int ticketIndex, int fieldCount) {
        StringBuilder sb = new StringBuilder(1024);
        for (int i = 0; i < fieldCount; i++) {
            String key = FIELD_KEYS[i % FIELD_KEYS.length];
            sb.append(key).append(":VAL_").append(ticketIndex).append("_").append(i).append(";");
        }
        return sb.toString();
    }

    public static StringBenchmark.BenchmarkResult runBenchmark(int ticketCount, int fieldsPerTicket, int trials, int warmups) {
        // Warmup
        for (int w = 0; w < warmups; w++) {
            for (int i = 0; i < Math.min(ticketCount, 100); i++) {
                generateTicketWithBuilder(i, fieldsPerTicket);
            }
        }
        System.gc();
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        long totalTimeNs = 0;
        long totalMemoryUsed = 0;
        // StringBuilder pre-sized: 1 builder + 1 final String = 2 objects per ticket
        long estimatedObjects = (long) 2 * ticketCount;

        for (int t = 1; t <= trials; t++) {
            System.gc();
            long memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long start = System.nanoTime();

            for (int i = 0; i < ticketCount; i++) {
                generateTicketWithBuilder(i, fieldsPerTicket);
            }

            long elapsed = System.nanoTime() - start;
            long memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memUsed = Math.max(0, memAfter - memBefore);

            totalTimeNs += elapsed;
            totalMemoryUsed += memUsed;
        }

        double avgTimeMs = (totalTimeNs / (double) trials) / 1_000_000.0;
        double avgMemMb = (totalMemoryUsed / (double) trials) / (1024.0 * 1024.0);

        return new StringBenchmark.BenchmarkResult("StringBuilder", ticketCount, fieldsPerTicket, trials, avgTimeMs, avgMemMb, estimatedObjects);
    }

    public static void main(String[] args) {
        System.out.println("=== Railway e-Ticket Benchmark: StringBuilder ===");
        System.out.println("Workload 1: 1 ticket, 5 fields");
        StringBenchmark.BenchmarkResult r1 = runBenchmark(1, 5, 5, 3);
        System.out.printf("Avg Time: %.4f ms, Memory: %.4f MB, Est. Objects: %d%n", r1.avgTimeMs, r1.avgMemoryMb, r1.estimatedObjects);

        System.out.println("\nWorkload 2: 2,500 tickets, 50 fields");
        StringBenchmark.BenchmarkResult r2 = runBenchmark(2500, 50, 5, 2);
        System.out.printf("Avg Time: %.4f ms, Memory: %.4f MB, Est. Objects: %d%n", r2.avgTimeMs, r2.avgMemoryMb, r2.estimatedObjects);

        System.out.println("\nWorkload 3: 10,000 tickets, 50 fields");
        StringBenchmark.BenchmarkResult r3 = runBenchmark(10000, 50, 3, 1);
        System.out.printf("Avg Time: %.4f ms, Memory: %.4f MB, Est. Objects: %d%n", r3.avgTimeMs, r3.avgMemoryMb, r3.estimatedObjects);
    }
}
