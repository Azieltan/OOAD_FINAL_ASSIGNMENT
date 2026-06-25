package manager;

import model.Bill;
import model.Equipment;
import model.User;

public class BillingManager {
    public Bill calculateBill(User user, Equipment equipment, int actualDurationDays, int plannedDurationDays, boolean isDamaged) {
        // 1. Calculate Base Fee (delegated to polymorphic equipment class)
        double baseFee = equipment.calculateBaseFee(plannedDurationDays);

        // 2. Calculate User Type Discounts
        double discountAmount = 0.0;
        if (user.getType() == User.UserType.STAFF) {
            discountAmount = baseFee * 0.20; // 20% discount for Staff
        } else if (user.getType() == User.UserType.FINAL_YEAR_STUDENT) {
            discountAmount = baseFee * 0.10; // 10% discount for Final Year Students
        }

        // 3. Calculate Penalties (delegated to polymorphic equipment class)
        int lateDays = Math.max(0, actualDurationDays - plannedDurationDays);
        double penaltyAmount = equipment.calculatePenalty(lateDays, isDamaged);

        // 4. Create and return the Bill
        return new Bill(baseFee, discountAmount, penaltyAmount);
    }
}
