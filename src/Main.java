import java.util.*;

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

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

class ReservationValidator {
    public void validate(String guestName, String roomType, RoomInventory inventory)
            throws InvalidBookingException {

        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        if (roomType == null || roomType.trim().isEmpty()) {
            throw new InvalidBookingException("Room type cannot be empty.");
        }

        String normalized = capitalize(roomType);

        if (!normalized.equals("Single") &&
                !normalized.equals("Double") &&
                !normalized.equals("Suite")) {
            throw new InvalidBookingException("Invalid room type selected.");
        }

        if (!inventory.getRoomAvailability().containsKey(normalized)) {
            throw new InvalidBookingException("Room type does not exist in inventory.");
        }

        int available = inventory.getRoomAvailability().get(normalized);

        if (available <= 0) {
            throw new InvalidBookingException("No rooms available for selected type.");
        }
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
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

        if (!availability.containsKey(roomType) || availability.get(roomType) <= 0) {
            System.out.println("No " + roomType + " Booking confirmed for Guest " + reservation.getGuestName());
            return;
        }

        int nextNumber = allocationCounters.getOrDefault(roomType, 0) + 1;
        allocationCounters.put(roomType, nextNumber);

        int updatedCount = availability.get(roomType) - 1;

        if (updatedCount < 0) {
            System.out.println("Error: Inventory inconsistency detected.");
            return;
        }

        inventory.updateAvailability(roomType, updatedCount);

        System.out.println("Booking confirmed for Guest " + reservation.getGuestName() +
                ", Room ID: " + roomType + "-" + nextNumber);
    }
}

class BookingHistory {
    private List<Reservation> confirmedReservations;

    BookingHistory() {
        confirmedReservations = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        confirmedReservations.add(reservation);
    }

    public List<Reservation> getConfirmedReservations() {
        return confirmedReservations;
    }
}

class BookingReportService {
    public void generateReport(BookingHistory history) {
        System.out.println("Booking History and Reporting\n");
        System.out.println("Booking History Report");

        List<Reservation> list = history.getConfirmedReservations();

        for (Reservation r : list) {
            System.out.println("Guest: " + r.getGuestName() + ", Room Type: " + r.getRoomType());
        }
    }
}


  void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();
        ReservationValidator validator = new ReservationValidator();

        System.out.println("Booking Validation");

        try {
            System.out.print("Enter guest name: ");
            String guestName = sc.nextLine();

            System.out.print("Enter room type (Single/Double/Suite): ");
            String roomType = sc.nextLine();

            validator.validate(guestName, roomType, inventory);

            roomType = roomType.substring(0, 1).toUpperCase() +
                    roomType.substring(1).toLowerCase();

            Reservation reservation = new Reservation(guestName, roomType);

            allocationService.allocateRoom(reservation, inventory);
            history.addReservation(reservation);

        } catch (InvalidBookingException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }

        reportService.generateReport(history);
    }
