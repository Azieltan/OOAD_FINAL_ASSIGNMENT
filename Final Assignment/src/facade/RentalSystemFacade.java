package facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import manager.BillingManager;
import manager.EquipmentManager;
import manager.RentalManager;
import model.Bill;
import model.Electronics;
import model.Equipment;
import model.LaboratoryEquipment;
import model.MediaEquipment;
import model.RentalRecord;
import model.User;

public class RentalSystemFacade {
    private EquipmentManager equipmentManager;
    private RentalManager rentalManager;
    private BillingManager billingManager;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public RentalSystemFacade() {
        this.equipmentManager = new EquipmentManager();
        this.rentalManager = new RentalManager();
        this.billingManager = new BillingManager();
        seedInitialData();
    }

    private void seedInitialData() {
        // Electronics
        equipmentManager.addEquipment(new Electronics("E101", "Dell XPS Laptop", 35.00));
        equipmentManager.addEquipment(new Electronics("E102", "iPad Pro", 25.00));
        // Media Equipment
        equipmentManager.addEquipment(new MediaEquipment("M201", "Canon DSLR Camera", 45.00));
        equipmentManager.addEquipment(new MediaEquipment("M202", "Epson Projector", 40.00));
        // Laboratory Equipment
        equipmentManager.addEquipment(new LaboratoryEquipment("L301", "Digital Oscilloscope", 60.00));
        equipmentManager.addEquipment(new LaboratoryEquipment("L302", "Compound Microscope", 50.00));
    }

    public void addEquipment(String id, String name, String category, double rate) {
        Equipment eq;
        switch (category.toLowerCase()) {
            case "electronics":
                eq = new Electronics(id, name, rate);
                break;
            case "media equipment":
            case "media":
                eq = new MediaEquipment(id, name, rate);
                break;
            case "laboratory equipment":
            case "laboratory":
            case "lab":
                eq = new LaboratoryEquipment(id, name, rate);
                break;
            default:
                throw new IllegalArgumentException("Unknown equipment category: " + category);
        }
        equipmentManager.addEquipment(eq);
    }

    public List<Equipment> getAllEquipment() {
        return equipmentManager.getAllEquipment();
    }

    public List<Equipment> getAvailableEquipment() {
        return equipmentManager.getAvailableEquipment();
    }

    public String rentEquipment(String userId, String userName, String userTypeStr, String equipmentId, int durationDays) {
        // Validate inputs
        if (userId.trim().isEmpty() || userName.trim().isEmpty()) {
            return "Error: User ID and Name cannot be empty.";
        }
        if (durationDays <= 0) {
            return "Error: Rental duration must be at least 1 day.";
        }

        Optional<Equipment> eqOpt = equipmentManager.findEquipmentById(equipmentId);
        if (!eqOpt.isPresent()) {
            return "Error: Equipment with ID " + equipmentId + " not found.";
        }
        Equipment equipment = eqOpt.get();
        if (!equipment.isAvailable()) {
            return "Error: Equipment is already rented.";
        }

        // Construct User type
        User.UserType userType;
        try {
            userType = User.UserType.valueOf(userTypeStr.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            userType = User.UserType.STUDENT; // Default
        }
        User user = new User(userId, userName, userType);

        // Perform transaction
        RentalRecord record = rentalManager.createRental(user, equipment, durationDays, LocalDate.now());
        return "Success! Rental Record ID: " + record.getRecordId() + "\n" +
               "Item: " + equipment.getName() + " successfully checked out to " + userName + ".";
    }

    public String returnEquipment(String recordId, int actualDurationDays, boolean isDamaged) {
        if (actualDurationDays < 0) {
            return "Error: Actual duration cannot be negative.";
        }

        Optional<RentalRecord> recOpt = rentalManager.findRecordById(recordId);
        if (!recOpt.isPresent()) {
            return "Error: Rental record " + recordId + " not found.";
        }
        RentalRecord record = recOpt.get();
        if (record.getStatus() == RentalRecord.RentalStatus.RETURNED) {
            return "Error: This record is already returned and closed.";
        }

        Equipment equipment = record.getEquipment();

        // 1. Calculate the bill
        Bill bill = billingManager.calculateBill(record.getUser(), equipment, actualDurationDays, record.getPlannedDurationDays(), isDamaged);
        record.setBill(bill);
        record.setReturnDate(record.getRentDate().plusDays(actualDurationDays));
        record.setStatus(RentalRecord.RentalStatus.RETURNED);

        // 2. Mark equipment available again
        equipment.setAvailable(true);

        return "Success! Item '" + equipment.getName() + "' returned.\n\n" + bill.generateDetailedReceipt();
    }

    public List<RentalRecord> getActiveRentals() {
        return rentalManager.getActiveRecords();
    }

    public List<RentalRecord> getAllRentals() {
        return rentalManager.getAllRecords();
    }
}
