import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;

class CreateReservation extends JPanel {

    private JComboBox<GuestItem> guestDropdown;

    public CreateReservation(CardLayout cardLayout, JPanel cardPanel, JFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 204, 236));

        JLabel titleLabel = new JLabel("Create Reservation", SwingConstants.CENTER);
        titleLabel.setFont(FontUtil.loadLobsterFont(50f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        guestDropdown = new JComboBox<>();
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

        JComboBox<RoomTypeItem> roomTypeDropdown = new JComboBox<>();
        JLabel roomRateLabel = new JLabel("Rate: $100.00");
        roomRateLabel.setFont(new Font("Serif", Font.PLAIN, 16));

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT room_id, room_type, room_desc, room_rate FROM hbs.room_type ORDER BY room_type";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roomTypeDropdown.addItem(new RoomTypeItem(
                        rs.getInt("room_id"),
                        rs.getString("room_type"),
                        rs.getString("room_desc"),
                        rs.getDouble("room_rate")
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading room types: " + ex.getMessage());
        }

        roomTypeDropdown.addActionListener(e -> {
            RoomTypeItem selected = (RoomTypeItem) roomTypeDropdown.getSelectedItem();
            if (selected != null) {
                roomRateLabel.setText(String.format("Rate: $%.2f", selected.rate));
            }
        });

        Font labelFont = new Font("Serif", Font.PLAIN, 16);

        JPanel guestPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        guestPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.decode("#FA74C4")),
                "Guest Information", 0, 0, new Font("Serif", Font.BOLD, 16)
        ));
        guestPanel.setOpaque(false);
        guestPanel.add(styledLabel("Select Guest:", labelFont));
        guestPanel.add(guestDropdown);
        guestPanel.add(styledLabel("First Name:", labelFont));
        guestPanel.add(firstNameField);
        guestPanel.add(styledLabel("Last Name:", labelFont));
        guestPanel.add(lastNameField);
        guestPanel.add(styledLabel("Email:", labelFont));
        guestPanel.add(emailField);
        guestPanel.add(styledLabel("Phone:", labelFont));
        guestPanel.add(phoneField);

        JTextField checkInField = new JTextField("YYYY-MM-DD");
        JTextField checkOutField = new JTextField("YYYY-MM-DD");
        JTextField numGuestsField = new JTextField("2");
        JTextField specialRequestsField = new JTextField("None");

        JPanel stayPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        stayPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.decode("#FA74C4")),
                "Stay Information", 0, 0, new Font("Serif", Font.BOLD, 16)
        ));
        stayPanel.setOpaque(false);
        stayPanel.add(styledLabel("Check-In Date:", labelFont));
        stayPanel.add(checkInField);
        stayPanel.add(styledLabel("Check-Out Date:", labelFont));
        stayPanel.add(checkOutField);
        stayPanel.add(styledLabel("Number of Guests:", labelFont));
        stayPanel.add(numGuestsField);
        stayPanel.add(styledLabel("Room Type:", labelFont));
        stayPanel.add(roomTypeDropdown);
        stayPanel.add(styledLabel("Room Rate:", labelFont));
        stayPanel.add(roomRateLabel);
        stayPanel.add(styledLabel("Special Requests:", labelFont));
        stayPanel.add(specialRequestsField);

        JButton saveButton = new JButton("Save Reservation");
        JButton returnButton = new JButton("Return");

        Consumer<JButton> styleButton = button -> {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setPreferredSize(new Dimension(180, 40));
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            button.setFont(new Font("Serif", Font.BOLD, 16));
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setBackground(new Color(250, 116, 196));
        };

        styleButton.accept(saveButton);
        styleButton.accept(returnButton);

        saveButton.addActionListener(e -> {
            GuestItem selectedGuest = (GuestItem) guestDropdown.getSelectedItem();
            RoomTypeItem selectedRoomType = (RoomTypeItem) roomTypeDropdown.getSelectedItem();

            if (selectedGuest == null) {
                JOptionPane.showMessageDialog(this, "Please select a guest.");
                return;
            }
            if (selectedRoomType == null) {
                JOptionPane.showMessageDialog(this, "Please select a room type.");
                return;
            }

            try {
                java.sql.Date checkInDate = java.sql.Date.valueOf(checkInField.getText().trim());
                java.sql.Date checkOutDate = java.sql.Date.valueOf(checkOutField.getText().trim());
                java.sql.Date today = java.sql.Date.valueOf(java.time.LocalDate.now());

                if (checkInDate.before(today)) {
                    JOptionPane.showMessageDialog(this, "Check-in date must be today or later.");
                    return;
                }
                if (!checkOutDate.after(checkInDate)) {
                    JOptionPane.showMessageDialog(this, "Check-out date must be after check-in date.");
                    return;
                }

                try (Connection conn = DBUtil.getConnection()) {
                    String sql = "INSERT INTO hbs.stay (guest_id, check_in_date, check_out_date, room_number, number_of_guests, special_requests, room_type_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, selectedGuest.id);
                    stmt.setDate(2, checkInDate);
                    stmt.setDate(3, checkOutDate);
                    stmt.setNull(4, java.sql.Types.VARCHAR);
                    stmt.setInt(5, Integer.parseInt(numGuestsField.getText()));
                    stmt.setString(6, specialRequestsField.getText());
                    stmt.setInt(7, selectedRoomType.id);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Reservation saved successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to save reservation.");
                    }
                }

            } catch (IllegalArgumentException dateEx) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving reservation: " + ex.getMessage());
            }
        });

        returnButton.addActionListener(e -> {
            mainFrame.setSize(800, 530);
            cardLayout.show(cardPanel, "ReservationMenu");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 204, 236));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(returnButton);

        formPanel.add(guestPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(stayPanel);
        formPanel.add(Box.createVerticalStrut(20));

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel styledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    public void refreshGuestDropdown() {
        guestDropdown.removeAllItems();
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
    }
}

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

class RoomTypeItem {
    int id;
    String type;
    String description;
    double rate;

    public RoomTypeItem(int id, String type, String description, double rate) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.rate = rate;
    }

    @Override
    public String toString() {
        return type;
    }
}
