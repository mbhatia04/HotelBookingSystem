public class Room {
    private int roomNumber;
    private String roomType;
    private boolean isAvailable;
    private Charge charge;

    // Getters, setters, and room availability logic can go here
    public void addCharge(Charge charge) {
        this.charge = charge;
    }
}
