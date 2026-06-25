package manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import model.Equipment;
import model.RentalRecord;
import model.User;

public class RentalManager {
    private List<RentalRecord> records;

    public RentalManager() {
        this.records = new ArrayList<>();
    }

    public RentalRecord createRental(User user, Equipment equipment, int durationDays, LocalDate rentDate) {
        String recordId = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        RentalRecord record = new RentalRecord(recordId, user, equipment, durationDays, rentDate);
        equipment.setAvailable(false);
        records.add(record);
        return record;
    }

    public List<RentalRecord> getAllRecords() {
        return new ArrayList<>(records);
    }

    public Optional<RentalRecord> findRecordById(String recordId) {
        return records.stream()
                .filter(r -> r.getRecordId().equalsIgnoreCase(recordId))
                .findFirst();
    }

    public List<RentalRecord> getActiveRecords() {
        List<RentalRecord> active = new ArrayList<>();
        for (RentalRecord r : records) {
            if (r.getStatus() == RentalRecord.RentalStatus.ACTIVE) {
                active.add(r);
            }
        }
        return active;
    }
}
