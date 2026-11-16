package src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Q3: Comprehensive HashSet & hashCode-equals Contract Experiments
 * Tests:
 * 1. Test where incorrect class appears to work.
 * 2. Test exposing duplicate entries (equals overridden without hashCode).
 * 3. Test demonstrating the danger of mutable key/fields in hash structures.
 * Also evaluates TreeSet and HashMap alternatives.
 *
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class HashSetExperiments {

    // --- Supplied Incorrect Student Class (Without hashCode) ---
    static class BrokenStudent {
        private final String regNo;
        private final String name;

        public BrokenStudent(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BrokenStudent that = (BrokenStudent) o;
            return Objects.equals(regNo, that.regNo);
        }

        // Deliberately omitted hashCode()! Uses System identityHashCode (Object.hashCode)
        @Override
        public String toString() {
            return "BrokenStudent{" + regNo + ", " + name + "}";
        }
    }

    // --- Mutable Student for Experiment 3 ---
    static class MutableStudent {
        private String regNo;
        private String name;

        public MutableStudent(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        public void setRegNo(String regNo) {
            this.regNo = regNo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MutableStudent that = (MutableStudent) o;
            return Objects.equals(regNo, that.regNo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(regNo);
        }

        @Override
        public String toString() {
            return "MutableStudent{" + regNo + ", " + name + "}";
        }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("  Q3: HASHSET & EQUALS-HASHCODE CONTRACT EXPERIMENTS");
        System.out.println("  Student: K VENKATA VAMSI GOUD (192472021) | Course: CSA09 (CO2)");
        System.out.println("===============================================================================");

        List<String[]> experimentResults = new ArrayList<>();

        // -------------------------------------------------------------
        // Experiment 1: Incorrect class appears to work
        // -------------------------------------------------------------
        System.out.println("\n>>> [EXPERIMENT 1] Incorrect Class Appears to Work (Distinct elements & same reference)");
        Set<BrokenStudent> exp1Set = new HashSet<>();
        BrokenStudent bs1 = new BrokenStudent("192472001", "Arun");
        BrokenStudent bs2 = new BrokenStudent("192472002", "Bala");
        exp1Set.add(bs1);
        exp1Set.add(bs2);
        exp1Set.add(bs1); // Re-adding the identical instance reference

        int exp1Predicted = 2;
        int exp1Actual = exp1Set.size();
        String exp1Status = (exp1Predicted == exp1Actual) ? "PASSED" : "FAILED";
        String exp1Explanation = "Appears to work because different regNos produce distinct objects, and reference equality (==) suppresses re-adding bs1.";
        System.out.printf("Predicted Size: %d | Actual Size: %d | Outcome: %s%n", exp1Predicted, exp1Actual, exp1Status);
        System.out.println("Explanation: " + exp1Explanation);
        experimentResults.add(new String[]{"Experiment 1: Incorrect Appears to Work", String.valueOf(exp1Predicted), String.valueOf(exp1Actual), exp1Status, exp1Explanation});

        // -------------------------------------------------------------
        // Experiment 2: Exposing Duplicate Bug (Distinct instances, same regNo)
        // -------------------------------------------------------------
        System.out.println("\n>>> [EXPERIMENT 2] Exposing Contract Violation (equals without hashCode)");
        Set<BrokenStudent> exp2BrokenSet = new HashSet<>();
        BrokenStudent b1 = new BrokenStudent("192472021", "Vamsi");
        BrokenStudent b2 = new BrokenStudent("192472021", "Vamsi Goud"); // Duplicate regNo!
        BrokenStudent b3 = new BrokenStudent("192472022", "Rahul");
        exp2BrokenSet.add(b1);
        exp2BrokenSet.add(b2);
        exp2BrokenSet.add(b3);

        int exp2PredictedBroken = 3;
        int exp2ActualBroken = exp2BrokenSet.size();
        System.out.println("[Supplied Code Without hashCode]");
        System.out.printf("Predicted Size: %d | Actual Size: %d | Output: Unique students: %d%n",
            exp2PredictedBroken, exp2ActualBroken, exp2ActualBroken);

        // Now test with Completed Student Class (Properly Overridden hashCode)
        Set<Student> exp2CorrectSet = new HashSet<>();
        Student s1 = new Student("192472021", "Vamsi");
        Student s2 = new Student("192472021", "Vamsi Goud"); // Duplicate regNo!
        Student s3 = new Student("192472022", "Rahul");
        exp2CorrectSet.add(s1);
        exp2CorrectSet.add(s2);
        exp2CorrectSet.add(s3);

        int exp2PredictedCorrect = 2;
        int exp2ActualCorrect = exp2CorrectSet.size();
        String exp2Status = (exp2ActualCorrect == 2) ? "PASSED" : "FAILED";
        String exp2Explanation = "Without hashCode(), Object.hashCode uses memory address; objects land in different buckets so equals() is never called. With overridden hashCode(), both map to same bucket and duplicate is eliminated.";
        System.out.println("[Completed Code With hashCode]");
        System.out.printf("Predicted Size: %d | Actual Size: %d | Output: Unique students: %d | Outcome: %s%n",
            exp2PredictedCorrect, exp2ActualCorrect, exp2ActualCorrect, exp2Status);
        System.out.println("Explanation: " + exp2Explanation);
        experimentResults.add(new String[]{"Experiment 2: Contract Violation Exposed", String.valueOf(exp2PredictedCorrect), String.valueOf(exp2ActualCorrect), exp2Status, exp2Explanation});

        // -------------------------------------------------------------
        // Experiment 3: Mutable Key Hazard in Hash-based Collections
        // -------------------------------------------------------------
        System.out.println("\n>>> [EXPERIMENT 3] Mutable Field Hazard (Mutation After Insertion)");
        Set<MutableStudent> exp3Set = new HashSet<>();
        MutableStudent ms1 = new MutableStudent("192472030", "Deepak");
        exp3Set.add(ms1);

        boolean foundBeforeMutation = exp3Set.contains(ms1);
        System.out.println("Before mutation: contains(ms1) = " + foundBeforeMutation);

        // Mutating key field while resident in HashSet
        ms1.setRegNo("192472999");
        boolean foundAfterMutation = exp3Set.contains(ms1);
        int exp3Predicted = 1; // Size is still 1, but element is 'lost'
        int exp3Actual = exp3Set.size();
        String exp3Status = (!foundAfterMutation && exp3Actual == 1) ? "PASSED" : "FAILED";
        String exp3Explanation = "Object remains in HashSet (size=1) but contains() returns FALSE! The new hashCode places lookup in a different bucket. Demonstrates why hash fields must be final/immutable.";
        System.out.printf("After mutation: contains(ms1) = %b | Size: %d | Outcome: %s%n", foundAfterMutation, exp3Actual, exp3Status);
        System.out.println("Explanation: " + exp3Explanation);
        experimentResults.add(new String[]{"Experiment 3: Mutable Field Hazard", String.valueOf(exp3Predicted), String.valueOf(exp3Actual), exp3Status, exp3Explanation});

        // -------------------------------------------------------------
        // Alternative Implementations: TreeSet & HashMap
        // -------------------------------------------------------------
        System.out.println("\n>>> [ALTERNATIVE SOLUTIONS]");
        // 1. TreeSet Solution
        Set<Student> treeSet = new TreeSet<>(Comparator.comparing(Student::getRegNo));
        treeSet.add(s1);
        treeSet.add(s2);
        treeSet.add(s3);
        System.out.println("1. TreeSet<Student> with Comparator on regNo -> Size: " + treeSet.size());
        for (Student st : treeSet) System.out.println("   " + st);

        // 2. HashMap Solution
        Map<String, Student> studentMap = new HashMap<>();
        studentMap.put(s1.getRegNo(), s1);
        studentMap.put(s2.getRegNo(), s2);
        studentMap.put(s3.getRegNo(), s3);
        System.out.println("2. HashMap<String, Student> keyed by regNo -> Size: " + studentMap.size());
        studentMap.forEach((reg, st) -> System.out.println("   Key: " + reg + " => " + st));

        // Save Results to CSV
        File resDir = new File("Q3_HashSetContract/results");
        resDir.mkdirs();
        File resCsv = new File(resDir, "Q3_experiment_results.csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(resCsv))) {
            pw.println("Experiment,PredictedSize,ActualSize,Status,Explanation");
            for (String[] row : experimentResults) {
                pw.printf("\"%s\",%s,%s,%s,\"%s\"%n", row[0], row[1], row[2], row[3], row[4]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\n[SUCCESS] Experiment results saved to: " + resCsv.getPath());
        System.out.println("===============================================================================");
    }
}
