# TASK 1: Problem Analysis and Candidate Objects

**Student Name:** K VENKATA VAMSI GOUD  
**Register Number:** 192472021  
**Course Code & Title:** CSA09 – Programming in Java  
**Institution:** SIMATS – Saveetha School of Engineering  

---

## 1. System Overview
The **Campus Smart-Parking System** is an automated, object-oriented software application engineered to manage vehicular ingress, parking bay allocation, fee computation, and vehicular egress within a university campus environment. The system addresses common campus parking bottlenecks—including congestion, unauthorized vehicle presence, lack of occupancy visibility, and manual ticketing errors—by providing automated slot assignment based on vehicle dimensions and engine type, robust validation against duplicate entries, tiered fee calculations, and comprehensive audit tracking.

---

## 2. System Inputs and Outputs

### 2.1 System Inputs
1. **Vehicle Registration Number (`String`):** Unique alphanumeric plate identification conforming to standard motor vehicle registration formats (e.g., `"TN01A1"`, `"TN02B2"`, `"AP39AB1234"`).
2. **Vehicle Category (`Enum` / `String`):** Classification determining slot compatibility and fee schedule (`TwoWheeler`, `Car`, `ElectricCar`, `DeliveryVan`, `EBike`).
3. **Driver / User Information (`String`):** User name, university ID / Roll Number, and user role (`Student`, `Faculty`, `Visitor`, `DeliveryPartner`).
4. **Ingress Timestamp (`LocalDateTime`):** System-generated or operator-entered timestamp recording the exact minute of entry.
5. **Egress Timestamp (`LocalDateTime`):** Timestamp recorded when the vehicle exits the facility.
6. **Floor and Slot Selection / Request:** Desired floor level or automated allocation request.
7. **Pass / Permit Token (`String`):** Verification code for pre-purchased single-day or semester parking passes.

### 2.2 System Outputs
1. **Parking Ticket (`ParkingTicket`):** A generated record containing:
   - Unique Ticket ID (e.g., `TKT-1001`)
   - Allocated Floor Number and Slot Index (e.g., `Floor 0, Slot 1`)
   - Vehicle Registration Number and Category
   - Formatted Entry Timestamp
2. **Real-Time Occupancy Summary:** Display of available vs. occupied slots per floor and category.
3. **Exit Receipt / Billing Invoice:**
   - Total parked duration (hours and minutes)
   - Applicable fee slab rate
   - Special surcharges (e.g., peak-hour commercial delivery rate) or discounts (e.g., EV green discount, Day-Pass waiver)
   - Final payable amount in Indian Rupees (INR)
4. **Slot Deallocation Notification:** Confirmation that the parking bay has been marked vacant.
5. **Daily Revenue and Statistical Report:** Aggregated count of vehicles processed and total revenue collected.

---

## 3. Candidate Classes and Their Responsibilities

| Class Name | Primary Responsibility | Key Attributes | Key Methods |
|---|---|---|---|
| **`Vehicle`** (Abstract) | Serves as the generalized base abstraction for all vehicles entering the campus. | `regNumber`, `brand`, `model`, `category` | `getRegNumber()`, `getCategory()`, `calculateFee(long hours)` |
| **`ParkingSlot`** | Encapsulates the state and properties of an individual parking space. | `slotId`, `floorNumber`, `slotNumber`, `supportedCategory`, `isOccupied`, `currentVehicle` | `assignVehicle(Vehicle v)`, `vacateSlot()`, `isAvailable()` |
| **`ParkingFloor`** | Models a physical building level containing an array/collection of parking slots. | `floorNumber`, `slots[]`, `maxCapacity` | `findAvailableSlot(VehicleCategory cat)`, `getOccupancyRate()`, `displayFloorStatus()` |
| **`ParkingTicket`** | Represents the active contractual token issued upon vehicle entry. | `ticketId`, `vehicle`, `allocatedSlot`, `entryTime`, `exitTime`, `feePaid`, `isActive` | `markExit(LocalDateTime time, double fee)`, `getDurationHours()`, `printTicket()` |
| **`ParkingFacility`** | Root orchestrator managing multiple floors, slot allocation algorithms, and facility capacity. | `facilityName`, `floors[]`, `totalCapacity`, `activeTicketsMap` | `allocateSlot(Vehicle v)`, `processExit(String regNo)`, `isFacilityFull()` |
| **`User`** | Models the vehicle owner or operator on campus. | `userId`, `name`, `contactNumber`, `userType` | `getUserDetails()`, `validateAuthorization()` |
| **`ParkingPass`** | Manages pre-paid parking privileges (e.g., Day-Pass, Semester Pass). | `passId`, `validDate`, `assignedUser`, `passType`, `isValid` | `validatePass(LocalDate date)`, `applyPassWaiver()` |
| **`FeeCalculator`** | Encapsulates the pricing strategies and business logic for parking charges. | `baseRatesMap`, `peakMultiplier`, `evChargingSurcharge` | `computeFee(Vehicle v, long durationMinutes, LocalDateTime entryTime)` |

---

## 4. Business Rules and Assumptions

1. **Uniqueness:** No two active vehicles in the facility can share the same registration number.
2. **Category Isolation:** Two-wheelers cannot occupy car slots, and standard cars cannot occupy dedicated electric charging slots unless no other parking space exists (subject to policy).
3. **Minimum Billing Unit:** Parking duration is billed in hourly increments; any partial hour beyond a 15-minute grace period is rounded up to the next full hour.
4. **Capacity Limits:** If all compatible slots on all floors are occupied, the facility enters a `FULL` state, rejecting new ingress requests with an informative error.
5. **Peak Hour Pricing:** Commercial delivery vehicles entering between 09:00 and 17:00 incur a 2.0x multiplier on base hourly rates to incentivize non-peak delivery scheduling.
6. **Pass Privilege:** A valid Day-Pass entitles a vehicle to unlimited ingress and egress during that operating day without recurring hourly fees.

---

## 5. Architectural Evaluation: Object-Oriented Design vs. Parallel String Arrays

An alternative approach commonly attempted in rudimentary procedural programs is the use of **parallel arrays** (e.g., `String[] regNumbers`, `String[] vehicleTypes`, `boolean[] isOccupied`, `int[] floorNos`, `int[] slotNos`).

### 5.1 Why Each Class Is Required in OOP
- **Encapsulation of State and Behavior:** A `ParkingSlot` couples the `isOccupied` boolean directly with the `Vehicle` reference and physical coordinates. In parallel arrays, these data items are decoupled; modifying one array without synchronizing the indices in others immediately leads to corrupted state.
- **Type Safety:** The OOP model relies on strong types (`LocalDateTime`, enums, custom classes), preventing invalid string representations from propagating through the system.
- **Polymorphism:** Extending the system with a new vehicle type (`ElectricCar`) merely requires subclassing `Vehicle` and overriding `calculateFee()`. In parallel arrays, every method would require nested `if-else` or `switch` branches checking raw string literals.

### 5.2 Big-O Complexity Comparison

| Operation | Object-Oriented Design (HashMap + 2D Slots) | Parallel Arrays Design (`String[]`, `boolean[]`) | Analysis & Justification |
|---|---|---|---|
| **Search by Registration Number** | **$\mathcal{O}(1)$ average** (via `HashMap<String, ParkingTicket>`) or **$\mathcal{O}(N)$** (linear traversal) | **$\mathcal{O}(N)$ strictly** (linear scan across parallel array) | In OOP, active tickets are indexed by registration number in a hash map, yielding constant-time lookup. In parallel arrays, searching requires scanning element-by-element up to $N$. |
| **Adding a New Vehicle Attribute** (e.g., `batteryCapacity`) | **$\mathcal{O}(1)$ developer effort**, zero code breakage for other types | **$\mathcal{O}(N)$ memory reallocation** + high maintenance overhead | In OOP, add field to `ElectricCar` subclass; existing classes remain untouched. In parallel arrays, a new `double[] batteryCapacities` array must be declared, resized, and kept in sync at every allocation/deallocation point. |
| **Slot Allocation** | **$\mathcal{O}(F \times S)$** worst case (or **$\mathcal{O}(1)$** with free-slot queue/stack) | **$\mathcal{O}(F \times S)$** linear scanning | Both can scan floors ($F$) and slots ($S$), but OOP allows storing free slot references in a `Deque` or `PriorityQueue` per category for instant $\mathcal{O}(1)$ retrieval. |
| **Memory Locality & Overhead** | Slightly higher memory per node due to object headers (16 bytes per object in 64-bit JVM) | Low primitive overhead, but cache pollution due to unassociated array lookups | OOP trades modest object header overhead for massive gains in maintainability, thread safety, and maintainable extensibility. |

---
