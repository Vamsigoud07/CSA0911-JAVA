# TASK A5: DATABASE TRANSACTIONS & SQL INJECTION MITIGATION

**Course:** Programming in Java (CSA09) – CO5: Data Persistence Engineering  
**Student Name:** K VENKATA VAMSI GOUD  
**Register Number:** 192472021  
**Institution:** Saveetha School of Engineering, SIMATS  

---

## 1. A5.1 – Atomic Transaction Design: Wallet Fund Transfer
A student transfers **₹500.00** from their **Hostel Mess Wallet** to their **Campus Canteen Wallet**. Both updates must execute as an atomic, indivisible unit of work adhering to ACID principles (Atomicity, Consistency, Isolation, Durability).

### SIMATS Algorithmic Pseudocode (Fund Transfer):
```text
ALGORITHM: TransferWalletFunds
INPUT: studentId (STRING), transferAmount (DECIMAL, e.g., 500.00)
OUTPUT: isSuccess (BOOLEAN)

BEGIN
    IF transferAmount <= 0 THEN
        LOG "Transfer amount must be strictly positive"
        RETURN FALSE
    ENDIF

    TRY (Connection conn <- DatabaseConnectionPool.GetConnection())
        originalAutoCommit <- conn.GetAutoCommit()
        conn.SetAutoCommit(FALSE) // BEGIN EXPLICIT TRANSACTION

        TRY
            // Step 1: Check Hostel Wallet Balance with Row Locking
            checkSql <- "SELECT balance FROM hostel_wallet WHERE student_id = ? FOR UPDATE"
            PREPARE checkStmt <- conn.PrepareStatement(checkSql)
            checkStmt.SetString(1, studentId)
            rs <- checkStmt.ExecuteQuery()

            IF NOT rs.Next() OR rs.GetDouble("balance") < transferAmount THEN
                LOG "Insufficient hostel wallet funds or wallet not found"
                conn.Rollback()
                RETURN FALSE
            ENDIF

            // Step 2: Debit Hostel Wallet
            debitSql <- "UPDATE hostel_wallet SET balance = balance - ? WHERE student_id = ?"
            PREPARE debitStmt <- conn.PrepareStatement(debitSql)
            debitStmt.SetDouble(1, transferAmount)
            debitStmt.SetString(2, studentId)
            rowsDebited <- debitStmt.ExecuteUpdate()

            // Step 3: Credit Canteen Wallet
            creditSql <- "UPDATE canteen_wallet SET balance = balance + ? WHERE student_id = ?"
            PREPARE creditStmt <- conn.PrepareStatement(creditSql)
            creditStmt.SetDouble(1, transferAmount)
            creditStmt.SetString(2, studentId)
            rowsCredited <- creditStmt.ExecuteUpdate()

            // Verify both operations affected exactly one row
            IF rowsDebited == 1 AND rowsCredited == 1 THEN
                conn.Commit() // ATOMIC COMMIT: Persist all modifications
                RETURN TRUE
            ELSE
                conn.Rollback() // PARTIAL UPDATE DETECTED: Abort
                RETURN FALSE
            ENDIF

        CATCH SQLException OR RuntimeException ex
            conn.Rollback() // SYSTEM CRASH / EXCEPTION: Full rollback to clean state
            LOG "Transaction aborted due to error: " + ex.GetMessage()
            RETURN FALSE
        FINALLY
            conn.SetAutoCommit(originalAutoCommit) // Restore original state
        ENDTRY
    END
```

### Protection Against Partial Updates:
If the system crashes, loses power, or encounters an arithmetic error after Step 2 (debit) but before Step 3 (credit):
- Without transaction control, ₹500 vanishes into thin air (the student was debited, but the canteen was never credited).
- With `setAutoCommit(false)` and `rollback()`, the database undoes the uncommitted debit from its undo logs. The student's balance remains intact, preserving **Financial Consistency**.

---

## 2. A5.2 – SQL Injection Vulnerability Analysis & Parameterized Redesign

### Analysis of the Vulnerable Query:
```java
String sql = "SELECT * FROM users WHERE id='" + id + "' AND pwd='" + pwd + "'";
```
- **Vulnerability Mechanism:** Dynamic string concatenation concatenates user input directly into the SQL syntax tree. The RDBMS SQL engine cannot distinguish between application developer commands and attacker data.

### Exploitation Scenario:
An adversary enters:
- `id` : `admin' --`
- `pwd`: `irrelevant_password`

The database concatenates these strings into:
```sql
SELECT * FROM users WHERE id='admin' --' AND pwd='irrelevant_password'
```
1. `'admin'` matches the administrator record.
2. The double-dash sequence (`--`) signifies an inline SQL comment.
3. The database parser discards the entire trailing clause (`' AND pwd='...'`), completely bypassing password authentication and logging the attacker in as system administrator.

### Secure Redesign with `PreparedStatement`:
```java
String sql = "SELECT * FROM users WHERE id = ? AND pwd = ?";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    pstmt.setString(1, id);
    pstmt.setString(2, pwd);
    try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
            // Securely Authenticated
        }
    }
}
```
- **Why PreparedStatement is Secure:**
  1. The SQL query structure is pre-compiled by the database engine into a static execution plan *before* any parameters are injected.
  2. The parameter values (`?`) are transferred via a separate binary protocol as raw literals.
  3. Even if the input contains quotes (`' OR '1'='1`), the database treats the string strictly as a literal character value rather than executable SQL syntax.

---

## 3. Alternative Concept: Single Transaction vs. Two Auto-Commit Updates

| Evaluation Dimension | Two Separate Auto-Commit Updates | Single Atomic Transaction |
| :--- | :--- | :--- |
| **Correctness Guarantee** | **Flawed**: If the second update fails, the system is left in an inconsistent corrupt state. | **Guaranteed**: Either both updates succeed or neither takes effect (All-or-Nothing). |
| **Failure Recovery** | Requires complicated manual reconciliation scripts and human investigation. | Handled automatically by database rollback recovery mechanisms. |
| **Concurrency / Isolation** | High risk of dirty reads; another thread can read the intermediate state where money was debited but not yet credited. | Complete isolation (`REPEATABLE READ` / `SERIALIZABLE`); intermediate states are invisible. |
| **Network & Disk Overhead** | Two separate network round trips and two physical disk write syncs (`fsync`). | Batched network packets and a single collective commit write to the transaction redo log. |
| **Execution Latency** | Slower overall due to multiple disk synchronization boundaries. | Faster overall execution and higher transaction throughput. |
