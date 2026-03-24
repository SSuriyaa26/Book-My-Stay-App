import java.util.*;

class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
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

    public String allocateRoom(Reservation reservation, RoomInventory inventory) {
        String roomType = reservation.getRoomType();
        Map<String, Integer> availability = inventory.getRoomAvailability();

        if (availability.containsKey(roomType) && availability.get(roomType) > 0) {
            int nextNumber = allocationCounters.getOrDefault(roomType, 0) + 1;
            allocationCounters.put(roomType, nextNumber);
            inventory.updateAvailability(roomType, availability.get(roomType) - 1);
            return roomType + "-" + nextNumber;
        } else {
            return null;
        }
    }
}

class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }
}

class AddOnServiceManager {
    private Map<String, List<AddOnService>> servicesByReservation;

    public AddOnServiceManager() {
        servicesByReservation = new HashMap<>();
    }

    public void addService(String reservationId, AddOnService service) {
        servicesByReservation
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    public List<AddOnService> getServices(String reservationId) {
        return servicesByReservation.getOrDefault(reservationId, Collections.emptyList());
    }

    public double calculateTotalServiceCost(String reservationId) {
        List<AddOnService> services = servicesByReservation.get(reservationId);

        if (services == null || services.isEmpty()) return 0.0;

        double total = 0.0;
        for (AddOnService s : services) {
            total += s.getCost();
        }
        return total;
    }
}


  void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        Reservation r1 = new Reservation("R1", "Abhi", "Single");
        Reservation r2 = new Reservation("R2", "Subha", "Single");
        Reservation r3 = new Reservation("R3", "Vanmathi", "Suite");

        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        Map<String, String> reservationToRoomId = new HashMap<>();

        while (bookingQueue.hasPendingRequests()) {
            Reservation next = bookingQueue.getNextRequest();
            String roomId = allocationService.allocateRoom(next, inventory);

            if (roomId != null) {
                reservationToRoomId.put(next.getReservationId(), roomId);
            } else {
                reservationToRoomId.put(next.getReservationId(), "");
            }
        }

        AddOnServiceManager addOnManager = new AddOnServiceManager();

        addOnManager.addService("R1", new AddOnService("Breakfast", 200.0));
        addOnManager.addService("R1", new AddOnService("Airport Pickup", 500.0));
        addOnManager.addService("R3", new AddOnService("Spa Package", 1200.0));

        String reservationKey = "R1";
        String roomId = reservationToRoomId.getOrDefault(reservationKey, "");
        double totalCost = addOnManager.calculateTotalServiceCost(reservationKey);

        System.out.println("Add on service selection:");
        System.out.println("Reservation ID: " + roomId);
        System.out.println("Total Add on cost: " + totalCost);
    }
