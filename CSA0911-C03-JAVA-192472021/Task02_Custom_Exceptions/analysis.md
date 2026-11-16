# Task 02: User-Defined Exceptions and Propagation in an Online Examination Portal

**Course:** CSA09 – Programming in Java  
**Student A:** K VENKATA VAMSI GOUD (Reg No: 192472021)  
**Student B:** [Student B Name] (Reg No: [Student B Register Number])  
**Institution:** SIMATS – Saveetha School of Engineering  
**Academic Year:** 2026–2027  

---

## 1. Problem Statement & Architecture
The Online Examination Portal simulates answer-script submission across a three-tier architecture:
1. **Controller Layer (`ExamController`):** Front-facing boundary; receives client requests and translates exceptions into HTTP status codes.
2. **Service Layer (`ExamService`):** Business logic; verifies enrollment status, submission deadlines, and maximum file size limits.
3. **Storage Layer (`ExamStorage`):** Physical persistence; writes answer-scripts to disk, intercepting low-level OS/disk failures (`IOException`) and rethrowing domain-specific chained exceptions (`SubmissionStorageException`).

---

## 2. Custom Exception Hierarchy & Student A Contribution

### 2.1 Hierarchy Class Diagram
```
                       java.lang.Throwable
                               |
                       java.lang.Exception
                               |
            +------------------+------------------+
            |                                     |
       ExamException                      java.lang.RuntimeException
      (Checked Base)                              |
            |                         FileSizeExceededException
     +------+------+                         (Unchecked)
     |             |
ExamWindow-   StudentNot-
Closed-       Registered-
Exception     Exception
(Checked)     (Checked)
            |
   SubmissionStorageException
   (Checked - Chained to IOException)
```

### 2.2 Checked vs Unchecked Design Rationale
- **`ExamException`, `ExamWindowClosedException`, `StudentNotRegisteredException`, `SubmissionStorageException` (Checked):** These represent anticipated business failure conditions and environmental storage constraints from which the client or controller can recover (e.g., prompt the student to register, display deadline expiry message, or failover to secondary cloud storage).
- **`FileSizeExceededException` (Unchecked - `RuntimeException`):** Client-side validation failure. Sending a 12 MB file when the specification mandates 5 MB represents an illegal argument / contract violation, best handled as an unchecked exception.

### 2.3 Exception Propagation Path
When a disk failure occurs in `ExamStorage`:
1. `ExamStorage.persistSubmission()` throws `IOException`.
2. Caught inside `ExamStorage` and wrapped into `SubmissionStorageException` (passing the `IOException` as the cause).
3. Propagates through `ExamService.processSubmission()` via `throws SubmissionStorageException`.
4. Handled in `ExamController.submitExam()` catch block, extracting high-level message and root cause.

---

## 3. Mandatory "Complete the Code" Program: `StorageDemo.java`

### 3.1 Solution Implementation
In `StorageDemo.java`, TODO 1 was completed as:
```java
throw new SubmissionStorageException("Could not store " + fileName, e);
```

### 3.2 Verification of Exact Required Output
```
Saved answer01.pdf
Error: Could not store answer02.pdf
Cause: Disk quota exceeded
```

### 3.3 What Happens to `getCause()` If `IOException` Is Not Passed?
If the constructor `new SubmissionStorageException("Could not store " + fileName)` is invoked without the second parameter `e`, the internal `cause` field remains `null`. Consequently:
- `e.getCause()` returns `null`.
- Calling `e.getCause().getMessage()` produces a fatal `NullPointerException`.
- In enterprise production environments, the operations team completely loses the diagnostic context (they cannot tell whether the error was due to disk quota exceeded, access denied, or hardware failure).

---

## 4. Student B Evidence: Failure Scenarios & Chained Traces

### 4.1 Summary of Tested Scenarios
| Scenario ID | Student Reg No | Exam ID | File Name | File Size | Injected Failure | Captured Exception | HTTP Status Code |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **S1 (Normal)** | `192472021` | `CSA09-CO3` | `vamsi_answersheet.pdf` | 2 MB | None | None (Success) | 200 OK |
| **S2 (Unregistered)**| `192479999` | `CSA09-CO3` | `guest_answersheet.pdf` | 1 MB | Unregistered RegNo | `StudentNotRegisteredException` | 403 Forbidden |
| **S3 (Window Closed)**| `192472021` | `CSA09-CO3` | `vamsi_late_sub.pdf` | 1 MB | Window Expired | `ExamWindowClosedException` | 410 Gone |
| **S4 (Oversized File)**| `192472021` | `CSA09-CO3` | `huge_recording.pdf` | 12 MB | > 5 MB limit | `FileSizeExceededException` | 413 Payload Too Large |
| **S5 (Storage Failure)**| `192472021` | `CSA09-CO3` | `disk_full_answersheet.pdf`| 1 MB | Disk Quota Exceeded | `SubmissionStorageException` (Chained) | 500 Internal Error |

### 4.2 Full Chained Stack Trace Output
From `ChainedVsUnchainedDemo.java`:
```
task02.SubmissionStorageException: Could not store answer script
	at task02.ChainedVsUnchainedDemo.simulateChainedFailure(ChainedVsUnchainedDemo.java:49)
	at task02.ChainedVsUnchainedDemo.main(ChainedVsUnchainedDemo.java:28)
Caused by: java.io.IOException: Kernel error: NFS mount point timed out [EIO 5]
	at task02.ChainedVsUnchainedDemo.simulateChainedFailure(ChainedVsUnchainedDemo.java:46)
	... 1 more
```

---

## 5. Alternative Concept & Complexity Analysis

### 5.1 Alternative: Unchecked Runtime Exceptions (`UncheckedExamDemo.java`)
In the alternative implementation, all domain exceptions extend `RuntimeException`.
```java
class UncheckedExamException extends RuntimeException { ... }
```
Methods in Controller, Service, and Storage do not require explicit `throws` clauses.

### 5.2 Comparative Analysis Table
| Dimension | Checked Exceptions (`ExamPortalApp`) | Unchecked Exceptions (`UncheckedExamDemo`) |
| :--- | :--- | :--- |
| **Method Signatures** | Verbose: Requires explicit `throws` declarations on every intermediary layer. | Clean: Signatures remain minimal and decouple callers from callee details. |
| **Compile-Time Safety** | High: Compiler verifies all checked failures are handled. | Low: Unhandled exceptions bubble silently, risking unhandled runtime crashes. |
| **Stack Trace Capture Overhead** | $O(d)$ time where $d$ is call stack depth upon instantiation. | Identical: $O(d)$ time to populate stack trace frames. |
| **Memory Overhead** | $O(d)$ space per exception; chaining introduces $O(k \cdot d)$ references where $k$ is chain length. | Identical: $O(d)$ space per exception instance. |
| **Time Complexity** | Throw/Catch: $O(d)$ where $d$ is call-stack depth; Normal flow: $O(1)$. | Throw/Catch: $O(d)$; Normal flow: $O(1)$. |
| **Space Complexity** | $O(d)$ auxiliary stack frame trace memory. | $O(d)$ auxiliary stack frame trace memory. |
| **Recommendation** | **Checked for recoverable business limits (window closed, student registration); Unchecked for programming/contract errors (file size overflow).** |
