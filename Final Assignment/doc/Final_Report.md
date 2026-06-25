# OBJECT-ORIENTED ANALYSIS AND DESIGN (CCP6224)
## FINAL ASSIGNMENT REPORT
### Smart Equipment Rental & Billing System

---

## 1. Assumptions & Design Decisions

### 1.1 Assumptions
1. **User Types**: Renter users are students or staff. Final-year students are eligible for a 10% discount on the base fee, and staff members are eligible for a 20% discount on the base fee.
2. **Category Specifics**:
   - **Electronics**: Base rate is linear. Late return penalty is $1.5 \times \text{daily rate} \times \text{late days}$. Damage incurs a flat $150.00 penalty.
   - **Media Equipment**: 10% discount on daily rate if rented for more than 7 days. Late return penalty is $2.0 \times \text{daily rate} \times \text{late days}$. Damage incurs a flat $200.00 penalty.
   - **Laboratory Equipment**: Base rate is linear. Late return penalty is $2.5 \times \text{daily rate} \times \text{late days}$. Damage incurs a flat $300.00 penalty.
3. **Data Persistence**: In-memory collections (Lists/Maps) are used to maintain state because a persistent database is out of scope for the GUI modeling constraints of this course.

### 1.2 Design Decisions
- **Separation of Concerns**: We separated the domain models (`model` package) from state management (`manager` package) and user interface (`gui` package).
- **GUI Framework**: Implemented using pure Java Swing (as mandated by the brief), leveraging a clean tabbed pane structure (`JTabbedPane`).
- **Design Pattern**: We applied the **Facade** Design Pattern to manage system workflows.

---

## 2. Object-Oriented Programming (OOP) Principles Applied

### 2.1 Abstraction
We defined an abstract base class `Equipment` inside the `model` package. It encapsulates shared properties (e.g., `equipmentId`, `name`, `dailyRentalRate`, `isAvailable`) and exposes abstract operations:
```java
public abstract double calculateBaseFee(int days);
public abstract double calculatePenalty(int lateDays, boolean isDamaged);
```
Clients (such as the `BillingManager`) interact with `Equipment` abstractly, without needing to know whether the physical device is a Laptop or a Microscope.

### 2.2 Inheritance
`Electronics`, `MediaEquipment`, and `LaboratoryEquipment` extend the abstract `Equipment` class. They inherit all common properties and get access to the parent's protected constructors and methods, which minimizes code duplication (reusability).

### 2.3 Polymorphism
Dynamic binding is leveraged to implement different calculations for different equipment categories. For example:
- In `MediaEquipment.java`:
  ```java
  @Override
  public double calculateBaseFee(int days) {
      double rate = getDailyRentalRate();
      if (days > 7) rate *= 0.9; // 10% long-term discount
      return rate * days;
  }
  ```
- In `LaboratoryEquipment.java`:
  ```java
  @Override
  public double calculatePenalty(int lateDays, boolean isDamaged) {
      double penalty = 0;
      if (lateDays > 0) penalty += getDailyRentalRate() * 2.5 * lateDays; // 2.5x strict penalty
      if (isDamaged) penalty += 300.0;
      return penalty;
  }
  ```
During billing, `BillingManager` invokes `equipment.calculateBaseFee(...)` and `equipment.calculatePenalty(...)`. The JVM resolves the method call at runtime depending on the actual concrete subclass instance, demonstrating clean polymorphism.

### 2.4 Aggregation vs. Composition
- **Aggregation**: `EquipmentManager` has an aggregation relationship with `Equipment`. The equipment objects exist independently in the system inventory even if they are not currently indexed by a specific manager instance.
- **Composition**: `RentalRecord` exhibits composition with `Bill`. A `Bill` is created solely within the context of a return operation and belongs exclusively to that specific transaction. When the `RentalRecord` is deleted, its associated `Bill` is also destroyed.

---

## 3. Design Pattern Application: Facade Pattern

### 3.1 Justification & Slides Link
According to **Lec11: Common Design Patterns (Structural)**, the **Facade** design pattern is defined as:
> **Intent**: "Provide a unified interface to a set of interfaces in a subsystem. Facade defines a higher-level interface that makes the subsystem easier to use."

### 3.2 System Implementation
Our implementation coordinates three subsystems via the `RentalSystemFacade` class:
1. `EquipmentManager` (Inventory tracking)
2. `RentalManager` (Checkout and record keeping)
3. `BillingManager` (Calculations for base rate, user discounts, and category penalties)

```
+------------------+
|  RentalAppGUI    |
+--------+---------+
         | (Simplified API)
         v
+--------+---------+          +--------------------+
|RentalSystemFacade+--------->|  EquipmentManager  |
+--------+---------+          +--------------------+
         |                    +--------------------+
         +------------------->|   RentalManager    |
         |                    +--------------------+
         |                    +--------------------+
         +------------------->|   BillingManager   |
                              +--------------------+
```

### 3.3 How it Improves Flexibility & Future-Proofing
1. **Shields the GUI**: The Swing interface (`RentalAppGUI`) only communicates with the `RentalSystemFacade`. If we decide to swap out the `BillingManager` calculation algorithms, update the database interface in `RentalManager`, or add logging subsystems, the GUI source code remains entirely unchanged.
2. **Easy Subsystem Extensions**: New equipment rules or categories can be added within the subsystem classes directly. The GUI remains cleanly decoupled, preserving the Open-Closed Principle (OCP).

---

## 4. UML Diagram Explanations

### 4.1 Use Case Diagram (`uml/use_case_diagram.puml`)
Illustrates interactions between the Actors (**Student**, **Staff**, **Final-Year Student** (generalization of Student), **Facilities Manager**) and the system use cases.
- *Includes/Extends*: "Return Equipment" includes the "Calculate Billing Details" use case. "Calculate Billing Details" is extended by "Apply User Discount" and "Apply Category-Specific Penalty" rules under conditional flows.

### 4.2 Class Diagram (`uml/class_diagram.puml`)
Shows the static structure. It clearly displays:
- Inheritance hierarchical structure for the three `Equipment` categories.
- Separation of concerns between `gui`, `facade`, `manager`, and `model` packages.
- Navigability, multiplicities, and specific attribute/method signatures.

### 4.3 Sequence Diagram (`uml/sequence_diagram.puml`)
Shows step-by-step object interactions during the Return & Billing process.
1. GUI prompts the Facade.
2. Facade fetches the User type and the polymorphic Equipment reference from `RentalRecord`.
3. Facade delegates calculation to `BillingManager`.
4. `BillingManager` dynamically calls the correct `calculateBaseFee` and `calculatePenalty` methods on the polymorphic `Equipment` instance, returning a structured `Bill`.

---

## 5. Q&A Preparation for Interview

* **Q: Why didn't you use multiple inheritance for equipment categories?**
  * *A*: Java does not support multiple inheritance of classes (to avoid the Diamond Problem). Instead, we used a single abstract base class `Equipment` and specialized subclasses.
* **Q: How does the Facade pattern make this system future-proof?**
  * *A*: If a client requirements change (e.g., migrating from in-memory collections to a SQL database), we only need to rewrite the implementation inside the manager subsystem. The Facade interface signature is preserved, so no GUI refactoring is needed.
* **Q: How is polymorphism displayed in billing?**
  * *A*: When the `BillingManager` calculates penalties, it calls `equipment.calculatePenalty()`. Because the `equipment` variable is declared as the abstract base class but points to a concrete subclass at runtime (e.g. `LaboratoryEquipment`), the JVM automatically calls the strict 2.5x rate logic instead of the default electronics rate.
