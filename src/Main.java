import java.util.Queue;
import java.util.LinkedList;
class Reservation{
    private String guestName;
    private String roomType;

    Reservation(String guestName,String roomType){
        this.guestName = guestName;
        this.roomType = roomType;
    }
    public String getGuestName(){
        return guestName;
    }
    public String getRoomType(){
        return roomType;
    }
}
class BookingRequestQueue{
    private Queue<Reservation> requestQueue;

    BookingRequestQueue(){
        requestQueue = new LinkedList<>();
    }
    public void addRequest(Reservation reservation){
        requestQueue.offer(reservation);
    }
    public Reservation getNextRequest(){
        return requestQueue.poll();
    }
    public boolean hasPendingRequests(){
        return !requestQueue.isEmpty();
    }
}

void main(String[] args ){

    System.out.println("Booking Request Queue");
    BookingRequestQueue bookingRequestQueue = new BookingRequestQueue();
    Reservation r1 = new Reservation("Abhi", "Single");
    Reservation r2 = new Reservation("Subha", "Double");
    Reservation r3 = new Reservation("Vanmathi", "Suite");
    bookingRequestQueue.addRequest(r1);
    bookingRequestQueue.addRequest(r2);
    bookingRequestQueue.addRequest(r3);

    while (bookingRequestQueue.hasPendingRequests()){
        Reservation next = bookingRequestQueue.getNextRequest();
        System.out.println("Processing reservation for guest: " + next.getGuestName() +
                " | Room Type: " + next.getRoomType());

    }
}
