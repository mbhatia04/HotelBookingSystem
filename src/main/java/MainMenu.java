import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class MainMenu extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private ViewReservationsScreen viewReservationsPanel;
    private CreateReservation createReservationPanel;
    private CheckInScreen checkInPanel;

    public MainMenu() {
        setTitle("MSB Hotel - Main Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel mainMenuPanel = createMainMenuPanel();
        JPanel reservationPanel = createReservationPanel();
        createReservationPanel = new CreateReservation(cardLayout, cardPanel, this);
        viewReservationsPanel = new ViewReservationsScreen(cardLayout, cardPanel, this);
        JPanel guestProfilePanel = new GuestProfileScreen(cardLayout, cardPanel, this);
        checkInPanel = new CheckInScreen(cardLayout, cardPanel, this);
        JPanel checkOutPanel = new CheckOutScreen(cardLayout, cardPanel, this);
        JPanel createChargesPanel = new CreateChargesScreen(cardLayout, cardPanel, this);

        cardPanel.add(mainMenuPanel, "MainMenu");
        cardPanel.add(reservationPanel, "ReservationMenu");
        cardPanel.add(createReservationPanel, "CreateReservation");
        cardPanel.add(viewReservationsPanel, "ViewReservations");
        cardPanel.add(guestProfilePanel, "GuestProfile");
        cardPanel.add(checkInPanel, "CheckInScreen");
        cardPanel.add(checkOutPanel, "CheckOutScreen");
        cardPanel.add(createChargesPanel, "CreateCharges");

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

        // Use BoxLayout for vertical button alignment
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));

        int verticalGap = 12;

        // Helper to configure each button
        Consumer<JButton> styleButton = button -> {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            button.setOpaque(true);
            button.setBorderPainted(false);
        };

        JButton checkInButton = new JButton("Check In");
        checkInButton.setBackground(new Color(204, 153, 255));
        checkInButton.addActionListener(e -> {
            this.setSize(500, 400);
            checkInPanel.refreshStayDropdown();
            cardLayout.show(cardPanel, "CheckInScreen");
        });
        styleButton.accept(checkInButton);
        buttonPanel.add(checkInButton);
        buttonPanel.add(Box.createVerticalStrut(verticalGap));

        JButton checkOutButton = new JButton("Check Out");
        checkOutButton.setBackground(new Color(255, 153, 204));
        checkOutButton.setOpaque(true);
        checkOutButton.setBorderPainted(false);
        checkOutButton.addActionListener(e -> {
            this.setSize(800, 600);
            cardLayout.show(cardPanel, "CheckOutScreen");
        });
        styleButton.accept(checkOutButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(Box.createVerticalStrut(verticalGap));

        JButton reservationButton = new JButton("Reservation");
        reservationButton.setBackground(new Color(255, 204, 0));
        reservationButton.addActionListener(e -> {
            this.setSize(500, 400);
            cardLayout.show(cardPanel, "ReservationMenu");
        });
        styleButton.accept(reservationButton);
        buttonPanel.add(reservationButton);
        buttonPanel.add(Box.createVerticalStrut(verticalGap));

        JButton createChargesButton = new JButton("Create Charges");
        createChargesButton.setBackground(new Color(153, 204, 255));
        createChargesButton.addActionListener(e -> {
            this.setSize(800, 500);
            cardLayout.show(cardPanel, "CreateCharges");
        });
        styleButton.accept(createChargesButton);
        buttonPanel.add(createChargesButton);
        buttonPanel.add(Box.createVerticalStrut(verticalGap));

        JButton exitButton = new JButton("Exit");
        exitButton.setBackground(Color.LIGHT_GRAY);
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
        styleButton.accept(exitButton);
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
            createReservationPanel.refreshGuestDropdown();
            cardLayout.show(cardPanel, "CreateReservation");
        });
        buttonPanel.add(createReservationButton);

        JButton viewReservationButton = new JButton("View Reservation");
        viewReservationButton.setBackground(new Color(204, 255, 255));
        viewReservationButton.setOpaque(true);
        viewReservationButton.setBorderPainted(false);
        buttonPanel.add(viewReservationButton);
        viewReservationButton.addActionListener(e -> {
            this.setSize(800, 600);
            ((ViewReservationsScreen) viewReservationsPanel).refreshTable();
            cardLayout.show(cardPanel, "ViewReservations");
        });

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
