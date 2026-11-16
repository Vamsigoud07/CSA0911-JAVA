# TASK A1: DESIGN A FILE FORMAT FOR TRANSPORT BUS-PASS RECORDS

**Course:** Programming in Java (CSA09) – CO5: Data Persistence Engineering  
**Student Name:** K VENKATA VAMSI GOUD  
**Register Number:** 192472021  
**Institution:** Saveetha School of Engineering, SIMATS  

---

## 1. Problem Overview and Record Structure
The University Transport Office issues student bus passes across multiple bus routes. Each record must store seven core attributes:
1. `pass_id` – Alphanumeric identifier (e.g., `P1001`)
2. `reg_no` – Student Register Number (e.g., `192472021`)
3. `name` – Student Full Name (e.g., `K Venkata Vamsi Goud`)
4. `route_no` – Bus Route Number (e.g., `12`)
5. `boarding_stop` – Designated pickup stop (e.g., `Poonamallee Bypass`)
6. `validity_date` – Expiry Date (e.g., `2026-12-31`)
7. `amount_paid` – Bus fee paid in INR (e.g., `4500.00`)

---

## 2. A1.1 – Representation of the Record: Delimited Text vs. Binary Format

### A. Delimited Text File Format (CSV / TSV)
- **Field Order:** `pass_id,reg_no,name,route_no,boarding_stop,validity_date,amount_paid`
- **Delimiter Selection:** Comma (`,`) is chosen as the standard field delimiter; newline (`\n` / `\r\n`) acts as the record separator.
- **Handling Delimiter Collisions:** When names or stops contain commas (e.g., `"Goud, Vamsi"`), fields are enclosed in double quotes (`"..."`). An embedded double quote is escaped by doubling it (`""`).
- **Sample Text Record:**
  ```text
  P1001,192472021,"Vamsi Goud, K",12,"Poonamallee Bypass",2026-12-31,4500.00
  ```

### B. Binary File Format (Java DataOutputStream / DataInputStream)
- **Field Layout & Types:**
  - `pass_id` : UTF String (`writeUTF()`) – 2-byte length prefix + modified UTF-8 bytes.
  - `reg_no` : UTF String (`writeUTF()`) – 2-byte length prefix + ASCII bytes.
  - `name` : UTF String (`writeUTF()`) – 2-byte length prefix + UTF-8 bytes.
  - `route_no` : 32-bit Integer (`writeInt()`) – 4 bytes fixed.
  - `boarding_stop`: UTF String (`writeUTF()`) – 2-byte length prefix + UTF-8 bytes.
  - `validity_date`: Long timestamp in milliseconds (`writeLong()`) – 8 bytes fixed.
  - `amount_paid` : 64-bit IEEE 754 Floating-Point (`writeDouble()`) – 8 bytes fixed.
- **Delimiter Handling:** No delimiters needed. Data streams use exact byte counts and type prefixes.

### C. Comparison: Delimited Text vs. Binary Format

| Feature | Delimited Text (CSV) | Binary File (`DataOutputStream`) |
| :--- | :--- | :--- |
| **Human Readability** | High; editable with standard text editors / Excel. | None; raw bytes require custom decoder tools. |
| **Storage Efficiency** | Larger; numbers stored as character strings. | Compact; numeric types occupy fixed 4/8 bytes. |
| **I/O Parsing Overhead** | Higher; string parsing and type casting overhead. | Low; direct machine-representation reading. |
| **Portability** | Universal across languages and operating systems. | Dependent on Java primitive data format conventions. |
| **Schema Evolution** | Flexible; additional fields can be appended. | Rigid; modifying stream layout invalidates old files. |

---

## 3. A1.2 – SIMATS Engineering Standard Algorithmic Pseudocode

### Algorithm A1.2.1: Add New Bus Pass Record (Safe Append)
```text
ALGORITHM: AddBusPassRecord
INPUT: filePath (STRING), newPass (RECORD containing pass_id, reg_no, name, route_no, boarding_stop, validity_date, amount)
OUTPUT: isSuccess (BOOLEAN)

BEGIN
    IF filePath IS NULL OR filePath IS EMPTY THEN
        RETURN FALSE
    ENDIF

    TRY
        OPEN fileWriter USING FileWriter(filePath, appendMode = TRUE)
        OPEN bufferedWriter USING BufferedWriter(fileWriter)

        formattedRecord <- FormatCSV(newPass.pass_id, newPass.reg_no, EscapeQuotes(newPass.name), 
                                     newPass.route_no, EscapeQuotes(newPass.boarding_stop), 
                                     newPass.validity_date, newPass.amount)

        WRITE formattedRecord TO bufferedWriter
        WRITE NEWLINE TO bufferedWriter
        FLUSH bufferedWriter
        CLOSE bufferedWriter
        RETURN TRUE
    CATCH IOException
        LOG "Error writing to storage file"
        RETURN FALSE
    ENDTRY
END
```
- **Time Complexity:** $O(1)$ amortized append.  
- **Space Complexity:** $O(1)$ auxiliary buffer memory.

---

### Algorithm A1.2.2: Find Bus Pass by Pass ID (Sequential Scan)
```text
ALGORITHM: FindBusPassById
INPUT: filePath (STRING), targetPassId (STRING)
OUTPUT: matchingRecord (RECORD or NULL)

BEGIN
    IF targetPassId IS NULL THEN
        RETURN NULL
    ENDIF

    TRY
        OPEN fileReader USING FileReader(filePath)
        OPEN bufferedReader USING BufferedReader(fileReader)

        WHILE (currentLine <- bufferedReader.ReadLine()) IS NOT NULL DO
            trimmedLine <- TRIM(currentLine)
            IF trimmedLine IS EMPTY THEN
                CONTINUE
            ENDIF

            fields <- SPLIT(trimmedLine, ",")
            IF LENGTH(fields) != 7 THEN
                LOG "Warning: Malformed row encountered, skipping."
                CONTINUE
            ENDIF

            extractedPassId <- TRIM(fields[0])
            IF extractedPassId EQUALS_IGNORE_CASE(targetPassId) THEN
                matchingRecord <- CONSTRUCT_RECORD(fields)
                CLOSE bufferedReader
                RETURN matchingRecord
            ENDIF
        ENDWHILE

        CLOSE bufferedReader
        RETURN NULL
    CATCH IOException
        LOG "File access error during lookup"
        RETURN NULL
    ENDTRY
END
```
- **Time Complexity:** Best Case $O(1)$ (record at top); Worst/Average Case $O(N)$ where $N$ is total records.  
- **Space Complexity:** $O(1)$ memory.

---

### Algorithm A1.2.3: List All Passes by Bus Route
```text
ALGORITHM: ListPassesByRoute
INPUT: filePath (STRING), targetRoute (INTEGER)
OUTPUT: passList (LIST OF RECORDS)

BEGIN
    INITIALIZE passList AS EMPTY LIST

    TRY
        OPEN bufferedReader USING BufferedReader(FileReader(filePath))

        WHILE (currentLine <- bufferedReader.ReadLine()) IS NOT NULL DO
            trimmedLine <- TRIM(currentLine)
            IF trimmedLine IS EMPTY THEN
                CONTINUE
            ENDIF

            fields <- SPLIT(trimmedLine, ",")
            IF LENGTH(fields) == 7 THEN
                TRY
                    routeNo <- PARSE_INTEGER(TRIM(fields[3]))
                    IF routeNo == targetRoute THEN
                        record <- CONSTRUCT_RECORD(fields)
                        APPEND record TO passList
                    ENDIF
                CATCH NumberFormatException
                    CONTINUE // Skip corrupted route number
                ENDTRY
            ENDIF
        ENDWHILE

        CLOSE bufferedReader
        RETURN passList
    CATCH IOException
        RETURN passList
    ENDTRY
END
```
- **Time Complexity:** $O(N)$ exhaustive full-file traversal.  
- **Space Complexity:** $O(K)$ where $K$ is the number of matching route passes.

---

## 4. Justification of Java Stream Hierarchy
1. **`FileReader` / `FileWriter`:** Character-based convenience bridges converting native bytes to UTF-16 character streams using the default character encoding.
2. **`BufferedReader` / `BufferedWriter`:** High-performance decorator streams maintaining an 8192-character internal buffer. They eliminate frequent, costly user-kernel boundary transitions and supply essential primitives like `readLine()` and `newLine()`.
3. **`FileInputStream` / `FileOutputStream`:** Raw byte-oriented streams for low-level binary data transfer without character set encoding alterations.

---

## 5. A1.3 – Designing for Damaged Data and Fault Tolerance
- **Damaged / Truncated Lines:** Validated using token length checks (`length == 7`). Invalid rows are skipped and logged to quarantine without terminating application flow.
- **Missing Files:** Guarded using `File.exists()` and clean initialization handlers.
- **Incomplete Writes / Sudden Power Loss (Safe Atomic File Write Strategy):**
  1. Write modifications to a separate temporary file on the same storage volume: `bus_passes.tmp`.
  2. Flush and force sync metadata to disk (`FileDescriptor.sync()`).
  3. Perform an atomic replacement of the target file using `java.nio.file.Files.move(src, dst, StandardCopyOption.ATOMIC_MOVE)`.
  4. If an unexpected power crash occurs mid-write, the original file remains uncorrupted.

---

## 6. Alternative Concept: Fixed-Length Records with `RandomAccessFile`
Instead of variable-length CSV lines, every record is allocated an exact fixed byte size (e.g., 256 bytes):
- Numeric fields occupy fixed byte widths; text fields are padded with spaces or null terminators (`\0`).

### Comparison: Variable-Length Sequential Scan vs. Fixed-Length Direct Seek

| Evaluation Metric | Variable-Length Text File | Fixed-Length `RandomAccessFile` |
| :--- | :--- | :--- |
| **Search by Known Index** | $O(N)$ sequential scanning from beginning. | $O(1)$ instantaneous direct jump: `seek(index * recordSize)`. |
| **Search by Arbitrary Key** | $O(N)$ linear inspection. | $O(N)$ sequential or $O(\log N)$ with binary search on sorted files. |
| **Storage Utilization** | Compact; stores only actual characters. | Inefficient; significant wasted slack space due to padding. |
| **Handling Long Names** | Unlimited character capacity. | Strictly capped; names exceeding field byte limits must be truncated. |
| **Record Deletions** | Requires full file rewrites. | Can mark records with a 1-byte tombstone flag (`isActive = false`). |
