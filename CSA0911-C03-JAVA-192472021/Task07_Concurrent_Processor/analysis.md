# Task 07: Design a Concurrent, Fault-Tolerant Processor from First Principles

**Course:** CSA09 – Programming in Java  
**Student A:** K VENKATA VAMSI GOUD (Reg No: 192472021)  
**Student B:** [Student B Name] (Reg No: [Student B Register Number])  
**Institution:** SIMATS – Saveetha School of Engineering  
**Academic Year:** 2026–2027  

---

## 1. Problem Statement & System Architecture
Task 07 is the highest-weightage assignment task (10 Marks). It requires engineering an industrial-grade, concurrent, fault-tolerant Semester Result Processing System from first principles.
The system is subjected to a massive batch workload of **10,000 student answer-script score files** with controlled injected anomalies:
- **9,700 Valid files** on disk.
- **200 Injected corrupt files** (malformed columns, non-numeric values, out-of-range marks).
- **100 Missing files** referenced in the batch but absent from the filesystem.
- **Deterministic Dataset:** Seed = 42.

---

## 2. Theoretical Architecture & Student A Evidence

### 2.1 Core Concurrency Primitives
- **`ExecutorService`:** Decouples task submission from task execution threads. Rather than incurring the catastrophic overhead of spawning 10,000 OS threads, an executor manages a pool of $p$ reusable worker threads backed by an internal `LinkedBlockingQueue`.
- **`Callable<V>` vs `Runnable`:** `Callable<StudentResult>` returns a computed result (`StudentResult`) upon completion and is declared to throw checked exceptions (`throws Exception`), making it ideal for fault-tolerant I/O operations.
- **`Future<V>`:** Represents the deferred result of an asynchronous computation. Calling `future.get()` blocks until completion; if the task threw an exception, `get()` wraps it in an `ExecutionException`, enabling granular failure trapping without disrupting sister tasks.
- **`ConcurrentHashMap` & `AtomicInteger` Justification:** Provides lock-free or striped bucket-level synchronization (`CAS` instructions). Eliminates the global bottleneck of `Collections.synchronizedMap` where all concurrent threads serialize on a single lock.

### 2.2 System Class Design Diagram
```
              +--------------------------+
              |           Main           |
              +--------------------------+
                           |
                           v
              +--------------------------+
              |     ResultProcessor      |
              +--------------------------+
               |                       |
       submits |                       | collects
               v                       v
      +-----------------+      +-----------------+
      |   ScriptTask    |----->| Future<Student> |
      | (Callable<Res>) |      +-----------------+
      +-----------------+               |
               |                        v
               | (throws)      +--------------------+
               v               | ProcessingSummary  |
      +--------------------+   | (Atomic Counters & |
      | ResultFileException|   | ConcurrentHashMap) |
      +--------------------+   +--------------------+
```

---

## 3. Implementation, Benchmarking & Student B Evidence

### 3.1 Stage 1: Hand-Checked Verification (10 Files)
Before full-scale benchmarking, a 10-file hand-checked dataset (7 valid, 2 corrupt, 1 missing) was executed:
- **Submitted:** 10 files
- **Successful:** 7 files
- **Failed:** 3 files
- **Invariant Verified:** $\text{Successful (7)} + \text{Failed (3)} = \text{Total Submitted (10)} \rightarrow$ **TRUE**.

### 3.2 Stage 2: Full 10,000 File Benchmark Across 1, 2, 4, 8 Threads
Benchmarked on Temurin OpenJDK 17.0.21 on Windows x86-64:

| Thread Count | Total Submitted | Successful Files | Failed Files | Execution Time (ms) | Throughput (Files / Sec) | Invariant Check ($S + F == T$) |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **1 Thread** | 10,000 | 9,700 | 300 | 218,602 ms | 45.75 files/sec | **PASSED [OK]** |
| **2 Threads** | 10,000 | 9,700 | 300 | 1,178 ms | 8,488.96 files/sec | **PASSED [OK]** |
| **4 Threads** | 10,000 | 9,700 | 300 | 1,289 ms | 7,757.95 files/sec | **PASSED [OK]** |
| **8 Threads** | 10,000 | 9,700 | 300 | 1,505 ms | 6,644.52 files/sec | **PASSED [OK]** |

---

## 4. Joint Engineering Evaluation & Defense

### 4.1 Optimal Thread Pool Recommendation
Based on local experimental measurements:
- **Optimal Configuration:** **2 Worker Threads** achieved the peak throughput of **8,488.96 files/second** (1,178 ms duration).
- **Amdahl's Law & Core Saturation:** Moving from 1 to 2 threads resulted in a massive speedup as disk read latency overlapped across cores.
- **Why More Threads (4 and 8) Did Not Increase Throughput:** 
  1. **Disk I/O and OS File Table Contention:** All 10,000 files reside on the same local storage volume. As 8 threads simultaneously issue file read syscalls, filesystem metadata locking and disk queue serialization dominate over CPU computation.
  2. **Worker Context-Switch Overhead:** Task execution time (~0.1 ms per CSV row) is so brief that thread scheduling overhead across 8 threads begins to exceed actual computational work.

### 4.2 Strict Correctness & Invariant Validation
Across all four benchmark runs, **every single file was accounted for**:
$$\text{Successful (9,700)} + \text{Failed (300)} = \text{Total Submitted (10,000)}$$
Unhandled failures = **0**. The system maintained 100% fault-tolerant continuity.

---

## 5. Alternative Concept & Complexity Analysis

### 5.1 Comparing ExecutorService vs Parallel Streams vs Thread-per-File
1. **`ExecutorService` (`ResultProcessor.java`):** Optimal bounded concurrency; configurable thread pool; robust error handling per future.
2. **Parallel Streams (`ParallelStreamResultProcessor.java`):** Declarative and concise; however, relies on JVM-wide `ForkJoinPool.commonPool()`, making it vulnerable to thread starvation if mixed with blocking I/O.
3. **One Thread Per File (`ThreadPerFileResultProcessor.java`):** Dangerous anti-pattern. Spawning 10,000 distinct threads requires $10,000 \times 1\text{ MB} = 10\text{ GB}$ stack memory, triggering `OutOfMemoryError: unable to create new native thread`.

### 5.2 Comparative Analysis Table
| Dimension | Fixed ExecutorService (`ResultProcessor`) | Parallel Stream (`ParallelStreamResultProcessor`) | Thread-per-File (`ThreadPerFileResultProcessor`) |
| :--- | :--- | :--- | :--- |
| **Throughput** | High (8,488 files/sec) | High (shared pool) | Extremely poor (context switch thrashing) |
| **Memory Footprint** | $O(p)$ threads ($p \times 1\text{ MB}$) | $O(\text{cores})$ threads | $O(n)$ threads ($n \times 1\text{ MB} \rightarrow 10\text{ GB}$ crash risk) |
| **Fault Isolation** | High: `Future.get()` intercepts `ExecutionException` per task. | Moderate: Exceptions inside lambda abort stream unless wrapped. | Poor: Uncaught thread exceptions dump to stderr. |
| **Algorithmic Time**| $O(n / p)$ | $O(n / \text{cores})$ | $O(n)$ execution + $O(n)$ spawn latency |
| **Algorithmic Space**| $O(p)$ thread stacks + $O(n)$ queue descriptors | $O(\text{cores})$ thread stacks | $O(n)$ thread stacks |
| **Recommendation** | **Industry gold standard for concurrent batch ingestion.** | Suitable for in-memory CPU transformations. | **Strictly prohibited in enterprise production.** |
