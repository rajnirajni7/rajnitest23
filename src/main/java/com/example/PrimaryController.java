package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javafx.scene.control.TextArea;


import java.sql.*;

public class PrimaryController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextArea lblMessage;
    @FXML private Button btnLogin;

    private final String DB_URL = "jdbc:mysql://localhost:3306/lab2b";
    private final String DB_USER = "root";
    private final String DB_PASS = "";

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        // File-based fallback login (optional)
        try {
            InputStream input = getClass().getResourceAsStream("/login.txt");
            if (input != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] credentials = line.split(",");
                    if (credentials.length == 2 &&
                        username.equals(credentials[0].trim()) &&
                        password.equals(credentials[1].trim())) {
                        loadSecondaryScene();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            lblMessage.setText("Error reading login.txt: " + e.getMessage());
        }

        // Database login
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                loadSecondaryScene();
            } else {
                lblMessage.setText("Invalid username or password.");
            }
        } catch (Exception e) {
            lblMessage.setText("Database error: " + e.getMessage());
        }
    }

    private void loadSecondaryScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/secondary.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            lblMessage.setText("Scene loading error: " + e.getMessage());
        }
    }
}