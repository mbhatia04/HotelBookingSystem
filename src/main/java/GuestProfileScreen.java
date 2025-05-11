import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

class GuestProfileScreen extends JPanel {

    public GuestProfileScreen(CardLayout cardLayout, JPanel cardPanel, JFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 204, 236));

        JLabel titleLabel = new JLabel("Create Guest Profile", SwingConstants.CENTER);
        titleLabel.setFont(FontUtil.loadLobsterFont(50f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titleLabel, BorderLayout.NORTH);

        JTextField firstNameField = new JTextField(15);
        JTextField lastNameField = new JTextField(15);
        JTextField emailField = new JTextField(30);
        JTextField phoneField = new JTextField(20);

        Font labelFont = new Font("Serif", Font.PLAIN, 16);

        JPanel guestPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        guestPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.decode("#FA74C4")),
                "Guest Information", 0, 0, new Font("Serif", Font.BOLD, 16)));
        guestPanel.setOpaque(false);
        guestPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        guestPanel.add(styledLabel("First Name:", labelFont));
        guestPanel.add(firstNameField);
        guestPanel.add(styledLabel("Last Name:", labelFont));
        guestPanel.add(lastNameField);
        guestPanel.add(styledLabel("Email:", labelFont));
        guestPanel.add(emailField);
        guestPanel.add(styledLabel("Phone:", labelFont));
        guestPanel.add(phoneField);

        JButton saveButton = new JButton("Save Guest");
        saveButton.setPreferredSize(new Dimension(160, 45));
        JButton returnButton = new JButton("Return");
        returnButton.setPreferredSize(new Dimension(160, 45));

        saveButton.setFont(new Font("Serif", Font.BOLD, 16));
        saveButton.setBackground(new Color(250, 116, 196));
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);

        returnButton.setFont(new Font("Serif", Font.BOLD, 16));
        returnButton.setBackground(new Color(250, 116, 196));
        returnButton.setOpaque(true);
        returnButton.setBorderPainted(false);

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
            mainFrame.setSize(800, 530);
            cardLayout.show(cardPanel, "ReservationMenu");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 204, 236));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(returnButton);

        add(guestPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel styledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }
}
