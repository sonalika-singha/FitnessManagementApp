package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TrainerHomePage extends JFrame implements ActionListener {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private String trainerName;
    private String specialization;
    private int experience;
    private String contact;

    public TrainerHomePage(String contactInfo) {
        this.contact = contactInfo;
        fetchTrainerDetails(contactInfo);

        setTitle("Trainer Home - " + trainerName);
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        JMenuItem profileItem = new JMenuItem("Profile");
        JMenuItem clientsItem = new JMenuItem("Clients");
        JMenuItem appointmentsItem = new JMenuItem("Appointments");
        JMenuItem workoutPlansItem = new JMenuItem("Workout Plans");

        profileItem.addActionListener(this);
        clientsItem.addActionListener(this);
        appointmentsItem.addActionListener(this);
        workoutPlansItem.addActionListener(this);

        menu.add(profileItem);
        menu.add(clientsItem);
        menu.add(appointmentsItem);
        menu.add(workoutPlansItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Content Panel
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(getProfilePanel(), "Profile");
        contentPanel.add(getClientsPanel(), "Clients");
        contentPanel.add(getAppointmentsPanel(), "Appointments");
        contentPanel.add(getWorkoutPlansPanel(), "WorkoutPlans");

        add(contentPanel);

        cardLayout.show(contentPanel, "Profile");

        setVisible(true);
    }

    private void fetchTrainerDetails(String contactInfo) {
        try {
            Conn c = new Conn();
            String query = "SELECT * FROM trainer WHERE Contact_Info = ?";
            PreparedStatement stmt = c.c.prepareStatement(query);
            stmt.setString(1, contactInfo);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                trainerName = rs.getString("Name");
                specialization = rs.getString("Specialization");
                experience = rs.getInt("Experience");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch trainer data.");
        }
    }

    private JPanel getProfilePanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));
        panel.add(new JLabel("Trainer Profile"));
        panel.add(new JLabel("Name: " + trainerName));
        panel.add(new JLabel("Specialization: " + specialization));
        panel.add(new JLabel("Experience: " + experience + " years"));
        panel.add(new JLabel("Contact: " + contact));
        return panel;
    }

    private JPanel getClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 14));

        try {
            Conn c = new Conn();
            String query = "SELECT Name, Contact_Info FROM trainer";
            ResultSet rs = c.s.executeQuery(query);

            area.append("List of Clients:\n\n");
            while (rs.next()) {
                area.append("- " + rs.getString("Name") + " (Contact: " + rs.getString("Contact_Info") + ")\n");
            }
        } catch (Exception e) {
            area.setText("Error loading clients.");
            e.printStackTrace();
        }

        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }


    
    private JPanel getAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 14));

        try {
            Conn c = new Conn();

            // Step 1: Get the Trainer_ID using contact info
            String trainerQuery = "SELECT Trainer_ID, Name FROM trainer WHERE Contact_Info = ?";
            PreparedStatement trainerStmt = c.c.prepareStatement(trainerQuery);
            trainerStmt.setString(1, contact);
            ResultSet trainerRs = trainerStmt.executeQuery();

            int trainerId = -1;
            if (trainerRs.next()) {
                trainerId = trainerRs.getInt("Trainer_ID");
            }

            if (trainerId != -1) {
                // Step 2: Fetch appointments for this Trainer_ID
            	String appointmentQuery = "SELECT Appointment_ID, Client_ID, Date, Time, Type, Status FROM appointment WHERE Trainer_ID = ?";

                PreparedStatement apptStmt = c.c.prepareStatement(appointmentQuery);
                apptStmt.setInt(1, trainerId);
                ResultSet rs = apptStmt.executeQuery();

                area.append("Appointments for " + trainerName + ":\n\n");

                while (rs.next()) {
                    int appointmentId = rs.getInt("Appointment_ID");
                    int clientId = rs.getInt("Client_ID");
                    String date = rs.getString("Date");
                    String time = rs.getString("Time");
                    String type = rs.getString("Type");
                    String status = rs.getString("Status");

                    area.append("Appointment ID: " + appointmentId + "\n");
                    area.append("Client ID: " + clientId + "\n"); // Optional: replace with client name if needed
                    area.append("Date: " + date + "\n");
                    area.append("Time: " + time + "\n");
                    area.append("Type: " + type + "\n");
                    area.append("Status: " + status + "\n\n");
                }
            } else {
                area.setText("Trainer not found.");
            }

        } catch (Exception e) {
            area.setText("Error loading appointments.");
            e.printStackTrace();
        }

        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private JPanel getWorkoutPlansPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 14));

        StringBuilder plansText = new StringBuilder("Workout Plans:\n\n");

        try {
            Conn c = new Conn();
            String query = "SELECT Plan_ID, Plan_Name, Duration, Difficulty_Level, Client_ID FROM workout_plan";
            ResultSet rs = c.s.executeQuery(query);

            while (rs.next()) {
                plansText.append("Plan ID: ").append(rs.getInt("Plan_ID")).append("\n")
                         .append("Name   : ").append(rs.getString("Plan_Name")).append("\n")
                         .append("Duration: ").append(rs.getInt("Duration")).append(" mins\n")
                         .append("Difficulty: ").append(rs.getString("Difficulty_Level")).append("\n")
                         .append("Client ID: ").append(rs.getInt("Client_ID")).append("\n")
                         .append("------------------------------------\n");
            }

            rs.close();
            c.s.close();
            c.c.close();
        } catch (Exception e) {
            plansText.append("Error loading workout plans.\n").append(e.getMessage());
            e.printStackTrace();
        }

        area.setText(plansText.toString());
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }


//    private JPanel getWorkoutPlansPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        JTextArea area = new JTextArea();
//        area.setEditable(false);
//        area.setFont(new Font("Arial", Font.PLAIN, 14));
//        area.setText("Workout Plans based on your specialization:\n\n" + specialization + " Specific Plan Goes Here...\n\n[Can be customized per trainer]");
//        panel.add(new JScrollPane(area), BorderLayout.CENTER);
//        return panel;
//    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        cardLayout.show(contentPanel, command.replace(" ", ""));
    }

    public static void main(String[] args) {
        new TrainerHomePage(" ");
    }
}

