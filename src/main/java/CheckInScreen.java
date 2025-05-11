import javax.swing.*;
import java.awt.*;
import java.sql.*;

class CheckInScreen extends JPanel {

    private JComboBox<CheckInStayItem> stayDropdown;
    private JLabel stayIdLabel;
    private JLabel roomTypeLabel;
    private JButton assignRoomButton;
    private JTextField roomNumberField;


    public CheckInScreen(CardLayout cardLayout, JPanel cardPanel, JFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 204, 236));

        JLabel titleLabel = new JLabel("Check-In Guest", SwingConstants.CENTER);
        titleLabel.setFont(FontUtil.loadLobsterFont(50f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titleLabel, BorderLayout.NORTH);

        stayDropdown = new JComboBox<>();
        stayIdLabel = new JLabel("Stay ID: ");
        roomTypeLabel = new JLabel("Room Type: ");
        roomNumberField = new JTextField();

        Font labelFont = new Font("Serif", Font.PLAIN, 16);
        stayIdLabel.setFont(labelFont);
        roomTypeLabel.setFont(labelFont);

        assignRoomButton = new JButton("Assign Room");
        assignRoomButton.setFont(new Font("Serif", Font.BOLD, 16));
        assignRoomButton.setBackground(new Color(250, 116, 196));
        assignRoomButton.setOpaque(true);
        assignRoomButton.setBorderPainted(false);
        assignRoomButton.setEnabled(false);
        assignRoomButton.addActionListener(e -> {
            CheckInStayItem selected = (CheckInStayItem) stayDropdown.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a reservation.");
                return;
            }
            String roomNumber = roomNumberField.getText().trim();
            if (roomNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a room number.");
                return;
            }

            try (Connection conn = DBUtil.getConnection()) {
                // Check if room is already occupied
                String checkSql = "SELECT COUNT(*) FROM hbs.stay\n" +
                        "WHERE room_number = ?\n" +
                        "AND checked_out_yn IS DISTINCT FROM 'Y'";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, roomNumber);
                ResultSet checkRs = checkStmt.executeQuery();
                if (checkRs.next() && checkRs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "This room is already occupied!");
                    return;
                }

                // Assign room
                String updateSql = "UPDATE hbs.stay SET room_number = ? WHERE stay_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, roomNumber);
                updateStmt.setInt(2, selected.stayId);
                int rows = updateStmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Room assigned successfully!");
                    assignRoomButton.setEnabled(false);
                    roomNumberField.setText("");
                    stayIdLabel.setText("Stay ID: ");
                    roomTypeLabel.setText("Room Type: ");
                    stayDropdown.removeItem(selected);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to assign room.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating stay: " + ex.getMessage());
            }
        });

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT s.stay_id, g.first_name, g.last_name, rt.room_type " +
                    "FROM hbs.stay s " +
                    "JOIN hbs.guest g ON s.guest_id = g.guest_id " +
                    "JOIN hbs.room_type rt ON s.room_type_id = rt.room_id " +
                    "WHERE s.room_number IS NULL " +
                    "ORDER BY g.last_name, g.first_name";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stayDropdown.addItem(new CheckInStayItem(
                        rs.getInt("stay_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("room_type")
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading stays: " + ex.getMessage());
        }

        stayDropdown.addActionListener(e -> {
            CheckInStayItem selected = (CheckInStayItem) stayDropdown.getSelectedItem();
            if (selected != null) {
                stayIdLabel.setText("" + selected.stayId);
                roomTypeLabel.setText("" + selected.roomType);
                assignRoomButton.setEnabled(true);
            }
        });

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.decode("#FA74C4")),
                "Reservation Info", 0, 0, new Font("Serif", Font.BOLD, 16)));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel selectLabel = new JLabel("Select Reservation:");
        selectLabel.setFont(new Font("Serif", Font.BOLD, 16));
        formPanel.add(selectLabel, gbc);
        gbc.gridx = 1;
        stayDropdown.setFont(new Font("Serif", Font.PLAIN, 16));
        formPanel.add(stayDropdown, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel stayIdText = new JLabel("Stay ID:");
        stayIdText.setFont(new Font("Serif", Font.BOLD, 16));
        formPanel.add(stayIdText, gbc);
        gbc.gridx = 1;
        stayIdLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        stayIdLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(stayIdLabel, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel roomTypeText = new JLabel("Room Type:");
        roomTypeText.setFont(new Font("Serif", Font.BOLD, 16));
        formPanel.add(roomTypeText, gbc);
        gbc.gridx = 1;
        roomTypeLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        roomTypeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(roomTypeLabel, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel assignLabel = new JLabel("Assign Room Number:");
        assignLabel.setFont(new Font("Serif", Font.BOLD, 16));
        formPanel.add(assignLabel, gbc);
        gbc.gridx = 1;
        roomNumberField.setFont(new Font("Serif", Font.PLAIN, 16));
        formPanel.add(roomNumberField, gbc);

        add(formPanel, BorderLayout.CENTER);

        JButton returnButton = new JButton("Return");
        returnButton.setFont(new Font("Serif", Font.BOLD, 16));
        returnButton.setBackground(new Color(250, 116, 196));
        returnButton.setOpaque(true);
        returnButton.setBorderPainted(false);
        returnButton.addActionListener(e -> {
            mainFrame.setSize(800, 640);
            cardLayout.show(cardPanel, "MainMenu");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 204, 236));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        assignRoomButton.setPreferredSize(new Dimension(160, 45));
        returnButton.setPreferredSize(new Dimension(160, 45));
        buttonPanel.add(assignRoomButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(returnButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshStayDropdown() {
        stayDropdown.removeAllItems();
        assignRoomButton.setEnabled(false);
        stayIdLabel.setText("Stay ID: ");
        roomTypeLabel.setText("Room Type: ");

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT s.stay_id, g.first_name, g.last_name, rt.room_type " +
                    "FROM hbs.stay s " +
                    "JOIN hbs.guest g ON s.guest_id = g.guest_id " +
                    "JOIN hbs.room_type rt ON s.room_type_id = rt.room_id " +
                    "WHERE s.room_number IS NULL " +
                    "ORDER BY g.last_name, g.first_name";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            boolean hasItems = false;
            while (rs.next()) {
                stayDropdown.addItem(new CheckInStayItem(
                        rs.getInt("stay_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("room_type")
                ));
                hasItems = true;
            }
            if (!hasItems) {
                JOptionPane.showMessageDialog(this, "No pending reservations found.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading stays: " + ex.getMessage());
        }
    }
}

class CheckInStayItem {
    int stayId;
    String guestName;
    String roomType;

    public CheckInStayItem(int stayId, String guestName, String roomType) {
        this.stayId = stayId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return guestName + " (Stay ID: " + stayId + ")";
    }
}
