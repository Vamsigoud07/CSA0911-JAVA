# Task 6: Campus Event Registration Desktop Application

**Course Code**: CSA09  
**Course Name**: Programming in Java  
**Student Name**: K VENKATA VAMSI GOUD  
**Register Number**: 192472021  
**Institution**: SIMATS – Saveetha School of Engineering  
**Academic Year**: 2026–2027  

---

## 6.1 Stage 1 – Requirements and Problem Interpretation

The **Campus Event Registration System** is a robust desktop enterprise software solution enabling university departments to coordinate student enrollments for technical conferences, hackathons, and symposiums.

### Stakeholders & User Roles:
1. **Students**: Submit registrations for target events, receiving immediate confirmation tokens.
2. **Event Coordinators / Faculty**: Monitor capacity, view real-time registrations in tabular form, filter records by event, and export participant rosters for gate attendance.

### Core Measurable Requirements:
- **Relational Integrity**: Enforce foreign keys between Students, Events, and Registrations.
- **Duplicate Prevention**: Absolute database-level prevention preventing a student from registering twice for the same event via `UNIQUE(student_id, event_id)`.
- **Atomic Capacity Enforcement**: Decrement seats only if `available_seats > 0` within an atomic transaction.
- **EDT Responsiveness**: Zero interface lag; all database queries and writes execute in background worker threads via `SwingWorker<T, V>`.
- **Fault-Tolerant Export**: Export filtered participant rosters to RFC-4180 compliant CSV files with correct quote/comma escaping.

---

## 6.2 Stage 2 – Screen Flow and Navigation Architecture

```
                                +---------------------------+
                                |      Top Menu Bar         |
                                |  File | Reg | View | Help |
                                +---------------------------+
                                              |
               +------------------------------+------------------------------+
               |                                                             |
               v                                                             v
+-------------------------------+                             +-------------------------------+
|    Tab 1: Registration Form   |                             |     Tab 2: Records & Filters  |
| - Student ID (Alphanumeric)   |                             | - Event Filter JComboBox      |
| - Full Name (2-80 chars)      |                             | - Filter Button (Async DB)    |
| - University Email (Regex)    |                             | - Refresh Button              |
| - Department Dropdown         |                             | - JTable (Registration Rows)  |
| - Event Selection Dropdown    |                             | - Export to CSV (JFileChooser)|
| - "Register for Event" Button |                             | - Status Bar (Matching Count) |
+-------------------------------+                             +-------------------------------+
               |                                                             |
               v                                                             v
+-------------------------------+                             +-------------------------------+
|  Modal Confirmation / Error   |                             |       CSV Export File         |
|  (JOptionPane Dialog)         |                             |   (registrations_export.csv)  |
+-------------------------------+                             +-------------------------------+
```

---

## 6.3 Stage 3 – Layered System Architecture

The application strictly separates responsibilities into distinct tiers:

```
+-------------------------------------------------------------------+
|                        PRESENTATION LAYER                         |
|  MainFrame | RegistrationPanel | RegistrationTablePanel          |
|  - Renders Swing components, captures user gestures               |
|  - Dispatches heavy DB operations to SwingWorker background pool  |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                          SERVICE LAYER                            |
|                       RegistrationService                         |
|  - Validates email regex, student ID syntax, name lengths         |
|  - Enforces domain rules prior to persistence                     |
|  - Formats and escapes RFC-4180 CSV export files                  |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                       DATA ACCESS LAYER (DAO)                     |
|                         RegistrationDAO                           |
|  - PreparedStatement parameterized SQL execution                  |
|  - Manages database transactions (setAutoCommit(false), rollback) |
|  - Resource cleanup via try-with-resources                        |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                        DATABASE / STORAGE                         |
|             MySQL 8.0 Server (campus_events_db)                  |
|  - InnoDB tables: students, events, registrations                 |
|  - Constraints: uk_student_event, chk_seats_non_negative          |
|  - Resilient synchronized fallback for offline testing           |
+-------------------------------------------------------------------+
```

---

## 6.4 Stage 4 – Class Design and Algorithmic Pseudocode

### Class Diagram

```
+-------------------------------+          +-------------------------------+
|           MainFrame           |          |      RegistrationService      |
+-------------------------------+          +-------------------------------+
| - service: RegistrationService|--------->| - dao: RegistrationDAO        |
| - tabbedPane: JTabbedPane     |          +-------------------------------+
+-------------------------------+          | + registerStudent(): Reg      |
                                           | + exportRegistrationsToCSV()  |
                                           +-------------------------------+
                                                           |
                                                           v
+-------------------------------+          +-------------------------------+
|        RegistrationDAO        |          |      DatabaseConnection       |
+-------------------------------+          +-------------------------------+
| - fallbackEvents: Map         |--------->| + getConnection(): Connection |
| - fallbackRegistrations: List |          | + isMySqlAvailable(): boolean |
+-------------------------------+          +-------------------------------+
| + getAllEvents(): List        |
| + registerStudentForEvent()   |
| + getRegistrationsByEvent()   |
+-------------------------------+
```

### Algorithm 1: RegisterAction (SIMATS Engineering Standard)

```
ALGORITHM RegisterAction(studentId, name, email, department, eventId)
// Purpose: Validates student input and asynchronously registers for an event within an atomic transaction
INPUT: studentId (STRING), name (STRING), email (STRING), department (STRING), eventId (STRING)
OUTPUT: Confirmed Registration object or ValidationException alert

START
    DECLARE student AS Student
    DECLARE reg AS Registration

    // 1. Validation Checks
    IF studentId IS NULL OR TRIM(studentId) == "" THEN
        THROW ValidationException("Student ID cannot be empty")
    END IF

    IF NOT MATCH_REGEX(studentId, "^[A-Za-z0-9]{5,20}$") THEN
        THROW ValidationException("Student ID must be alphanumeric (5-20 characters)")
    END IF

    IF name IS NULL OR LENGTH(TRIM(name)) < 2 THEN
        THROW ValidationException("Student Name must be at least 2 characters")
    END IF

    IF NOT MATCH_REGEX(email, "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$") THEN
        THROW ValidationException("Invalid email format")
    END IF

    IF eventId IS NULL OR eventId == "" THEN
        THROW ValidationException("An event must be selected")
    END IF

    // 2. Duplicate Check
    IF CALL dao.isDuplicateRegistration(studentId, eventId) THEN
        THROW ValidationException("Duplicate Registration: Student already registered for event")
    END IF

    // 3. Atomic Database Transaction (inside SwingWorker doInBackground)
    CALL conn.setAutoCommit(FALSE)
    TRY
        DECLARE seats AS INTEGER
        SET seats = CALL QUERY_FOR_UPDATE("SELECT available_seats FROM events WHERE event_id = ?", eventId)
        
        IF seats <= 0 THEN
            CALL conn.rollback()
            THROW ValidationException("Capacity Exhausted: No available seats remaining")
        END IF

        CALL EXECUTE_UPDATE("INSERT INTO students VALUES (?, ?, ?, ?)", studentId, name, email, department)
        SET reg = CALL EXECUTE_INSERT_REGISTRATION(studentId, eventId)
        CALL EXECUTE_UPDATE("UPDATE events SET available_seats = available_seats - 1 WHERE event_id = ?", eventId)

        CALL conn.commit()
    CATCH Exception e
        CALL conn.rollback()
        THROW e
    FINALLY
        CALL conn.setAutoCommit(TRUE)
    END TRY

    RETURN reg
STOP
```

### Algorithm 2: FilterAction (SIMATS Engineering Standard)

```
ALGORITHM FilterAction(selectedEventFilter)
// Purpose: Asynchronously retrieves filtered registrations and updates JTable on EDT
INPUT: selectedEventFilter (STRING)
OUTPUT: JTable populated with matching rows; status bar updated

START
    DECLARE eventId AS STRING
    DECLARE matchedList AS LIST OF Registration

    IF selectedEventFilter == "All Events" THEN
        SET eventId = "ALL"
    ELSE
        SET eventId = EXTRACT_PREFIX_BEFORE_DASH(selectedEventFilter)
    END IF

    // Asynchronous Execution via SwingWorker
    SET matchedList = CALL dao.getRegistrationsByEvent(eventId)

    // EDT Callback (done method)
    CALL tableModel.setRegistrations(matchedList)
    CALL statusLabel.setText("Filtered by " + eventId + ": " + matchedList.size() + " records found")
STOP
```

---

## 6.5 Stage 5 – Experimental Verification Suite (9 Test Scenarios)

The application was subjected to all nine mandatory test scenarios:

| Test Case | Scenario / Input | Expected Result | Actual Result | Status | Screenshot File |
|:---:|:---|:---|:---|:---:|:---|
| **Test 1** | **Valid Registration**<br>ID: `192472021`, Name: `K VENKATA VAMSI GOUD`, Event: `EVT-101` | Registration confirmed, UUID generated, seats decremented | Success! Generated `REG-` token; added to table. | **PASS** | `Task06_ValidRegistration.png` |
| **Test 2** | **Missing Student Name**<br>ID: `192472021`, Name: `""` (empty) | Rejected with descriptive validation message | `ValidationException: Student Name cannot be empty.` | **PASS** | `Task06_InvalidRegistration.png` |
| **Test 3** | **Invalid Email Format**<br>Email: `not-a-valid-email` | Regex catches missing domain and '@' | `ValidationException: Invalid email format.` | **PASS** | Verified in logs |
| **Test 4** | **Missing Event Selection**<br>Event: `""` (unselected) | Rejected before querying database | `ValidationException: An event must be selected.` | **PASS** | Verified in logs |
| **Test 5** | **Duplicate Registration Attempt**<br>Student `192472021` registering twice for `EVT-101` | Blocked by `uk_student_event` database constraint | Rejected: Student already registered for this event. | **PASS** | `Task06_DuplicateRegistration.png` |
| **Test 6** | **Capacity Exhaustion (0 Seats)**<br>Attempting 3rd booking on `EVT-105` (capacity = 2) | Blocked when `available_seats == 0` | Rejected: `Capacity Exhausted: No available seats remaining.` | **PASS** | Verified in logs |
| **Test 7** | **Filter by Event**<br>Select `EVT-101` in filter dropdown | Displays only participants for `EVT-101` | Table refreshed; status bar displays exact matching count. | **PASS** | `Task06_Filter.png` |
| **Test 8** | **Export to CSV**<br>Save table data via `JFileChooser` | RFC-4180 compliant CSV file created | Exported valid CSV file `registrations_export.csv`. | **PASS** | `Task06_CSVExport.png` |
| **Test 9** | **Database Offline / Fallback**<br>Unreachable MySQL server / sandbox isolation | Zero crashes; transparent resilient transactional fallback | All operations executed flawlessly without crashing. | **PASS** | Verified in logs |

---

## 6.6 Alternative Concept & Complexity Analysis

### Comparison: Loading All Records vs. Keyset/Offset Pagination

In desktop database applications, managing large datasets requires careful architectural trade-offs:

1. **Approach A: Load All Records into `JTable`**:
   The entire result set ($N = 100,000$ records) is fetched in one query (`SELECT * FROM registrations`) and loaded into Java heap memory.
2. **Approach B: Keyset / Offset Pagination via `SwingWorker`**:
   Data is retrieved in small chunks ($P = 50$ records) using `LIMIT P OFFSET K`.

### Detailed Complexity and Trade-Off Matrix

| Evaluation Dimension | Approach A: Load All 100,000 Records | Approach B: SQL LIMIT/OFFSET Pagination (Selected) |
|:---|:---|:---|
| **Database Query Cost** | $O(N)$ massive full table scan: 1.5–4.5 seconds disk I/O | $O(P)$ indexed scan: **< 5 milliseconds** |
| **Network Transfer Volume** | ~25 MB payload transfer over socket | **~12 KB** minimal payload per page |
| **Client Memory (RAM)** | High ($O(N)$): ~60 MB heap for 100k objects | **Minimal ($O(P)$)**: ~35 KB heap |
| **Initial Screen Render Time** | 2,000–5,000 ms (significant UI delay) | **< 50 ms (instantaneous initial display)** |
| **EDT Responsiveness** | Freezes UI during rendering unless backgrounded | Smooth, lag-free scrolling and instant response |
| **Large Offset Penalty** | Not applicable (all data already in memory) | `OFFSET 90000` causes DB to scan 90k discarded rows ($O(K)$) |
| **Recommended Use Case** | Small datasets ($N < 2,000$ rows) | **Enterprise databases ($N > 10,000$ rows)** |

**Architectural Recommendation**: For enterprise-scale applications exceeding 10,000 records, Approach B (Pagination) is essential to preserve client heap memory and prevent network saturation. For large offsets, **Keyset Pagination** (`WHERE registration_id > last_seen_id ORDER BY registration_id LIMIT P`) should replace `OFFSET` to achieve $O(1)$ indexed retrieval.
