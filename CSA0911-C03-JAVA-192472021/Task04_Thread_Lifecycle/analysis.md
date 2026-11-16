# Task 04: Thread Creation and the Thread Life Cycle

**Course:** CSA09 – Programming in Java  
**Student A:** K VENKATA VAMSI GOUD (Reg No: 192472021)  
**Student B:** [Student B Name] (Reg No: [Student B Register Number])  
**Institution:** SIMATS – Saveetha School of Engineering  
**Academic Year:** 2026–2027  

---

## 1. Problem Statement & Objectives
The task requires implementing parallel optical answer-script scanning across four concurrent stations (Station-1 to Station-4) using two fundamental Java multithreading approaches:
1. Extending `java.lang.Thread` (`ExtendingThread.java`).
2. Implementing `java.lang.Runnable` (`ImplementingRunnable.java`).

The system explores thread lifecycle transitions, scheduling non-determinism, priority hints, daemon worker behavior, and worker thread pool architectures.

---

## 2. Theoretical Architecture & Student A Evidence

### 2.1 Thread Life-Cycle State Transitions
A Java thread operates within six well-defined states defined in `java.lang.Thread.State`:

```
      [NEW]
        |  thread.start()
        v
   [RUNNABLE] <-----------------------+
     |   ^                            |
     |   | scheduler context switch   |
     |   v                            |
     |  (Running on CPU)              |
     |                                |
     +---> [BLOCKED] -----------------+ (Lock acquired)
     |      (Waiting for monitor lock)
     |
     +---> [WAITING] -----------------+ (notify() / join() complete)
     |      (wait(), join())
     |
     +---> [TIMED_WAITING] -----------+ (sleep(t), wait(t), timeout elapsed)
     |      (Thread.sleep(ms))
     |
     v  run() finishes / uncaught exception
  [TERMINATED]
```

### 2.2 Extending `Thread` vs Implementing `Runnable`
| Feature | Extending `Thread` | Implementing `Runnable` |
| :--- | :--- | :--- |
| **Inheritance** | Consumes Java's single class inheritance (`extends Thread`). | Preserves inheritance (`implements Runnable, extends BaseDomain`). |
| **Separation of Concerns** | Tightly couples the task workload with the OS execution engine. | Decouples task logic (`Runnable`) from thread execution mechanism. |
| **Thread Pool Compatibility** | Incompatible with standard executors (`ExecutorService`). | Directly acceptable by `ExecutorService.execute(Runnable)`. |
| **State Sharing** | Each thread is an isolated object instance; sharing is awkward. | A single `Runnable` task instance can be shared across multiple threads. |
| **Academic Recommendation**| Acceptable only when customizing core thread behavior. | **Industry standard best practice.** |

### 2.3 `start()` vs `run()` and the Role of `join()`
- **`start()`:** Allocates a native OS thread stack (typically 1 MB), invokes the OS thread creation syscall, transitions state from `NEW` to `RUNNABLE`, and schedules the `run()` method on the newly spawned thread asynchronously.
- **`run()`:** Ordinary method call. Invoking `t.run()` directly executes code synchronously on the caller's thread (e.g. `main`), defeating multithreading completely.
- **`join()`:** Causes the calling thread to enter the `WAITING` state until the target thread terminates. Essential for barrier synchronization before aggregating results.

---

## 3. Experimental Evidence & Student B Contribution

### 3.1 Ten-Run Thread Scheduling & Priority Experiment
Stations were configured with varying priorities:
- **Station-1:** Priority 1 (`MIN_PRIORITY`)
- **Station-2:** Priority 5 (`NORM_PRIORITY`)
- **Station-3:** Priority 5 (`NORM_PRIORITY`)
- **Station-4:** Priority 10 (`MAX_PRIORITY`)

Ten independent runs were executed using `RunTenTrials.java`. Raw results recorded in `ten_run_results.csv`:

| Trial # | Run Time (ms) | 1st Finished | 2nd Finished | 3rd Finished | 4th Finished | Did Max-Priority (Station-4) Win? |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| 1 | 179 | Station-1 | Station-4 | Station-3 | Station-2 | **NO** |
| 2 | 175 | Station-3 | Station-1 | Station-4 | Station-2 | **NO** |
| 3 | 180 | Station-4 | Station-1 | Station-3 | Station-2 | **YES** |
| 4 | 174 | Station-1 | Station-4 | Station-3 | Station-2 | **NO** |
| 5 | 181 | Station-1 | Station-4 | Station-3 | Station-2 | **NO** |
| 6 | 177 | Station-3 | Station-2 | Station-4 | Station-1 | **NO** |
| 7 | 181 | Station-4 | Station-2 | Station-3 | Station-1 | **YES** |
| 8 | 177 | Station-4 | Station-3 | Station-2 | Station-1 | **YES** |
| 9 | 177 | Station-4 | Station-3 | Station-1 | Station-2 | **YES** |
| 10 | 185 | Station-1 | Station-4 | Station-2 | Station-3 | **NO** |

### 3.2 Key Scientific Observation
**Thread priorities do NOT guarantee execution order.**
Across 10 runs, Station-4 (Priority 10) finished first in only 4 out of 10 trials (40%). In 4 trials, Station-1 (Priority 1) finished first despite having the lowest priority. Modern OS schedulers (such as the Windows preemptive thread scheduler) use dynamic priority boosting, time slicing, and core affinity algorithms, treating Java priority settings merely as non-binding hints.

### 3.3 Daemon Thread Observation (`DaemonScannerDemo.java`)
- The background `HardwareDiagnosticsDaemon` ran an infinite diagnostic loop (`while (true)`).
- When the user scanner thread completed its three batches, the `main` thread exited.
- The JVM terminated immediately without waiting for the daemon thread, confirming that daemon threads do not prevent JVM shutdown.

---

## 4. Alternative Implementation & Complexity Analysis

### 4.1 Fixed Thread Pool (`ThreadPoolScanner.java`)
Instead of creating ephemeral `Thread` instances for every batch, an `ExecutorService` maintains a fixed pool of $p$ reusable worker threads.

### 4.2 Comparative Analysis Table
| Dimension | Manual Thread Creation (`ExtendingThread`) | Fixed Thread Pool (`ThreadPoolScanner`) |
| :--- | :--- | :--- |
| **Creation Cost** | High: Allocates 1 MB native stack + OS thread descriptor per task. | Low: Reuses existing threads from pool; zero per-task creation overhead. |
| **Workload Distribution** | Static: Each station thread assigned a fixed batch upfront. | Dynamic: Tasks placed in `BlockingQueue`; workers fetch next task when free. |
| **Memory Footprint** | $O(n)$ threads $\rightarrow n \times 1\text{ MB}$ memory; risks `OutOfMemoryError: unable to create new native thread`. | Bounded $O(p)$ threads $\rightarrow p \times 1\text{ MB}$ memory regardless of $n$. |
| **Time Complexity** | $O(n/p)$ execution time, plus $O(n)$ thread creation overhead. | $O(n/p)$ execution time, zero thread creation overhead during run. |
| **Space Complexity** | $O(n)$ thread stack frames. | $O(p)$ thread stack frames + $O(n)$ task descriptor references in queue. |
| **Recommendation** | Unsuitable for production workloads; **Fixed Thread Pool is the industry standard.** |
