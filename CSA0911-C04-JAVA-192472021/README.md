# CSA09 – Programming in Java: Assignment 4 (CO4)
## Event-Driven GUI Design

**Institution**: SIMATS – Saveetha School of Engineering  
**Department**: Computer Science and Engineering  
**Academic Year**: 2026–2027  
**Nature of Submission**: INDIVIDUAL ASSIGNMENT  

---

### Student Credentials
- **Student Name**: K VENKATA VAMSI GOUD
- **Register Number**: 192472021
- **Course Code**: CSA09
- **Course Name**: Programming in Java
- **Topic**: Event-Driven GUI Design (AWT, Swing, JDBC, SwingWorker)
- **GitHub Repository**: [CSA09-192472021-Assignment4](https://github.com/vamsigoud192472021/CSA09-192472021-Assignment4)

---

## 1. Project Directory Structure

```
CSA09-192472021-Assignment4/
│
├── README.md                                  # Complete project documentation & execution guide
├── Assignment4_Report.docx                    # Comprehensive 810 KB academic laboratory report
├── .gitignore                                 # Git exclusion rules
│
├── lib/
│   ├── ecj.jar                                # Eclipse Compiler for Java (v3.26.0)
│   └── mysql-connector-j-8.3.0.jar            # MySQL JDBC Connector Type 4 Driver
│
├── Task01_GUI_Design_Review/
│   ├── src/
│   │   ├── PoorFeeWindow.java                 # Diagnosed flawed legacy implementation
│   │   ├── FeeWindow.java                     # Repaired GUI with SwingWorker & feedback
│   │   ├── FeeService.java                    # Business logic & input bounds validation
│   │   ├── FeeRepository.java                 # PreparedStatement JDBC data access layer
│   │   ├── PaymentResult.java                 # Immutable value object for payment receipts
│   │   ├── CaptureHelper.java                 # Native screen capture utility
│   │   └── Task01Runner.java                  # Automated test & screenshot runner
│   ├── screenshots/                           # Genuine window captures (Task01_*.png)
│   ├── results/                               # Empirical test logs (test_results.txt)
│   └── analysis.md                            # Comprehensive 11-defect diagnostic review
│
├── Task02_Event_Driven_Design/
│   ├── src/
│   │   ├── Doctor.java                        # Medical specialist model
│   │   ├── AppointmentDesk.java               # Event-driven hospital appointment desk
│   │   └── Task02Runner.java                  # Event cascade test runner
│   ├── diagrams/                              # Component containment hierarchy tree
│   ├── screenshots/                           # Appointment window & exit confirmation dialogs
│   └── analysis.md                            # Event tables, SIMATS pseudocode & applet comparison
│
├── Task03_Layout_Design/
│   ├── src/
│   │   ├── Book.java                          # Library catalog entity
│   │   ├── LibrarySearchScreen.java           # Multi-panel library search with expanding JTable
│   │   └── Task03Runner.java                  # Multi-resolution resizing test runner
│   ├── screenshots/                           # Small (480x360), Medium (720x480), Large (1024x700)
│   └── analysis.md                            # Layout manager evaluation & resizing analysis
│
├── Task04_Event_Handling_Forensics/
│   ├── src/
│   │   ├── DrawPadFlawed.java                 # Defective code with misspelled callback & getGraphics
│   │   ├── DrawPad.java                       # Repaired Complete-the-Code vector drawing pad
│   │   ├── DrawPadBuffered.java               # Alternative off-screen BufferedImage raster pad
│   │   ├── MisspelledOverrideExperiment.java  # Deliberate @Override compile-time experiment
│   │   └── Task04Runner.java                  # Persistence & minimize/restore test runner
│   ├── screenshots/                           # Before/After minimize & click count captures
│   ├── results/                               # Captured compiler diagnostic error output
│   └── analysis.md                            # Forensic defect report & complexity trade-offs
│
├── Task05_Event_Handling_Styles/
│   ├── src/
│   │   ├── ConverterFrameListener.java        # Style A: Frame implements ActionListener
│   │   ├── ConverterNamedInnerClass.java      # Style B: Named inner class handler
│   │   ├── ConverterAnonymousClass.java       # Style C: Anonymous inner class handler
│   │   ├── ConverterLambda.java               # Style D: Java 8 Lambda expression handler
│   │   ├── Converter.java                     # Mandatory Complete-the-Code official implementation
│   │   ├── ConverterActionDemo.java           # Alternative concept: Swing AbstractAction
│   │   └── Task05Runner.java                  # Test runner covering all 4 styles & dialogs
│   ├── screenshots/                           # Valid USD, invalid amount, and Enter key captures
│   ├── results/                               # Test logs verifying all 4 event-handling styles
│   └── analysis.md                            # Comparative evaluation & code-size metrics
│
└── Task06_Campus_Event_Registration/
    ├── src/com/campus/
    │   ├── model/                             # Student.java, Event.java, Registration.java
    │   ├── exception/                         # ValidationException.java
    │   ├── util/                              # DatabaseConnection.java
    │   ├── dao/                               # RegistrationDAO.java (Transactional PreparedStatement)
    │   ├── service/                           # RegistrationService.java (Validation & CSV exporter)
    │   ├── ui/                                # MainFrame, RegistrationPanel, TablePanel, TableModel
    │   ├── Main.java                          # Desktop application launcher
    │   └── Task06Runner.java                  # Automated 9-scenario verification suite
    ├── database/
    │   └── event_registration.sql             # Complete MySQL relational schema & seed data
    ├── screenshots/                           # 6 genuine screenshots: Dashboard, Valid, Duplicate, etc.
    ├── results/                               # Test execution logs & registrations_export.csv
    └── analysis.md                            # Complete architecture, pseudocode, and pagination analysis
```

---

## 2. Prerequisites & Dependencies

- **Java Development Kit**: JDK 1.8.0 or newer (tested with IBM J9 VM / OpenJDK 8+).
- **Java Compiler**: Standalone ECJ compiler provided in `lib/ecj.jar` (or system `javac`).
- **Relational Database**: MySQL Server 8.0 (optional; system includes seamless resilient transactional fallback).
- **JDBC Driver**: MySQL Connector/J 8.3.0 provided in `lib/mysql-connector-j-8.3.0.jar`.

---

## 3. Database Setup Instructions (Task 6)

1. Open MySQL Workbench or MySQL Shell 8.0:
   ```bash
   mysql -u root -p
   ```
2. Execute the provided SQL script located in `Task06_Campus_Event_Registration/database/event_registration.sql`:
   ```sql
   SOURCE Task06_Campus_Event_Registration/database/event_registration.sql;
   ```
3. Verify created tables and constraints:
   ```sql
   USE campus_events_db;
   SHOW TABLES;
   SELECT * FROM events;
   ```

### Database Environment Variables (Optional):
The application safely reads database credentials from the operating system environment:
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/campus_events_db"
$env:DB_USER="root"
$env:DB_PASSWORD="your_password"
```
*Note: If MySQL is offline, the application transparently engages its synchronized in-memory relational fallback engine with full duplicate checking and capacity transactions.*

---

## 4. Compilation and Execution Instructions

All tasks can be built and executed from the project root using either `ecj.jar` or standard `javac`:

### Task 1 – GUI Design Review
```powershell
# Compile
java -cp "lib/ecj.jar" org.eclipse.jdt.internal.compiler.batch.Main -8 -warn:-serial Task01_GUI_Design_Review/src/*.java -d Task01_GUI_Design_Review/bin

# Run Test Suite & Generate Screenshots
java -cp "Task01_GUI_Design_Review/bin" task01.Task01Runner .

# Run Interactive Fee Window
java -cp "Task01_GUI_Design_Review/bin" task01.FeeWindow
```

### Task 2 – Hospital Outpatient Appointment Desk
```powershell
# Compile
java -cp "lib/ecj.jar;Task01_GUI_Design_Review/bin" org.eclipse.jdt.internal.compiler.batch.Main -8 -warn:-serial -cp "Task01_GUI_Design_Review/bin" Task02_Event_Driven_Design/src/*.java -d Task02_Event_Driven_Design/bin

# Run Test Suite & Generate Screenshots
java -cp "Task02_Event_Driven_Design/bin;Task01_GUI_Design_Review/bin" task02.Task02Runner .

# Run Interactive Desk
java -cp "Task02_Event_Driven_Design/bin" task02.AppointmentDesk
```

### Task 3 – Campus Library Search Screen
```powershell
# Compile
java -cp "lib/ecj.jar;Task01_GUI_Design_Review/bin" org.eclipse.jdt.internal.compiler.batch.Main -8 -warn:-serial -cp "Task01_GUI_Design_Review/bin" Task03_Layout_Design/src/*.java -d Task03_Layout_Design/bin

# Run Resizing Tests (Small, Medium, Large)
java -cp "Task03_Layout_Design/bin;Task01_GUI_Design_Review/bin" task03.Task03Runner .

# Run Interactive Search Screen
java -cp "Task03_Layout_Design/bin" task03.LibrarySearchScreen
```

### Task 4 – Drawing Pad Forensics & Debugging
```powershell
# Run Deliberate Compiler Experiment (shows @Override catching misspelled callback)
java -cp "lib/ecj.jar" org.eclipse.jdt.internal.compiler.batch.Main -8 -warn:-serial Task04_Event_Handling_Forensics/src/MisspelledOverrideExperiment.java

# Compile Repaired Application
java -cp "lib/ecj.jar;Task01_GUI_Design_Review/bin" org.eclipse.jdt.internal.compiler.batch.Main -8 -warn:-serial -cp "Task01_GUI_Design_Review/bin" Task04_Event_Handling_Forensics/src/DrawPad.java Task04_Event_Handling_Forensics/src/DrawPadFlawed.java Task04_Event_Handling_Forensics/src/DrawPadBuffered.java Task04_Event_Handling_Forensics/src/Task04Runner.java -d Task04_Event_Handling_Forensics/bin

# Run Persistence & Minimize/Restore Tests
java -cp "Task04_Event_Handling_Forensics/bin;Task01_GUI_Design_Review/bin" task04.Task04Runner .

# Run Interactive DrawPad
java -cp "Task04_Event_Handling_Forensics/bin" task04.DrawPad
```

### Task 5 – Currency Converter (4 Styles + Mandatory Complete-the-Code)
```powershell
# Compile
java -cp "lib/ecj.jar;Task01_GUI_Design_Review/bin" org.eclipse.jdt.internal.compiler.batch.Main -8 -warn:-serial -cp "Task01_GUI_Design_Review/bin" Task05_Event_Handling_Styles/src/*.java -d Task05_Event_Handling_Styles/bin

# Run Test Suite & Generate Screenshots
java -cp "Task05_Event_Handling_Styles/bin;Task01_GUI_Design_Review/bin" task05.Task05Runner .

# Run Mandatory Converter.java
java -cp "Task05_Event_Handling_Styles/bin" task05.Converter
```

### Task 6 – Campus Event Registration Desktop Application
```powershell
# Compile Layered Architecture
java -cp "lib/ecj.jar;lib/mysql-connector-j-8.3.0.jar;Task01_GUI_Design_Review/bin" org.eclipse.jdt.internal.compiler.batch.Main -8 -warn:-serial -cp "lib/mysql-connector-j-8.3.0.jar;Task01_GUI_Design_Review/bin" Task06_Campus_Event_Registration/src/com/campus/model/*.java Task06_Campus_Event_Registration/src/com/campus/exception/*.java Task06_Campus_Event_Registration/src/com/campus/util/*.java Task06_Campus_Event_Registration/src/com/campus/dao/*.java Task06_Campus_Event_Registration/src/com/campus/service/*.java Task06_Campus_Event_Registration/src/com/campus/ui/*.java Task06_Campus_Event_Registration/src/com/campus/Main.java Task06_Campus_Event_Registration/src/com/campus/Task06Runner.java -d Task06_Campus_Event_Registration/bin

# Run Complete 9-Scenario Automated Test Suite
java -cp "Task06_Campus_Event_Registration/bin;lib/mysql-connector-j-8.3.0.jar;Task01_GUI_Design_Review/bin" com.campus.Task06Runner .

# Launch Desktop GUI Application
java -cp "Task06_Campus_Event_Registration/bin;lib/mysql-connector-j-8.3.0.jar" com.campus.Main
```

---

## 5. Experimental Test Results Summary

| Task | Test Description | Key Execution Result | Verdict |
|:---:|:---|:---|:---:|
| **Task 1** | Monolithic Code Refactoring | Layered architecture implemented; SwingWorker non-blocking payment; PreparedStatement SQL safety. | **PASS** |
| **Task 2** | Appointment Desk Event Flow | Dynamic doctor cascade on department change; modal windowClosing confirmation. | **PASS** |
| **Task 3** | Library Search Resizing | Responsive nested layout tested at Small (480x360), Medium (720x480), and Large (1024x700). | **PASS** |
| **Task 4** | DrawPad Forensics & @Override | Fixed `mouseClicked(MouseEvent e)`; vector dot persistence verified across minimize/restore. | **PASS** |
| **Task 5** | Four Event-Handling Styles | Frame Listener, Named Inner, Anonymous, and Lambda benchmarked; Enter key trigger tested. | **PASS** |
| **Task 6** | Desktop Event Registration | All 9 test scenarios passed (Valid, Missing Name, Invalid Email, Duplicate, Capacity, Filter, CSV Export). | **PASS** |

---

## 6. Git Progressive Commit Plan

```bash
git init
git remote add origin https://github.com/vamsigoud192472021/CSA09-192472021-Assignment4.git

# Commit 1: Initial project structure and documentation
git add README.md .gitignore lib/
git commit -m "feat: initialize repository structure, libraries, and README"

# Commit 2: Task 1 GUI design review
git add Task01_GUI_Design_Review/
git commit -m "feat(task1): diagnose poor GUI design and implement layered FeeWindow with SwingWorker"

# Commit 3: Task 2 event-driven design
git add Task02_Event_Driven_Design/
git commit -m "feat(task2): implement hospital appointment desk with dynamic doctor cascade and event model"

# Commit 4: Task 3 layout design
git add Task03_Layout_Design/
git commit -m "feat(task3): construct campus library search screen with multi-resolution resizing evidence"

# Commit 5: Task 4 drawing pad forensics
git add Task04_Event_Handling_Forensics/
git commit -m "feat(task4): repair DrawPad callback defect, ensure repaint persistence, and document @Override"

# Commit 6: Task 5 event handling styles
git add Task05_Event_Handling_Styles/
git commit -m "feat(task5): implement four converter event styles and mandatory Converter lambda"

# Commit 7: Task 6 campus event registration
git add Task06_Campus_Event_Registration/
git commit -m "feat(task6): implement Swing-JDBC campus event registration desktop application with MySQL schema"

# Commit 8: Final report and project packaging
git add Assignment4_Report.docx
git commit -m "docs: generate comprehensive academic laboratory report with embedded execution evidence"
```

---
*Created by K VENKATA VAMSI GOUD (192472021) for SIMATS University Course CSA09.*
