# Task 1: GUI Design Review – Student Fee Payment Window

**Course Code**: CSA09  
**Course Name**: Programming in Java  
**Student Name**: K VENKATA VAMSI GOUD  
**Register Number**: 192472021  
**Institution**: SIMATS – Saveetha School of Engineering  
**Academic Year**: 2026–2027  

---

## 1.1 Diagnose the Design

The supplied legacy implementation of `PoorFeeWindow` exhibits severe design anti-patterns, security vulnerabilities, thread-safety violations, and algorithmic inefficiencies. Below is the systematic diagnosis table detailing 11 major defects:

| No. | Design Deficiency | Why It Is Wrong | User Impact | Recommended Fix |
|:---:|:---|:---|:---|:---|
| **1** | **Monolithic Architecture** | GUI presentation, business validation, and direct JDBC access are tightly coupled within a single AWT Frame class, violating the Single Responsibility Principle (SRP). | Code cannot be unit tested, maintained, or adapted to other interfaces. Any DB schema update breaks the GUI. | Refactor into layered architecture: `FeeWindow` (GUI), `FeeService` (Business Logic), and `FeeRepository` (Data Access). |
| **2** | **Reference Equality (`==`) for Strings** | `cmd == "Pay Fee"` checks memory reference identity rather than character sequence equality. String literals might fail identity checks depending on JVM interning and ActionCommand source. | Buttons intermittently fail to trigger actions or silently ignore clicks, leading to unresponsive user controls. | Replace with `cmd.equals("Pay Fee")` or use separate listener instances / Swing Actions. |
| **3** | **Missing Input Validation** | `Double.parseDouble(amtStr)` directly parses raw user input without checking for null, empty strings, alphabetic characters, negative numbers, or extreme bounds. | Application throws unhandled `NumberFormatException`, crashes, or accepts negative/zero fee payments into the ledger. | Implement robust multi-tier validation checking non-empty, regex regex match, finite bounds, and minimum payable threshold. |
| **4** | **Database Operations on EDT** | `DriverManager.getConnection()`, `createStatement()`, and `executeUpdate()` run synchronously on the Swing Event Dispatch Thread (EDT). | Freezes the GUI during network or DB latency; OS flags window as "Not Responding"; painting and keyboard inputs halt. | Delegate all database and network interactions to background worker threads using `SwingWorker<T, V>`. |
| **5** | **`Thread.sleep()` on EDT** | `Thread.sleep(3000)` pauses the calling thread, which is the EDT responsible for redrawing and event dispatching. | Total interface paralysis for 3 seconds; animations freeze, window cannot be dragged, repainted, or canceled. | Remove arbitrary sleep calls or run simulated delays strictly inside `SwingWorker.doInBackground()`. |
| **6** | **Lack of User Feedback & Double Submit** | No progress bar, loading spinner, or button disablement while the transaction is processing. | Confused users repeatedly click "Pay Fee", triggering duplicate billing and database lock contention. | Disable payment controls upon click, display indeterminate `JProgressBar`, and update dynamic status text. |
| **7** | **Unimplemented Receipt Button** | `receiptButton` is instantiated and added to the layout, but no `ActionListener` is registered with it. | Users click "Print Receipt" expecting a voucher or confirmation dialog, but absolutely nothing happens (dead UI control). | Implement dedicated `handlePrintReceipt()` handler and enable the button only upon confirmed successful transaction. |
| **8** | **SQL Injection Vulnerability** | Direct string concatenation: `"INSERT INTO fee_payments VALUES ('" + reg + "', " + amt + ")"`. | Malicious input (e.g., `' OR '1'='1`) can alter database schema, delete tables, or compromise student records. | Use parameterized `PreparedStatement` with bind variables (`?`) to ensure SQL parameters are treated strictly as data literals. |
| **9** | **Unclosed Database Resources** | `Connection` and `Statement` objects are never closed after query execution. | Connection pool starvation, database socket exhaustion, memory leakage, and eventual server shutdown. | Employ modern Java `try-with-resources` blocks to guarantee deterministic resource deallocation even upon failure. |
| **10** | **Swallowed Checked Exceptions** | Catches `Exception` and merely executes `ex.printStackTrace()` without updating the user interface. | Silent failure: user assumes transaction succeeded or is left completely uninformed when network or DB fails. | Catch specific exceptions, present informative `JOptionPane.showMessageDialog` error alerts, and restore UI controls. |
| **11** | **Missing Window-Closing Logic** | In AWT `Frame`, clicking the window close button ('X') does not dispose the frame or exit the program without a `WindowListener`. | Window remains stuck on screen or application process remains alive as an invisible orphan process in task manager. | Use `JFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)` or attach a `WindowAdapter` implementing `windowClosing()`. |

---

## 1.2 Repaired Design Architecture

The refactored design separates concerns across three clearly demarcated tiers:

```
+-------------------------------------------------------+
|                    PRESENTATION LAYER                 |
|                      (FeeWindow)                      |
| - Collects regNo & amount                             |
| - Renders JProgressBar & status alerts                |
| - Uses SwingWorker for non-blocking EDT dispatch     |
+-------------------------------------------------------+
                           |
                           v invokes
+-------------------------------------------------------+
|                     SERVICE LAYER                     |
|                      (FeeService)                     |
| - Enforces business rules & regex validation          |
| - Bounds checking (INR 100 to 500,000)                |
| - Generates UUID Transaction IDs                      |
+-------------------------------------------------------+
                           |
                           v invokes
+-------------------------------------------------------+
|                     DATA ACCESS LAYER                 |
|                     (FeeRepository)                   |
| - PreparedStatement parameterized queries             |
| - Try-with-resources resource management              |
| - Thread-safe in-memory or JDBC persistence           |
+-------------------------------------------------------+
```

### Class Diagram

```
+-------------------------------+       +-------------------------------+
|           FeeWindow           |       |          FeeService           |
+-------------------------------+       +-------------------------------+
| - feeService: FeeService      |------>| - repository: FeeRepository   |
| - regField: JTextField        |       +-------------------------------+
| - amountField: JTextField     |       | + processPayment(): Result    |
| - payButton: JButton          |       +-------------------------------+
| - clearButton: JButton        |                       |
| - receiptButton: JButton      |                       v
| - statusLabel: JLabel         |       +-------------------------------+
| - progressBar: JProgressBar   |       |         FeeRepository         |
| - lastResult: PaymentResult   |       +-------------------------------+
+-------------------------------+       | - dbUrl: String               |
| - handlePayment(): void       |       | - paymentRecords: Map         |
| - handleClear(): void         |       +-------------------------------+
| - handlePrintReceipt(): void  |       | + savePayment(): boolean      |
+-------------------------------+       +-------------------------------+
```

---

## 1.3 Design Defence (96 Words)

The repaired architecture adheres to the Single Responsibility Principle by decoupling UI rendering, validation logic, and data persistence into distinct, cohesive classes. Encapsulating business rules within `FeeService` enables automated unit testing without instantiating graphical components. Database persistence within `FeeRepository` guarantees resource management via try-with-resources and prevents SQL injection using `PreparedStatement`. Asynchronous execution via `SwingWorker` keeps the Event Dispatch Thread responsive, eliminating interface freezing. Adding alternative payment gateways or persistence stores requires modifying only isolated service or repository implementations, leaving GUI presentation completely unaffected.

---

## 1.4 Alternative Concept & Complexity Analysis

### Comparison of Event-Handling Architectures

1. **Single ActionListener with `if-else` Chain**:
   A single listener inspects `e.getActionCommand()` through an $O(k)$ sequential string comparison chain, where $k$ is the number of buttons. It creates tight coupling between button action commands and controller logic.
2. **Dedicated Listener Objects**:
   Each component receives its own lightweight listener or lambda expression. The AWT/Swing event subsystem directly dispatches to the registered component callback without string comparisons ($O(1)$ dispatch).
3. **Swing Action (`AbstractAction`)**:
   Encapsulates state, event handler, label, icon, and enabled/disabled state in a unified object that can be attached simultaneously to buttons, menu items, and keyboard accelerator keystrokes.

### Complexity and Trade-off Comparison Table

| Metric | Single ActionListener (if-else) | Dedicated Listener / Lambda | Swing Action (`AbstractAction`) |
|:---|:---|:---|:---|
| **Event Dispatch Time** | $O(k)$ sequential string comparisons | $O(1)$ direct method dispatch | $O(1)$ direct method dispatch |
| **Memory Footprint** | Small: 1 shared listener object | Modest: $k$ small lambda/listener instances | Moderate: $k$ rich Action objects |
| **Maintainability** | Poor; high cyclomatic complexity | High; clean separation of handlers | Excellent; centralized state and triggers |
| **UI Reusability** | Zero; tied to button string labels | Low; handler tied to specific component | High; reusable across Buttons, Menus, Keys |
| **Recommended Use** | Simple demo with 1–2 buttons | Modern desktop GUIs with standard controls | Enterprise GUIs with menus and shortcuts |
