import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    public MainMenu() {
        setTitle("MSB Hotel - Main Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel mainMenuPanel = createMainMenuPanel();
        JPanel reservationPanel = createReservationPanel();
        JPanel createReservationPanel = new CreateReservation(cardLayout, cardPanel, this);
        JPanel guestProfilePanel = new GuestProfileScreen(cardLayout, cardPanel, this);

        cardPanel.add(mainMenuPanel, "MainMenu");
        cardPanel.add(reservationPanel, "ReservationMenu");
        cardPanel.add(createReservationPanel, "CreateReservation");
        cardPanel.add(guestProfilePanel, "GuestProfile");

        add(cardPanel);
        setVisible(true);
    }

    private JPanel createMainMenuPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 255, 204));

        JLabel titleLabel = new JLabel("MSB Hotel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        JButton checkInButton = new JButton("Check In");
        checkInButton.setBackground(new Color(204, 153, 255));
        checkInButton.setOpaque(true);
        checkInButton.setBorderPainted(false);
        buttonPanel.add(checkInButton);

        JButton checkOutButton = new JButton("Check Out");
        checkOutButton.setBackground(new Color(255, 153, 204));
        checkOutButton.setOpaque(true);
        checkOutButton.setBorderPainted(false);
        buttonPanel.add(checkOutButton);

        JButton reservationButton = new JButton("Reservation");
        reservationButton.setBackground(new Color(255, 204, 0));
        reservationButton.setOpaque(true);
        reservationButton.setBorderPainted(false);
        reservationButton.addActionListener(e -> {
            this.setSize(500, 400);
            cardLayout.show(cardPanel, "ReservationMenu");
        });
        buttonPanel.add(reservationButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setBackground(Color.LIGHT_GRAY);
        exitButton.setOpaque(true);
        exitButton.setBorderPainted(false);
        exitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        buttonPanel.add(exitButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createReservationPanel() {
        JPanel reservationPanel = new JPanel(new BorderLayout());
        reservationPanel.setBackground(new Color(255, 255, 204));

        JLabel titleLabel = new JLabel("Reservation Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        reservationPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        JButton createReservationButton = new JButton("Create Reservation");
        createReservationButton.setBackground(new Color(204, 255, 204));
        createReservationButton.setOpaque(true);
        createReservationButton.setBorderPainted(false);
        createReservationButton.addActionListener(e -> {
            this.setSize(800, 600);
            cardLayout.show(cardPanel, "CreateReservation");
        });
        buttonPanel.add(createReservationButton);

        JButton viewReservationButton = new JButton("View Reservation");
        viewReservationButton.setBackground(new Color(204, 255, 255));
        viewReservationButton.setOpaque(true);
        viewReservationButton.setBorderPainted(false);
        buttonPanel.add(viewReservationButton);

        JButton createGuestButton = new JButton("Create Guest Profile");
        createGuestButton.setBackground(new Color(255, 204, 153));
        createGuestButton.setOpaque(true);
        createGuestButton.setBorderPainted(false);
        createGuestButton.addActionListener(e -> {
            this.setSize(500, 400);
            cardLayout.show(cardPanel, "GuestProfile");
        });
        buttonPanel.add(createGuestButton);

        JButton returnButton = new JButton("Return");
        returnButton.setBackground(Color.LIGHT_GRAY);
        returnButton.setOpaque(true);
        returnButton.setBorderPainted(false);
        returnButton.addActionListener(e -> {
            this.setSize(500, 400);
            cardLayout.show(cardPanel, "MainMenu");
        });
        buttonPanel.add(returnButton);

        reservationPanel.add(buttonPanel, BorderLayout.CENTER);
        return reservationPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}
