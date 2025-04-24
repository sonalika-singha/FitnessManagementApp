package Main;

import javax.swing.*;   //packages
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ClientHomePage extends JFrame implements ActionListener {
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Variables to hold client data
    String name, gender, goal;
    int age, height, weight;

    public ClientHomePage(int id) {
        setTitle("Client Home");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Fetch client data from DB using contact info
        fetchClientData(id);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem profileItem = new JMenuItem("Profile");
        JMenuItem workoutItem = new JMenuItem("Workout Plan");
        JMenuItem appointmentItem = new JMenuItem("Take Appointment");
        JMenuItem dietItem = new JMenuItem("Diet Plan");

        profileItem.addActionListener(this);
        workoutItem.addActionListener(this);
        appointmentItem.addActionListener(this);
        dietItem.addActionListener(this);

        menu.add(profileItem);
        menu.add(workoutItem);
        menu.add(appointmentItem);
        menu.add(dietItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(getProfilePanel(), "Profile");
        contentPanel.add(getWorkoutPanel(), "Workout");
        contentPanel.add(getTakeAppointment(id), "Take Appointment");
        contentPanel.add(getDietPanel(), "Diet");

        add(contentPanel);
        cardLayout.show(contentPanel, "Profile");

        setVisible(true);
    }

    private void fetchClientData(int id) {
        try {
            Conn c = new Conn();
            String query = "SELECT * FROM client WHERE Client_Id = ?";
            PreparedStatement ps = c.c.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("Name");
                age = rs.getInt("Age");
                gender = rs.getString("Gender");
                height = rs.getInt("Height");
                weight = rs.getInt("Weight");
                goal = rs.getString("Fitness_Goal");
            } else {
                JOptionPane.showMessageDialog(this, "Client data not found!");
                this.dispose(); // close the frame
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel getProfilePanel() {
        JPanel panel = new JPanel(new GridLayout(7, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        panel.add(new JLabel("Welcome, " + name + "!"));
        panel.add(new JLabel("Age: " + age));
        panel.add(new JLabel("Gender: " + gender));
        panel.add(new JLabel("Height: " + height + " cm"));
        panel.add(new JLabel("Weight: " + weight + " kg"));
        panel.add(new JLabel("Goal: " + goal));
        panel.add(new JLabel("Contact: Hidden for now")); // Optional

        return panel;
    }

    private JPanel getWorkoutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea("Your Workout Plan:\n\n(Monday) Push Day\n(Tuesday) Pull Day\n(Wednesday) Legs...");
        area.setEditable(false);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private JPanel getDietPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea("Your Diet Plan:\n\nBreakfast: Eggs & Oats\nLunch: Chicken & Rice\nDinner: Salad & Paneer...");
        area.setEditable(false);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }
        
    private JPanel getTakeAppointment(int id) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Manage Appointments", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JTextField tfDate = new JTextField();
        JTextField tfTime = new JTextField();
        String[] types = {"Consultation", "Workout", "Diet Review", "Fitness Check"};
        JComboBox<String> cbType = new JComboBox<>(types);
        JTextField tfStatus = new JTextField("Pending");

        JComboBox<String> cbTrainer = new JComboBox<>();
        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT Trainer_ID, Name FROM trainer");
            while (rs.next()) {
                int trainerId = rs.getInt("Trainer_ID");
                String name = rs.getString("Name");
                cbTrainer.addItem(trainerId + " - " + name);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(tfDate);
        formPanel.add(new JLabel("Time (HH:MM):"));
        formPanel.add(tfTime);
        formPanel.add(new JLabel("Type:"));
        formPanel.add(cbType);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(tfStatus);
        formPanel.add(new JLabel("Trainer:"));
        formPanel.add(cbTrainer);

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton insertBtn = new JButton("Take Appointment");
        buttonPanel.add(insertBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        insertBtn.addActionListener(e -> {
            try {
                Conn c = new Conn();

                String trainerString = (String) cbTrainer.getSelectedItem();
                if (trainerString == null || !trainerString.contains(" - ")) {
                    JOptionPane.showMessageDialog(panel, "Please select a trainer.");
                    return;
                }

                int trainerId = Integer.parseInt(trainerString.split(" - ")[0]);

                String sql = "INSERT INTO appointment (Trainer_ID, Client_ID, Date, Time, Type, Status) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = c.c.prepareStatement(sql);
                stmt.setInt(1, trainerId);
                stmt.setInt(2, id);
                stmt.setString(3, tfDate.getText());
                stmt.setString(4, tfTime.getText());
                stmt.setString(5, (String) cbType.getSelectedItem());
                stmt.setString(6, tfStatus.getText());

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Appointment inserted successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        return panel;
    }





    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Profile":
                cardLayout.show(contentPanel, "Profile");
                break;
            case "Workout Plan":
                cardLayout.show(contentPanel, "Workout");
                break;
            case "Diet Plan":
                cardLayout.show(contentPanel, "Diet");
                break;
            case "Take Appointment":
                cardLayout.show(contentPanel, "Take Appointment");
                break;
        }
    }

    public static void main(String[] args) {
        new ClientHomePage(0); // Replace with real contact info after login
    }
}
