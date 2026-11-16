# TASK A3: DATA ACCESS OBJECT (DAO) DESIGN PATTERN

**Course:** Programming in Java (CSA09) – CO5: Data Persistence Engineering  
**Student Name:** K VENKATA VAMSI GOUD  
**Register Number:** 192472021  
**Institution:** Saveetha School of Engineering, SIMATS  

---

## 1. Architectural Motivation
In multi-entity academic systems handling `StudentDB`, `CourseDB`, and `MarksDB`, interleaving database query mechanics with business logic or user interface code creates severe coupling, spaghetti code, and maintenance nightmares. The **Data Access Object (DAO)** pattern encapsulates all persistence operations behind clean, contract-driven interfaces.

---

## 2. A3.1 – Decomposition of Common Infrastructure vs. Entity-Specific Logic

Persistence workflows share repeated boilerplate steps that must be decoupled from domain logic:

| Common Infrastructure Logic (Abstract / Utility) | Entity-Specific Logic (Specialized DAO) |
| :--- | :--- |
| 1. Establishing / Pooling JDBC `Connection` instances. | 1. Specialized SQL syntax (`INSERT INTO student...`). |
| 2. Allocating `PreparedStatement` and managing statement caches. | 2. Binding domain object fields to SQL positional parameters (`pstmt.setString(1, s.getName())`). |
| 3. Handling low-level `SQLException` and transaction rollbacks. | 3. Entity-specific Object-Relational mapping (`rs.getString("reg_no") -> student.setRegNo()`). |
| 4. Deterministic resource cleanup (`AutoCloseable` / try-with-resources). | 4. Complex entity joins and domain filter criteria. |

---

## 3. A3.2 – Generic Interface Contract & Student Implementation

### Generic Interface Contract (`Dao<T, K>`):
```text
INTERFACE: Dao<T, K>
WHERE:
    T = Domain Entity Type (e.g., Student, Course)
    K = Primary Key Type (e.g., String, Long)

METHODS:
    METHOD insert(entity: T): BOOLEAN
    METHOD findById(id: K): OPTIONAL<T>
    METHOD findAll(): LIST<T>
    METHOD update(entity: T): BOOLEAN
    METHOD delete(id: K): BOOLEAN
ENDINTERFACE
```

### SIMATS Algorithmic Pseudocode (StudentDao Implementation):
```text
CLASS StudentDao IMPLEMENTS Dao<Student, String>

    ALGORITHM: findById
    INPUT: regNo (STRING)
    OUTPUT: Optional<Student>

    BEGIN
        sqlQuery <- "SELECT reg_no, full_name, department, year_of_study, cgpa FROM student WHERE reg_no = ?"

        TRY (Connection conn <- DatabaseConnectionPool.GetConnection();
             PreparedStatement pstmt <- conn.PrepareStatement(sqlQuery))

            pstmt.SetString(1, regNo)

            TRY (ResultSet rs <- pstmt.ExecuteQuery())
                IF rs.Next() THEN
                    student <- NEW Student()
                    student.SetRegNo(rs.GetString("reg_no"))
                    student.SetName(rs.GetString("full_name"))
                    student.SetDepartment(rs.GetString("department"))
                    student.SetYear(rs.GetInt("year_of_study"))
                    student.SetCgpa(rs.GetDouble("cgpa"))
                    RETURN Optional.OF(student)
                ELSE
                    RETURN Optional.EMPTY()
                ENDIF
            ENDTRY
        CATCH SQLException
            LOG "Database lookup failure for student: " + regNo
            RETURN Optional.EMPTY()
        ENDTRY
    END

    ALGORITHM: insert
    INPUT: s (Student)
    OUTPUT: isSuccess (BOOLEAN)

    BEGIN
        sqlInsert <- "INSERT INTO student (reg_no, full_name, department, year_of_study, cgpa) VALUES (?, ?, ?, ?, ?)"

        TRY (Connection conn <- DatabaseConnectionPool.GetConnection();
             PreparedStatement pstmt <- conn.PrepareStatement(sqlInsert))

            pstmt.SetString(1, s.GetRegNo())
            pstmt.SetString(2, s.GetName())
            pstmt.SetString(3, s.GetDepartment())
            pstmt.SetInt(4, s.GetYear())
            pstmt.SetDouble(5, s.GetCgpa())

            rowsAffected <- pstmt.ExecuteUpdate()
            RETURN (rowsAffected > 0)
        CATCH SQLException
            LOG "Insert failed: " + s.GetRegNo()
            RETURN FALSE
        ENDTRY
    END
ENDCLASS
```

---

## 4. A3.3 – Object-Relational Mapping (ORM) Table

### Mapping between Java `Student` Class and RDBMS `student` Table:

| Java Field (Class `Student`) | Java Data Type | Database Column (`student`) | SQL Data Type | Key Constraints |
| :--- | :--- | :--- | :--- | :--- |
| `regNo` | `String` | `reg_no` | `VARCHAR(30)` | **PRIMARY KEY (Unique, Not Null)** |
| `name` | `String` | `full_name` | `VARCHAR(100)` | `NOT NULL` |
| `department` | `String` | `department` | `VARCHAR(50)` | `NOT NULL` |
| `yearOfStudy` | `int` | `year_of_study` | `INT` | Check ($1 \le \text{year} \le 4$) |
| `cgpa` | `double` | `cgpa` | `DECIMAL(3, 2)`| Check ($0.00 \le \text{cgpa} \le 10.00$) |

- **Primary Key Designation:** `reg_no` uniquely identifies every student across SIMATS; it enforces entity integrity and serves as the B-Tree clustering index in MySQL/InnoDB.

---

## 5. Alternative Concept: Generic DAO vs. Direct Embedded SQL in GUI/Service

Embedding inline SQL strings inside JavaFX/Swing event handlers or service classes is an anti-pattern. The following comparison illustrates why the DAO layer is mandatory in enterprise software engineering:

| Evaluation Dimension | Direct SQL in GUI / Services | Generic DAO Architecture |
| :--- | :--- | :--- |
| **Maintainability** | Extremely poor. SQL queries are scattered across dozens of UI controllers. | High. All persistence logic is isolated in a dedicated persistence package. |
| **Code Duplication** | High. Connection handling, statement preparation, and mapping are re-written everywhere. | Zero. Common CRUD operations are encapsulated and reused. |
| **Impact of Schema Changes** | Catastrophic. Renaming a table column requires modifying 20+ GUI source files. | Minimal. Only the single `mapRowToEntity()` method in the DAO requires updating. |
| **Database Portability** | Inflexible. Switching from MySQL to PostgreSQL requires rewriting client code. | High. Swap the DAO implementation class; business services remain untouched. |
| **Testability (Mocking)** | Impossible to unit test business rules without a live database online. | Trivial. Service layers can be tested using mock in-memory DAO stubs. |
| **Complexity** | $O(1)$ query execution, but $O(M \times N)$ development and maintenance overhead. | $O(1)$ query execution with clean $O(1)$ maintenance complexity. |
