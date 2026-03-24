
import java.util.*;

class RoomInventory {
    private Map<String, Integer> data = new HashMap<>();

    public void put(String type, int count) {
        data.put(type, count);
    }

    public Map<String, Integer> all() {
        return data;
    }

    public void reset() {
        data.clear();
    }
}

class FilePersistenceService {

    public void saveInventory(RoomInventory inventory, String filePath) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Integer> e : inventory.all().entrySet()) {
            lines.add(e.getKey() + "=" + e.getValue());
        }
        try {
            Files.write(Path.of(filePath), lines);
        } catch (Exception ignored) {
        }
    }

    public void loadInventory(RoomInventory inventory, String filePath) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(path);
            inventory.reset();
            for (String line : lines) {
                String[] p = line.split("=");
                if (p.length == 2) {
                    inventory.put(p[0], Integer.parseInt(p[1]));
                }
            }
        } catch (Exception ignored) {
            inventory.reset();
        }
    }
}


 void main(String[] args) {
        String file = "inventory.txt";

        RoomInventory inv = new RoomInventory();
        FilePersistenceService svc = new FilePersistenceService();

        svc.loadInventory(inv, file);

        if (inv.all().isEmpty()) {
            System.out.println("System Recovery");
            System.out.println("No valid inventory data found. Starting fresh.\n");

            inv.put("Single", 5);
            inv.put("Double", 3);
            inv.put("Suite", 2);
        } else {
            System.out.println("System Recovery");
            System.out.println("Inventory restored from file.\n");
        }

        System.out.println("Current Inventory:");
        for (var e : inv.all().entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }

        svc.saveInventory(inv, file);
        System.out.println("Inventory saved successfully.");
    }
