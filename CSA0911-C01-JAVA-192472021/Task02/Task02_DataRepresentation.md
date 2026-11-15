# TASK 2: Data Representation and Memory Structures

**Student Name:** K VENKATA VAMSI GOUD  
**Register Number:** 192472021  
**Course Code & Title:** CSA09 – Programming in Java  
**Institution:** SIMATS – Saveetha School of Engineering  

---

## 1. Java Data Types in Campus Smart-Parking System

In Java, data representation is divided into **primitive types** (stored directly on the execution call stack) and **reference types** (references/pointers stored on the stack pointing to heap memory allocations).

| Attribute | Chosen Java Data Type | Type Classification | Justification & Architectural Role |
|---|---|---|---|
| `floorNumber`, `slotIndex` | `int` (32-bit signed integer) | Primitive | Sufficient range ($-2^{31}$ to $2^{31}-1$) for zero-based array indexing and campus floor indices. Direct CPU register manipulation ensures peak performance. |
| `parkingDurationMinutes` | `long` (64-bit signed integer) | Primitive | Elapsed time in minutes or epoch milliseconds can grow large over multi-day stays; `long` prevents integer overflow. |
| `hourlyRate`, `totalFee` | `double` (64-bit IEEE 754 floating-point) | Primitive | Required for fractional currency calculations involving multipliers (e.g., $1.5\times$ or $2.0\times$ peak surcharges) and discount rates. |
| `isOccupied`, `hasPass` | `boolean` (logical true/false) | Primitive | Binary occupancy tracking and validation flags with minimal memory footprint. |
| `regNumber`, `vehicleType` | `String` | Reference (Immutable Object) | Unicode alphanumeric strings representing vehicle license plates; immutability guarantees thread safety and hash code caching. |
| `entryTime`, `exitTime` | `java.time.LocalDateTime` | Reference (ISO-8601 Temporal) | Replaces legacy `java.util.Date`; provides immutable, thread-safe date-time arithmetic with nanosecond precision and timezone awareness. |

---

## 2. Two-Dimensional and Jagged Arrays for Parking Floors

### 2.1 Multi-Dimensional Grid vs. Real Campus Architecture
In a simplified multi-storey parking facility, every floor might have an identical rectangular grid represented as:
```java
boolean[][] uniformParkingGrid = new boolean[5][20]; // 5 floors, 20 slots each
```
However, realistic campus civil architecture dictates that different floors accommodate different numbers of bays due to ramp geometries, structural load-bearing pillars, electrical transformer rooms, and pedestrian stairs.

### 2.2 Jagged (Ragged) Array Representation
Java inherently implements multi-dimensional arrays as **arrays of arrays**, enabling **jagged arrays** where each row points to an independent 1D array of distinct length:
```java
// Defining 5 floors with variable slot capacities
boolean[][] campusParking = new boolean[5][];
campusParking[0] = new boolean[10]; // Floor 0 (Ground): 10 bays (buses, vans, visitor cars)
campusParking[1] = new boolean[8];  // Floor 1: 8 bays
campusParking[2] = new boolean[12]; // Floor 2: 12 bays (compact slots for two-wheelers)
campusParking[3] = new boolean[6];  // Floor 3: 6 bays
campusParking[4] = new boolean[4];  // Floor 4 (Rooftop): 4 bays (EV charging bays)
```

#### Memory Layout of Jagged Array:
- `campusParking` reference variable sits on the **Call Stack**.
- On the **Heap**, `campusParking` references an array of 5 reference pointers.
- Each index `campusParking[i]` references a separately allocated 1D array of `boolean` primitives on the heap.
- Total memory overhead = $1$ main array object header ($16$ bytes) + $5 \times 8$ byte pointers + $5$ sub-array headers ($5 \times 16$ bytes) + raw boolean elements.

---

## 3. Scanner-Based Input Management

Reading interactive user input in Java console applications requires the `java.util.Scanner` class. Improper handling of Scanner buffers is one of the most pervasive sources of software bugs in Java applications.

### 3.1 The `nextLine()` Trap After `nextInt()` or `nextDouble()`
When `scanner.nextInt()` reads a number, it scans tokens up to whitespace or newline (`\n`), leaving the trailing newline character in the input buffer. If a subsequent call to `scanner.nextLine()` is made, it immediately consumes that leftover `\n` and returns an empty string without waiting for user input.

#### Defensive Solution:
```java
System.out.print("Enter Floor Number: ");
int floor = Integer.parseInt(scanner.nextLine().trim()); // Eliminates buffer residue
System.out.print("Enter Vehicle Registration: ");
String reg = scanner.nextLine().trim(); // Accurately captures user input
```

### 3.2 Handling Input Validation and Exceptions
Robust systems must intercept `InputMismatchException` and `NoSuchElementException` using defensive validation:
```java
while (!scanner.hasNextInt()) {
    System.out.println("Invalid numeric input. Please enter an integer: ");
    scanner.next(); // Discard corrupted token
}
int choice = scanner.nextInt();
```

---

## 4. Type Conversion and Casting Mechanics

### 4.1 Widening (Implicit) vs. Narrowing (Explicit) Casting
- **Widening Conversion:** Occurs automatically when converting a smaller numerical type to a larger type without data loss:
  ```java
  int baseRate = 30;
  double fee = baseRate; // Implicit conversion: int (32-bit) -> double (64-bit IEEE 754)
  ```
- **Narrowing Conversion:** Requires explicit casting syntax because precision truncation or overflow may occur:
  ```java
  double exactHours = 3.75;
  int billedHours = (int) Math.ceil(exactHours); // Explicit cast after ceiling operation
  ```

### 4.2 Integer Division Precision Pitfall
In Java, dividing two integers performs integer division, which **truncates the fractional component** rather than rounding:
```java
int totalMinutes = 75;
double hoursParkedIncorrect = totalMinutes / 60; // Evaluates 75 / 60 = 1, then widens to 1.0!
double hoursParkedCorrect = totalMinutes / 60.0; // Evaluates 75 / 60.0 = 1.25
```
In fee calculations, this truncation error causes a 25-minute overstay to go unbilled, resulting in revenue leakage. Using floating-point literals (`60.0`) or casting `(double) totalMinutes / 60` enforces true floating-point arithmetic.

---

## 5. Alternative Data Structures: 1D Flattened Array vs. `ArrayList<ParkingSlot>`

| Dimension | 2D / Jagged Array (`boolean[][]`) | 1D Flattened Array (`boolean[]`) | Dynamic Collection (`ArrayList<ParkingSlot>`) |
|---|---|---|---|
| **Index Mapping Formula** | Direct `[floor][slot]` access | Formula: $\text{index} = \sum_{k=0}^{\text{floor}-1} \text{slots}[k] + \text{slot}$ | Object lookup: `slots.get(i)` |
| **Random Access Time** | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ |
| **Dynamic Resizing** | Impossible (fixed size at instantiation) | Impossible (fixed size at instantiation) | $\mathcal{O}(1)$ amortized append; $\mathcal{O}(N)$ when internal array doubles |
| **Memory Overhead** | Modest (array object headers for each sub-array) | Lowest memory footprint | High (each element wrapped in `ParkingSlot` object + 50% capacity buffer) |
| **Expressiveness & Type Safety** | Good for physical floor grid mapping | Poor; index math prone to off-by-one errors | Highest; provides rich OOP behaviors, streams, and filtering |

---
