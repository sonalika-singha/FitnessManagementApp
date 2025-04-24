package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MultiRoleLogin extends JFrame implements ActionListener {  //inheritance
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;

    MultiRoleLogin() {
        setTitle("Multi-Role Login");
        setSize(400, 300);
        setLocation(500, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Panel
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 30, 100, 30);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 30, 180, 30);
        panel.add(usernameField);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 80, 100, 30);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 80, 180, 30);
        panel.add(passwordField);

        // Role
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(50, 130, 100, 30);
        panel.add(roleLabel);

        String[] roles = { "Admin", "Client", "Trainer" };
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setBounds(150, 130, 180, 30);
        panel.add(roleComboBox);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setBounds(150, 180, 100, 30);
        loginButton.addActionListener(this);
        panel.add(loginButton);
        
        

        add(panel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();
        String role = roleComboBox.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Conn c = new Conn();
            Connection conn = c.c;
            String table = role.equalsIgnoreCase("Admin") ? "admin"
                        : role.equalsIgnoreCase("Client") ? "client"
                        : "trainer";

            String usernameColumn = role.equalsIgnoreCase("Admin") ? "username"
                               : "Contact_Info";

            String passwordColumn = "password";

            String query = "SELECT * FROM " + table + " WHERE " + usernameColumn + "=? AND " + passwordColumn + "=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            System.out.println(rs);

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful as " + role + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // You can redirect to different dashboards based on role here
                // e.g., new AdminDashboard(), ClientDashboard(), TrainerDashboard()
                
//                if (role.equalsIgnoreCase("Admin")) {
//                    new AdminDashboard(username);
//                } else 
                if (role.equalsIgnoreCase("Trainer")) {
                	new TrainerHomePage(rs.getString("Contact_Info"));
                } else if (role.equalsIgnoreCase("Admin")) {
                    // new AdminDashboard(username);
                	new AdminDashboard(username);
                } else {
                	new ClientHomePage(rs.getInt("Client_ID"));
                }

                this.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } 
        catch(SQLException e1) {
        	e1.printStackTrace();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new MultiRoleLogin();
    }
}

