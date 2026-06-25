package facade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    
    // User session and DB simulated state
    private List<User> registeredUsers;
    private User currentUser;
    private boolean isAdminSession;

    public RentalSystemFacade() {
        this.equipmentManager = new EquipmentManager();
        this.rentalManager = new RentalManager();
        this.billingManager = new BillingManager();
        this.registeredUsers = new ArrayList<>();
        this.currentUser = null;
        this.isAdminSession = false;
        seedInitialData();
    }

    private void seedInitialData() {
        // Pre-seeded inventory
        equipmentManager.addEquipment(new Electronics("E101", "Dell XPS Laptop", 35.00));
        equipmentManager.addEquipment(new Electronics("E102", "iPad Pro", 25.00));
        equipmentManager.addEquipment(new MediaEquipment("M201", "Canon DSLR Camera", 45.00));
        equipmentManager.addEquipment(new MediaEquipment("M202", "Epson Projector", 40.00));
        equipmentManager.addEquipment(new LaboratoryEquipment("L301", "Digital Oscilloscope", 60.00));
        equipmentManager.addEquipment(new LaboratoryEquipment("L302", "Compound Microscope", 50.00));
        
        // Pre-seeded renters (for returning test)
        User student = new User("S101", "Alice", User.UserType.STUDENT);
        registeredUsers.add(student);
    }

    // --- Authentication ---

    public boolean adminLogin(String id, String password) {
        if ("admin123".equals(id) && "admin123".equals(password)) {
            isAdminSession = true;
            currentUser = null;
            return true;
        }
        return false;
    }

    public String userLogin(String id, String name, String userTypeStr) {
        if (id.trim().isEmpty() || name.trim().isEmpty()) {
            return "Error: User ID and Name cannot be empty.";
        }
        
        // Find existing user
        Optional<User> existing = registeredUsers.stream()
                .filter(u -> u.getUserId().equalsIgnoreCase(id))
                .findFirst();
        
        if (existing.isPresent()) {
            User user = existing.get();
            // Verify name matches
            if (!user.getName().equalsIgnoreCase(name)) {
                return "Error: User ID exists but name does not match. Please enter correct name.";
            }
            currentUser = user;
        } else {
            // Register new user
            User.UserType userType;
            try {
                userType = User.UserType.valueOf(userTypeStr.toUpperCase().replace(" ", "_"));
            } catch (IllegalArgumentException e) {
                userType = User.UserType.STUDENT;
            }
            User newUser = new User(id, name, userType);
            registeredUsers.add(newUser);
            currentUser = newUser;
        }
        
        isAdminSession = false;
        return "Success";
    }

    public void logout() {
        currentUser = null;
        isAdminSession = false;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // --- Equipment Operations ---

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

    public void removeEquipment(String id) {
        Optional<Equipment> eqOpt = equipmentManager.findEquipmentById(id);
        if (eqOpt.isPresent()) {
            equipmentManager.getAllEquipment().remove(eqOpt.get());
        }
    }

    public void updateEquipment(String id, String name, double rate, String statusStr) {
        Optional<Equipment> eqOpt = equipmentManager.findEquipmentById(id);
        if (eqOpt.isPresent()) {
            Equipment eq = eqOpt.get();
            eq.setName(name);
            eq.setDailyRentalRate(rate);
            try {
                eq.setStatus(Equipment.EquipmentStatus.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Invalid status ignored
            }
        }
    }

    public List<Equipment> getAllEquipment() {
        return equipmentManager.getAllEquipment();
    }

    public List<Equipment> getAvailableEquipment() {
        return equipmentManager.getAvailableEquipment();
    }

    // --- Checkout & Return ---

    public String rentEquipmentList(List<String> equipmentIds, int durationDays) {
        if (currentUser == null) {
            return "Error: No user session active. Please log in.";
        }
        if (equipmentIds == null || equipmentIds.isEmpty()) {
            return "Error: No equipment selected.";
        }
        if (durationDays <= 0) {
            return "Error: Rental duration must be at least 1 day.";
        }

        StringBuilder response = new StringBuilder();
        double totalPaidAmount = 0.0;
        int successCount = 0;

        for (String id : equipmentIds) {
            Optional<Equipment> eqOpt = equipmentManager.findEquipmentById(id);
            if (eqOpt.isPresent()) {
                Equipment eq = eqOpt.get();
                if (eq.isAvailable()) {
                    double baseFee = eq.calculateBaseFee(durationDays);
                    
                    // User Type Discount
                    double discount = 0.0;
                    if (currentUser.getType() == User.UserType.STAFF) {
                        discount = baseFee * 0.20;
                    } else if (currentUser.getType() == User.UserType.FINAL_YEAR_STUDENT) {
                        discount = baseFee * 0.10;
                    }
                    
                    double deposit = 50.00; // Flat deposit
                    double netDue = baseFee - discount + deposit;
                    totalPaidAmount += netDue;

                    rentalManager.createRental(currentUser, eq, durationDays, LocalDate.now(), deposit);
                    successCount++;
                } else {
                    response.append("Item ").append(eq.getName()).append(" is no longer available.\n");
                }
            }
        }

        if (successCount > 0) {
            return "Success! Checked out " + successCount + " items.\n" +
                   String.format("Total Paid Immediately (Base Rate + Deposits): $%.2f", totalPaidAmount);
        } else {
            return "Checkout Failed.\n" + response.toString();
        }
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
        Bill bill = billingManager.calculateBill(
                record.getUser(), 
                equipment, 
                actualDurationDays, 
                record.getPlannedDurationDays(), 
                isDamaged, 
                record.getDepositPaid()
        );
        
        record.setBill(bill);
        record.setReturnDate(record.getRentDate().plusDays(actualDurationDays));
        record.setStatus(RentalRecord.RentalStatus.RETURNED);

        // 2. Reset equipment status
        if (isDamaged) {
            equipment.setStatus(Equipment.EquipmentStatus.DAMAGED);
        } else {
            equipment.setStatus(Equipment.EquipmentStatus.AVAILABLE);
        }

        return "Success! Item '" + equipment.getName() + "' returned.\n\n" + bill.generateDetailedReceipt();
    }

    public List<RentalRecord> getActiveRentals() {
        return rentalManager.getActiveRecords();
    }

    public List<RentalRecord> getCurrentUserActiveRentals() {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        return rentalManager.getActiveRecords().stream()
                .filter(r -> r.getUser().getUserId().equalsIgnoreCase(currentUser.getUserId()))
                .collect(Collectors.toList());
    }

    public List<RentalRecord> getAllRentals() {
        return rentalManager.getAllRecords();
    }
}
