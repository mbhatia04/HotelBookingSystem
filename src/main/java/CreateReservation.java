import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

        // Guest dropdown and fields
        JComboBox<GuestItem> guestDropdown = new JComboBox<>();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
        emailField.setEditable(false);
        phoneField.setEditable(false);

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT guest_id, first_name, last_name, email, phone FROM hbs.guest ORDER BY last_name, first_name";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                guestDropdown.addItem(new GuestItem(
                        rs.getInt("guest_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone")
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading guests: " + ex.getMessage());
        }

        guestDropdown.addActionListener(e -> {
            GuestItem selected = (GuestItem) guestDropdown.getSelectedItem();
            if (selected != null) {
                firstNameField.setText(selected.firstName);
                lastNameField.setText(selected.lastName);
                emailField.setText(selected.email);
                phoneField.setText(selected.phone);
            }
        });

        JPanel guestPanel = new JPanel(new GridLayout(5, 2));
        guestPanel.setBorder(BorderFactory.createTitledBorder("Guest Information"));
        guestPanel.add(new JLabel("Select Guest:"));
        guestPanel.add(guestDropdown);
        guestPanel.add(new JLabel("First Name:"));
        guestPanel.add(firstNameField);
        guestPanel.add(new JLabel("Last Name:"));
        guestPanel.add(lastNameField);
        guestPanel.add(new JLabel("Email:"));
        guestPanel.add(emailField);
        guestPanel.add(new JLabel("Phone:"));
        guestPanel.add(phoneField);

        // Stay fields
        JTextField checkInField = new JTextField("YYYY-MM-DD");
        JTextField checkOutField = new JTextField("YYYY-MM-DD");
        JTextField numGuestsField = new JTextField("2");
        JTextField roomNumberField = new JTextField("101");
        JTextField specialRequestsField = new JTextField("None");

        JPanel stayPanel = new JPanel(new GridLayout(6, 2));
        stayPanel.setBorder(BorderFactory.createTitledBorder("Stay Information"));
        stayPanel.add(new JLabel("Check-In Date:"));
        stayPanel.add(checkInField);
        stayPanel.add(new JLabel("Check-Out Date:"));
        stayPanel.add(checkOutField);
        stayPanel.add(new JLabel("Number of Guests:"));
        stayPanel.add(numGuestsField);
        stayPanel.add(new JLabel("Room Number:"));
        stayPanel.add(roomNumberField);
        stayPanel.add(new JLabel("Special Requests:"));
        stayPanel.add(specialRequestsField);

        JButton saveButton = new JButton("Save Reservation");
        JButton returnButton = new JButton("Return");

        saveButton.addActionListener(e -> {
            GuestItem selectedGuest = (GuestItem) guestDropdown.getSelectedItem();
            if (selectedGuest == null) {
                JOptionPane.showMessageDialog(this, "Please select a guest.");
                return;
            }

            try (Connection conn = DBUtil.getConnection()) {
                String sql = "INSERT INTO hbs.stay (guest_id, check_in_date, check_out_date, room_number, number_of_guests, special_requests) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, selectedGuest.id);
                stmt.setDate(2, java.sql.Date.valueOf(checkInField.getText()));
                stmt.setDate(3, java.sql.Date.valueOf(checkOutField.getText()));
                stmt.setString(4, roomNumberField.getText());
                stmt.setInt(5, Integer.parseInt(numGuestsField.getText()));
                stmt.setString(6, specialRequestsField.getText());

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Reservation saved successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save reservation.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving reservation: " + ex.getMessage());
            }
        });

        returnButton.addActionListener(e -> {
            mainFrame.setSize(500, 400);
            cardLayout.show(cardPanel, "ReservationMenu");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(returnButton);

        formPanel.add(guestPanel);
        formPanel.add(stayPanel);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}

// Helper class for dropdown items
class GuestItem {
    int id;
    String firstName;
    String lastName;
    String email;
    String phone;

    public GuestItem(int id, String firstName, String lastName, String email, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
