# TASK 3: Control Logic and Algorithmic Flow

**Student Name:** K VENKATA VAMSI GOUD  
**Register Number:** 192472021  
**Course Code & Title:** CSA09 – Programming in Java  
**Institution:** SIMATS – Saveetha School of Engineering  

---

## 1. Algorithmic Pseudocode

### 1.1 Algorithm 1: Allocate a Free Parking Slot
```text
ALGORITHM AllocateFreeSlot(occupiedGrid)
INPUT: occupiedGrid (Jagged 2D boolean array where occupiedGrid[f][s] represents slot state)
OUTPUT: [floorIndex, slotIndex] coordinates if slot found, or NULL if full

BEGIN
    FOR floorIndex FROM 0 TO LENGTH(occupiedGrid) - 1 DO
        FOR slotIndex FROM 0 TO LENGTH(occupiedGrid[floorIndex]) - 1 DO
            IF NOT occupiedGrid[floorIndex][slotIndex] THEN
                occupiedGrid[floorIndex][slotIndex] <- TRUE   // Mark bay as occupied
                RETURN [floorIndex, slotIndex]               // Early return on first match
            END IF
        END FOR
    END FOR
    
    RETURN NULL   // Facility is completely full
END
```

### 1.2 Algorithm 2: Calculate Parking Fee
```text
ALGORITHM CalculateParkingFee(vehicleCategory, parkedMinutes, entryTimestamp)
INPUT: vehicleCategory (String), parkedMinutes (Integer), entryTimestamp (DateTime)
OUTPUT: totalPayableFee (Decimal/Double)

BEGIN
    IF parkedMinutes <= 0 THEN
        RETURN 0.0
    END IF

    // Grace period evaluation
    IF parkedMinutes <= 15 THEN
        RETURN 0.0
    END IF

    // Calculate billed hours (ceiling rounding)
    billedHours <- CEIL(parkedMinutes / 60.0)

    // Base hourly rates by category
    SWITCH vehicleCategory DO
        CASE "TwoWheeler":
            hourlyRate <- 10.0
        CASE "Car":
            hourlyRate <- 30.0
        CASE "ElectricCar":
            hourlyRate <- 25.0
        CASE "DeliveryVan":
            hourlyRate <- 50.0
        DEFAULT:
            hourlyRate <- 20.0
    END SWITCH

    baseFee <- hourlyRate * billedHours

    // Peak-hour multiplier for commercial delivery vans (09:00 - 17:00)
    IF vehicleCategory = "DeliveryVan" AND HOUR(entryTimestamp) >= 9 AND HOUR(entryTimestamp) < 17 THEN
        baseFee <- baseFee * 2.0
    END IF

    // Extended stay discount (> 8 hours)
    IF billedHours >= 8 THEN
        baseFee <- baseFee * 0.90   // 10% daily parking discount
    END IF

    RETURN baseFee
END
```

---

## 2. Control Statements Analysis

### 2.1 Loops (`for`, `while`, `do-while`, enhanced `for-each`)
- **Nested `for` Loops:** The conventional indexed `for` loop `for (int f = 0; f < occupied.length; f++)` is preferred for grid traversal because both the floor coordinate ($f$) and slot coordinate ($s$) are directly accessible and needed to construct the return coordinate `new int[]{f, s}`.
- **Enhanced `for-each` Limitation:** While `for (boolean[] floor : occupied)` is cleaner syntax, it conceals the integer loop counter, requiring an auxiliary variable to track indices.
- **`while` / `do-while`:** Used in interactive menu loops where termination depends on runtime sentinel input rather than a known iteration count.

### 2.2 Jump Statements (`break`, `continue`, `return`)
- **`return`:** Instantly terminates method execution, unwinds the current stack frame, and yields the allocated coordinate array `new int[]{f, s}`. This eliminates the need for boolean sentinel flags (e.g., `boolean found = false`).
- **`break`:** Terminates the innermost loop immediately. In multi-level search without `return`, a labeled break (`break FLOOR_SEARCH;`) is necessary to exit both the inner slot loop and outer floor loop simultaneously.
- **`continue`:** Skips the remaining instructions in the current iteration and jumps to the update expression of the loop header. Used when filtering out incompatible bays (e.g., skipping non-charging slots when allocating an EV).

### 2.3 Branching (`if-else` vs. `switch`)
- **`if-else`:** Best suited for range validations, null safety checks, and composite relational expressions (e.g., `if (hours >= 8 && isMember)`).
- **`switch` Expressions (Java 14+ / Java 17):** Provides concise pattern matching, exhaustiveness verification, and $\mathcal{O}(1)$ bytecode jump tables (`tableswitch` or `lookupswitch`) when evaluating discrete vehicle categories.

---

## 3. Alternative Allocation Strategy: Stack-Based Free-Slot Allocation ($\mathcal{O}(1)$)

In the current iterative scan, searching for an available bay takes $\mathcal{O}(F \times S)$ time in the worst case (when the facility is full or only the very last bay is vacant).

### 3.1 Stack / Deque Architecture
Instead of scanning the 2D grid repeatedly:
1. At facility startup, all slot coordinates are pushed into a `java.util.ArrayDeque<int[]>` acting as a LIFO stack or FIFO queue.
2. **Allocation:** `freeSlotsStack.pop()` or `poll()` operates in strictly **$\mathcal{O}(1)$ constant time**.
3. **Deallocation:** When a vehicle exits, its slot coordinate `[f, s]` is pushed back onto `freeSlotsStack.push(new int[]{f, s})` in **$\mathcal{O}(1)$ time**.
4. **Trade-off:** Requires $\mathcal{O}(N)$ additional heap memory to store coordinate reference wrappers, but reduces CPU latency during peak morning ingress periods.

---
