import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.Vector;

public class CheckOutScreen extends JPanel {
    private JComboBox<CheckOutStayItem> stayDropdown;
    private JLabel guestNameLabel = new JLabel(" ");
    private JLabel roomLabel = new JLabel(" ");
    private JLabel durationLabel = new JLabel(" ");
    private JLabel totalLabel = new JLabel(" ");
    private JTable chargeTable;
    private DefaultTableModel tableModel;

    public CheckOutScreen(CardLayout layout, JPanel cardPanel, JFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 204));

        JLabel title = new JLabel("Check-Out", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new GridLayout(5, 2, 10, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        stayDropdown = new JComboBox<>();
        loadStayDropdown();

        stayDropdown.addActionListener(e -> loadStayDetails());

        topPanel.add(new JLabel("Select Stay:"));
        topPanel.add(stayDropdown);
        topPanel.add(new JLabel("Guest:"));
        topPanel.add(guestNameLabel);
        topPanel.add(new JLabel("Room:"));
        topPanel.add(roomLabel);
        topPanel.add(new JLabel("Stay Duration:"));
        topPanel.add(durationLabel);
        topPanel.add(new JLabel("Total Charges:"));
        topPanel.add(totalLabel);

        add(topPanel, BorderLayout.NORTH);

        // Table for charges
        String[] columns = {"Date", "Type", "Amount"};
        tableModel = new DefaultTableModel(columns, 0);
        chargeTable = new JTable(tableModel);
        add(new JScrollPane(chargeTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton printButton = new JButton("Check Out & Print Receipt");
        JButton returnButton = new JButton("Return");

        printButton.addActionListener(e -> {
            CheckOutStayItem selected = (CheckOutStayItem) stayDropdown.getSelectedItem();
            if (selected == null) return;

            // Update stay table
            try (Connection conn = DBUtil.getConnection()) {
                String updateSql = "UPDATE hbs.stay SET checked_out_yn = 'Y', actual_check_out_date = CURRENT_DATE WHERE stay_id = ?";
                PreparedStatement stmt = conn.prepareStatement(updateSql);
                stmt.setInt(1, selected.stayId);
                stmt.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to update checkout status.");
                return;
            }

            new File("./receipts").mkdirs();
            // ReceiptGenerator.generateReceipt(selected.stayId, "./receipts");
            JOptionPane.showMessageDialog(this, "Receipt saved and guest checked out!");
            loadStayDropdown();  // Refresh dropdown
        });

        returnButton.addActionListener(e -> {
            mainFrame.setSize(500, 400);
            layout.show(cardPanel, "MainMenu");
        });

        buttonPanel.add(printButton);
        buttonPanel.add(returnButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadStayDropdown() {
        stayDropdown.removeAllItems();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = """
                SELECT s.stay_id, g.first_name, g.last_name, s.room_number, s.check_in_date, s.check_out_date
                FROM hbs.stay s
                JOIN hbs.guest g ON s.guest_id = g.guest_id
                WHERE s.checked_out_yn IS DISTINCT FROM 'Y'
                AND s.room_number is not null
                ORDER BY s.stay_id DESC
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CheckOutStayItem stay = new CheckOutStayItem(
                        rs.getInt("stay_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("room_number"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate()
                );
                stayDropdown.addItem(stay);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading stays.");
        }
    }

    private void loadStayDetails() {
        CheckOutStayItem selected = (CheckOutStayItem) stayDropdown.getSelectedItem();
        if (selected == null) return;

        guestNameLabel.setText(selected.firstName + " " + selected.lastName);
        roomLabel.setText(selected.roomNumber);

        long days = ChronoUnit.DAYS.between(selected.checkIn, selected.checkOut);
        durationLabel.setText(days + " nights");

        tableModel.setRowCount(0); // Clear table
        double total = 0.0;

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT charge_date, charge_type, amount FROM hbs.charge WHERE stay_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selected.stayId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getDate("charge_date").toString());
                row.add(rs.getString("charge_type"));
                double amt = rs.getDouble("amount");
                row.add(String.format("$%.2f", amt));
                total += amt;
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        totalLabel.setText(String.format("$%.2f", total));
    }
}

class CheckOutStayItem {
    int stayId;
    String firstName;
    String lastName;
    String roomNumber;
    LocalDate checkIn;
    LocalDate checkOut;

    public CheckOutStayItem(int stayId, String firstName, String lastName, String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        this.stayId = stayId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    @Override
    public String toString() {
        return String.format("%s %s — Room %s", firstName, lastName, roomNumber);
    }
}
