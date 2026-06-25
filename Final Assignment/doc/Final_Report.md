# OBJECT-ORIENTED ANALYSIS AND DESIGN (CCP6224)
## FINAL ASSIGNMENT REPORT
### Smart Equipment Rental & Billing System (Self-Service Kiosk Model)

---

## 1. Assumptions & Design Decisions

### 1.1 Authentication & Self-Service Flow
1. **Self-Service Renter Portal**: To build a modern "Smart System," we implemented a self-service kiosk workflow. Reners (Students and Staff) register and log in simply by inputting their **User ID** and **Name**.
2. **Persistent Sessions**: Once a user has rented equipment, they can return to the kiosk, log in with the exact same ID and Name, and the system loads their active rental records.
3. **Admin Verification**: Administrative access is strictly gated. The admin button triggers a credential prompt requiring ID: `admin123` and Password: `admin123`.

### 1.2 Deposit & Settlement Logic
1. **Flat Security Deposit**: For each item checked out, a security deposit of **$50.00** is paid immediately alongside the base rental fee.
2. **Return Deductions**: When returning an item, any late fees or damage penalties are automatically deducted from the paid deposit:
   - **Refund**: If `Deposit > Total Penalties`, the user is immediately refunded the difference.
   - **Invoicing**: If `Total Penalties > Deposit`, the user is billed for the remaining outstanding balance.

### 1.3 Equipment Statuses
Instead of binary availability, equipment has condition status levels:
- `AVAILABLE` (Ready for checkout)
- `RENTED` (Currently checked out)
- `MAINTENANCE` (Temporarily unavailable; restricted by Admin)
- `DAMAGED` (Damaged during rental; restricted by Admin until resolved)

---

## 2. Object-Oriented Programming (OOP) Principles Applied

### 2.1 Abstraction
We defined an abstract base class `Equipment` inside the `model` package. It encapsulates shared properties (e.g., `equipmentId`, `name`, `dailyRentalRate`, `status`) and exposes abstract operations:
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
|    LoginFrame    |
+--------+---------+
         | (Switch view / Authenticate)
         v
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
1. **Shields the GUI**: The Swing interface (`RentalAppGUI` and `LoginFrame`) only communicates with the `RentalSystemFacade`. If we decide to swap out the `BillingManager` calculation algorithms, update the database interface in `RentalManager`, or add logging subsystems, the GUI source code remains entirely unchanged.
2. **Easy Subsystem Extensions**: New equipment rules or categories can be added within the subsystem classes directly. The GUI remains cleanly decoupled, preserving the Open-Closed Principle (OCP).

---

## 4. UML Diagram Explanations

### 4.1 Use Case Diagram (`uml/use_case_diagram.puml`)
Illustrates interactions between the Actors (**Student/Staff (Renter)**, **Administrator**) and the system use cases.
- *Includes/Extends*: "Select & Checkout Multiple Items" includes the "Pay Security Deposit & Rental Fee" use case. "Return Items & Process Deductions" includes the "Settle Refund / Balance Invoice" use case.

### 4.2 Class Diagram (`uml/class_diagram.puml`)
Shows the static structure. It displays:
- The new `LoginFrame` class and its association with `RentalSystemFacade`.
- `EquipmentStatus` and `UserType` enums.
- Navigability, multiplicities, and specific attribute/method signatures.

### 4.3 Sequence Diagram (`uml/sequence_diagram.puml`)
Shows step-by-step object interactions during the Return & Billing process.
1. GUI prompts the Facade.
2. Facade fetches the User type, the polymorphic Equipment reference, and the paid deposit amount from the `RentalRecord`.
3. Facade delegates calculation to `BillingManager`.
4. `BillingManager` dynamically calls the correct `calculateBaseFee` and `calculatePenalty` methods on the polymorphic `Equipment` instance, returning a structured `Bill`.

---

## 5. Q&A Preparation for Interview

* **Q: Why did you implement a self-service checkout rather than a desk clerk?**
  * *A*: Self-service kiosks represent modern client demands. It reduces administrative overhead. Renter identity is authenticated during entry, auto-populating checkouts and restricting return lists to their own specific active rentals.
* **Q: How does the deposit deduction logic work?**
  * *A*: When a user checks out an item, they pay a $50 deposit. When returning, the system calculates the actual fees (including late returns and damages). These fees are settled directly using the deposit, and the system either refunds the remainder or prompts for additional payment.
* **Q: How does the system handle different equipment conditions?**
  * *A*: Equipment has a `status` field (`AVAILABLE`, `RENTED`, `MAINTENANCE`, `DAMAGED`). If an item is returned damaged, its status becomes `DAMAGED` automatically, preventing other users from renting it until the Admin updates it to `AVAILABLE` or `MAINTENANCE` via the Admin panel.
