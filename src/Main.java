
public abstract class Room {
    protected String roomType;
    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;
    public int availability;

    public Room(String roomType, int numberOfBeds, int squareFeet, double pricePerNight) {
        this.roomType = roomType;
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }

    public void displayRoomDetails() {
        System.out.println("Room Type: " + roomType +
                "\nBeds: " + numberOfBeds +
                "\nSize: " + squareFeet + " sq.ft" +
                "\nPrice per night: $" + pricePerNight +
                "\nAvailability: " + availability + "\n");
    }
}

class SingleRoom extends Room {
    public SingleRoom() {
        super("Single", 1, 250, 1500.0);
        this.availability = 5;
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double", 2, 400, 2500.0);
        this.availability = 3;
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite", 3, 750, 5000.0);
        this.availability = 2;
    }
}

public class RoomInventory {
    private Map<String, Room> roomDetails;
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        initializeInventory();
    }

    private void initializeInventory() {
        roomDetails = new HashMap<>();
        roomDetails.put("Single", new SingleRoom());
        roomDetails.put("Double", new DoubleRoom());
        roomDetails.put("Suite", new SuiteRoom());

        roomAvailability = new HashMap<>();
        roomAvailability.put("Single", roomDetails.get("Single").availability);
        roomAvailability.put("Double", roomDetails.get("Double").availability);
        roomAvailability.put("Suite", roomDetails.get("Suite").availability);
    }

    public Map<String, Integer> getRoomAvailability() {
        return roomAvailability;
    }

    public void updateAvailability(String roomType, int count) {
        if (roomAvailability.containsKey(roomType)) {
            roomAvailability.put(roomType, count);
            roomDetails.get(roomType).availability = count;
        }
    }

    public void displayAllRooms() {
        System.out.println("Hotel Room Inventory Details:\n");
        for (Room room : roomDetails.values()) {
            room.displayRoomDetails();
        }
    }


}
void main(String[] args) {
    System.out.println("Hotel Room Initialization\n");

    RoomInventory inventory = new RoomInventory();

    System.out.println("Initial availability:");
    inventory.displayAllRooms();

    inventory.updateAvailability("Single", 8);
    System.out.println("Updated availability:");
    inventory.displayAllRooms();
}