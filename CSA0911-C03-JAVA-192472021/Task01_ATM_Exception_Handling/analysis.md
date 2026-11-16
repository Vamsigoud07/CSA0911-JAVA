# Task 01: Handling Failure in a Bank ATM Simulation

**Course:** CSA09 – Programming in Java  
**Student A:** K VENKATA VAMSI GOUD (Reg No: 192472021)  
**Student B:** [Student B Name] (Reg No: [Student B Register Number])  
**Institution:** SIMATS – Saveetha School of Engineering  
**Academic Year:** 2026–2027  

---

## 1. Problem Statement & Objectives
The objective of this task is to design, implement, and analyze an end-to-end Bank ATM withdrawal simulation in Java demonstrating robust exception handling. The system processes account numbers, withdrawal amounts, verifies card hardware configuration files, validates positive denominations, and enforces account balance constraints using structured `try`, `catch`, multi-catch, and `finally` blocks.

---

## 2. Theoretical Architecture & Student A Evidence

### 2.1 Java Throwable Hierarchy Relevant to ATM
In Java, all runtime errors and exceptions inherit from `java.lang.Throwable`. The hierarchy branches into `Error` (serious system-level conditions that applications should not catch) and `Exception` (conditions that a reasonable application might want to catch).

```
                       java.lang.Throwable
                               |
            +------------------+------------------+
            |                                     |
     java.lang.Error                     java.lang.Exception
  (OutOfMemoryError,                       (Checked Exceptions)
   StackOverflowError)                            |
                                 +----------------+----------------+
                                 |                                 |
                        java.io.IOException              java.lang.RuntimeException
                                 |                         (Unchecked Exceptions)
                    java.io.FileNotFoundException                  |
                    (Card Reader Hardware Missing)       +---------+---------+
                                                         |                   |
                                            java.lang.NumberFormatException  java.lang.IllegalArgumentException
                                              (Invalid Numeric Input)                 |
                                                                             InvalidAmountException
                                                                             (Negative/Zero Amount)
```

### 2.2 Exception Classification Table
| Exception Class | Type | Checked / Unchecked | Originating Condition in ATM | Handling Policy |
| :--- | :--- | :--- | :--- | :--- |
| `FileNotFoundException` | Subclass of `IOException` | **Checked** | Missing `card_reader.cfg` configuration file | Mandatory `try-catch` or `throws`; aborts transaction safely |
| `NumberFormatException` | Subclass of `IllegalArgumentException` | **Unchecked** | Non-numeric input strings (e.g., `"two_thousand"`) | Caught in UI parser layer; prompts user for valid digits |
| `InvalidAmountException`| Subclass of `IllegalArgumentException` | **Unchecked** | Negative or zero withdrawal amounts (e.g., `-500.00`) | Rejects transaction; enforces positive currency amounts |
| `InsufficientBalanceException` | Subclass of `Exception` | **Checked** | Requested amount exceeds available balance | Checked business rule; forces calling method to handle decline |
| `Exception` | Superclass of all exceptions | **Checked** | Unforeseen runtime failures | Top-level catch fallback for unexpected anomalies |

### 2.3 Catch Block Ordering Rules
Java enforces strict ordering for catch blocks: **Subclasses must always precede their superclasses**.
When an exception occurs, the JVM evaluates `catch` clauses sequentially from top to bottom. If a broader superclass catch block (such as `catch (Exception e)`) were placed before a more specific subclass block (such as `catch (FileNotFoundException e)`), the first block would match every conceivable subclass, rendering the second block dead code. The Java compiler detects this at compile time and rejects it with:
```
error: exception FileNotFoundException has already been caught
```

### 2.4 The Guarantees of the `finally` Block
The `finally` block guarantees execution regardless of whether:
1. The `try` block completes normally.
2. A caught exception occurs and executes a `catch` block.
3. An uncaught exception occurs (executing `finally` before the exception propagates up the call stack).
4. A control transfer statement (`return`, `break`, `continue`) is executed inside `try` or `catch`.

In this ATM simulation, the `finally` block guarantees that the card reader hardware session is closed, the physical card is safely ejected, and security audit logs are recorded.

---

## 3. Experimental Evidence & Student B Contribution

### 3.1 Five Executed Test Scenarios
All five scenarios were executed using the compiled `ATMWithdrawalSimulation` program.

| Test Case | Account No | Amount | Card Config | Expected Exception | Catch Block Executed | Did `finally` Run? | Final Balance |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC 1 (Normal)** | `1234567890` | `2000.00` | `card_reader.cfg` | None (Success) | None (Normal flow) | **YES** | INR 8000.00 |
| **TC 2 (Invalid Format)**| `1234567890` | `two_thousand`| `card_reader.cfg` | `NumberFormatException` | `catch (NumberFormatException e)` | **YES** | INR 10000.00 |
| **TC 3 (Negative Value)** | `1234567890` | `-500.00` | `card_reader.cfg` | `InvalidAmountException` | `catch (InvalidAmountException e)` | **YES** | INR 10000.00 |
| **TC 4 (Overdraft)** | `1234567890` | `25000.00` | `card_reader.cfg` | `InsufficientBalanceException` | `catch (InsufficientBalanceException e)` | **YES** | INR 10000.00 |
| **TC 5 (Missing File)** | `1234567890` | `1000.00` | `missing_reader.cfg`| `FileNotFoundException` | `catch (FileNotFoundException e)` | **YES** | INR 10000.00 |

### 3.2 Uncaught Exception Demonstration
When an exception is not intercepted by any catch block (as demonstrated in `UncaughtExceptionDemo.java`), the JVM invokes the uncaught exception handler of the current thread:
```
Exception in thread "main" java.lang.ArithmeticException: / by zero
	at task01.UncaughtExceptionDemo.methodB(UncaughtExceptionDemo.java:31)
	at task01.UncaughtExceptionDemo.methodA(UncaughtExceptionDemo.java:23)
	at task01.UncaughtExceptionDemo.main(UncaughtExceptionDemo.java:17)
```
The thread terminates abruptly, leaving subsequent statements unexecuted.

---

## 4. Joint Engineering Discussion: Catch-All `catch (Exception e)`

Catching generic exceptions with `catch (Exception e)` is generally **poor engineering practice** for core business logic:
- **Masking Distinct Bugs:** Catching `Exception` indiscriminately traps programming bugs (such as `NullPointerException`, `IndexOutOfBoundsException`) alongside legitimate domain errors, concealing logic flaws.
- **Loss of Granular Recovery:** An ATM cannot differentiate between network loss, database disconnection, and input formatting errors if all are trapped in one block.
- **When Acceptable:** Generic `catch (Exception e)` is only acceptable at the top-most boundary of an application (e.g., web server dispatchers or CLI entry points) strictly as a fallback logger to prevent entire process crashes.

---

## 5. Alternative Implementation & Complexity Analysis

### 5.1 Alternative Approach: Error Codes & Scanner Validation
In `ATMErrorCodeValidation.java`, input formatting is validated using `Scanner.hasNextLong()` and `Scanner.hasNextDouble()`, and business rule failures return integer status codes (`0` for SUCCESS, `1` for MISSING_CONFIG, `2` for FORMAT_ERROR, `3` for NEGATIVE_AMOUNT, `4` for INSUFFICIENT_BALANCE).

```java
// Alternative snippet:
if (!scanner.hasNextDouble()) return ERR_INVALID_NUMERIC_FORMAT;
double amount = scanner.nextDouble();
if (amount <= 0) return ERR_NEGATIVE_AMOUNT;
if (amount > balance) return ERR_INSUFFICIENT_BALANCE;
```

### 5.2 Comparative Analysis Table
| Dimension | Exception-Based Approach (`ATMWithdrawalSimulation`) | Error-Code-Based Approach (`ATMErrorCodeValidation`) |
| :--- | :--- | :--- |
| **Readability** | High: Separates normal business flow from error paths cleanly. | Moderate: Normal code is cluttered with manual `if-else` return checks. |
| **Propagation** | Automatic: Exceptions bubble up the call stack to the handler. | Manual: Every intermediary method must explicitly check and pass codes. |
| **Execution Overhead** | Slight overhead on exceptional path due to stack trace creation. | Extremely low: Zero allocation overhead; simple integer return. |
| **Time Complexity** | Best/Average: $O(1)$, Worst-case: $O(d)$ where $d$ is call-stack depth. | Best/Average/Worst: $O(1)$. |
| **Space Complexity** | $O(d)$ stack frame capture during exception instantiation. | $O(1)$ auxiliary memory. |
| **Recommendation** | **Recommended for Java Enterprise Systems** to ensure clean separation of concerns and robust error propagation. |
