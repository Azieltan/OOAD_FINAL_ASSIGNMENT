package model;

public class MediaEquipment extends Equipment {
    public MediaEquipment(String equipmentId, String name, double dailyRentalRate) {
        super(equipmentId, name, "Media Equipment", dailyRentalRate);
    }

    @Override
    public double calculateBaseFee(int days) {
        double rate = getDailyRentalRate();
        if (days > 7) {
            rate *= 0.9; // 10% discount on daily rate for long-term rentals (> 7 days)
        }
        return rate * days;
    }

    @Override
    public double calculatePenalty(int lateDays, boolean isDamaged) {
        double penalty = 0;
        if (lateDays > 0) {
            penalty += getDailyRentalRate() * 2.0 * lateDays; // 2.0x daily rate per late day
        }
        if (isDamaged) {
            penalty += 200.0; // Flat damage fee for media equipment
        }
        return penalty;
    }
}
