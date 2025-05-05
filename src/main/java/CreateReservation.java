import javax.swing.*;
import java.awt.*;

class CreateReservation extends JPanel {

    public CreateReservation(CardLayout cardLayout, JPanel cardPanel, JFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 204));

        JLabel titleLabel = new JLabel("Create Reservation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 1));
        formPanel.setOpaque(false);

        JPanel guestPanel = new JPanel(new GridLayout(6, 2));
        guestPanel.setBorder(BorderFactory.createTitledBorder("Guest Information"));
        guestPanel.add(new JLabel("First Name:"));
        guestPanel.add(new JTextField(15));
        guestPanel.add(new JLabel("Middle Initial:"));
        guestPanel.add(new JTextField(2));
        guestPanel.add(new JLabel("Last Name:"));
        guestPanel.add(new JTextField(15));
        guestPanel.add(new JLabel("Phone:"));
        guestPanel.add(new JTextField(10));
        guestPanel.add(new JLabel("Email:"));
        guestPanel.add(new JTextField(20));
        guestPanel.add(new JLabel("Address:"));
        guestPanel.add(new JTextField(30));

        JPanel stayPanel = new JPanel(new GridLayout(5, 2));
        stayPanel.setBorder(BorderFactory.createTitledBorder("Stay Information"));
        stayPanel.add(new JLabel("Check-In Date:"));
        stayPanel.add(new JTextField("YYYY-MM-DD"));
        stayPanel.add(new JLabel("Check-Out Date:"));
        stayPanel.add(new JTextField("YYYY-MM-DD"));
        stayPanel.add(new JLabel("Number of Guests:"));
        stayPanel.add(new JTextField("2"));
        stayPanel.add(new JLabel("Children (under 12):"));
        stayPanel.add(new JTextField("0"));
        stayPanel.add(new JLabel("Room Type:"));
        stayPanel.add(new JComboBox<>(new String[]{"King", "Double"}));

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save Reservation");
        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(e -> {
            mainFrame.setSize(500, 400);
            cardLayout.show(cardPanel, "ReservationMenu");
        });
        buttonPanel.add(saveButton);
        buttonPanel.add(returnButton);

        formPanel.add(guestPanel);
        formPanel.add(stayPanel);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}