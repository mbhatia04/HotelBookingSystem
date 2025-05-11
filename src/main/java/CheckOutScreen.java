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
        setBackground(new Color(255, 204, 236));

        JLabel title = new JLabel("Check-Out", SwingConstants.CENTER);
        title.setFont(FontUtil.loadLobsterFont(50f));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.decode("#FA74C4")),
                "Stay Summary", 0, 0, new Font("Serif", Font.BOLD, 16)));
        Font labelFont = new Font("Serif", Font.BOLD, 16);
        Font valueFont = new Font("Serif", Font.PLAIN, 16);

        stayDropdown = new JComboBox<>();
        stayDropdown.setFont(valueFont);
        loadStayDropdown();
        stayDropdown.addActionListener(e -> loadStayDetails());

        JLabel stayLabel = new JLabel("Select Stay:");
        stayLabel.setFont(labelFont);
        JLabel guestLabel = new JLabel("Guest:");
        guestLabel.setFont(labelFont);
        JLabel roomTypeLabel = new JLabel("Room:");
        roomTypeLabel.setFont(labelFont);
        JLabel durationText = new JLabel("Stay Duration:");
        durationText.setFont(labelFont);
        JLabel totalText = new JLabel("Total Charges:");
        totalText.setFont(labelFont);

        guestNameLabel.setFont(valueFont);
        roomLabel.setFont(valueFont);
        durationLabel.setFont(valueFont);
        totalLabel.setFont(valueFont);

        topPanel.add(stayLabel);      topPanel.add(stayDropdown);
        topPanel.add(guestLabel);     topPanel.add(guestNameLabel);
        topPanel.add(roomTypeLabel);  topPanel.add(roomLabel);
        topPanel.add(durationText);   topPanel.add(durationLabel);
        topPanel.add(totalText);      topPanel.add(totalLabel);

        JPanel paddedTop = new JPanel(new BorderLayout());
        paddedTop.setOpaque(false);
        paddedTop.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));
        paddedTop.add(topPanel, BorderLayout.CENTER);
        add(paddedTop, BorderLayout.NORTH);

        String[] columns = {"Date", "Type", "Amount"};
        tableModel = new DefaultTableModel(columns, 0);
        chargeTable = new JTable(tableModel);
        chargeTable.setFont(valueFont);
        chargeTable.setRowHeight(24);
        chargeTable.getTableHeader().setFont(new Font("Serif", Font.BOLD, 16));
        add(new JScrollPane(chargeTable), BorderLayout.CENTER);

        JButton printButton = new JButton("Check Out & Print Receipt");
        JButton returnButton = new JButton("Return");
        printButton.setFont(labelFont);
        returnButton.setFont(labelFont);
        printButton.setPreferredSize(new Dimension(240, 45));
        returnButton.setPreferredSize(new Dimension(160, 45));
        printButton.setBackground(new Color(250, 116, 196));
        returnButton.setBackground(new Color(250, 116, 196));
        printButton.setOpaque(true);
        returnButton.setOpaque(true);
        printButton.setBorderPainted(false);
        returnButton.setBorderPainted(false);

        printButton.addActionListener(e -> {
            CheckOutStayItem selected = (CheckOutStayItem) stayDropdown.getSelectedItem();
            if (selected == null) return;

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

            new File("./HotelBookingSystem/receipts").mkdirs();
            ReceiptGenerator.generateReceipt(selected.stayId, "./HotelBookingSystem/receipts");
            JOptionPane.showMessageDialog(this, "Receipt saved and guest checked out!");
            loadStayDropdown();
        });

        returnButton.addActionListener(e -> {
            mainFrame.setSize(800, 640);
            layout.show(cardPanel, "MainMenu");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 204, 236));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.add(printButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(returnButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    void loadStayDropdown() {
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

        tableModel.setRowCount(0);
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
