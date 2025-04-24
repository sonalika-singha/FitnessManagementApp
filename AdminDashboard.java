

package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminDashboard extends JFrame implements ActionListener {
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Variables to hold client data
    String name, gender, goal;
    int age, height, weight;

    public AdminDashboard(String username) {
        setTitle("Client Home");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Fetch client data from DB using contact info
        fetchClientData(username);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem profileItem = new JMenuItem("Profile");
        JMenuItem addAppointmentItem = new JMenuItem("Add Appointment");
        JMenuItem updateAppointmentItem = new JMenuItem("Update Appointment");
        JMenuItem deleteAppointmentItem = new JMenuItem("Delete Appointment");


        profileItem.addActionListener(this);
        addAppointmentItem.addActionListener(this);
        updateAppointmentItem.addActionListener(this);
        deleteAppointmentItem.addActionListener(this);


        menu.add(profileItem);
        menu.add(addAppointmentItem);
        menu.add(updateAppointmentItem);
        menu.add(deleteAppointmentItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(getProfilePanel(), "Profile");
        contentPanel.add(addAppointment(), "add Appointment");
        contentPanel.add(updateAppointment(), "Update Appointment");
        contentPanel.add(deleteAppointment(), "Delete Appointment");

        add(contentPanel);
        cardLayout.show(contentPanel, "Profile");

        setVisible(true);
    }

    private void fetchClientData(String username) {
        try {
            Conn c = new Conn();
            String query = "SELECT * FROM admin WHERE username = ?";
            PreparedStatement ps = c.c.prepareStatement(query);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("username");
            } else {
                JOptionPane.showMessageDialog(this, "Admin data not found!");
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
        return panel;
    }

    
    private JPanel addAppointment() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Manage Appointments", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JTextField tfClientName = new JTextField(); // New: Client name input
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

        formPanel.add(new JLabel("Client Name:"));
        formPanel.add(tfClientName); // Add to form
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
        JButton insertBtn = new JButton("Add Appointment");
        buttonPanel.add(insertBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        insertBtn.addActionListener(e -> {
            try {
                String clientName = tfClientName.getText().trim();

                // 1. Fetch client_id using name
                Conn c = new Conn();
                String query = "SELECT Client_ID FROM client WHERE Name = ?";
                PreparedStatement fetchStmt = c.c.prepareStatement(query);
                fetchStmt.setString(1, clientName);
                ResultSet rs = fetchStmt.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(panel, "Client not found. Please check the name.");
                    return;
                }

                int clientId = rs.getInt("Client_ID");

                // 2. Prepare appointment values
                String date = tfDate.getText().trim();
                String time = tfTime.getText().trim();
                String type = (String) cbType.getSelectedItem();
                String status = tfStatus.getText().trim();

                String trainerString = (String) cbTrainer.getSelectedItem();
                if (trainerString == null || !trainerString.contains(" - ")) {
                    JOptionPane.showMessageDialog(panel, "Please select a trainer.");
                    return;
                }

                int trainerId = Integer.parseInt(trainerString.split(" - ")[0]);

                // 3. Insert appointment
                String sql = "INSERT INTO appointment (Trainer_ID, Client_ID, Date, Time, Type, Status) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = c.c.prepareStatement(sql);
                stmt.setInt(1, trainerId);
                stmt.setInt(2, clientId);
                stmt.setString(3, date);
                stmt.setString(4, time);
                stmt.setString(5, type);
                stmt.setString(6, status);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Appointment inserted successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        return panel;
    }

    private JPanel updateAppointment() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Update Appointment", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JTextField tfAppointmentID = new JTextField();
        JTextField tfClientName = new JTextField();
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
                cbTrainer.addItem(rs.getInt("Trainer_ID") + " - " + rs.getString("Name"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        formPanel.add(new JLabel("Appointment ID:"));
        formPanel.add(tfAppointmentID);
        formPanel.add(new JLabel("Client Name:"));
        formPanel.add(tfClientName);
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
        JButton updateBtn = new JButton("Update Appointment");
        buttonPanel.add(updateBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        updateBtn.addActionListener(e -> {
            try {
                int appointmentId = Integer.parseInt(tfAppointmentID.getText().trim());
                String clientName = tfClientName.getText().trim();

                Conn c = new Conn();
                String fetchClient = "SELECT Client_ID FROM client WHERE Name = ?";
                PreparedStatement pst = c.c.prepareStatement(fetchClient);
                pst.setString(1, clientName);
                ResultSet rs = pst.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(panel, "Client not found.");
                    return;
                }

                int clientId = rs.getInt("Client_ID");
                String trainerString = (String) cbTrainer.getSelectedItem();
                int trainerId = Integer.parseInt(trainerString.split(" - ")[0]);

                String sql = "UPDATE appointment SET Trainer_ID=?, Client_ID=?, Date=?, Time=?, Type=?, Status=? WHERE Appointment_ID=?";
                PreparedStatement stmt = c.c.prepareStatement(sql);
                stmt.setInt(1, trainerId);
                stmt.setInt(2, clientId);
                stmt.setString(3, tfDate.getText().trim());
                stmt.setString(4, tfTime.getText().trim());
                stmt.setString(5, (String) cbType.getSelectedItem());
                stmt.setString(6, tfStatus.getText().trim());
                stmt.setInt(7, appointmentId);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(panel, "Appointment updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(panel, "Appointment not found.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        return panel;
    }

    private JPanel deleteAppointment() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Delete Appointment", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JTextField tfAppointmentID = new JTextField();

        formPanel.add(new JLabel("Appointment ID to Delete:"));
        formPanel.add(tfAppointmentID);

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton deleteBtn = new JButton("Delete Appointment");
        buttonPanel.add(deleteBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        deleteBtn.addActionListener(e -> {
            try {
                int appointmentId = Integer.parseInt(tfAppointmentID.getText().trim());

                Conn c = new Conn();
                String sql = "DELETE FROM appointment WHERE Appointment_ID=?";
                PreparedStatement stmt = c.c.prepareStatement(sql);
                stmt.setInt(1, appointmentId);

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(panel, "Appointment deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(panel, "Appointment not found.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        return panel;
    }




    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "Profile":
                cardLayout.show(contentPanel, "Profile");
                break;
            case "Add Appointment":
                cardLayout.show(contentPanel, "add Appointment"); 
                break;
            case "Update Appointment":
                cardLayout.show(contentPanel, "Update Appointment");
                break;
            case "Delete Appointment":
                cardLayout.show(contentPanel, "Delete Appointment");
                break;
        }
    }



    public static void main(String[] args) {
        new AdminDashboard(""); 
    }
}
