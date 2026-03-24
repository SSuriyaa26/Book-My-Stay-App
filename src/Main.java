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
        for (String type : roomDetails.keySet()) {
            roomAvailability.put(type, roomDetails.get(type).availability);
        }
    }

    public Map<String, Integer> getRoomAvailability() {
        return roomAvailability;
    }

    public void updateAvailability(String roomType, int count) {
        roomAvailability.put(roomType, count);
        roomDetails.get(roomType).availability = count;
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

        String normalized = roomType.substring(0, 1).toUpperCase() +
                roomType.substring(1).toLowerCase();

        if (!normalized.equals("Single") &&
                !normalized.equals("Double") &&
                !normalized.equals("Suite")) {
            throw new InvalidBookingException("Invalid room type selected.");
        }

        int available = inventory.getRoomAvailability().get(normalized);

        if (available <= 0) {
            throw new InvalidBookingException("No rooms available for selected type.");
        }
    }
}

class RoomAllocationService {
    private Map<String, Integer> allocationCounters = new HashMap<>();

    public String allocateRoom(Reservation reservation, RoomInventory inventory) {
        String roomType = reservation.getRoomType();
        Map<String, Integer> availability = inventory.getRoomAvailability();

        if (!availability.containsKey(roomType) || availability.get(roomType) <= 0) {
            System.out.println("No " + roomType + " Booking confirmed for Guest " + reservation.getGuestName());
            return null;
        }

        int nextNumber = allocationCounters.getOrDefault(roomType, 0) + 1;
        allocationCounters.put(roomType, nextNumber);

        int updatedCount = availability.get(roomType) - 1;
        inventory.updateAvailability(roomType, updatedCount);

        String reservationId = roomType + "-" + nextNumber;

        System.out.println("Booking confirmed for Guest " + reservation.getGuestName() +
                ", Room ID: " + reservationId);

        return reservationId;
    }
}

class BookingHistory {
    private List<Reservation> confirmedReservations = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        confirmedReservations.add(reservation);
    }

    public List<Reservation> getConfirmedReservations() {
        return confirmedReservations;
    }
}

class BookingReportService {
    public void generateReport(BookingHistory history) {
        System.out.println("\nBooking History Report");
        for (Reservation r : history.getConfirmedReservations()) {
            System.out.println("Guest: " + r.getGuestName() + ", Room Type: " + r.getRoomType());
        }
    }
}

class CancellationService {
    private Stack<String> releasedRoomIds = new Stack<>();
    private Map<String, String> reservationRoomTypeMap = new HashMap<>();

    public void registerBooking(String reservationId, String roomType) {
        reservationRoomTypeMap.put(reservationId, roomType);
    }

    public void cancelBooking(String reservationId, RoomInventory inventory) {
        System.out.println("\nBooking Cancellation");

        if (!reservationRoomTypeMap.containsKey(reservationId)) {
            System.out.println("Cancellation failed: Invalid reservation ID.");
            return;
        }

        String roomType = reservationRoomTypeMap.get(reservationId);
        Map<String, Integer> availability = inventory.getRoomAvailability();

        int updatedCount = availability.get(roomType) + 1;
        inventory.updateAvailability(roomType, updatedCount);

        releasedRoomIds.push(reservationId);
        reservationRoomTypeMap.remove(reservationId);

        System.out.println("Booking cancelled successfully. Inventory restored for room type: " + roomType);
    }

    public void showRollbackHistory(RoomInventory inventory, String roomType) {
        System.out.println("\nRollback History (Most Recent First):");

        while (!releasedRoomIds.isEmpty()) {
            System.out.println("Released Reservation ID: " + releasedRoomIds.pop());
        }

        int updatedAvailability = inventory.getRoomAvailability().get(roomType);
        System.out.println("\nUpdated " + roomType + " Room Availability: " + updatedAvailability);
    }
}


 void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();
        ReservationValidator validator = new ReservationValidator();
        CancellationService cancellationService = new CancellationService();

        try {
            System.out.print("Enter guest name: ");
            String guestName = sc.nextLine();

            System.out.print("Enter room type (Single/Double/Suite): ");
            String roomType = sc.nextLine();

            validator.validate(guestName, roomType, inventory);

            roomType = roomType.substring(0, 1).toUpperCase() +
                    roomType.substring(1).toLowerCase();

            Reservation reservation = new Reservation(guestName, roomType);

            String reservationId = allocationService.allocateRoom(reservation, inventory);

            if (reservationId != null) {
                history.addReservation(reservation);
                cancellationService.registerBooking(reservationId, roomType);

                System.out.print("Do you want to cancel this booking? (yes/no): ");
                String choice = sc.nextLine();

                if (choice.equalsIgnoreCase("yes")) {
                    cancellationService.cancelBooking(reservationId, inventory);
                    cancellationService.showRollbackHistory(inventory, roomType);
                }
            }

        } catch (InvalidBookingException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }

        reportService.generateReport(history);
    }
