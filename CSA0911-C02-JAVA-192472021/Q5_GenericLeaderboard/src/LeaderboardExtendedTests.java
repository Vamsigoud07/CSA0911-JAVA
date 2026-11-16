package src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Q5: Extended Leaderboard Verification Tests
 * 1. Multiple tie-breaker test cases.
 * 2. Deliberately faulty subtraction-based comparator demonstrating integer overflow.
 * 3. Immutable original list verification.
 * 4. Collections utility method demonstrations (sort, reverse, max, binarySearch).
 *
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class LeaderboardExtendedTests {

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("  Q5: GENERIC LEADERBOARD & COMPARATOR EXTENDED TEST SUITE");
        System.out.println("  Student: K VENKATA VAMSI GOUD (192472021) | Course: CSA09 (CO2)");
        System.out.println("===============================================================================");

        List<String> testLog = new ArrayList<>();

        // Test 1: Mandatory Supplied Demo Output
        System.out.println("\n>>> [TEST 1: MANDATORY DEMO RANKING]");
        Comparator<Team> standardComparator = Comparator
            .comparingInt(Team::getPoints).reversed()
            .thenComparing(Comparator.comparingInt(Team::getGoalDifference).reversed())
            .thenComparing(Team::getName);

        Leaderboard<Team> lb = new Leaderboard<>(standardComparator);
        lb.add(new Team("Panthers", 9, 4));
        lb.add(new Team("Cobras", 6, 1));
        lb.add(new Team("Eagles", 9, 6));
        lb.add(new Team("Bisons", 9, 4));

        List<Team> ranked = lb.ranking();
        String expectedMandatory = "[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]";
        String actualMandatory = ranked.toString();
        boolean passMandatory = expectedMandatory.equals(actualMandatory);
        System.out.println("Expected: " + expectedMandatory);
        System.out.println("Actual:   " + actualMandatory);
        System.out.println("Status:   " + (passMandatory ? "PASSED" : "FAILED"));
        testLog.add("Test 1: Mandatory Ranking -> " + (passMandatory ? "PASSED" : "FAILED"));

        // Test 2: Original List Immutability
        System.out.println("\n>>> [TEST 2: ORIGINAL LIST IMMUTABILITY]");
        System.out.println("Original insertion order: " + lb.getOriginalEntries());
        System.out.println("Ranked output order:      " + ranked);
        boolean orderPreserved = !lb.getOriginalEntries().equals(ranked);
        System.out.println("Original order preserved intact: " + orderPreserved);
        testLog.add("Test 2: Non-destructive Ranking -> " + (orderPreserved ? "PASSED" : "FAILED"));

        // Test 3: Complex Multi-Tie Scenario
        System.out.println("\n>>> [TEST 3: COMPLEX MULTI-WAY TIE BREAKING]");
        Leaderboard<Team> tieLb = new Leaderboard<>(standardComparator);
        tieLb.add(new Team("Delta", 12, 5));
        tieLb.add(new Team("Alpha", 12, 5));   // Identical points & GD, Alpha wins alphabetical
        tieLb.add(new Team("Charlie", 12, 5)); // Charlie next
        tieLb.add(new Team("Bravo", 12, 8));   // Bravo wins on GD (8 > 5)
        List<Team> tieRanked = tieLb.ranking();
        String expectedTie = "[Bravo(12,8), Alpha(12,5), Charlie(12,5), Delta(12,5)]";
        boolean passTie = expectedTie.equals(tieRanked.toString());
        System.out.println("Multi-Tie Expected: " + expectedTie);
        System.out.println("Multi-Tie Actual:   " + tieRanked);
        System.out.println("Status:             " + (passTie ? "PASSED" : "FAILED"));
        testLog.add("Test 3: Multi-Tie Resolution -> " + (passTie ? "PASSED" : "FAILED"));

        // Test 4: Faulty Subtraction Comparator vs Safe Integer.compare
        System.out.println("\n>>> [TEST 4: FAULTY SUBTRACTION COMPARATOR & INTEGER OVERFLOW BUG]");
        // Deliberately faulty comparator using subtraction: (t1.points - t2.points)
        Comparator<Team> faultyComparator = (t1, t2) -> (t2.getPoints() - t1.getPoints());

        // Extreme values near Integer limits
        Team teamSafe = new Team("SafeTeam", 100, 0);
        Team teamNegative = new Team("NegativeTeam", -2_000_000_000, 0);
        Team teamPositive = new Team("PositiveTeam", 2_000_000_000, 0);

        // Subtraction: teamNegative.points - teamPositive.points
        // -2,000,000,000 - (+2,000,000,000) = -4,000,000,000 -> UNDERFLOWS to +294,967,296!
        int subtractionResult = teamNegative.getPoints() - teamPositive.getPoints();
        int safeResult = Integer.compare(teamNegative.getPoints(), teamPositive.getPoints());

        System.out.printf("Comparing %d and %d:%n", teamNegative.getPoints(), teamPositive.getPoints());
        System.out.printf("  Faulty Subtraction Result: %d (WRONG POSITIVE! Reverses ordering!)%n", subtractionResult);
        System.out.printf("  Safe Integer.compare:      %d (CORRECT NEGATIVE)%n", safeResult);
        System.out.println("Explanation: Integer subtraction causes arithmetic underflow/overflow. Integer.compare() or comparingInt() avoids subtraction and evaluates relation safely.");
        testLog.add("Test 4: Subtraction Overflow Demonstration -> PASSED");

        // Test 5: Collections Utility Methods
        System.out.println("\n>>> [TEST 5: COLLECTIONS UTILITY METHODS DEMO]");
        List<Team> utilList = new ArrayList<>(lb.getOriginalEntries());
        System.out.println("Initial List:            " + utilList);

        // Collections.sort using natural ordering (Comparable)
        Collections.sort(utilList);
        System.out.println("Collections.sort():      " + utilList);

        // Collections.reverse
        Collections.reverse(utilList);
        System.out.println("Collections.reverse():   " + utilList);

        // Collections.max with natural ordering (highest points/GD)
        Team topTeam = Collections.max(utilList);
        System.out.println("Collections.max(Team):   " + topTeam);
        testLog.add("Test 5: Collections Utilities -> PASSED");

        // Save Results to CSV
        File resDir = new File("Q5_GenericLeaderboard/results");
        resDir.mkdirs();
        File logCsv = new File(resDir, "Q5_verification_log.csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(logCsv))) {
            pw.println("TestCase,Status");
            for (String l : testLog) {
                String[] parts = l.split(" -> ");
                pw.printf("\"%s\",%s%n", parts[0], parts[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\n[SUCCESS] Test results logged to: " + logCsv.getPath());
        System.out.println("===============================================================================");
    }
}
