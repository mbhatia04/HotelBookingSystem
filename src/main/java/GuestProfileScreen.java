import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

class GuestProfileScreen extends JPanel {

    public GuestProfileScreen(CardLayout cardLayout, JPanel cardPanel, JFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 204));

        JLabel titleLabel = new JLabel("Create Guest Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titleLabel, BorderLayout.NORTH);

        JTextField firstNameField = new JTextField(15);
        JTextField lastNameField = new JTextField(15);
        JTextField emailField = new JTextField(30);
        JTextField phoneField = new JTextField(20);

        JPanel guestPanel = new JPanel(new GridLayout(4, 2));
        guestPanel.setBorder(BorderFactory.createTitledBorder("Guest Information"));
        guestPanel.add(new JLabel("First Name:"));
        guestPanel.add(firstNameField);
        guestPanel.add(new JLabel("Last Name:"));
        guestPanel.add(lastNameField);
        guestPanel.add(new JLabel("Email:"));
        guestPanel.add(emailField);
        guestPanel.add(new JLabel("Phone:"));
        guestPanel.add(phoneField);

        JButton saveButton = new JButton("Save Guest");
        JButton returnButton = new JButton("Return");

        saveButton.addActionListener(e -> {
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "INSERT INTO hbs.guest (first_name, last_name, email, phone) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, firstNameField.getText());
                stmt.setString(2, lastNameField.getText());
                stmt.setString(3, emailField.getText());
                stmt.setString(4, phoneField.getText());
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Guest profile saved successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving guest: " + ex.getMessage());
            }
        });

        returnButton.addActionListener(e -> {
            mainFrame.setSize(500, 400);
            cardLayout.show(cardPanel, "ReservationMenu");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(returnButton);

        add(guestPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
