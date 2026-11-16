# Task 03: Resource Safety, Debugging and Robust Code

**Course:** CSA09 – Programming in Java  
**Student A:** K VENKATA VAMSI GOUD (Reg No: 192472021)  
**Student B:** [Student B Name] (Reg No: [Student B Register Number])  
**Institution:** SIMATS – Saveetha School of Engineering  
**Academic Year:** 2026–2027  

---

## 1. Problem Statement & Architecture
The Daily Sales Report Generator processes multiple regional store transaction CSV files, accumulates valid revenue figures, detects missing or corrupt files, and outputs a consolidated sales summary while guaranteeing that every opened OS file handle is safely closed.

---

## 2. Resource Management Analysis & Student B Contribution

### 2.1 Complete List of Opened Resources
For each store file processed, the following resources are instantiated:
1. `java.io.FileReader`: Binds to the underlying operating system file descriptor.
2. `java.io.BufferedReader`: Buffers the character input stream in a memory buffer (default 8 KB).
3. `java.io.FileWriter`: Binds to the destination summary report file descriptor.
4. `java.io.PrintWriter`: Provides formatted text printing capabilities over the `FileWriter`.

### 2.2 Manual Resource Closing (`ManualCloseReport.java`)
In traditional Java (pre-Java 7), closing resources requires verbose and error-prone `try-catch-finally` constructs:
- References must be declared outside the `try` block with `null` initializers so they are in scope within `finally`.
- Each `close()` invocation must be wrapped in its own separate `try-catch` block because `close()` throws checked `IOException`.
- If the first `reader.close()` throws an exception, subsequent `fr.close()` calls will be skipped unless nested defensive blocks are created.

### 2.3 Try-With-Resources & Reverse-Order Closing (`TryWithResourcesReport.java`)
Java 7 introduced the try-with-resources statement for any object implementing `java.lang.AutoCloseable`:
- Resources declared inside the parenthesized header `try (...)` are automatically closed at block exit.
- **Reverse-Order Closing Guarantee:** If resources are declared as `ResA` then `ResB`, the JVM closes `ResB` first, followed by `ResA`. This ensures dependent wrappers (e.g. `BufferedReader`) flush and terminate before the underlying raw resource (`FileReader`) is released.

### 2.4 Handling Suppressed Exceptions (`getSuppressed()`)
When an exception occurs inside a manual `try` block and another exception occurs inside `finally.close()`, the `finally` exception completely **shadows and discards** the primary business exception.
With try-with-resources, the JVM preserves the primary exception as the main throwable and attaches any secondary exceptions thrown during `close()` as **suppressed exceptions**. These can be inspected via `e.getSuppressed()`, as demonstrated in our test output:
```
[CATCH] Caught Primary Exception: PRIMARY FAILURE: Database transaction timed out!
Number of Suppressed Exceptions attached: 2
  -> Suppressed [1]: Secondary Close Failure on resource: AuditLogStream
  -> Suppressed [2]: Secondary Close Failure on resource: DatabaseChannel
```

---

## 3. Student A Contribution: Debugging Evidence & Failure Analysis

### 3.1 Failure Input Analysis (`store3_hyderabad_corrupt.csv`)
- Line 4 contained the corrupt string: `TXN304,CORRUPT_NOT_A_NUMBER`.
- During execution, `Double.parseDouble("CORRUPT_NOT_A_NUMBER")` threw `java.lang.NumberFormatException`.
- **Debugging Breakpoint Observation:**
  - Breakpoint set at `ManualCloseReport.java:77`.
  - Local variables at breakpoint: `line = "TXN304,CORRUPT_NOT_A_NUMBER"`, `parts = ["TXN304", "CORRUPT_NOT_A_NUMBER"]`, `storeTotal = 6750.50`, `validTxns = 3`.
  - Stepping into `Double.parseDouble()` immediately triggered the catch block.
  - The catch block logged the error: `[FAILED] Exception caught for file 'store3_hyderabad_corrupt.csv'`.
  - Execution successfully proceeded to `store4_mumbai.csv` without aborting the overall batch.

### 3.2 Resource Leak Comparison Table
| Behavior | Manual `try-finally` | Try-with-Resources | No Close (`NoResourceCloseDemo`) |
| :--- | :--- | :--- | :--- |
| **Normal Path** | Closed | Closed | Leaked until GC |
| **Exception in Try** | Closed (if written defensively) | Guaranteed Closed | Leaked until GC |
| **Exception in Close** | Can mask primary exception | Attached as Suppressed | N/A |
| **Code Verbosity** | High (20+ lines boilerplate) | Minimal (1 line per resource) | None (Dangerous) |

---

## 4. Joint Discussion: Limits of Try-With-Resources

**Why try-with-resources alone does not guarantee complete application robustness:**
1. **Business State Consistency:** Try-with-resources guarantees file handle cleanup, but does NOT roll back partial memory state or database mutations (e.g., if store 3 fails on line 4, the ₹6,750.50 from lines 1-3 must be explicitly rolled back or discarded).
2. **Resource Re-acquisition:** It does not handle transient network reconnects or retry logic.
3. **External Lock Release:** Locks or distributed semaphores not implementing `AutoCloseable` will remain held unless manually managed.

---

## 5. Alternative Concept & Complexity Analysis

### 5.1 Complexity for $f$ Store Files
- Let $f$ be the number of store files, and $m$ be the maximum transactions per file.
- **Time Complexity:**
  - Sequential processing: Each line is read and parsed once: $O(f \cdot m)$ time.
  - Resource close overhead: $O(f)$ close invocations.
  - Total Time: $O(f \cdot m)$ algorithmic time.
- **Space Complexity (File Handles):**
  - **Sequential Open (Current Implementation):** At most $1$ store file open + $1$ summary writer open = $\mathbf{O(1)}$ open file handles simultaneously.
  - **Concurrent Open (Naive parallel stream opening all files):** Up to $f + 1 = \mathbf{O(f)}$ simultaneous file handles, risking OS descriptor exhaustion for large $f$.
  - Auxiliary Memory: $O(1)$ buffer memory ($8\text{ KB}$ per active reader).
