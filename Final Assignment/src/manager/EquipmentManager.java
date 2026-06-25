package manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Equipment;

public class EquipmentManager {
    private List<Equipment> inventory;

    public EquipmentManager() {
        this.inventory = new ArrayList<>();
    }

    public void addEquipment(Equipment equipment) {
        inventory.add(equipment);
    }

    public void removeEquipment(Equipment equipment) {
        inventory.remove(equipment);
    }

    public List<Equipment> getAllEquipment() {
        return new ArrayList<>(inventory);
    }

    public List<Equipment> getAvailableEquipment() {
        List<Equipment> available = new ArrayList<>();
        for (Equipment eq : inventory) {
            if (eq.isAvailable()) {
                available.add(eq);
            }
        }
        return available;
    }

    public Optional<Equipment> findEquipmentById(String id) {
        return inventory.stream()
                .filter(eq -> eq.getEquipmentId().equalsIgnoreCase(id))
                .findFirst();
    }
}
