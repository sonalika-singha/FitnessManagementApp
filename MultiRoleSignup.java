package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MultiRoleSignup extends JFrame implements ActionListener, ItemListener {
    private JComboBox<String> roleComboBox;
    private JTextField IDField, nameField, ageField, genderField, heightField, weightField, goalField, specializationField, experienceField, contactField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton signupButton;

    // Add label references
    private JLabel ageLabel, genderLabel, heightLabel, weightLabel, goalLabel, specializationLabel, experienceLabel;

    MultiRoleSignup() {
        setTitle("Multi-Role Signup");
        setSize(600, 700);
        setLocation(500, 100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel roleLabel = new JLabel("Sign up as:");
        roleLabel.setBounds(50, 20, 100, 25);
        add(roleLabel);

        String[] roles = {"Client", "Trainer"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setBounds(180, 20, 180, 25);
        roleComboBox.addItemListener(this);
        add(roleComboBox);
        
        IDField = new JTextField();           addLabeledField("ID", IDField, 80);
        nameField = new JTextField();         addLabeledField("Name", nameField, 60);
        ageField = new JTextField();          ageLabel = addLabeledField("Age (Client only)", ageField, 100);
        genderField = new JTextField();       genderLabel = addLabeledField("Gender (Client only)", genderField, 140);
        heightField = new JTextField();       heightLabel = addLabeledField("Height (Client only)", heightField, 180);
        weightField = new JTextField();       weightLabel = addLabeledField("Weight (Client only)", weightField, 220);
        goalField = new JTextField();         goalLabel = addLabeledField("Fitness Goal (Client only)", goalField, 260);
        specializationField = new JTextField(); specializationLabel = addLabeledField("Specialization (Trainer only)", specializationField, 300);
        experienceField = new JTextField();   experienceLabel = addLabeledField("Experience (Trainer only)", experienceField, 340);
        contactField = new JTextField();      addLabeledField("Contact Info", contactField, 380);

        passwordField = new JPasswordField(); addLabeledField("Password", passwordField, 420);
        confirmPasswordField = new JPasswordField(); addLabeledField("Confirm Password", confirmPasswordField, 460);

        signupButton = new JButton("Sign Up");
        signupButton.setBounds(160, 500, 120, 30);
        signupButton.addActionListener(this);
        add(signupButton);

        updateFormVisibility(); // initial visibility

        setVisible(true);
    }

    private JLabel addLabeledField(String labelText, JTextField field, int y) {
        JLabel label = new JLabel(labelText + ":");
        label.setBounds(50, y, 250, 25);
        field.setBounds(250, y, 180, 25);
        add(label);
        add(field);
        return label;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        updateFormVisibility();
    }

    private void updateFormVisibility() {
        boolean isClient = roleComboBox.getSelectedItem().equals("Client");

        // Client fields
        ageField.setVisible(isClient);
        ageLabel.setVisible(isClient);
        genderField.setVisible(isClient);
        genderLabel.setVisible(isClient);
        heightField.setVisible(isClient);
        heightLabel.setVisible(isClient);
        weightField.setVisible(isClient);
        weightLabel.setVisible(isClient);
        goalField.setVisible(isClient);
        goalLabel.setVisible(isClient);

        // Trainer fields
        specializationField.setVisible(!isClient);
        specializationLabel.setVisible(!isClient);
        experienceField.setVisible(!isClient);
        experienceLabel.setVisible(!isClient);

        repaint(); // ensures UI refresh
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String role = roleComboBox.getSelectedItem().toString();
        String id = IDField.getText().trim();
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword()).trim();

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Conn c = new Conn();
            Connection conn = c.c;

            if (role.equals("Client")) {
                int age = Integer.parseInt(ageField.getText().trim());
                String gender = genderField.getText().trim();
                int height = Integer.parseInt(heightField.getText().trim());
                int weight = Integer.parseInt(weightField.getText().trim());
                String goal = goalField.getText().trim();

                String query = "INSERT INTO client (Client_ID, Name, Age, Gender, Height, Weight, Fitness_Goal, Contact_Info, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, id);
                stmt.setString(2, name);
                stmt.setInt(3, age);
                stmt.setString(4, gender);
                stmt.setInt(5, height);
                stmt.setInt(6, weight);
                stmt.setString(7, goal);
                stmt.setString(8, contact);
                stmt.setString(9, password);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Client registered successfully!");
                this.setVisible(false);

            } else if (role.equals("Trainer")) {
                String specialization = specializationField.getText().trim();
                int experience = Integer.parseInt(experienceField.getText().trim());

                String query = "INSERT INTO trainer (Trainer_ID, Name, Specialization, Experience, Contact_Info, password) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                
                stmt.setString(1, id);
                stmt.setString(2, name);
                stmt.setString(3, specialization);
                stmt.setInt(4, experience);
                stmt.setString(5, contact);
                stmt.setString(6, password);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Trainer registered successfully!");
                this.setVisible(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during signup", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new MultiRoleSignup();
    }
}
