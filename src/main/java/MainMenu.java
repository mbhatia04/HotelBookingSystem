import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class MainMenu extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private ViewReservationsScreen viewReservationsPanel;
    private CreateReservation createReservationPanel;
    private CheckInScreen checkInPanel;
    private CheckOutScreen checkOutPanel;
    private CreateChargesScreen createChargesPanel;

    public MainMenu() {
        setTitle("MSB Hotel - Main Menu");
        setSize(800, 640);
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
        checkOutPanel = new CheckOutScreen(cardLayout, cardPanel, this);
        createChargesPanel = new CreateChargesScreen(cardLayout, cardPanel, this);

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
        mainPanel.setBackground(new Color(255, 204, 236));

        JLabel titleLabel = new JLabel("MSB Hotel", SwingConstants.CENTER);
        titleLabel.setFont(FontUtil.loadLobsterFont(75f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Use BoxLayout for vertical button alignment
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));

        int verticalGap = 30;

        Consumer<JButton> styleButton = button -> {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setFont(new Font("Serif", Font.BOLD, 18));
        };

        JButton checkInButton = new JButton("Check In");
        checkInButton.setBackground(new Color(255, 230, 245));
        checkInButton.addActionListener(e -> {
            this.setSize(575, 460);
            checkInPanel.refreshStayDropdown();
            cardLayout.show(cardPanel, "CheckInScreen");
        });
        styleButton.accept(checkInButton);
        buttonPanel.add(checkInButton);
        buttonPanel.add(Box.createVerticalStrut(verticalGap));

        JButton checkOutButton = new JButton("Check Out");
        checkOutButton.setBackground(new Color(255, 230, 245));
        checkOutButton.setOpaque(true);
        checkOutButton.setBorderPainted(false);
        checkOutButton.addActionListener(e -> {
            this.setSize(800, 640);
            ((CheckOutScreen) checkOutPanel).loadStayDropdown();
            cardLayout.show(cardPanel, "CheckOutScreen");
        });
        styleButton.accept(checkOutButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(Box.createVerticalStrut(verticalGap));

        JButton reservationButton = new JButton("Handle Reservations");
        reservationButton.setBackground(new Color(255, 230, 245));
        reservationButton.addActionListener(e -> {
            this.setSize(800, 530);
            cardLayout.show(cardPanel, "ReservationMenu");
        });
        styleButton.accept(reservationButton);
        buttonPanel.add(reservationButton);
        buttonPanel.add(Box.createVerticalStrut(verticalGap));

        JButton createChargesButton = new JButton("Create Charges");
        createChargesButton.setBackground(new Color(255, 230, 245));
        createChargesButton.addActionListener(e -> {
            this.setSize(800, 640);
            ((CreateChargesScreen) createChargesPanel).reloadGuestList();
            cardLayout.show(cardPanel, "CreateCharges");
        });
        styleButton.accept(createChargesButton);
        buttonPanel.add(createChargesButton);
        buttonPanel.add(Box.createVerticalStrut(verticalGap));

        JButton exitButton = new JButton("Exit");
        exitButton.setBackground(new Color(250, 116, 196));
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
        reservationPanel.setBackground(new Color(255, 204, 236));

        JLabel titleLabel = new JLabel("Reservation Menu", SwingConstants.CENTER);
        titleLabel.setFont(FontUtil.loadLobsterFont(50f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        reservationPanel.add(titleLabel, BorderLayout.NORTH);

        // Match main menu layout and spacing
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));

        int verticalGap = 30;

        Consumer<JButton> styleButton = button -> {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setFont(new Font("Serif", Font.BOLD, 18));
        };

        JButton createReservationButton = new JButton("Create Reservation");
        createReservationButton.setBackground(new Color(255, 230, 245));
        createReservationButton.addActionListener(e -> {
            this.setSize(800, 700);
            createReservationPanel.refreshGuestDropdown();
            cardLayout.show(cardPanel, "CreateReservation");
        });
        styleButton.accept(createReservationButton);
        buttonPanel.add(createReservationButton);
        buttonPanel.add(Box.createVerticalStrut(verticalGap));

        JButton viewReservationButton = new JButton("View Reservation");
        viewReservationButton.setBackground(new Color(255, 230, 245));
        viewReservationButton.addActionListener(e -> {
            this.setSize(900, 600);
            viewReservationsPanel.refreshTable();
            cardLayout.show(cardPanel, "ViewReservations");
        });
        styleButton.accept(viewReservationButton);
        buttonPanel.add(viewReservationButton);
        buttonPanel.add(Box.createVerticalStrut(verticalGap));

        JButton createGuestButton = new JButton("Create Guest Profile");
        createGuestButton.setBackground(new Color(255, 230, 245));
        createGuestButton.addActionListener(e -> {
            this.setSize(800, 500);
            cardLayout.show(cardPanel, "GuestProfile");
        });
        styleButton.accept(createGuestButton);
        buttonPanel.add(createGuestButton);
        buttonPanel.add(Box.createVerticalStrut(verticalGap));

        JButton returnButton = new JButton("Return");
        returnButton.setBackground(new Color(250, 116, 196));
        returnButton.addActionListener(e -> {
            this.setSize(800, 640);
            cardLayout.show(cardPanel, "MainMenu");
        });
        styleButton.accept(returnButton);
        buttonPanel.add(returnButton);

        reservationPanel.add(buttonPanel, BorderLayout.CENTER);
        return reservationPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}
