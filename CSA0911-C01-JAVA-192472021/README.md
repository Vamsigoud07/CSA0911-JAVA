# Campus Smart-Parking System – Java Assignment 1 (CO1)

**Course Code & Title:** CSA09 – Programming in Java  
**Component:** Assignment 1 (Course Outcome 1)  
**Total Marks:** 50  
**Student Name:** K VENKATA VAMSI GOUD  
**Register Number:** 192472021  
**Institution:** SIMATS – Saveetha School of Engineering, Saveetha University  
**GitHub Repository Name:** `CSA09-192472021-Assignment1`  
**GitHub Repository URL:** `https://github.com/vamsi/CSA09-192472021-Assignment1` *(Replace with your GitHub profile handle upon upload)*  

---

## 1. Project Overview
The **Campus Smart-Parking System** is an end-to-end, enterprise-grade object-oriented application written in Java (JDK 17 LTS). It solves real-world university parking challenges—including congestion, unauthorized vehicles, inaccurate time tracking, and revenue leakage—by automating vehicular ingress, tiered tariff calculation, slot allocation across asymmetric multi-floor grids (jagged arrays), visitor day-passes, dynamic peak pricing, and real-time ledger accounting.

---

## 2. Technical Stack & Prerequisites
- **Language:** Java Standard Edition (Java SE)
- **Tested JDK Version:** OpenJDK 17.0.21 LTS (Amazon Corretto / Eclipse Temurin)
- **Supported Operating Systems:** Windows 10/11, Linux, macOS
- **Build Tools:** Standard `javac` compiler and `java` launcher (no external heavy frameworks required; standard Java API only)
- **Documentation Tools:** Microsoft Word (`Assignment-1-Report.docx`), Markdown, and Python (Pillow/Matplotlib for diagram generation)

---

## 3. Project Directory Structure
```
CSA09-192472021-Assignment1/
│
├── Assignment-1-Report.docx       # Complete 43-page professionally formatted academic report
├── README.md                      # Comprehensive execution & technical documentation
│
├── Task01/
│   └── Task01_ProblemAnalysis.md  # Domain analysis, candidate classes, OOP vs parallel arrays
│
├── Task02/
│   └── Task02_DataRepresentation.md # Primitives vs reference types, jagged arrays, Scanner pitfalls
│
├── Task03/
│   ├── ParkingFacility.java       # Control logic implementation (mandatory output)
│   ├── Task03_Pseudocode.md       # Standard algorithmic pseudocode & control structures
│   └── screenshots/               # Actual terminal execution screenshot
│
├── Task04/
│   ├── ConstructorDemo.java       # Overloading, chaining this(), super(), copy constructors
│   └── screenshots/
│
├── Task05/
│   ├── StaticMemberDemo.java      # Static counters, object lifecycle, multi-facility telemetry
│   └── screenshots/
│
├── Task06/
│   ├── VehicleHierarchy/          # Vehicle, TwoWheeler, Car, ElectricCar, DeliveryVan, Demo
│   ├── VehicleUML.png             # High-resolution UML Class Diagram
│   └── screenshots/
│
├── Task07/
│   ├── MethodOverridingDemo.java  # @Override, toString(), equals/hashCode contract, final
│   └── screenshots/
│
├── Task08/
│   ├── FeeDemo.java               # Exact assignment code, upcasting, dynamic dispatch
│   ├── Chargeable.java            # Interface contract with constants & green rebates
│   └── screenshots/
│
├── Task09/
│   ├── AbstractionDemo.java       # Abstract classes vs interfaces, default & static methods
│   └── screenshots/
│
├── Task10/
│   ├── parking/
│   │   ├── model/                 # Vehicle.java, ParkingSlot.java, ParkingTicket.java
│   │   ├── service/               # ParkingService.java, FeeCalculationService.java
│   │   └── app/                   # ParkingApp.java (Integer cache trap & wrappers)
│   └── screenshots/
│
├── Task11/
│   ├── CompleteParkingSystem/     # Complete 14-requirement menu-driven application
│   │   ├── VehicleCategory.java
│   │   ├── Vehicle.java
│   │   ├── ParkingSlot.java
│   │   ├── ParkingTicket.java
│   │   ├── SmartParkingManager.java
│   │   ├── SmartParkingConsoleApp.java
│   │   └── AutomatedTestDriver.java
│   ├── TestCases.md               # 11-test execution matrix & manual revenue reconciliation
│   └── screenshots/
│
└── Task12/
    ├── ExtendedParkingSystem/     # Day-4 Design Challenge extension
    │   ├── PricingStrategy.java   # Strategy Pattern interface
    │   ├── ParkingPass.java       # One-day visitor pass (waives hourly fees)
    │   ├── ExtendedVehicle.java   # Abstract base vehicle
    │   ├── EBike.java             # Flat Rs 15.00 pricing
    │   ├── DeliveryVan.java       # Double rate between 9 AM and 5 PM
    │   ├── ExtendedCar.java
    │   ├── ExtendedParkingFacility.java # 5-Floor jagged array facility
    │   └── DesignChallengeDemo.java
    ├── DesignChallenge.md         # Impact analysis, OCP evaluation & evidence-based defence
    └── screenshots/
```

---

## 4. Compilation and Execution Guide (Task-by-Task)

From the project root directory (`CSA09-192472021-Assignment1`), run the commands below using standard `javac` and `java`:

### Task 03: Control Logic
```bash
javac -d bin Task03/ParkingFacility.java
java -cp bin Task03.ParkingFacility
```
**Expected Mandatory Output:**
```
[0, 0]
[0, 1]
[1, 0]
null
true false false
[0, 1]
```

### Task 04: Classes, Objects, and Constructors
```bash
javac -d bin Task04/ConstructorDemo.java
java -cp bin Task04.ConstructorDemo
```

### Task 05: Static Members and Object Lifecycle
```bash
javac -d bin Task05/StaticMemberDemo.java
java -cp bin Task05.StaticMemberDemo
```

### Task 06: Vehicle Inheritance Hierarchy
```bash
javac -d bin Task06/VehicleHierarchy/*.java
java -cp bin Task06.VehicleHierarchy.InheritanceDemo
```

### Task 07: Method Overriding and Contracts
```bash
javac -d bin Task07/MethodOverridingDemo.java
java -cp bin Task07.MethodOverridingDemo
```

### Task 08: Polymorphism and Interfaces
```bash
javac -d bin Task08/Chargeable.java Task08/FeeDemo.java
java -cp bin Task08.FeeDemo
```
**Expected Mandatory Output:**
```
TwoWheeler TN01A1 -> Rs 30.0
Car TN02B2 -> Rs 90.0
ElectricCar TN03C3 -> Rs 210.0
true
```

### Task 09: Abstraction and Multiple Interfaces
```bash
javac -d bin Task09/AbstractionDemo.java
java -cp bin Task09.AbstractionDemo
```

### Task 10: Packages and Encapsulation
```bash
javac -d bin Task10/parking/model/*.java Task10/parking/service/*.java Task10/parking/app/*.java
java -cp bin parking.app.ParkingApp
```

### Task 11: Complete End-to-End Parking System
**1. Interactive Console Menu Application:**
```bash
javac -d bin Task11/CompleteParkingSystem/*.java
java -cp bin Task11.CompleteParkingSystem.SmartParkingConsoleApp
```
**2. Automated Test Suite (All 11 Test Cases):**
```bash
java -cp bin Task11.CompleteParkingSystem.AutomatedTestDriver
```

### Task 12: Day-4 Design Challenge
```bash
javac -d bin Task12/ExtendedParkingSystem/*.java
java -cp bin Task12.ExtendedParkingSystem.DesignChallengeDemo
```

---

## 5. Automated Test Results Summary (Task 11)

| Test ID | Test Scenario | Input / Action | Observed Output | Status |
|---|---|---|---|---|
| **TC01** | Normal Parking (Car Ingress) | `TN01A1000` | Bay `[0, 1]` Allocated | **PASS** |
| **TC02** | Category Isolation (Bike) | `TN01B2000` | Bay `[0, 0]` Dedicated | **PASS** |
| **TC03** | Last Available Slot Allocation | `TN01C3000` | Bay `[1, 0]` (Free: 0) | **PASS** |
| **TC04** | Full Parking Rejection | `TN01D4000` | Rejected (`null`) | **PASS** |
| **TC05** | Grace Period Stay ($\le 15$ min) | `TN01B2000` (10m) | Fee = `Rs 0.0` (Free) | **PASS** |
| **TC06** | Fee Boundary Check | `60 min` vs `61 min` | `Rs 10.0` vs `Rs 20.0` | **PASS** |
| **TC07** | Duplicate Plate Prevention | `TN01A1000` | Caught Duplicate | **PASS** |
| **TC08** | Blank Plate Rejection | Whitespace (`"   "`) | Caught Invalid Reg | **PASS** |
| **TC09** | Unknown Vehicle Exit | `AP99ZZ9999` | Caught NoSuchRecord | **PASS** |
| **TC10** | Vehicle Exit & Slot Deallocation | `TN01A1000` (125m) | `Rs 90.0` (Slot freed) | **PASS** |
| **TC11** | Daily Revenue Reconciliation | Sum of 3 Checkouts | `Rs 120.0` Reconciled | **PASS** |

**Result:** **11 / 11 Tests Passed (100.0% Success Rate)**.

---

## 6. Git & GitHub Publishing Instructions

To upload this assignment repository to your personal GitHub account:

1. **Initialize Git inside the project directory:**
   ```bash
   cd CSA09-192472021-Assignment1
   git init
   git branch -M main
   ```
2. **Stage and commit all completed assignment files:**
   ```bash
   git add .
   git commit -m "feat: complete SIMATS Java Assignment 1 (Tasks 1 to 12, report, and tests)"
   ```
3. **Link to your newly created GitHub repository:**
   ```bash
   git remote add origin https://github.com/<your-username>/CSA09-192472021-Assignment1.git
   ```
4. **Push the repository to GitHub:**
   ```bash
   git push -u origin main
   ```

---
*Created by **K VENKATA VAMSI GOUD (Register No: 192472021)** for SIMATS – Saveetha School of Engineering.*
