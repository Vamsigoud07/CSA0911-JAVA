# Task 5: Same Screen, Different Event-Handling Styles – Currency Converter

**Course Code**: CSA09  
**Course Name**: Programming in Java  
**Student Name**: K VENKATA VAMSI GOUD  
**Register Number**: 192472021  
**Institution**: SIMATS – Saveetha School of Engineering  
**Academic Year**: 2026–2027  

---

## 5.1 The Four Event-Handling Paradigms

In Java GUI programming, event listeners can be implemented using multiple structural styles. To analyze their trade-offs, four separate working versions of the Currency Converter were implemented:

1. **Style A: Frame Implements `ActionListener` (`ConverterFrameListener.java`)**:
   - The outer `JFrame` class explicitly declares `implements ActionListener`.
   - A single `actionPerformed(ActionEvent e)` callback receives events from all registered sources.
   - Requires conditional branching (`if (e.getSource() == convertButton)`) to identify the source.
2. **Style B: Named Inner Class (`ConverterNamedInnerClass.java`)**:
   - A dedicated private member class (`private class ConvertHandler implements ActionListener`) is declared inside the frame.
   - Retains clean access to the outer class's private instance variables (`amountField`, `currencyCombo`).
   - Highly reusable within the enclosing frame.
3. **Style C: Anonymous Inner Class (`ConverterAnonymousClass.java`)**:
   - An unnamed class instantiated inline at the point of listener registration (`new ActionListener() { ... }`).
   - Localizes handling logic directly next to component creation.
   - Generates a separate `$1.class` bytecode file on disk.
4. **Style D: Java 8 Lambda Expression (`ConverterLambda.java`)**:
   - Modern functional syntax leveraging `ActionListener` as a Single Abstract Method (SAM) functional interface.
   - Eliminates boilerplate class declarations (`e -> performConversion()`).
   - Uses `invokedynamic` bytecode instruction, reducing disk footprint and classloader overhead.

---

## 5.2 Requirement Change Impact Analysis (Task 5.2)

### New Requirements:
1. Trigger conversion automatically when the **Enter** key is pressed within the `amountField`.
2. Display the formatted conversion result within a popup `JOptionPane.showMessageDialog`.

### Modifications and Code Delta per Style:

| Style | Modifications Required | Lines Changed / Added | Complexity Impact |
|:---|:---|:---:|:---|
| **Style A**<br>(Frame Listener) | Added `amountField.addActionListener(this);` and modified `if (e.getSource() == convertButton \|\| e.getSource() == amountField)`. | 2 lines | Minimal; branches logic in single method. |
| **Style B**<br>(Named Inner Class) | Instantiated handler `ConvertActionHandler handler = new ConvertActionHandler();` and attached to `amountField.addActionListener(handler);`. | 2 lines | Extremely low; reuses same inner class instance. |
| **Style C**<br>(Anonymous Class) | Extracted anonymous listener to reference `ActionListener anon = new ActionListener() { ... };` and passed to both controls. | 3 lines | Moderate; requires naming reference to avoid duplicating anonymous class code. |
| **Style D**<br>(Lambda Expression) | Stored lambda in variable `ActionListener action = e -> performConversion();` and registered with both `convertButton` and `amountField`. | 2 lines | **Lowest and cleanest**; zero boilerplate. |

---

## 5.3 Comprehensive Comparison Table (Task 5.3)

| Event Handling Style | Readability | Access to Frame Fields | Reusability | Code Size (Lines) | Ease of Modification |
|:---|:---|:---|:---|:---:|:---|
| **Frame Implements Listener** | Moderate; cluttered by `if-else` source checks | Direct (`this.field`) | None outside frame | ~85 | Poor; growing `if-else` chain degrades maintainability |
| **Named Inner Class** | High; structured and well-encapsulated | Direct outer access (`Outer.this.field`) | High within enclosing frame | ~92 | Excellent; clean modular class definition |
| **Anonymous Inner Class** | Low; verbose boilerplate obscures logic | Direct (fields must be effectively final if local) | Zero; single-use only | ~90 | Moderate; modifying requires updating class body |
| **Lambda Expression** | **Highest**; concise, clear intent | Direct lexical scope capture | High if assigned to a functional reference | **~65** | **Highest**; one-liner modification |

### Architectural Suitability Guidelines:
- **Small Applications / Student Assignments**: **Lambda Expressions (Style D)** provide maximum brevity, crystal-clear readability, and zero boilerplate.
- **Large Enterprise Applications**: **Named Inner Classes (Style B)** or separate top-level controllers ensure strict modularity, testability, and separation of concerns.
- **Reusable Cross-Control Actions**: **Swing `AbstractAction`** is the gold standard for binding an operation simultaneously across push buttons, menu items, and keyboard accelerators.

---

## 5.4 Alternative Concept & Complexity Analysis

### Lambda vs. Method Reference vs. Swing Action (`AbstractAction`)

```
1. Lambda Expression:
   convertButton.addActionListener(e -> performConversion());

2. Method Reference:
   convertButton.addActionListener(this::performConversion);

3. Shared Swing Action:
   Action convertAction = new AbstractAction("Convert") { ... };
   convertButton.setAction(convertAction);
   menuItem.setAction(convertAction);
   amountField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "convert");
   amountField.getActionMap().put("convert", convertAction);
```

### Complexity and Trade-Off Table

| Metric | Lambda Expression | Method Reference (`this::perform`) | Swing Action (`AbstractAction`) |
|:---|:---|:---|:---|
| **Syntactic Overhead** | Low (`e -> ...`) | Minimal (`this::method`) | Moderate (class extending `AbstractAction`) |
| **Bytecode Generation** | `invokedynamic` (single call site) | `invokedynamic` (lightweight call site) | Generates dedicated `.class` instance |
| **State Synchronization** | None; manual enabling/disabling | None | **Automatic**: Calling `action.setEnabled(false)` disables button, menu, and shortcut simultaneously! |
| **KeyStroke Binding** | Requires manual `KeyListener` or `InputMap` | Manual registration | Built-in accelerator integration |
| **Recommended Usage** | Quick handlers with 1–2 lines | Direct method delegator | Standard professional Swing desktop suites |
