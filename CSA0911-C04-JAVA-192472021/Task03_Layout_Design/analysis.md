# Task 3: From Sketch to Layout – Campus Library Search Screen

**Course Code**: CSA09  
**Course Name**: Programming in Java  
**Student Name**: K VENKATA VAMSI GOUD  
**Register Number**: 192472021  
**Institution**: SIMATS – Saveetha School of Engineering  
**Academic Year**: 2026–2027  

---

## 3.1 Layout Manager Selection and Rationale

Java GUI programming relies on layout managers to compute component dimensions and coordinate positions dynamically across platforms and display resolutions. For the Campus Library Search Screen, each container was assigned a layout manager optimized for its structural role:

1. **`BorderLayout` (Top-Level Window & Outer Panels)**:
   - **Rationale**: `BorderLayout` divides space into five regions (`NORTH`, `SOUTH`, `EAST`, `WEST`, and `CENTER`). Its defining characteristic is that components in `NORTH` and `SOUTH` are granted their preferred heights across the full width, whereas the `CENTER` component absorbs **all remaining horizontal and vertical space**. This makes it the ideal choice for housing the expandable `JScrollPane`/`JTable` catalog view.
2. **`FlowLayout` (Search Controls & Action Buttons)**:
   - **Rationale**: `FlowLayout` places components in a horizontal sequence at their preferred natural dimensions, wrapping them to subsequent lines if the container narrows. It is ideal for the Search Row (label, text field, dropdown, search button) and Bottom Action Row (Previous, Next, Reserve buttons) where components should maintain consistent sizing rather than stretching artificially.
3. **`GridLayout` (Evaluation)**:
   - **Rationale**: Divides space into a rigid grid of identical-sized rectangular cells. While excellent for calculator keypads or tabular dashboards, it is unsuitable for search toolbars because labels, text boxes, and buttons require different natural widths.
4. **`CardLayout` (Evaluation)**:
   - **Rationale**: Stacks multiple panels on top of one another like a deck of cards, displaying only one at a time. Ideal for multi-step wizards or switching between grid and list views, but not required for a unified single-screen search portal.

---

## 3.2 Nested Container Architecture

To avoid assigning multiple components to the same `BorderLayout` region (which causes earlier components to be overwritten), a strict nested panel hierarchy was constructed:

```
JFrame (LibrarySearchScreen)
 └── Layout: BorderLayout(5, 5)
      │
      ├── NORTH: northContainer (JPanel) [BorderLayout]
      │    ├── NORTH: titlePanel (JPanel) [FlowLayout(CENTER)]
      │    │    └── JLabel ("SIMATS CAMPUS LIBRARY SEARCH PORTAL")
      │    └── SOUTH: searchPanel (JPanel) [FlowLayout(LEFT, 10, 8)]
      │         ├── JLabel ("Keyword:")
      │         ├── JTextField (searchField, 15 columns)
      │         ├── JLabel ("Category:")
      │         ├── JComboBox (categoryCombo)
      │         └── JButton ("Search")
      │
      ├── CENTER: centerPanel (JPanel) [BorderLayout]  <=== DYNAMICALLY EXPANDS!
      │    └── JScrollPane
      │         └── JTable (resultsTable - DefaultTableModel)
      │
      └── SOUTH: southContainer (JPanel) [BorderLayout]
           ├── NORTH: buttonPanel (JPanel) [FlowLayout(CENTER, 15, 6)]
           │    ├── JButton ("<< Previous")
           │    ├── JButton ("Next >>")
           │    └── JButton ("Reserve Selected Book")
           └── SOUTH: statusPanel (JPanel) [FlowLayout(LEFT, 10, 4)]
                └── JLabel (statusLabel: "Showing 1 - 6 of 15 matches | Page 1 of 3")
```

---

## 3.3 Resizing Evidence & Usability Evaluation

The application was compiled, executed, and dynamically captured at three distinct display resolutions to evaluate layout responsiveness:

| Test Window Size | Captured File | Search Row Usability | Results Table Expansion | Button Usability | Status Bar Usability |
|:---|:---|:---|:---|:---|:---|
| **Small**<br>(480 × 360 px) | `Task03_SmallWindow.png` | Fully visible; compact input fields fit snugly within width. | Table displays 4–5 visible rows; vertical/horizontal scrollbars engage automatically. | Buttons wrap neatly or fit without clipping; fully clickable. | Remains anchored at bottom; text truncated gracefully if necessary. |
| **Medium**<br>(720 × 480 px) | `Task03_MediumWindow.png` | Standard comfortable layout; generous padding and margins. | Displays entire page (6 records) without vertical scrolling; columns nicely proportioned. | Perfectly centered with standard 15 px horizontal spacing. | Fully displayed with complete pagination status details. |
| **Large**<br>(1024 × 700 px) | `Task03_LargeWindow.png` | Remains at top with fixed preferred height (no ugly vertical stretching). | Absorbs all extra vertical and horizontal pixels; table expands to reveal extensive details. | Buttons remain neatly centered at bottom; no distortion. | Full status message rendered clearly at the bottom left. |

---

## 3.4 Alternative Concept & Complexity Analysis

### Comparison of Layout Paradigms

```
1. Nested BorderLayout + FlowLayout (Implemented Approach)
   - Clean, modular, low layout computation cost.
   - Combines regional anchors with natural flow.

2. GridBagLayout (Single Container Alternative)
   - Extremely flexible with GridBagConstraints (weightx, weighty, gridwidth).
   - High cognitive complexity; verbose code; slower constraint evaluation.

3. Absolute Positioning (setLayout(null) + setBounds())
   - Hardcoded (x, y, w, h) coordinates.
   - Complete layout failure on window resize or cross-platform font changes!
```

### Comparative Analysis Table

| Metric | Nested BorderLayout + FlowLayout | GridBagLayout | Absolute Positioning (`setLayout(null)`) |
|:---|:---|:---|:---|
| **Resizing Behavior** | Excellent; `CENTER` expands, `NORTH`/`SOUTH` stay fixed. | Excellent; fine-grained control via weights. | **Catastrophic Failure**; components remain frozen or disappear off-screen. |
| **Layout Computation Time** | $O(N)$ linear traversal across shallow component tree. | $O(N)$ with higher constant factor (matrix calculations). | $O(1)$ (no calculation performed by manager). |
| **Container Count** | Moderate (4 nested `JPanel` instances). | Low (1 container with multiple constraints). | Minimal (1 container). |
| **Cross-Platform DPI Scaling** | High; adapts automatically to system font metrics. | High; recalculates cell geometry. | **Zero**; text truncates on high-DPI or non-Windows fonts. |
| **Maintainability** | High; each panel is an independent modular block. | Low; modifying one row alters whole grid alignment. | Extremely Poor; altering one element requires recomputing all offsets. |
| **Final Recommendation** | **Recommended for Clean Desktop Design** | Recommended for complex form grids | **Strictly Discouraged** |
