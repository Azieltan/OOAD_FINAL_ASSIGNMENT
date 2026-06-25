package model;

public abstract class Equipment {
    private String equipmentId;
    private String name;
    private String category;
    private double dailyRentalRate;
    private boolean isAvailable;

    public Equipment(String equipmentId, String name, String category, double dailyRentalRate) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.category = category;
        this.dailyRentalRate = dailyRentalRate;
        this.isAvailable = true;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getDailyRentalRate() {
        return dailyRentalRate;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    // Abstract methods to demonstrate abstraction and allow polymorphic pricing/penalties
    public abstract double calculateBaseFee(int days);
    public abstract double calculatePenalty(int lateDays, boolean isDamaged);

    @Override
    public String toString() {
        return name + " [" + category + "] - $" + dailyRentalRate + "/day";
    }
}
