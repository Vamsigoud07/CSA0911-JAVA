# TASK A4: JAVA OBJECT SERIALIZATION & DATA PERSISTENCE

**Course:** Programming in Java (CSA09) – CO5: Data Persistence Engineering  
**Student Name:** K VENKATA VAMSI GOUD  
**Register Number:** 192472021  
**Institution:** Saveetha School of Engineering, SIMATS  

---

## 1. Domain Object-Graph Context
A campus accommodation system manages interconnected entities:
- A `Hostel` contains a collection of `Room` objects.
- Each `Room` contains a list of `Resident` (student) objects.
- A `Resident` object contains profile data, contact information, and security credentials.

---

## 2. A4.1 – Object-Graph Serialization Mechanics & Transient Fields

### Object-Graph Traversal:
When `ObjectOutputStream.writeObject(hostel)` is invoked:
1. Java uses recursive reflection to traverse the entire reference graph starting from the root `Hostel` instance.
2. It detects cycles (e.g., if `Resident` has a bidirectional back-reference to `Room`) using an internal object identity cache to prevent infinite recursion.
3. Every class in the reachable object hierarchy must implement `java.io.Serializable`; if an unannotated non-serializable object is reached, the JVM immediately aborts with `java.io.NotSerializableException`.

### Identifying Non-Serializable & Transient Fields:
Fields marked with the `transient` keyword are deliberately skipped during serialization:
1. **Live Database `Connection` / Sockets / File Handles:**
   - *Reason:* Sockets, file descriptors, and JDBC connections represent ephemeral operating system resources tied to active kernel process tables. Persisting raw memory pointers across JVM restarts is physically meaningless.
2. **Security Credentials & Passwords:**
   - *Reason:* Raw passwords must **never** be serialized or stored in plaintext. If authentication state must be preserved, store a cryptographically salted one-way hash (e.g., Argon2, bcrypt, or PBKDF2 with SHA-256) rather than reversibly serializing credentials. Runtime plaintext password fields must always be declared `transient`.
3. **Calculated / Cached Values:**
   - Transient flags prevent persisting derived state (e.g., `transient double currentMonthlyMessBill`), which can be recalculated dynamically upon deserialization.

---

## 3. A4.2 – Schema Evolution, `serialVersionUID`, and Compatibility

### Role of `serialVersionUID`:
`serialVersionUID` is a 64-bit hash representing the structural fingerprint of a class:
```java
private static final long serialVersionUID = 192472021001L;
```
- If an explicit `serialVersionUID` is omitted, the JVM compiler automatically computes one at runtime based on field names, methods, and interfaces.
- **The Danger:** Modifying any minor element (e.g., adding a private helper method) causes the compiler to generate a different hash, causing deserialization of older files to immediately fail with `java.io.InvalidClassException: local class incompatible`.

### Scenario: Adding a `phoneNumber` Field to `Resident`:
When the new field `private String phoneNumber;` is introduced:
1. **Maintaining Compatibility:** Keep `serialVersionUID` identical to the original version.
2. **Behavior on Reading Legacy Serialized Files:**
   - The JVM reads the legacy byte stream.
   - Because the stream lacks the `phoneNumber` field, Java initializes it to its default type value: `null` for objects, `0` for numeric primitives, or `false` for booleans.
3. **Graceful Handling (`readObject` Hook):**
   ```java
   private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
       in.defaultReadObject(); // Read all existing fields
       if (this.phoneNumber == null) {
           this.phoneNumber = "NOT_PROVIDED"; // Backward compatibility default
       }
   }
   ```

---

## 4. Alternative Concept: Java Native Serialization vs. JSON vs. CSV

While Java Native Serialization is built into the standard library, enterprise architectures overwhelmingly favor JSON or CSV for long-term data persistence.

### Architectural Comparison Table:

| Metric | Java Native Serialization | JSON (Jackson / Gson) | Delimited Text (CSV) |
| :--- | :--- | :--- | :--- |
| **File Format** | Opaque binary byte-stream. | Human-readable structured text. | Flat tabular text. |
| **Cross-Platform Portability** | Zero; strictly tied to Java runtime. | Universal across Java, Python, C#, Node.js, Go, web. | Universal across all tools, scripts, and databases. |
| **Security Risk** | **Critical Vulnerability**: Deserialization of untrusted data allows Remote Code Execution (RCE) via gadget chains. | Minimal; safe parser configuration without polymorphic typing. | Negligible; plain text tokens without executable object instantiation. |
| **Schema Evolution** | Extremely brittle; sensitive to `serialVersionUID` and class bytecode changes. | Highly resilient; unrecognized fields can be ignored gracefully. | Simple append of columns; legacy rows handled with token length checks. |
| **Storage Efficiency** | Medium; contains verbose Java package/class metadata. | Medium; repeated JSON key names. | Highest; purely values separated by commas. |
| **Save/Load Performance** | Moderate; heavy reflection overhead. | Fast with optimized streaming parsers. | Fastest; minimal string splitting overhead. |
