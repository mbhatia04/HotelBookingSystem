import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

class ViewReservationsScreen extends JPanel {

    private JTable reservationTable;
    private DefaultTableModel tableModel;

    public ViewReservationsScreen(CardLayout cardLayout, JPanel cardPanel, JFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 204, 236));

        JLabel titleLabel = new JLabel("All Current Reservations", SwingConstants.CENTER);
        titleLabel.setFont(FontUtil.loadLobsterFont(50f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Guest Name", "Email", "Phone", "Room Status", "Check-In Date", "Check-Out Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        reservationTable = new JTable(tableModel);
        reservationTable.getTableHeader().setFont(new Font("Serif", Font.BOLD, 16));
        reservationTable.setFont(new Font("Serif", Font.PLAIN, 14));
        reservationTable.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton returnButton = new JButton("Return");
        returnButton.setFont(new Font("Serif", Font.BOLD, 16));
        returnButton.setPreferredSize(new Dimension(160, 45));
        returnButton.setBackground(new Color(250, 116, 196));
        returnButton.setOpaque(true);
        returnButton.setBorderPainted(false);
        returnButton.addActionListener(e -> {
            mainFrame.setSize(800, 530);
            cardLayout.show(cardPanel, "ReservationMenu");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 204, 236));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        buttonPanel.add(returnButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT g.first_name, g.last_name, g.email, g.phone, " +
                    "COALESCE(s.room_number, 'Not checked in') AS room_status, " +
                    "s.check_in_date, s.check_out_date " +
                    "FROM hbs.stay s " +
                    "JOIN hbs.guest g ON s.guest_id = g.guest_id " +
                    "WHERE s.checked_out_yn = 'N'" +
                    "ORDER BY s.check_in_date";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String guestName = rs.getString("first_name") + " " + rs.getString("last_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String roomStatus = rs.getString("room_status");
                Date checkIn = rs.getDate("check_in_date");
                Date checkOut = rs.getDate("check_out_date");

                tableModel.addRow(new Object[]{guestName, email, phone, roomStatus, checkIn, checkOut});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading reservations: " + ex.getMessage());
        }
    }
}
