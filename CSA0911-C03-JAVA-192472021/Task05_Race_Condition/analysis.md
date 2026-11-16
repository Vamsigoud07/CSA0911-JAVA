# Task 05: Race Conditions in a Cinema Seat-Booking System

**Course:** CSA09 – Programming in Java  
**Student A:** K VENKATA VAMSI GOUD (Reg No: 192472021)  
**Student B:** [Student B Name] (Reg No: [Student B Register Number])  
**Institution:** SIMATS – Saveetha School of Engineering  
**Academic Year:** 2026–2027  

---

## 1. Problem Statement & Architecture
The Cinema Seat-Booking System simulates high-concurrency ticket reservations where 100 customer threads compete for 10 cinema seats. Without proper concurrency control, a classic **check-then-act** race condition occurs, resulting in double bookings and severe overselling.

---

## 2. Theory of Race Conditions & Student A Contribution

### 2.1 The Check-Then-Act Race Condition
In unsynchronized code:
```java
if (seatsRemaining > 0) {   // 1. Check (Read state)
    seatsRemaining--;       // 2. Act   (Mutate state)
    seatsSold++;
}
```
Because the check and mutation are not atomic, multiple threads read `seatsRemaining == 1` simultaneously before any single thread decrements it.

```
Thread 1:  [Reads remaining = 1] --------------> [Decrements to 0, Sells seat]
Thread 2:     [Reads remaining = 1] -----------> [Decrements to -1, DOUBLE BOOKING!]
Thread 3:        [Reads remaining = 1] --------> [Decrements to -2, TRIPLE BOOKING!]
```

### 2.2 Ten-Run Race Condition Experimental Evidence
Ten trials were executed with 100 competing threads against 10 seats using `TenRunSeatRace.java`:
- **Unsynchronized Execution:** Consistently sold between 79 and 100 seats (massive overselling in 100% of trials).
- **Synchronized Execution:** Consistently sold **exactly 10 seats** (100% correctness).

### 2.3 Verification of Mandatory Program: `SeatBooking.java`
Execution of the completed `SeatBooking.java` produced the exact expected output:
```
Seats sold: 10
```

---

## 3. Synchronization Mechanisms & Student B Contribution

### 3.1 Synchronized Method vs Synchronized Block
1. **Synchronized Method (`SeatBookingSynchronizedMethod.java`):**
   - Automatically acquires the intrinsic monitor lock of `this` upon entry and releases it upon exit.
   - Simple syntax, but locks the entire method scope, preventing concurrent execution of non-critical preparation code.
2. **Synchronized Block (`SeatBookingSynchronizedBlock.java`):**
   - Allows fine-grained locking over an explicit lock object: `synchronized(lock) { ... }`.
   - Minimizes critical section hold time, allowing logging, authentication, and validation to run concurrently.

### 3.2 Intrinsic Locks & Static Synchronization
- **Intrinsic Lock (Monitor):** Every Java object has an internal monitor. A thread that acquires the monitor has exclusive access to any block synchronized on that object.
- **Static Synchronization:** Synchronizing a static method locks the `Class` object (`SeatBooking.class`) rather than an instance. All instances share the single class lock.

### 3.3 High-Contention Benchmark (10,000 Attempts)
Benchmarked under high contention using `BookingBenchmark.java`:
| Synchronization Strategy | Total Attempts | Seats Available | Final Seats Sold | Total Execution Time (ms) | Average Time per Attempt (ns) |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **Synchronized Method** | 10,000 | 500 | 500 | 777 ms | 77,791 ns |
| **Synchronized Block** | 10,000 | 500 | 500 | 761 ms | 76,138 ns |
| **ReentrantLock** | 10,000 | 500 | 500 | 799 ms | 79,933 ns |
| **AtomicInteger (CAS)** | 10,000 | 500 | 500 | 765 ms | 76,526 ns |

---

## 4. Alternative Implementation & Complexity Analysis

### 4.1 Lock-Free AtomicInteger CAS (`AtomicSeatBooking.java`)
Uses hardware-level atomic instructions (`CMPXCHG` on x86-64) via `AtomicInteger.compareAndSet()`:
```java
while (true) {
    int current = remaining.get();
    if (current <= 0) return false;
    if (remaining.compareAndSet(current, current - 1)) {
        sold.incrementAndGet();
        return true;
    }
}
```

### 4.2 Comparative Analysis Table
| Dimension | `synchronized` Keyword | `ReentrantLock` | `AtomicInteger` (CAS) |
| :--- | :--- | :--- | :--- |
| **Mechanism** | JVM intrinsic monitor lock. | User-space lock via `AbstractQueuedSynchronizer`. | Hardware-level atomic test-and-set instruction. |
| **Thread Blocking** | Thread enters `BLOCKED` state; kernel context switch. | Thread queued in FIFO wait queue. | Lock-free; thread spins in user space (CAS loop). |
| **Fairness Policy**| Unfair (barging allowed). | Configurable (Fair / Non-fair). | N/A (Lock-free contention). |
| **Time Complexity** | $O(1)$ amortized; worst-case blocked on contention. | $O(1)$ amortized lock acquisition. | $O(1)$ expected; $O(k)$ retries under extreme contention. |
| **Space Complexity** | $O(1)$ auxiliary memory (header bits). | $O(1)$ auxiliary object reference. | $O(1)$ auxiliary memory (single 32-bit atomic word). |
| **Recommendation** | **Synchronized block for simple critical sections; AtomicInteger for single numeric counters under moderate contention.** |
