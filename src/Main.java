import java.util.*;
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
        System.out.println(
                "Beds: " + numberOfBeds +
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
public class RoomSearchService {
    public void searchAvailableRooms(RoomInventory inventory,
                                     Room singleRoom,
                                     Room doubleRoom,
                                     Room suiteRoom) {

        Map<String, Integer> availability = inventory.getRoomAvailability();

        if (availability.get("Single") > 0) {
            System.out.println("Single Room:");
            singleRoom.displayRoomDetails();
        }

        if (availability.get("Double") > 0) {
            System.out.println("Double Room:");
            doubleRoom.displayRoomDetails();
        }

        if (availability.get("Suite") > 0) {
            System.out.println("Suite Room:");
            suiteRoom.displayRoomDetails();
        }
    }


}
void main(String[] args) {
    RoomInventory inventory = new RoomInventory();
    Room single = new SingleRoom();
    Room dbl = new DoubleRoom();
    Room suite = new SuiteRoom();

    RoomSearchService service = new RoomSearchService();
    System.out.println("Room Search\n");
    service.searchAvailableRooms(inventory, single, dbl, suite);
}
