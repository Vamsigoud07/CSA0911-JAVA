# TASK 12: Day-4 Design Challenge Architectural Report

**Student Name:** K VENKATA VAMSI GOUD  
**Register Number:** 192472021  
**Course Code & Title:** CSA09 – Programming in Java  
**Institution:** SIMATS – Saveetha School of Engineering  

---

## 1. Executive Summary & New Requirements
The Day-4 Design Challenge mandated the incorporation of four significant feature extensions and structural adaptations into the pre-existing Campus Smart-Parking System without destabilizing or unnecessarily rewriting already tested production classes:
1. **Introduction of E-Bike Category:** Low-emission electric bicycles charged at a flat rate of $\text{Rs } 15.00$ regardless of stay duration up to 24 hours.
2. **Introduction of Commercial Delivery Van Category:** Delivery vehicles operating under dynamic time-of-day pricing.
3. **Peak-Hour Dynamic Pricing Rule:** Delivery vans entering between 09:00 and 17:00 incur double the standard car hourly rate ($2 \times \text{Rs } 30.00 = \text{Rs } 60.00/\text{hr}$), whereas off-peak ingress is charged at the normal car rate ($\text{Rs } 30.00/\text{hr}$).
4. **One-Day Visitor Pass (`ParkingPass`):** Pre-purchased $\text{Rs } 100.00$ single-day pass granting unlimited entries and exits within 24 hours, completely waiving recurring hourly exit fees.
5. **Extension to 5 Physical Floors:** Vertical expansion from 2–3 floors to 5 structural levels.
6. **Asymmetric Jagged Array Capacities:** Each floor supports a different number of parking bays (e.g., Floor 0: 6 bays, Floor 1: 5 bays, Floor 2: 4 bays, Floor 3: 7 bays, Floor 4: 3 bays = 25 total bays).

---

## 2. Section A: Impact Analysis

| System Dimension | Prior Architecture (Tasks 10–11) | Proposed Day-4 Requirement | Blast Radius & Technical Impact |
|---|---|---|---|
| **Vehicular Model** | Fixed Enum categories (`CAR`, `TWO_WHEELER`, etc.) with static rate lookups. | Dynamic categories (`EBike`, `DeliveryVan`) with algorithmic variations (flat fee, peak multiplier). | Adding new enum constants would require modifying switch statements across multiple services. Refactored to **Strategy Pattern** via `PricingStrategy`. |
| **Fee Calculation** | Monolithic `computeParkingFee` method containing switch-case logic based purely on duration. | Time-of-day awareness (9 AM–5 PM peak window) and pass-privilege overrides. | Modifying the central method violates the **Open-Closed Principle (OCP)**. Strategy pattern delegates calculation to individual strategy implementations. |
| **Pass Entitlement** | Tickets tracked only ingress timestamp and hourly accumulation. | Pre-purchased passes must be verified against vehicle plate and operating calendar date. | Requires `ParkingPass` abstraction and lookup dictionary in facility registry. |
| **Floor Topology** | Uniform or 2–3 floor arrays with semi-fixed capacities. | 5 floors with variable, non-uniform bay allocations. | Leverages Java's jagged array capabilities (`ExtendedSlot[][]`) instantiated with variable sub-array lengths. |

---

## 3. Section B: Evaluation of Original Design

The original Task 10 and 11 design utilized an enum-driven category system (`VehicleCategory`) and a centralized `FeeCalculationService`.
- **Strengths of Original Design:** Highly readable, fast compilation, simple data structures, and compact code footprint.
- **Vulnerabilities Exposed by Day-4 Requirements:**
  - **OCP Violation:** When the `EBike` (flat fee) and `DeliveryVan` (peak time multiplier) rules arrived, the original `computeParkingFee` method would have required nested `if-else` conditionals checking `LocalDateTime.getHour()`. Every future vehicle category would demand editing tested code.
  - **Tight Coupling:** The pricing logic was tightly coupled to the enum type rather than encapsulated as an interchangeable behavioral strategy.

---

## 4. Section C: Adaptation and Architectural Redesign

To accommodate Day-4 requirements elegantly, the system was refactored using the **Strategy Design Pattern** and **Composition**:
1. **`PricingStrategy` Interface:**
   ```java
   public interface PricingStrategy {
       double calculateFee(ExtendedVehicle vehicle, long durationMinutes, LocalDateTime entryTime, ParkingPass activePass);
   }
   ```
2. **Autonomous Strategy Implementations:**
   - `EBikePricingStrategy`: Implements flat $\text{Rs } 15.00$ logic with pass and grace-period checks.
   - `DeliveryVanPricingStrategy`: Evaluates `entryTime.getHour() >= 9 && entryTime.getHour() < 17` to apply the $2.0\times$ peak multiplier.
   - `CarPricingStrategy`: Standard linear hourly accumulation.
3. **`ParkingPass` Component:** Encapsulates date validation (`LocalDate.equals()`) and vehicle plate matching, returning $\text{Rs } 0.00$ when an active pass is detected.
4. **5-Floor Jagged Array Grid:**
   ```java
   ExtendedSlot[][] facilityGrid = new ExtendedSlot[5][];
   facilityGrid[0] = new ExtendedSlot[6]; // Ground (Heavy/Delivery)
   facilityGrid[1] = new ExtendedSlot[5];
   facilityGrid[2] = new ExtendedSlot[4]; // E-Bikes
   facilityGrid[3] = new ExtendedSlot[7]; // General
   facilityGrid[4] = new ExtendedSlot[3]; // Rooftop EV
   ```

---

## 5. Section D: Evidence-Based Defence

### How Many Existing Classes Needed Modification?
- **Zero core business classes required destructive modification!**
- By establishing the `Task12.ExtendedParkingSystem` package, the original `Task10` and `Task11` classes remained **100% untouched and functional**, preventing any regression bugs in pre-existing test suites.
- Extensibility was achieved purely through **additive sub-classing and interface implementation**:
  - `ExtendedVehicle` extended `Vehicle` abstractions.
  - `EBike` and `DeliveryVan` added new leaves to the inheritance tree.
  - `ParkingPass` integrated as a non-invasive pass-through collaborator.
  - The 5-floor facility reused jagged array concepts from Task 2 while expanding dimension bounds.

### Verification Evidence:
- **E-Bike Test:** 7-hour parking yielded exactly $\text{Rs } 15.00$ flat fee.
- **Peak Delivery Van Test:** 2 hours parked between 11:00 AM and 1:00 PM yielded $2 \times \text{Rs } 60.00 = \text{Rs } 120.00$.
- **Off-Peak Delivery Van Test:** 2 hours parked between 7:00 PM and 9:00 PM yielded $2 \times \text{Rs } 30.00 = \text{Rs } 60.00$.
- **One-Day Pass Test:** 9 hours parked by pass holder yielded $\text{Rs } 0.00$ exit fee.
- **5-Floor Jagged Grid Telemetry:** Confirmed 25 slots across 5 asymmetric floors ($6 + 5 + 4 + 7 + 3 = 25$).

---
