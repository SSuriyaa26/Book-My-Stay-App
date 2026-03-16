public abstract class Room{
    protected  int  numberOfBeds;
    protected  int squareFeet;
    protected  double pricePerNight;
    public  int availability;

    public  Room(int numberOfBeds, int squareFeet, double pricePerNight){
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight= pricePerNight;

    }

    public void displayRoomDetails(){
        System.out.println("Beds: "+numberOfBeds+"\nSize: "+squareFeet+"\nPrice per night: "+pricePerNight+"\nAvailability: "+availability+"\n");

    }

}

public class SingleRoom extends Room{

    public SingleRoom(){super(1,250,1500.0);
    this.availability=5;}
}
public class DoubleRoom extends Room{

    public DoubleRoom(){super(2,400,2500.0);
        this.availability=3;}
}
public class SuiteRoom extends Room {

    public SuiteRoom() {
        super(3, 750, 5000.0);
        this.availability=2;
    }
}
void main() {
        System.out.println("Hotel Room Initialization\n");
        SingleRoom r1 = new SingleRoom();
        DoubleRoom r2 = new DoubleRoom();
        SuiteRoom r3 = new SuiteRoom();

        r1.displayRoomDetails();
        r2.displayRoomDetails();
        r3.displayRoomDetails();

    }

