# TASK 11: Test Suite Specification and Manual Audit Verification

**Student Name:** K VENKATA VAMSI GOUD  
**Register Number:** 192472021  
**Course Code & Title:** CSA09 – Programming in Java  
**Institution:** SIMATS – Saveetha School of Engineering  

---

## 1. Test Suite Execution Matrix

The following test matrix documents the execution of 11 comprehensive automated test scenarios designed to validate all 14 core functional requirements of the Campus Smart-Parking System.

| Test ID | Test Scenario | Vehicle Category | Test Input Parameters | Expected System Behavior | Actual Observed Output | Status |
|---|---|---|---|---|---|---|
| **TC01** | Normal Parking Ingress | `Car` | Reg: `TN01A1000`, Owner: `Student A`, Time: `10:00 AM` | Allocates first available Car slot on Floor 0, Slot 1; generates ticket. | Ticket `TKT-01001` issued; Bay `[0, 1]` occupied. | **PASS** |
| **TC02** | Category Isolation | `Two-Wheeler` | Reg: `TN01B2000`, Owner: `Faculty B`, Time: `10:00 AM` | Allocates dedicated bike slot on Floor 0, Slot 0; isolates from car bays. | Ticket `TKT-01002` issued; Bay `[0, 0]` occupied. | **PASS** |
| **TC03** | Last Available Slot | `Car` | Reg: `TN01C3000`, Owner: `Visitor C`, Time: `10:00 AM` | Allocates final remaining vacant slot on Floor 1, Slot 0; free slot count drops to 0. | Ticket `TKT-01003` issued; Bay `[1, 0]` occupied; Remaining: `0`. | **PASS** |
| **TC04** | Full Facility Rejection | `Car` | Reg: `TN01D4000`, Owner: `Visitor D`, Time: `10:00 AM` | Facility detects zero free bays; denies entry and returns `null`. | Ingress rejected with `[FACILITY FULL]` notification; returns `null`. | **PASS** |
| **TC05** | Grace Period Stay (Zero Fee) | `Two-Wheeler` | Reg: `TN01B2000`, Duration: `10 minutes` ($\le 15$ min) | Applies 15-minute grace period policy; returns Rs 0.0 fee. | Exit processed; Billed Fee = `Rs 0.00`; Slot freed. | **PASS** |
| **TC06** | Fee Slab Boundary Check | `Two-Wheeler` | Test A: `60 min` (exact 1 hr), Test B: `61 min` (ceil 2 hrs) | Test A bills exactly 1 hr (Rs 10.0); Test B rounds up to 2 hrs (Rs 20.0). | `60 min -> Rs 10.0`; `61 min -> Rs 20.0`. Exact boundary confirmed. | **PASS** |
| **TC07** | Duplicate Plate Prevention | `Car` | Duplicate Reg: `TN01A1000` already parked inside | Intercepts duplicate registration attempt and throws `IllegalStateException`. | `IllegalStateException` thrown: `"Duplicate entry rejected"`. | **PASS** |
| **TC08** | Blank Plate Validation | `Car` | Registration: `"   "` (empty string / whitespace) | Validates non-blank constraint and throws `IllegalArgumentException`. | `IllegalArgumentException` thrown: `"Registration plate cannot be blank"`. | **PASS** |
| **TC09** | Unknown Vehicle Egress | N/A | Reg: `AP99ZZ9999` (non-existent record) | Intercepts invalid exit request and throws `NoSuchElementException`. | `NoSuchElementException` caught: `"No active parking record found"`. | **PASS** |
| **TC10** | Vehicle Exit & Bay Release | `Car` | Reg: `TN01A1000`, Parked: `125 min` (10:00 to 12:05) | Computes 3 billed hours ($3 \times 30 = \text{Rs } 90.0$); frees bay `[0, 1]`. | Exit processed; Billed Fee = `Rs 90.00`; Available slots incremented. | **PASS** |
| **TC11** | Daily Revenue Reconciliation | System-wide | Sum of all closed sessions: `TC05 + TC10 + TC03` exit | Total revenue equals exact mathematical sum of closed sessions ($0 + 90 + 30 = \text{Rs } 120.0$). | Daily Total = `Rs 120.00`; Reconciled $100\%$ with ledger. | **PASS** |

---

## 2. Sample Day Manual Revenue Reconciliation

To demonstrate mathematical correctness, a simulated operating session is calculated by hand and compared against the program's output.

### 2.1 Session Activity Log:
1. **Vehicle 1 (`TN01B2000`, Two-Wheeler):**
   - Entry: `10:00 AM`
   - Exit: `10:10 AM`
   - Duration: $10 \text{ minutes}$
   - Rule Applied: $\text{Duration} \le 15 \text{ min}$ (Grace period waiver)
   - Calculated Fee: $\mathbf{\text{Rs } 0.00}$

2. **Vehicle 2 (`TN01A1000`, Car):**
   - Entry: `10:00 AM`
   - Exit: `12:05 PM`
   - Duration: $125 \text{ minutes}$
   - Rule Applied: $\lceil 125 / 60 \rceil = 3 \text{ billed hours}$. Base Car rate = $\text{Rs } 30.0/\text{hr}$.
   - Calculation: $3 \times \text{Rs } 30.0 = \mathbf{\text{Rs } 90.00}$

3. **Vehicle 3 (`TN01C3000`, Car):**
   - Entry: `10:00 AM`
   - Exit: `11:00 AM`
   - Duration: $60 \text{ minutes}$
   - Rule Applied: $\lceil 60 / 60 \rceil = 1 \text{ billed hour}$. Base Car rate = $\text{Rs } 30.0/\text{hr}$.
   - Calculation: $1 \times \text{Rs } 30.0 = \mathbf{\text{Rs } 30.00}$

### 2.2 Mathematical Summation:
$$\text{Total Daily Revenue} = \text{Rs } 0.00 + \text{Rs } 90.00 + \text{Rs } 30.00 = \mathbf{\text{Rs } 120.00}$$

### 2.3 Software Output Verification:
- Programmed Telemetry Call: `testFacility.getDailyTotalRevenue()`
- Program Output: `Rs 120.00`
- Variance: $\mathbf{\text{Rs } 0.00}$ (Exact mathematical match; zero discrepancy).

---
