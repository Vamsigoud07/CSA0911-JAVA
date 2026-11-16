# Task 06: Deadlock and Inter-Thread Communication

**Course:** CSA09 – Programming in Java  
**Student A:** K VENKATA VAMSI GOUD (Reg No: 192472021)  
**Student B:** [Student B Name] (Reg No: [Student B Register Number])  
**Institution:** SIMATS – Saveetha School of Engineering  
**Academic Year:** 2026–2027  

---

## 1. Problem Statement & Architecture
This task explores two core multithreading synchronization challenges:
- **Part A (Hostel Wallet Transfers):** Investigates resource deadlock across account transfers and proves mathematically how consistent global lock ordering eliminates deadlock.
- **Part B (Mess Token Producer & Consumer):** Implements coordinated inter-thread signaling using `wait()`, `notifyAll()`, and a circular bounded buffer, demonstrating why conditional checks must reside inside `while` loops.

---

## 2. Part A: Deadlock Analysis & Student B Contribution

### 2.1 The Four Coffman Conditions for Deadlock
Deadlock can occur if and only if all four conditions hold simultaneously:
1. **Mutual Exclusion:** Only one thread can hold a given wallet lock at a time.
2. **Hold and Wait:** Thread 1 holds `WalletA` while waiting to acquire `WalletB`; Thread 2 holds `WalletB` while waiting to acquire `WalletA`.
3. **No Preemption:** The JVM runtime cannot forcibly confiscate an intrinsic monitor lock held by a thread.
4. **Circular Wait:** A closed dependency loop exists: Thread 1 $\rightarrow$ Wallet B $\rightarrow$ Thread 2 $\rightarrow$ Wallet A $\rightarrow$ Thread 1.

### 2.2 Deadlock Detection Watchdog Output (`WalletDeadlockDemo.java`)
```
[Thread-1] ACQUIRED lock on Wallet-A
[Thread-1] Waiting to acquire lock on Wallet-B (CIRCULAR WAIT)
[Thread-2] ACQUIRED lock on Wallet-B
[Thread-2] Waiting to acquire lock on Wallet-A (CIRCULAR WAIT)
[DEADLOCK WATCHDOG ALERT] Deadlock Detected!
  Thread-1 State: BLOCKED (Holding Wallet-A, Waiting for Wallet-B)
  Thread-2 State: BLOCKED (Holding Wallet-B, Waiting for Wallet-A)
```

### 2.3 Deadlock Elimination via Consistent Lock Ordering (`WalletOrderedLockDemo.java`)
By enforcing a strict, global lock acquisition hierarchy (e.g., lexicographical comparison of Wallet IDs):
```java
if (from.getId().compareTo(to.getId()) < 0) {
    firstLock = from;  secondLock = to;
} else {
    firstLock = to;    secondLock = from;
}
synchronized (firstLock) {
    synchronized (secondLock) { ... }
}
```
Because all threads acquire lower-ordered locks before higher-ordered locks, a circular wait chain is structurally impossible. Both transfers complete with zero contention stalls.

---

## 3. Part B: Producer-Consumer Signaling & Student A Contribution

### 3.1 Producer-Consumer Coordination Primitives
- **`wait()` inside synchronized block:** Calling `wait()` releases the monitor lock on `this` and places the thread into the object's wait set in the `WAITING` state. It can only be called from within a synchronized context.
- **`notify()` vs `notifyAll()`:** `notify()` awakens a single arbitrary thread from the wait set. If the wrong thread is awakened (e.g. a producer wakes another producer when the buffer is full), system-wide deadlock (thread starvation) ensues. `notifyAll()` awakens all waiting threads, allowing the CPU scheduler and condition checks to safely advance progress.
- **`sleep()` vs `wait()`:** `Thread.sleep(t)` pauses the thread but **retains all held locks**, blocking other threads. `wait()` atomically **releases the lock** and yields CPU.

### 3.2 Verification of Mandatory Program: `TokenCounter.java`
Completed TODO 1 and TODO 2 with circular buffer arithmetic:
```java
// In produce:
while (count == buffer.length) wait();
buffer[in] = token; in = (in + 1) % buffer.length; count++; notifyAll();

// In consume:
while (count == 0) wait();
int token = buffer[out]; out = (out + 1) % buffer.length; count--; notifyAll(); return token;
```
Execution Output:
```
Sum of tokens collected: 210 (expected 210)
```

### 3.3 The Spurious Wakeup Flaw: Why `while` is Required (`TokenCounterIfDemo.java`)
Replacing `while` with `if` creates a race condition:
```java
if (count == buffer.length) wait(); // FLAW!
```
Under POSIX/JVM threads, spurious wakeups (wakeups without explicit notify) can occur. Furthermore, if Thread A signals `notifyAll()`, two consumer threads may wake up. Consumer 1 consumes the item, reducing count to 0. If Consumer 2 was using `if`, it resumes execution without re-verifying `count > 0`, attempting to read from an empty buffer (buffer underflow). A `while` loop guarantees the condition is re-evaluated upon every wake.

---

## 4. Alternative Concepts & Complexity Analysis

### 4.1 Alternatives Implemented
1. **Part A Alternative (`WalletTryLockDemo.java`):** Uses `ReentrantLock.tryLock(timeout)` with exponential backoff.
2. **Part B Alternative (`ArrayBlockingQueueDemo.java`):** High-level concurrency utility managing circular buffering and condition variables internally.

### 4.2 Comparative Analysis Table
| Aspect | Low-Level `wait()` / `notifyAll()` (`TokenCounter`) | High-Level `ArrayBlockingQueue` (`ArrayBlockingQueueDemo`) |
| :--- | :--- | :--- |
| **Simplicity** | Requires manual circular index management, count tracking, loop-checks. | One-line `queue.put(item)` and `queue.take()`. |
| **Error-Proneness** | High: Risk of deadlocks if `notify()` is misused or `while` is replaced by `if`. | Extremely low: Encapsulated and proven concurrency primitives. |
| **Condition Queues**| Single wait-set for both empty and full signals. | Two separate Condition queues (`notEmpty`, `notFull`), preventing wake storms. |
| **Time Complexity** | $O(1)$ amortized insert/delete; $O(k)$ notify broadcast overhead. | $O(1)$ insert and remove. |
| **Space Complexity** | $O(c)$ bounded array capacity ($c=5$). | $O(c)$ bounded array capacity ($c=5$). |
| **Recommendation** | Educational foundation; **ArrayBlockingQueue is strongly recommended for production applications.** |
