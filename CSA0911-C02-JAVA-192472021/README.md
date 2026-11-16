# Programming in Java - Assignment 2 (CO2)
**Course:** Programming in Java (CSA09)  
**Course Outcome:** CO2  
**Institution:** SIMATS – Saveetha School of Engineering  
**Student Name:** K VENKATA VAMSI GOUD  
**Register Number:** 192472021  
**GitHub Repository:** `CSA09-192472021-Assignment2`  
**GitHub Repository URL:** `https://github.com/vamsigoud192472021/CSA09-192472021-Assignment2` (Placeholder for manual upload / push)

---

## 1. Project Overview
This repository contains the complete implementation, experimental benchmarking, and algorithmic analysis for **Assignment 2 (CO2)** of the *Programming in Java* curriculum. The assignment covers foundational and advanced aspects of Java core libraries and algorithms:
- **Q1: String Performance:** Empirical evaluation of String concatenation (`+`), `StringBuilder`, and `StringBuffer` under varying workloads (1, 2,500, and 10,000 tickets).
- **Q2: List Comparison:** Comparative benchmarking of `ArrayList`, `LinkedList`, and `ArrayDeque` across playlist sizes (1,000, 10,000, and 100,000 elements) for 8 core operations.
- **Q3: HashSet Contract:** Event Attendance De-duplication verifying the `hashCode()` and `equals()` contract, demonstrating contract violations, mutable field hazards, and TreeSet/HashMap alternatives.
- **Q4: Map Comparison:** Pharmacy Inventory Management comparing `HashMap`, `TreeMap`, `LinkedHashMap`, and Sorted Array (`Arrays.binarySearch()`) across 1k, 10k, and 100k items.
- **Q5: Generic Leaderboard:** Reusable sports leaderboard using Java Generics (`<T>`), Comparator chaining, non-destructive ranking, and overflow-safe sorting.
- **Q6: Hospital PriorityQueue:** Emergency Room triage queue prioritizing clinical severity (S1 to S5) with an arrival counter to preserve FIFO fairness, benchmarked against multi-queue and sorted linked list architectures.
- **Q7: Day-4 Design Challenge:** In-depth diagnosis, pre-implementation prediction, architectural redesign, and experimental defense of the string concatenation bottleneck in high-throughput ticket generation.

---

## 2. Environment & Requirements
- **Operating System:** Windows 10/11 (64-bit)
- **Java Development Kit (JDK):** OpenJDK 17 (Temurin-17.0.21+9 Hotspot 64-Bit Server VM)
- **Compiler:** `javac 17.0.21`
- **Runtime:** `java 17.0.21`
- **Python (for graphs and report generation):** Python 3.11 with `matplotlib`, `Pillow`, `python-docx`

---

## 3. Project Directory Structure
```
CSA09-192472021-Assignment2/
│
├── Assignment-2-Report.docx         # Comprehensive 50-mark final academic Word report
├── README.md                        # Project documentation and execution guide
├── .gitignore                       # Git ignore configuration
│
├── Q1_StringPerformance/
│   ├── src/
│   │   ├── StringBenchmark.java
│   │   ├── StringBuilderBenchmark.java
│   │   ├── StringBufferBenchmark.java
│   │   └── Q1BenchmarkSuite.java
│   ├── data/                        # Q1_raw_benchmark.csv
│   ├── results/                     # Q1_summary.csv, Q1_execution_time.png, Q1_memory_comparison.png
│   └── screenshots/                 # Q1_terminal_execution.png
│
├── Q2_ListComparison/
│   ├── src/
│   │   ├── Song.java
│   │   └── ListBenchmark.java
│   ├── data/                        # Q2_raw_benchmark.csv
│   ├── results/                     # Q2_summary.csv, Q2_operations_comparison.png
│   └── screenshots/                 # Q2_terminal_execution.png
│
├── Q3_HashSetContract/
│   ├── src/
│   │   ├── Student.java
│   │   ├── Attendance.java          # Complete the code program (Unique students: 2)
│   │   └── HashSetExperiments.java  # 3 contract violation & mutation experiments
│   ├── results/                     # Q3_experiment_results.csv
│   └── screenshots/                 # Q3_attendance_execution.png, Q3_experiments_execution.png
│
├── Q4_MapComparison/
│   ├── src/
│   │   ├── Medicine.java
│   │   └── PharmacyInventory.java   # Map benchmark and sorted array binarySearch
│   ├── data/                        # Q4_raw_benchmark.csv
│   ├── results/                     # Q4_summary.csv, Q4_lookup_performance.png, Q4_operations_comparison.png
│   └── screenshots/                 # Q4_terminal_execution.png
│
├── Q5_GenericLeaderboard/
│   ├── src/
│   │   ├── Team.java
│   │   ├── Leaderboard.java         # Generic Leaderboard<T>
│   │   ├── LeaderboardDemo.java     # Complete the code demo
│   │   └── LeaderboardExtendedTests.java # Tie-breaker, overflow bug, and utility demos
│   ├── results/                     # Q5_verification_log.csv
│   └── screenshots/                 # Q5_demo_execution.png, Q5_extended_tests_execution.png
│
├── Q6_PriorityQueue/
│   ├── src/
│   │   ├── Patient.java
│   │   ├── Triage.java              # Complete the code demo
│   │   └── TriageBenchmark.java     # PriorityQueue vs 5-FIFO-Queues vs Sorted LinkedList
│   ├── results/                     # Q6_raw_benchmark.csv, Q6_summary.csv, Q6_triage_comparison.png
│   └── screenshots/                 # Q6_triage_mandatory_execution.png, Q6_triage_benchmark_execution.png
│
└── Q7_DesignChallenge/
    ├── OriginalSolution/
    │   └── OriginalTicketGenerator.java
    ├── RedesignedSolution/
    │   └── OptimizedTicketGenerator.java
    ├── experiments/
    │   ├── Prediction_Before_Testing.md # Pre-implementation prediction document
    │   └── DesignChallengeBenchmark.java
    ├── results/                     # Q7_raw_benchmark.csv, Q7_summary.csv
    ├── graphs/                      # Q7_execution_time_comparison.png, Q7_memory_allocation_comparison.png
    └── screenshots/                 # Q7_terminal_execution.png
```

---

## 4. Compilation & Execution Instructions

Navigate to the project root directory in PowerShell or Terminal:
```powershell
cd CSA09-192472021-Assignment2
```

### Q1 – Railway e-Ticket String Benchmark
```powershell
# Compile
javac -d Q1_StringPerformance\bin Q1_StringPerformance\src\*.java

# Execute Master Benchmark Suite
java -cp Q1_StringPerformance\bin src.Q1BenchmarkSuite
```
**Expected Outcome:** Generates `Q1_raw_benchmark.csv` and `Q1_summary.csv` showing `StringBuilder` running 2.4x–3.7x faster than `String (+)` and creating 95.9% fewer heap objects.

---

### Q2 – Music Playlist Editor List Benchmark
```powershell
# Compile
javac -d Q2_ListComparison\bin Q2_ListComparison\src\*.java

# Execute
java -cp Q2_ListComparison\bin src.ListBenchmark
```
**Expected Outcome:** Generates `Q2_raw_benchmark.csv` and `Q2_summary.csv`. Demonstrates $O(1)$ random access for `ArrayList` (1.25 ms vs 482.48 ms for `LinkedList` at $N=100,000$), and $O(1)$ front/end performance for `ArrayDeque` and `LinkedList`.

---

### Q3 – Event Attendance De-duplication (HashSet Contract)
```powershell
# Compile
javac -d Q3_HashSetContract\bin Q3_HashSetContract\src\*.java

# Run Complete the Code Program (Mandatory Expected Output)
java -cp Q3_HashSetContract\bin src.Attendance

# Run Full 3-Experiment Suite
java -cp Q3_HashSetContract\bin src.HashSetExperiments
```
**Mandatory Expected Output:**
```
Unique students: 2
```
**Experiment Outputs:**
- Experiment 1 (Distinct elements): Predicted: 2 | Actual: 2 (PASSED)
- Experiment 2 (Contract violation): Broken without hashCode: 3 | Fixed with hashCode: 2 (PASSED)
- Experiment 3 (Mutable key hazard): Object mutated while resident in HashSet: `contains()` returns `false` (Element lost!)

---

### Q4 – Pharmacy Inventory Map Comparison
```powershell
# Compile
javac -d Q4_MapComparison\bin Q4_MapComparison\src\*.java

# Execute
java -cp Q4_MapComparison\bin src.PharmacyInventory
```
**Expected Outcome:** Generates `Q4_raw_benchmark.csv` and `Q4_summary.csv`. Demonstrates $O(1)$ lookups for `HashMap` and `LinkedHashMap` (< 0.07 µs/lookup), $O(\log n)$ for `TreeMap` and `SortedArray`, natural code-order reporting in `TreeMap` (0.95 ms vs 22.27 ms for `HashMap`), and insertion-order preservation in `LinkedHashMap`.

---

### Q5 – Generic Sports Leaderboard
```powershell
# Compile
javac -d Q5_GenericLeaderboard\bin Q5_GenericLeaderboard\src\*.java

# Run Supplied Main Method Demo (Mandatory Expected Output)
java -cp Q5_GenericLeaderboard\bin src.LeaderboardDemo

# Run Extended Verification Suite
java -cp Q5_GenericLeaderboard\bin src.LeaderboardExtendedTests
```
**Mandatory Expected Output:**
```
[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
```
**Extended Test Outcomes:**
- Non-destructive ranking: Original order preserved intact.
- Multi-tie resolution: Verified points $\to$ goal difference $\to$ team name alphabetical order.
- Subtraction overflow demo: Shows how integer subtraction underflows from $-4\times 10^9$ to $+294,967,296$, proving why `Integer.compare` is mandatory.

---

### Q6 – Hospital Emergency Triage (PriorityQueue)
```powershell
# Compile
javac -d Q6_PriorityQueue\bin Q6_PriorityQueue\src\*.java

# Run Supplied Triage Demo (Mandatory Expected Output)
java -cp Q6_PriorityQueue\bin src.Triage

# Run Triage Architecture Benchmark
java -cp Q6_PriorityQueue\bin src.TriageBenchmark
```
**Mandatory Expected Output:**
```
Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
```
**Independent 8-Patient Test Output:**
```
Ananya(S1) -> Sneha(S1) -> Vikram(S2) -> Sanjay(S2) -> Deepak(S3) -> Pooja(S3) -> Rahul(S4) -> Priya(S5) [100% Match]
```

---

### Q7 – Day-4 Design Challenge
```powershell
# Compile
javac -d Q7_DesignChallenge\bin Q7_DesignChallenge\OriginalSolution\*.java Q7_DesignChallenge\RedesignedSolution\*.java Q7_DesignChallenge\experiments\*.java

# Execute Benchmark & Validation
java -cp Q7_DesignChallenge\bin experiments.DesignChallengeBenchmark
```
**Outcome:**
- Output correctness: 100% Exact match.
- Latency reduction: **43.8% to 64.7%** reduction in execution time.
- Memory reduction: **95.9%** reduction in intermediate heap objects (from 490,000 to 20,000 objects).
- Prediction audit: Confirmed and defended against pre-implementation predictions.

---

## 5. Summary of Verified Experimental Results

| Question | Core Topic | Mandatory / Primary Output | Key Benchmark Finding | Status |
| :--- | :--- | :--- | :--- | :--- |
| **Q1** | String vs StringBuilder vs StringBuffer | Generated 10k tickets in 3 workloads | StringBuilder is 2.4x–3.7x faster than String (+); reduces objects by 95.9% | **PASSED** |
| **Q2** | ArrayList vs LinkedList vs ArrayDeque | Benchmarked at 1k, 10k, 100k elements | ArrayList random access is 383x faster than LinkedList at 100k | **PASSED** |
| **Q3** | HashSet Contract & Hashing | `Unique students: 2` | Missing hashCode creates duplicate entries; mutation makes resident object unreachable | **PASSED** |
| **Q4** | Map Comparison & Sorted Array | 100k lookups across 1k, 10k, 100k | HashMap/LinkedHashMap lookups < 0.07 µs; TreeMap excels at sorted reports | **PASSED** |
| **Q5** | Generics & Leaderboard | `[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]` | Comparator chaining handles 3-level ties; Integer.compare prevents overflow | **PASSED** |
| **Q6** | Hospital Emergency Triage | `Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)` | PriorityQueue processes 5k patients in < 1 ms; 5-FIFO queues offer O(1) ops | **PASSED** |
| **Q7** | Day-4 Design Challenge | Output equivalence + Performance Defense | Pre-sized StringBuilder cuts latency by up to 64.7% and cuts heap churn by 95.9% | **PASSED** |

---

## 6. Author Information
- **Student Name:** K VENKATA VAMSI GOUD
- **Register Number:** 192472021
- **Degree:** B.Tech Computer Science and Engineering
- **Institution:** SIMATS – Saveetha School of Engineering
