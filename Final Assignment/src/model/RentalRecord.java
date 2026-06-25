package model;

import java.time.LocalDate;

public class RentalRecord {
    public enum RentalStatus {
        ACTIVE,
        RETURNED
    }

    private String recordId;
    private User user;
    private Equipment equipment;
    private int plannedDurationDays;
    private LocalDate rentDate;
    private LocalDate returnDate;
    private RentalStatus status;
    private Bill bill;

    public RentalRecord(String recordId, User user, Equipment equipment, int plannedDurationDays, LocalDate rentDate) {
        this.recordId = recordId;
        this.user = user;
        this.equipment = equipment;
        this.plannedDurationDays = plannedDurationDays;
        this.rentDate = rentDate;
        this.status = RentalStatus.ACTIVE;
    }

    public String getRecordId() {
        return recordId;
    }

    public User getUser() {
        return user;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public int getPlannedDurationDays() {
        return plannedDurationDays;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
}
