# PROGRAMMING IN JAVA – ASSIGNMENT 5 (CO5)
## DATA PERSISTENCE ENGINEERING – FILES AND DATABASES

---

### Student and Course Credentials
- **Student Name:** K VENKATA VAMSI GOUD
- **Register Number:** 192472021
- **Course Title:** Programming in Java
- **Course Code:** CSA09
- **Course Outcome:** CO5 (Data Persistence Engineering – Files and Databases)
- **Institution:** Saveetha School of Engineering, SIMATS
- **GitHub Repository Name:** `CSA09-192472021-Assignment5`
- **Main Project Folder:** `Assignment-5`

---

## 1. Assignment Objectives
This assignment demonstrates comprehensive mastery of data persistence engineering in Java, spanning both flat-file I/O architectures and relational database management systems (RDBMS):
1. **File Format Engineering:** Delimited text (CSV) vs. binary streams vs. fixed-length random-access files.
2. **I/O Performance Optimization:** Quantitative analysis and experimental measurement of unbuffered vs. buffered stream I/O.
3. **Data Access Patterns:** Decoupling persistence via the Data Access Object (DAO) design pattern.
4. **Object Serialization:** Object-graph traversal, transient fields, and backward compatibility management with `serialVersionUID`.
5. **Database Transaction Integrity & Security:** ACID atomicity in financial transfers, SQL injection mitigation via `PreparedStatement`.
6. **High-Throughput Ingestion:** Empirical evaluation of `Statement` vs. `PreparedStatement` vs. batch processing across 1,000 to 100,000 records.
7. **Enterprise Migration:** Building a fault-tolerant migration pipeline to transition flat files into a relational database with detailed quarantine logging.

---

## 2. System and Environment Requirements
- **Operating System:** Windows 10 / 11 (64-bit)
- **Java Development Kit:** JDK 17 (Eclipse Temurin 17.0.21+9 verified)
- **Relational Database:** MySQL 8.0 Community Server / Pure-Java H2 Relational Database 2.2.224 in MySQL mode
- **JDBC Driver:** MySQL Connector/J 8.3.0 / H2 Database Driver 4.2
- **Python (Optional for Graph Generation):** Python 3.11 with `matplotlib` and `python-docx`

---

## 3. Project Directory Structure

```text
Assignment-5/
│
├── README.md                                  # Complete project documentation and execution guide
├── Assignment-5_Report.docx                   # Comprehensive formal academic report (Word)
├── Assignment-5_Report.pdf                    # Submission-ready PDF version of report
├── db.properties                              # Active database configuration
├── db.properties.example                      # Sample template for local MySQL setup
│
├── Part-A_Design_and_Pseudocode/              # 25 Marks: Theoretical & Algorithmic Designs
│   ├── Task-A1_File_Format/task_a1_design.md
│   ├── Task-A2_Buffered_IO/task_a2_design.md
│   ├── Task-A3_DAO_Pattern/task_a3_design.md
│   ├── Task-A4_Serialization/task_a4_design.md
│   └── Task-A5_Transactions_and_SQL_Injection/task_a5_design.md
│
├── Part-B_Implementation/                     # 25 Marks: Java Source Code, Inputs, Outputs, Evidence
│   ├── Task-B1_File_Store/
│   │   ├── src/ (PassStore.java, BufferingBenchmark.java, SearchGrowthBenchmark.java)
│   │   ├── input/ (sample_passes.txt, test_1MB.txt, test_10MB.txt, test_50MB.txt, etc.)
│   │   ├── output/ (pass_store_output.txt, buffering_benchmark_output.txt, search_benchmark_output.txt)
│   │   ├── measurements/ (buffering_results.csv, file_search_results.csv)
│   │   └── screenshots/ (screenshot_pass_store.png, screenshot_buffering_benchmark.png, etc.)
│   │
│   ├── Task-B2_Adversarial_Input/
│   │   ├── src/ (AdversarialAuthTest.java, AdversarialFileValidator.java)
│   │   ├── input/ (adversarial_passes.txt)
│   │   ├── output/ (sql_injection_output.txt, adversarial_file_output.txt)
│   │   └── screenshots/ (screenshot_sql_injection.png, screenshot_file_validation.png)
│   │
│   ├── Task-B3_JDBC_Batch/
│   │   ├── src/ (PassLoader.java, JdbcPerformanceBenchmark.java)
│   │   ├── sql/ (create_database.sql, create_tables.sql, sample_data.sql)
│   │   ├── output/ (pass_loader_output.txt, jdbc_benchmark_output.txt)
│   │   ├── measurements/ (jdbc_insert_results.csv)
│   │   └── screenshots/ (screenshot_pass_loader_batch_rollback.png, screenshot_jdbc_benchmark.png)
│   │
│   └── Task-B4_Final_Decision/
│       ├── src/ (MigrationProgram.java)
│       ├── input/ (legacy_passes_mixed.txt)
│       ├── output/ (migration_output.txt, migration_rejections.csv)
│       ├── measurements/ (rejections summary)
│       └── screenshots/ (screenshot_migration_report.png)
│
├── Database/                                  # Canonical SQL DDL and DML scripts
│   ├── create_database.sql
│   ├── create_tables.sql
│   └── sample_data.sql
│
├── Performance_Results/                       # Raw benchmark data and visual graphs
│   ├── buffering_results.csv
│   ├── file_search_results.csv
│   ├── jdbc_insert_results.csv
│   ├── performance_analysis.md
│   ├── graph_buffering_performance.png
│   ├── graph_file_search_growth.png
│   └── graph_jdbc_insertion.png
│
├── Screenshots/                               # Consolidated genuine execution evidence
│   ├── Part-A/
│   └── Part-B/
│
└── lib/                                       # Required JDBC Driver Libraries
    ├── h2.jar
    └── mysql-connector-j.jar
```

---

## 4. Database Setup Instructions

### Option A: Local MySQL Server (Default)
1. Ensure MySQL Server is running on port 3306.
2. Log into MySQL CLI:
   ```bash
   mysql -u root -p
   ```
3. Execute the provided SQL scripts:
   ```sql
   SOURCE Database/create_database.sql;
   SOURCE Database/create_tables.sql;
   SOURCE Database/sample_data.sql;
   ```
4. Update `db.properties` with your local MySQL password:
   ```properties
   db.driver=com.mysql.cj.jdbc.Driver
   db.url=jdbc:mysql://localhost:3306/transport_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&rewriteBatchedStatements=true
   db.user=root
   db.password=YOUR_PASSWORD
   ```

### Option B: Embedded Relational Database (Pre-configured Fallback)
The project comes pre-configured with pure-Java H2 running in MySQL compatibility mode. It requires **zero external configuration**, no background services, and zero root credentials:
```properties
db.driver=org.h2.Driver
db.url=jdbc:h2:file:./Part-B_Implementation/Task-B3_JDBC_Batch/output/transport_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;FILE_LOCK=NO;DB_CLOSE_DELAY=-1
db.user=sa
db.password=
```

---

## 5. Compilation and Execution Guide

All commands should be executed from the `Assignment-5/` root directory.

### Step 1: Compile All Java Modules
```powershell
$cp = "bin;lib/h2.jar;lib/mysql-connector-j.jar"
javac -cp $cp -d bin `
  Part-B_Implementation/Task-B1_File_Store/src/*.java `
  Part-B_Implementation/Task-B2_Adversarial_Input/src/*.java `
  Part-B_Implementation/Task-B3_JDBC_Batch/src/*.java `
  Part-B_Implementation/Task-B4_Final_Decision/src/*.java
```

---

### Step 2: Run Task B1 – Bus-Pass Store & Buffering Experiments

#### Run PassStore (TODO 1 Verification):
```powershell
java -cp bin task_b1.PassStore
```
**Expected Output:**
```text
--- Testing findPass for 'P1003' ---
[P1003, 192311003, Divya, 7, 3800.0]

--- Testing findPass for non-existent 'P9999' ---
null
```

#### Run Buffering Benchmark (Task B1.3 - 1 MB, 10 MB, 50 MB):
```powershell
java -cp bin task_b1.BufferingBenchmark
```
*Measures and writes results to `Performance_Results/buffering_results.csv`.*

#### Run File Search Growth Benchmark (Task B1.4 - 1K, 10K, 50K, 100K):
```powershell
java -cp bin task_b1.SearchGrowthBenchmark
```
*Measures and writes results to `Performance_Results/file_search_results.csv`.*

---

### Step 3: Run Task B2 – Adversarial Input Testing

#### Run SQL Injection Demonstration:
```powershell
java -cp "bin;lib/h2.jar" task_b2.AdversarialAuthTest
```
*Demonstrates that `Statement` is breached by `admin' --` while `PreparedStatement` safely repels the attack.*

#### Run Damaged File Validator:
```powershell
java -cp bin task_b2.AdversarialFileValidator
```
*Tests wrong field counts, non-numeric amounts, negative routes, empty lines without crashing.*

---

### Step 4: Run Task B3 – JDBC Batch Processing & Transaction Integrity

#### Run PassLoader (TODO 1, 2, 3 Verification):
```powershell
java -cp "bin;lib/h2.jar;lib/mysql-connector-j.jar" task_b3.PassLoader
```
*Verifies 10-record insertion, `ResultSetMetaData` extraction, transaction rollback of poisoned batch, and 100K loading.*

#### Run Statement vs. PreparedStatement vs. Batch Benchmark:
```powershell
java -cp "bin;lib/h2.jar;lib/mysql-connector-j.jar" task_b3.JdbcPerformanceBenchmark
```
*Measures execution times and saves them to `Performance_Results/jdbc_insert_results.csv`.*

---

### Step 5: Run Task B4 – Migration Pipeline & Database Metadata

```powershell
java -cp "bin;lib/h2.jar;lib/mysql-connector-j.jar" task_b4.MigrationProgram
```
*Migrates legacy flat-file data into relational database with batching, logs rejected rows to `migration_rejections.csv`, and displays `DatabaseMetaData`.*

---

## 6. Summary of Experimental Results

| Benchmark | Test Scale | Key Finding | Speedup / Scalability |
| :--- | :--- | :--- | :--- |
| **Buffering I/O** | 50 MB File | Unbuffered: 176,850 ms vs. Buffered: 31 ms | **5,704.8x faster** |
| **File Search** | 100,000 Records | Best Case: 4.05 ms vs. Worst Case: 14.91 ms | **Linear $O(N)$ growth** |
| **JDBC Ingestion** | 1,000 Records | Statement: 154 ms vs. Batch: 20 ms | **7.7x faster** |
| **ACID Rollback** | Row 11 Poisoned | Zero rows persisted after exception | **100% Atomicity preserved** |

---

## 7. Submission Checklist & Verification
- [x] Part A: All 5 design tasks completed with SIMATS algorithmic pseudocode and Big-O complexity.
- [x] Part B: `PassStore` TODO 1 completed exactly as specified.
- [x] Part B: `PassLoader` TODO 1, 2, and 3 completed and transaction rollback verified.
- [x] Part B: Adversarial SQL injection and damaged file validation fully executed.
- [x] Part B: Real empirical measurements captured in CSV format.
- [x] Part B: High-resolution performance graphs generated.
- [x] Part B: Database metadata and migration pipeline implemented.
- [x] Authentic execution screenshots captured and cataloged.
- [x] Formal academic Word report (`Assignment-5_Report.docx`) generated.
- [x] Submission PDF (`Assignment-5_Report.pdf`) generated.
- [x] Final ZIP archive (`CSA09-192472021-Assignment5.zip`) prepared.
