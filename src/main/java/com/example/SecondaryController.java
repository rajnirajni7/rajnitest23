package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class SecondaryController {

    @FXML private TextField txtName, txtEmail, txtPhone;
    @FXML private TableView<Student> tableView;
    @FXML private TableColumn<Student, String> colName, colEmail, colPhone;

    private final String DB_URL = "jdbc:mysql://localhost:3307/database_lab2_b";
    private final String DB_USER = "root";
    private final String DB_PASS = "";

    private ObservableList<Student> dataList = FXCollections.observableArrayList();
    private Student selectedStudent;

    @FXML
    public void initialize() {
        colName.setCellValueFactory(cell -> cell.getValue().nameProperty());
        colEmail.setCellValueFactory(cell -> cell.getValue().emailProperty());
        colPhone.setCellValueFactory(cell -> cell.getValue().phoneProperty());

        tableView.setItems(dataList);
        tableView.setOnMouseClicked(e -> {
            selectedStudent = tableView.getSelectionModel().getSelectedItem();
            if (selectedStudent != null) {
                txtName.setText(selectedStudent.getName());
                txtEmail.setText(selectedStudent.getEmail());
                txtPhone.setText(selectedStudent.getPhone());
            }
        });

        loadData();
    }

    private void loadData() {
        dataList.clear();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM students");
            while (rs.next()) {
                dataList.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("phone")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd() {
        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO students (name, email, phone) VALUES (?, ?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.executeUpdate();
            clearFields();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedStudent == null) return;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE students SET name=?, email=?, phone=? WHERE id=?");
            stmt.setString(1, txtName.getText());
            stmt.setString(2, txtEmail.getText());
            stmt.setString(3, txtPhone.getText());
            stmt.setInt(4, selectedStudent.getId());
            stmt.executeUpdate();
            clearFields();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedStudent == null) return;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM students WHERE id=?");
            stmt.setInt(1, selectedStudent.getId());
            stmt.executeUpdate();
            clearFields();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    private void clearFields() {
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
        tableView.getSelectionModel().clearSelection();
        selectedStudent = null;
    }
}