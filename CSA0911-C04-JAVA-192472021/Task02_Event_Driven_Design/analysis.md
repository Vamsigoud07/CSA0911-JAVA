# Task 2: From Requirement to Event-Driven Design – Hospital Outpatient Appointment Desk

**Course Code**: CSA09  
**Course Name**: Programming in Java  
**Student Name**: K VENKATA VAMSI GOUD  
**Register Number**: 192472021  
**Institution**: SIMATS – Saveetha School of Engineering  
**Academic Year**: 2026–2027  

---

## 2.1 Event Model Table

The following table comprehensively maps the graphical components to their generated event objects, listener interfaces, callback methods, and resulting business actions:

| Event Source | Event Object | Listener Interface | Handler Method | Action Triggered |
|:---|:---|:---|:---|:---|
| **Department ComboBox** (`departmentCombo`) | `java.awt.event.ItemEvent` | `java.awt.event.ItemListener` | `itemStateChanged(ItemEvent e)` | Filters and repopulates `doctorCombo` based on selected department when `e.getStateChange() == ItemEvent.SELECTED`. |
| **Doctor ComboBox** (`doctorCombo`) | `java.awt.event.ItemEvent` | `java.awt.event.ItemListener` | `itemStateChanged(ItemEvent e)` | Updates status bar with selected doctor's qualification and room details. |
| **Time Slot ComboBox** (`timeSlotCombo`) | `java.awt.event.ItemEvent` | `java.awt.event.ItemListener` | `itemStateChanged(ItemEvent e)` | Validates slot availability for the selected doctor. |
| **Patient ID Field** (`patientIdField`) | `java.awt.event.FocusEvent` / `KeyEvent` | `java.awt.event.FocusListener` | `focusLost(FocusEvent e)` | Performs inline format checking on Patient ID (e.g., alphanumeric check). |
| **Patient Name Field** (`patientNameField`) | `java.awt.event.ActionEvent` | `java.awt.event.ActionListener` | `actionPerformed(ActionEvent e)` | Pressing **Enter** directly triggers appointment validation and booking. |
| **Book Button** (`bookButton`) | `java.awt.event.ActionEvent` | `java.awt.event.ActionListener` | `actionPerformed(ActionEvent e)` | Validates all form inputs, assigns booking token, updates status label, and displays confirmation dialog. |
| **Clear Button** (`clearButton`) | `java.awt.event.ActionEvent` | `java.awt.event.ActionListener` | `actionPerformed(ActionEvent e)` | Resets text fields, returns combo boxes to index 0, and restores default status label text. |
| **Window Frame** (`AppointmentDesk`) | `java.awt.event.WindowEvent` | `java.awt.event.WindowListener` (`WindowAdapter`) | `windowClosing(WindowEvent e)` | Intercepts 'X' close request; checks if unbooked patient data exists, prompting a confirmation dialog before disposal. |

---

## 2.2 Component Hierarchy

```
JFrame (AppointmentDesk) [BorderLayout(10, 10)]
 ├── NORTH: Header Panel (JPanel) [FlowLayout(CENTER)]
 │    └── JLabel ("OUTPATIENT APPOINTMENT BOOKING")
 ├── CENTER: Form Panel (JPanel) [GridBagLayout]
 │    ├── (0, 0): JLabel ("Patient ID:")
 │    ├── (1, 0): JTextField (patientIdField, 18)
 │    ├── (0, 1): JLabel ("Patient Name:")
 │    ├── (1, 1): JTextField (patientNameField, 18)
 │    ├── (0, 2): JLabel ("Department:")
 │    ├── (1, 2): JComboBox<String> (departmentCombo)
 │    ├── (0, 3): JLabel ("Doctor:")
 │    ├── (1, 3): JComboBox<Doctor> (doctorCombo)
 │    ├── (0, 4): JLabel ("Time Slot:")
 │    ├── (1, 4): JComboBox<String> (timeSlotCombo)
 │    └── (0..1, 5): JLabel (statusLabel)
 └── SOUTH: Action Button Panel (JPanel) [FlowLayout(CENTER, 15, 12)]
      ├── JButton ("Book Appointment")
      └── JButton ("Clear Form")
```

---

## 2.3 Handler Pseudocode (SIMATS Engineering Standard)

### Algorithm 1: RefreshDoctorList

```
ALGORITHM RefreshDoctorList(department)
// Purpose: Dynamically populates the doctor combo box when the user alters department selection
INPUT: department (STRING)
OUTPUT: doctorCombo updated with relevant medical specialists

START
    DECLARE doctorList AS LIST OF Doctor
    DECLARE doc AS Doctor

    IF department IS NULL OR department == "" THEN
        OUTPUT "Invalid department specified"
        RETURN
    END IF

    // Query in-memory catalog or cache
    SET doctorList = CALL doctorCatalog.get(department)

    // Clear previous items from dropdown
    CALL doctorCombo.removeAllItems()

    IF doctorList IS NOT NULL AND NOT doctorList.isEmpty() THEN
        FOR EACH doc IN doctorList DO
            CALL doctorCombo.addItem(doc)
        END FOR
        CALL statusLabel.setText("Loaded " + doctorList.size() + " doctors for " + department)
    ELSE
        CALL statusLabel.setText("No doctors available for selected department")
    END IF
STOP
```

### Algorithm 2: ValidateAndBookAppointment

```
ALGORITHM ValidateAndBookAppointment()
// Purpose: Validates patient inputs, checks selections, and generates confirmed booking
INPUT: patientIdField, patientNameField, departmentCombo, doctorCombo, timeSlotCombo
OUTPUT: Booking confirmation dialog or error alert

START
    DECLARE patientId AS STRING
    DECLARE patientName AS STRING
    DECLARE selectedDept AS STRING
    DECLARE selectedDoctor AS Doctor
    DECLARE selectedSlot AS STRING
    DECLARE token AS STRING

    // 1. Input Acquisition
    SET patientId = CALL patientIdField.getText().trim()
    SET patientName = CALL patientNameField.getText().trim()

    // 2. Validation Checks
    IF patientId == "" THEN
        CALL statusLabel.setText("Error: Patient ID is required")
        CALL DISPLAY_ALERT("Please enter Patient ID", "Missing Information", WARNING)
        CALL patientIdField.requestFocus()
        RETURN
    END IF

    IF patientName == "" THEN
        CALL statusLabel.setText("Error: Patient Name is required")
        CALL DISPLAY_ALERT("Please enter Patient Name", "Missing Information", WARNING)
        CALL patientNameField.requestFocus()
        RETURN
    END IF

    SET selectedDoctor = CALL doctorCombo.getSelectedItem()
    IF selectedDoctor IS NULL THEN
        CALL DISPLAY_ALERT("Please select a consulting doctor", "Selection Required", WARNING)
        RETURN
    END IF

    SET selectedDept = CALL departmentCombo.getSelectedItem()
    SET selectedSlot = CALL timeSlotCombo.getSelectedItem()

    // 3. Token Generation and Confirmation
    SET token = "APP-" + CALL GENERATE_UUID_SUBSTRING(6)
    CALL statusLabel.setText("Booked: Token " + token)

    CALL DISPLAY_MODAL_DIALOG(
        "Appointment Confirmed!\n" +
        "Token: " + token + "\n" +
        "Patient: " + patientName + " (" + patientId + ")\n" +
        "Dept: " + selectedDept + "\n" +
        "Doctor: " + selectedDoctor.getName() + "\n" +
        "Time: " + selectedSlot,
        "Booking Confirmation",
        INFORMATION
    )
STOP
```

---

## 2.4 Event Sequence & Lifecycle Comparison

### Application Lifecycle Sequence
1. **Window Creation**: Operating system window peer instantiated; `JFrame` constructor sets layout and visual dimensions.
2. **Component Initialization**: Child widgets (`JTextField`, `JComboBox`, `JButton`) allocated in heap.
3. **Listener Registration**: `ItemListener`, `ActionListener`, and `WindowListener` attached to component event multicast sources.
4. **Window Display**: `setVisible(true)` maps native OS window surface; EDT performs initial layout pass and repaints.
5. **Department Selection**: User clicks dropdown; native mouse event generates AWT `ItemEvent` (DESELECTED then SELECTED); EDT invokes `itemStateChanged()`.
6. **Doctor Refresh**: Handler clears and re-populates `doctorCombo` model; combo box fires `ListDataEvent` causing redraw.
7. **Booking Trigger**: User clicks Book or presses Enter; `ActionEvent` dispatches to `validateAndBookAppointment()`.
8. **Confirmation Modal**: `JOptionPane` blocks parent frame; user acknowledges dialog.
9. **Window Closing**: User clicks title bar 'X'; `WindowEvent.WINDOW_CLOSING` delivered to `windowClosing()`; confirmation dialog queries user; upon approval `dispose()` releases native display resources.

### Comparison with Java Applet Lifecycle
- **`init()`**: Equivalent to constructor and component initialization (`initUI()`).
- **`start()`**: Equivalent to `frame.setVisible(true)` when window becomes active and visible.
- **`paint()`**: Equivalent to Swing's `paintComponent(Graphics g)` called whenever dirty regions require redisplay.
- **`stop()`**: Equivalent to window minimization or focus lost (`windowDeactivated`).
- **`destroy()`**: Equivalent to `frame.dispose()` where native peers and graphics buffers are deallocated.

---

## 2.5 Alternative Concept & Complexity Analysis

### Database Query per Department vs. Pre-cached In-Memory Catalog

| Attribute | Approach 1: On-Demand Database Query | Approach 2: In-Memory `Map<String, List<Doctor>>` (Selected) |
|:---|:---|:---|
| **Query Latency** | $O(T_{\text{network}} + T_{\text{disk}})$: 15–80 ms per department click | $O(1)$ expected hash map lookup: < 1 µs |
| **Lookup Complexity** | $O(\log N)$ on indexed DB table | $O(1)$ amortized `HashMap.get()` |
| **Memory Footprint** | Minimal client memory ($O(1)$) | Modest client RAM: $O(d + m)$ where $d$ = depts, $m$ = total doctors |
| **Offline Resilience** | Fails completely if network connection drops | Robust; appointments can still be scheduled or queued |
| **Data Freshness** | Always reflects real-time DB changes immediately | Requires event-driven cache invalidation or periodic TTL refresh |
| **EDT Responsiveness** | Freezes UI unless wrapped in `SwingWorker` | Instantly responsive; synchronous UI update safe |

**Recommendation**: Approach 2 (`Map<String, List<Doctor>>`) is vastly superior for outpatient department rosters because doctor directories are small ($<1,000$ doctors) and change infrequently. Cache pre-loading provides instantaneous sub-millisecond dropdown response without network roundtrip jitter.
