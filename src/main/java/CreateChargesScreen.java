import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;

public class CreateChargesScreen extends JPanel {

    JComboBox<GuestStayItem> guestDropdown;
    private JTextField roomField;
    JPanel chargesPanel;
    ArrayList<ChargeRowPanel> chargeRows = new ArrayList<>();

    public CreateChargesScreen(CardLayout cardLayout, JPanel cardPanel, JFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 204, 236));

        JLabel titleLabel = new JLabel("Create Charges", SwingConstants.CENTER);
        titleLabel.setFont(FontUtil.loadLobsterFont(50f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titleLabel, BorderLayout.NORTH);

        Font labelFont = new Font("Serif", Font.BOLD, 16);
        Font valueFont = new Font("Serif", Font.PLAIN, 16);

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.decode("#FA74C4")),
                "Select Guest", 0, 0, labelFont));
        topPanel.setOpaque(false);

        guestDropdown = new JComboBox<>();
        guestDropdown.setFont(valueFont);
        roomField = new JTextField();
        roomField.setEditable(false);
        roomField.setFont(valueFont);

        JLabel guestLabel = new JLabel("Guest:");
        guestLabel.setFont(labelFont);
        JLabel roomLabel = new JLabel("Room Number:");
        roomLabel.setFont(labelFont);

        topPanel.add(guestLabel);
        topPanel.add(guestDropdown);
        topPanel.add(roomLabel);
        topPanel.add(roomField);

        guestDropdown.addActionListener(e -> {
            GuestStayItem selected = (GuestStayItem) guestDropdown.getSelectedItem();
            if (selected != null) {
                roomField.setText(selected.roomNumber);
            }
        });

        JPanel paddedTop = new JPanel(new BorderLayout());
        paddedTop.setOpaque(false);
        paddedTop.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));
        paddedTop.add(topPanel, BorderLayout.CENTER);
        add(paddedTop, BorderLayout.NORTH);

        chargesPanel = new JPanel();
        chargesPanel.setLayout(new BoxLayout(chargesPanel, BoxLayout.Y_AXIS));
        chargesPanel.setOpaque(false);

        JPanel chargesWrapper = new JPanel(new BorderLayout());
        chargesWrapper.setOpaque(false);
        chargesWrapper.add(chargesPanel, BorderLayout.NORTH); // <-- key to top-aligning

        JScrollPane scrollPane = new JScrollPane(chargesWrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        addChargeRow();

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(255, 204, 236));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton addRowButton = new JButton("Add Charge");
        JButton saveButton = new JButton("Save Charges");
        JButton returnButton = new JButton("Return");

        addRowButton.setFont(labelFont);
        saveButton.setFont(labelFont);
        returnButton.setFont(labelFont);

        addRowButton.setPreferredSize(new Dimension(160, 45));
        saveButton.setPreferredSize(new Dimension(160, 45));
        returnButton.setPreferredSize(new Dimension(160, 45));

        Color btnColor = new Color(250, 116, 196);
        for (JButton b : new JButton[]{addRowButton, saveButton, returnButton}) {
            b.setBackground(btnColor);
            b.setOpaque(true);
            b.setBorderPainted(false);
        }

        addRowButton.addActionListener(e -> addChargeRow());
        saveButton.addActionListener(e -> saveCharges());
        returnButton.addActionListener(e -> {
            mainFrame.setSize(800, 640);
            cardLayout.show(cardPanel, "MainMenu");
        });

        bottomPanel.add(addRowButton);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(saveButton);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(returnButton);

        add(bottomPanel, BorderLayout.SOUTH);

        reloadGuestList();
    }

    public void reloadGuestList() {
        guestDropdown.removeAllItems();
        roomField.setText("");
        try (Connection conn = DBUtil.getConnection()) {
            String sql = """
                SELECT s.stay_id, g.first_name, g.last_name, s.room_number
                FROM hbs.stay s
                JOIN hbs.guest g ON s.guest_id = g.guest_id
                WHERE room_number is not null
                AND s.checked_out_yn = 'N'
                ORDER BY g.last_name, g.first_name
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                guestDropdown.addItem(new GuestStayItem(
                        rs.getInt("stay_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("room_number")
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading guest list: " + ex.getMessage());
        }
    }

    private void addChargeRow() {
        ChargeRowPanel row = new ChargeRowPanel(this);
        chargeRows.add(row);
        chargesPanel.add(row);

        // Ensure the new row appears at the bottom and is visible
        chargesPanel.revalidate();
        chargesPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            chargesPanel.scrollRectToVisible(row.getBounds());
        });
    }

    protected void removeChargeRow(ChargeRowPanel row) {
        chargesPanel.remove(row);
        chargeRows.remove(row);
        revalidate();
        repaint();
    }

    private void saveCharges() {
        GuestStayItem selectedGuest = (GuestStayItem) guestDropdown.getSelectedItem();
        if (selectedGuest == null) {
            JOptionPane.showMessageDialog(this, "Please select a guest.");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            String validateSql = "SELECT check_out_date FROM hbs.stay WHERE stay_id = ?";
            PreparedStatement validateStmt = conn.prepareStatement(validateSql);
            validateStmt.setInt(1, selectedGuest.stayId);
            ResultSet rs = validateStmt.executeQuery();
            if (rs.next()) {
                Date checkout = rs.getDate("check_out_date");
                if (checkout.toLocalDate().isBefore(java.time.LocalDate.now())) {
                    JOptionPane.showMessageDialog(this, "This guest has already checked out. Cannot add charges.");
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Could not find the selected stay.");
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error validating stay: " + ex.getMessage());
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            String sql = "INSERT INTO hbs.charge (stay_id, charge_date, charge_type, amount) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (ChargeRowPanel row : chargeRows) {
                String date = row.dateField.getText().trim();
                String type = (String) row.typeDropdown.getSelectedItem();
                String amt = row.amountField.getText().trim();

                stmt.setInt(1, selectedGuest.stayId);
                stmt.setDate(2, java.sql.Date.valueOf(date));
                stmt.setString(3, type);
                stmt.setBigDecimal(4, new java.math.BigDecimal(amt));
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
            JOptionPane.showMessageDialog(this, "Charges saved successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving charges: " + ex.getMessage());
        }
    }
}

// Guest dropdown entry
class GuestStayItem {
    int stayId;
    String fullName;
    String roomNumber;

    public GuestStayItem(int stayId, String firstName, String lastName, String roomNumber) {
        this.stayId = stayId;
        this.fullName = firstName + " " + lastName;
        this.roomNumber = roomNumber;
    }

    @Override
    public String toString() {
        return fullName;
    }
}

// A single charge row panel
class ChargeRowPanel extends JPanel {
    JTextField dateField;
    JComboBox<String> typeDropdown;
    JTextField amountField;

    public ChargeRowPanel(CreateChargesScreen parent) {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        dateField = new JTextField("YYYY-MM-DD", 10);
        typeDropdown = new JComboBox<>(new String[]{
                "Room Rate",
                "Room Tax 10%",
                "Room Service",
                "Holiday Surcharge",
                "Damages"
        });
        amountField = new JTextField(8);

        // Date field placeholder behavior
        dateField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (dateField.getText().equals("YYYY-MM-DD")) {
                    dateField.setText("");
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (dateField.getText().isEmpty()) {
                    dateField.setText("YYYY-MM-DD");
                }
            }
        });

        // Respond to charge type selection
        typeDropdown.addActionListener(e -> {
            String selectedType = (String) typeDropdown.getSelectedItem();
            if ("Room Rate".equals(selectedType)) {
                GuestStayItem selectedGuest = (GuestStayItem) parent.guestDropdown.getSelectedItem();
                if (selectedGuest != null) {
                    try (Connection conn = DBUtil.getConnection()) {
                        String sql = """
                            SELECT r.room_rate
                            FROM hbs.stay s
                            JOIN hbs.room_type r ON s.room_type_id = r.room_id
                            WHERE s.stay_id = ?
                        """;
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, selectedGuest.stayId);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            double rate = rs.getDouble("room_rate");
                            amountField.setText(String.format("%.2f", rate));

                            // Automatically add a tax row
                            ChargeRowPanel taxRow = new ChargeRowPanel(parent);
                            taxRow.typeDropdown.setSelectedItem("Room Tax 10%");
                            taxRow.dateField.setText(dateField.getText());
                            taxRow.amountField.setText(String.format("%.2f", rate * 0.10));
                            parent.chargesPanel.add(taxRow);
                            parent.chargeRows.add(taxRow);
                            parent.revalidate();
                            parent.repaint();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error loading room rate: " + ex.getMessage());
                    }
                }
            }
        });

        JButton removeButton = new JButton("Remove Charge");
        removeButton.addActionListener((ActionEvent e) -> parent.removeChargeRow(this));

        add(new JLabel("Date:"));
        add(dateField);
        add(new JLabel("Type:"));
        add(typeDropdown);
        add(new JLabel("Amount:"));
        add(amountField);
        add(removeButton);
    }
}
