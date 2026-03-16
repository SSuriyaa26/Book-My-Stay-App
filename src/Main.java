import java.util.*;

class Reservation {
    private String guestName;
    private String roomType;

    Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
    }

    public Reservation getNextRequest() {
        return requestQueue.poll();
    }

    public boolean hasPendingRequests() {
        return !requestQueue.isEmpty();
    }
}

abstract class Room {
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

class RoomInventory {
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
}

class RoomAllocationService {
    private Map<String, Integer> allocationCounters;

    public RoomAllocationService() {
        allocationCounters = new HashMap<>();
    }

    public void allocateRoom(Reservation reservation, RoomInventory inventory) {
        String roomType = reservation.getRoomType();
        Map<String, Integer> availability = inventory.getRoomAvailability();

        if (availability.containsKey(roomType) && availability.get(roomType) > 0) {
            int nextNumber = allocationCounters.getOrDefault(roomType, 0) + 1;
            allocationCounters.put(roomType, nextNumber);

            inventory.updateAvailability(roomType, availability.get(roomType) - 1);

            System.out.println("Booking confirmed for Guest " + reservation.getGuestName() +
                    ", Room ID: " + roomType+ "-" + nextNumber);
        } else {
            System.out.println("No " + roomType + " Booking confirmed for Guest " + reservation.getGuestName());
        }
    }
}

void main(String[] args) {
        System.out.println("Room Allocation Processing");

        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Subha", "Single"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Suite"));

        while (bookingQueue.hasPendingRequests()) {
            Reservation next = bookingQueue.getNextRequest();
            allocationService.allocateRoom(next, inventory);
        }
    }
