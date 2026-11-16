# Programming in Java – Assignment 3 (CO3)

## Repository Details
- **Repository Name:** `CSA09-192472021-Assignment3`
- **Institution:** SIMATS – Saveetha School of Engineering
- **Department:** Department of Computer Science and Engineering
- **Course Code & Title:** CSA09 – Programming in Java
- **Academic Year:** 2026–2027

---

## Student Information

| Role | Student Name | Register Number | GitHub Username / Profile |
| :--- | :--- | :--- | :--- |
| **Student A** | K VENKATA VAMSI GOUD | **192472021** | `https://github.com/vamsi` |
| **Student B** | `[Student B Name]` | `[Student B Register Number]` | `[Student B GitHub Profile]` |

*Note: Student B fields are kept editable for university collaborative evaluation.*

---

## Development Environment & Toolchain
- **Java Version:** OpenJDK 17.0.21 (Eclipse Temurin 64-Bit Server VM)
- **Compiler:** `javac 17.0.21`
- **IDE Used:** Antigravity IDE / Visual Studio Code
- **Operating System:** Windows 11 Enterprise (x86_64)
- **Supporting Toolchain:** Python 3.11 (Matplotlib, python-docx, Pillow)

---

## Project Folder Structure

```
CSA09-192472021-Assignment3/
│
├── README.md                          # Master project documentation
├── Assignment3_Report.docx            # Professional comprehensive academic Word report
├── .gitignore                         # Git exclusion rules
│
├── Task01_ATM_Exception_Handling/     # Q1: Bank ATM Simulation (try/catch/finally)
│   ├── src/                           # Java source files
│   ├── test-cases/                    # 5 input test scenarios
│   ├── results/                       # Captured console logs
│   ├── screenshots/                   # Authentic terminal screenshots
│   └── analysis.md                    # Detailed theory & complexity analysis
│
├── Task02_Custom_Exceptions/          # Q2: Online Exam Portal (Chaining & Propagation)
│   ├── src/                           # 3-layer architecture & StorageDemo.java
│   ├── test-cases/                    # Test cases
│   ├── results/                       # Console logs & getCause() output
│   ├── screenshots/                   # Terminal screenshots
│   └── analysis.md                    # Exception hierarchy & analysis
│
├── Task03_Resource_Safety/            # Q3: Daily Sales Report (AutoCloseable)
│   ├── src/                           # ManualCloseReport & TryWithResourcesReport
│   ├── input/                         # Regional CSVs including corrupt data
│   ├── results/                       # Generated reports & logs
│   ├── screenshots/                   # Terminal screenshots
│   └── analysis.md                    # File descriptor analysis & debugging evidence
│
├── Task04_Thread_Lifecycle/           # Q4: Parallel Scanner Stations (Lifecycles)
│   ├── src/                           # Thread, Runnable, Daemon, ThreadPool
│   ├── results/                       # ten_run_results.csv & logs
│   ├── screenshots/                   # Terminal screenshots
│   └── analysis.md                    # Priority non-determinism & lifecycle states
│
├── Task05_Race_Condition/             # Q5: Cinema Seat Booking (Race & Synchronization)
│   ├── src/                           # Unsynchronized, Synchronized method & block
│   ├── results/                       # ten_run_race_comparison.csv, benchmark.csv
│   ├── screenshots/                   # Terminal screenshots & contention chart
│   └── analysis.md                    # Check-then-act race & intrinsic locks
│
├── Task06_Deadlock_Communication/     # Q6: Wallet Deadlock & Mess Token Buffer
│   ├── src/                           # Ordered locking, TokenCounter wait/notifyAll
│   ├── results/                       # Deadlock watchdog logs & token results
│   ├── screenshots/                   # Terminal screenshots
│   └── analysis.md                    # Coffman conditions & spurious wakeups
│
└── Task07_Concurrent_Processor/       # Q7: Semester Result Processor (10,000 files)
    ├── src/                           # ExecutorService, Callable, Future, Invariant
    ├── input/                         # 10,000 score files (9,700 valid, 200 corrupt, 100 missing)
    ├── results/                       # benchmark_results.csv & throughput charts
    ├── screenshots/                   # Terminal screenshots
    └── analysis.md                    # Amdahl's law, I/O saturation & defense
```

---

## Compilation & Execution Guide

Ensure Java 17 is available on your terminal. From the root directory `CSA09-192472021-Assignment3/`, run:

### Task 01: ATM Exception Handling
```powershell
# Compile
javac -d Task01_ATM_Exception_Handling/bin Task01_ATM_Exception_Handling/src/*.java

# Run Valid Withdrawal (Normal flow)
java -cp Task01_ATM_Exception_Handling/bin task01.ATMWithdrawalSimulation 1234567890 2000.00 Task01_ATM_Exception_Handling/card_reader.cfg

# Run Alternative (Scanner & Error Codes)
java -cp Task01_ATM_Exception_Handling/bin task01.ATMErrorCodeValidation 1234567890 1500.00 Task01_ATM_Exception_Handling/card_reader.cfg
```

### Task 02: Custom Exceptions & Exception Chaining
```powershell
# Compile
javac -d Task02_Custom_Exceptions/bin Task02_Custom_Exceptions/src/*.java

# Run Mandatory Complete-the-Code Program (StorageDemo)
java -cp Task02_Custom_Exceptions/bin task02.StorageDemo

# Run Full 3-Layer Exam Portal
java -cp Task02_Custom_Exceptions/bin task02.ExamPortalApp
```

### Task 03: Resource Safety & Debugging
```powershell
# Compile
javac -d Task03_Resource_Safety/bin Task03_Resource_Safety/src/*.java

# Run Try-With-Resources (AutoCloseable & Suppressed Exceptions)
java -cp Task03_Resource_Safety/bin task03.TryWithResourcesReport

# Run Manual Close Report (try-finally)
java -cp Task03_Resource_Safety/bin task03.ManualCloseReport
```

### Task 04: Thread Creation & Lifecycle
```powershell
# Compile
javac -d Task04_Thread_Lifecycle/bin Task04_Thread_Lifecycle/src/*.java

# Run Thread Inheritance & Runnable Simulations
java -cp Task04_Thread_Lifecycle/bin task04.ExtendingThread
java -cp Task04_Thread_Lifecycle/bin task04.ImplementingRunnable

# Run Student B 10-Run Priority Non-Determinism Experiment
java -cp Task04_Thread_Lifecycle/bin task04.RunTenTrials
```

### Task 05: Cinema Seat Booking & Race Conditions
```powershell
# Compile
javac -d Task05_Race_Condition/bin Task05_Race_Condition/src/*.java

# Run Mandatory Complete-the-Code Program (SeatBooking)
java -cp Task05_Race_Condition/bin task05.SeatBooking

# Run 10-Run Unsynchronized vs Synchronized Race Comparison
java -cp Task05_Race_Condition/bin task05.TenRunSeatRace

# Run 10,000 Contention Benchmark Suite
java -cp Task05_Race_Condition/bin task05.BookingBenchmark
```

### Task 06: Deadlock & Inter-Thread Communication
```powershell
# Compile
javac -d Task06_Deadlock_Communication/bin Task06_Deadlock_Communication/src/*.java

# Run Part A: Deadlock Detection & Ordered Lock Prevention
java -cp Task06_Deadlock_Communication/bin task06.WalletDeadlockDemo
java -cp Task06_Deadlock_Communication/bin task06.WalletOrderedLockDemo

# Run Part B: Mandatory TokenCounter (Circular wait/notifyAll buffer)
java -cp Task06_Deadlock_Communication/bin task06.TokenCounter
```

### Task 07: Concurrent Fault-Tolerant Processor (10,000 Files)
```powershell
# Compile
javac -d Task07_Concurrent_Processor/bin Task07_Concurrent_Processor/src/*.java

# Run Full Benchmark (Hand-checked 10 files + 10,000 files across 1, 2, 4, 8 threads)
java -Xmx2g -cp Task07_Concurrent_Processor/bin task07.Main
```

---

## Experimental Results Summary

| Task | Key Finding / Result | Status |
| :--- | :--- | :---: |
| **Q1 (ATM)** | Specific catch blocks executed as designed; `finally` executed in 100% of cases. Uncaught stack trace and unreachable catch compiler errors demonstrated. | **VERIFIED** |
| **Q2 (Exam Portal)** | Mandatory `StorageDemo` produced exact required output. 3-tier propagation, `getCause()`, and chained stack traces verified. | **VERIFIED** |
| **Q3 (Resource Safety)** | `store3_hyderabad_corrupt.csv` caught on line 4. Reverse-order resource cleanup and `e.getSuppressed()` demonstrated. | **VERIFIED** |
| **Q4 (Thread Lifecycle)** | 10-run CSV proves priorities do NOT guarantee execution order on modern preemptive OS schedulers (Max-priority won only 4/10 trials). | **VERIFIED** |
| **Q5 (Race Condition)** | Mandatory `SeatBooking` sold exactly 10 seats. Unsynchronized sold 79-100 seats (oversold). 10,000 contention benchmark recorded. | **VERIFIED** |
| **Q6 (Deadlock/Comm)** | Mandatory `TokenCounter` produced exact sum: 210. Watchdog trapped circular wait deadlock; ordered locking resolved deadlock. | **VERIFIED** |
| **Q7 (Processor)** | 10,000 files processed across 1, 2, 4, 8 threads. Peak throughput: 8,488.96 files/sec (2 threads). Invariant $9,700 + 300 = 10,000$ passed with 0 unhandled failures. | **VERIFIED** |

---

## GitHub Progressive Commit Plan

```bash
# Task 01 Commit
git add Task01_ATM_Exception_Handling/
git commit -m "feat(task01): implement ATM exception handling, test cases, and unreachable catch demo"

# Task 02 Commit
git add Task02_Custom_Exceptions/
git commit -m "feat(task02): implement 3-tier exam portal, custom exceptions, and StorageDemo.java"

# Task 03 Commit
git add Task03_Resource_Safety/
git commit -m "feat(task03): implement sales report generator, try-with-resources, and suppressed exceptions"

# Task 04 Commit
git add Task04_Thread_Lifecycle/
git commit -m "feat(task04): implement scanner station threads, runnable tasks, and 10-run priority benchmarks"

# Task 05 Commit
git add Task05_Race_Condition/
git commit -m "feat(task05): implement cinema seat booking, race condition demonstrations, and contention benchmarks"

# Task 06 Commit
git add Task06_Deadlock_Communication/
git commit -m "feat(task06): implement wallet deadlock detection, ordered lock prevention, and TokenCounter"

# Task 07 Commit
git add Task07_Concurrent_Processor/
git commit -m "feat(task07): implement concurrent fault-tolerant semester result processor for 10,000 files"

# Documentation & Report Commit
git add README.md Assignment3_Report.docx .gitignore
git commit -m "docs: finalize academic Word report, master complexity tables, and repository README"
```
