import java.util.Date;

public class Booking {
    private int bookingId;
    private Date checkInDate;
    private int numGuests;
    private Room room;

    public void checkIn() {
        // cs3913.hotelbooking.Booking check-in logic
    }

    public void checkOut() {
        // cs3913.hotelbooking.Booking check-out logic
    }

    public void assignRoom(Room room) {
        this.room = room;
    }
}