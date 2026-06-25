package model;

public class Electronics extends Equipment {
    public Electronics(String equipmentId, String name, double dailyRentalRate) {
        super(equipmentId, name, "Electronics", dailyRentalRate);
    }

    @Override
    public double calculateBaseFee(int days) {
        return getDailyRentalRate() * days;
    }

    @Override
    public double calculatePenalty(int lateDays, boolean isDamaged) {
        double penalty = 0;
        if (lateDays > 0) {
            penalty += getDailyRentalRate() * 1.5 * lateDays; // 1.5x daily rate per late day
        }
        if (isDamaged) {
            penalty += 150.0; // Flat damage fee for electronics
        }
        return penalty;
    }
}
