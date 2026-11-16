# Task 4: Event-Handling Forensics – Drawing Pad Defect Analysis and Persistence

**Course Code**: CSA09  
**Course Name**: Programming in Java  
**Student Name**: K VENKATA VAMSI GOUD  
**Register Number**: 192472021  
**Institution**: SIMATS – Saveetha School of Engineering  
**Academic Year**: 2026–2027  

---

## 4.1 Defect Diagnosis: The Misspelled Callback

In the supplied legacy `DrawPad` code, mouse clicks were completely ignored and the status label never updated due to a silent method signature mismatch:

```java
// DEFECT IN SUPPLIED CODE:
addMouseListener(new MouseAdapter() {
    public void mouseclicked(MouseEvent e) {  // <--- LOWERCASE 'c' ERROR!
        points.add(e.getPoint());
        statusLabel.setText("Clicks: " + points.size());
    }
});
```

### Forensic Explanation:
1. **Case-Sensitivity in Java**: The Java programming language enforces strict case sensitivity for all identifiers, method names, and member variables.
2. **Interface Contract Violation**: The interface `java.awt.event.MouseListener` specifies five exact method signatures:
   - `public void mouseClicked(MouseEvent e)`
   - `public void mousePressed(MouseEvent e)`
   - `public void mouseReleased(MouseEvent e)`
   - `public void mouseEntered(MouseEvent e)`
   - `public void mouseExited(MouseEvent e)`
3. **`MouseAdapter` Default Implementation**: `MouseAdapter` provides empty default implementations of all five methods. When the developer wrote `mouseclicked(MouseEvent e)`, the compiler treated it as a brand-new, non-overriding helper method unique to the anonymous inner class.
4. **Silent Failure**: The AWT Event Dispatcher searches for and invokes `mouseClicked(MouseEvent e)`. Because that method was never overridden, the adapter's empty body executed, doing nothing. The custom `mouseclicked` was dead code.

---

## 4.2 Failing Interaction: Non-Persistent `getGraphics()` Drawing

When developers draw directly onto a window surface using `Graphics g = getGraphics()` inside an event handler, the drawing is strictly transient:

```
[User clicks window] ──> getGraphics().fillOval(...) draws onto current OS display buffer
                                     │
[User minimizes window] ─────────────┴─> OS deallocates / hides window surface buffer
                                     │
[User restores window] ──────────────┴─> OS sends repaint/expose event to AWT Event Queue
                                     │
[AWT invokes paint(g)] ────────────────> Default paint() clears background;
                                         TRANSIENT DOTS ARE PERMANENTLY DESTROYED!
```

### Root Cause:
Direct `getGraphics()` calls write only to the current video raster buffer of the native display surface. The operating system does not store vector commands or pixel histories. Whenever a window is obscured, resized, minimized, or moved off-screen, the OS marks the surface region as "dirty" and generates an expose event (`PaintEvent`). The AWT/Swing runtime responds by clearing the damaged region and invoking `paint(Graphics g)`. Because `paint()` possessed no knowledge of past click coordinates, the dots vanished.

---

## 4.3 Repaired Implementation Architecture (`DrawPad.java`)

To ensure 100% rendering persistence across minimize, restore, resize, and repaint cycles, the application separates the **data model** from the **rendering pipeline**:

1. **Model Storage**: An `ArrayList<Point>` acts as the single source of truth, retaining the coordinate of every valid user click.
2. **Event Handling (TODO 1)**:
   - Annotated with `@Override public void mouseClicked(MouseEvent e)`.
   - Records `points.add(e.getPoint())`.
   - Updates status label with current `points.size()`.
   - Requests a scheduled repaint via `repaint()`.
3. **Deterministic Rendering (TODO 2)**:
   - In `public void paint(Graphics g)`:
   - Iterates through every stored `Point p` in `points`.
   - Renders a 6 × 6 circle centred at $(p.x, p.y)$ via:
     ```java
     g.fillOval(p.x - 3, p.y - 3, 6, 6);
     ```

---

## 4.4 Prevention of Recurrence: The `@Override` Annotation

The `@Override` annotation is a compile-time check that instructs the Java compiler to verify that the annotated method actually overrides an existing method in a superclass or implements an interface method.

### The Compiler Experiment

When `MisspelledOverrideExperiment.java` is compiled:

```java
public class MisspelledOverrideExperiment extends MouseAdapter {
    @Override
    public void mouseclicked(MouseEvent e) { ... }
}
```

The Java compiler halts immediately with a fatal diagnostic error:

```
ERROR in MisspelledOverrideExperiment.java (at line 13):
    @Override
    public void mouseclicked(MouseEvent e) {
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
The method mouseclicked(MouseEvent) of type MisspelledOverrideExperiment
must override or implement a supertype method
```

**Conclusion**: If the original author had included `@Override`, the code would never have compiled, completely eliminating the defect before reaching runtime or production.

---

## 4.5 Alternative Concept & Complexity Analysis

### Comparison: Vector Point List vs. Off-Screen `BufferedImage`

| Parameter | Approach 1: `ArrayList<Point>` Redrawn in `paint()` (Implemented) | Approach 2: Off-Screen `BufferedImage` Bit-Blit (`DrawPadBuffered`) |
|:---|:---|:---|
| **Click Handler Cost** | $O(1)$ amortized append (`points.add()`) | $O(1)$ constant raster circle rasterization |
| **Repaint Time Complexity** | $O(N)$ where $N$ is total dots placed | $O(W \times H)$ proportional to display pixel dimensions |
| **Performance for Low $N$ ($N < 5,000$)**| **Extremely Fast** (< 1 ms) | Fast, but fixed bit-blit raster copy overhead |
| **Performance for High $N$ ($N > 100,000$)**| Degradation due to traversing massive list | **Constant performance** independent of total dots |
| **Memory Footprint** | $O(N)$ dynamic; 24 bytes per `Point` object | Fixed $O(W \times H)$; $500 \times 400 \times 4 \approx 800\text{ KB}$ |
| **Resolution Independence** | Infinite; points can scale cleanly on DPI change | Fixed raster; pixelates if window stretches |
| **Editing & Undo Support** | Trivial; remove last item from list | Difficult; requires retaining pixel undo stacks |

**Recommendation**: Approach 1 (`ArrayList<Point>`) is superior for interactive drawing pads, vector CAD, and educational diagramming where the number of points is moderate, undo capabilities are needed, and coordinate fidelity is paramount.
