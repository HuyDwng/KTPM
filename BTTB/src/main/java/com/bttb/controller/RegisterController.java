/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.controller;

import com.bttb.pojo.User;
import com.bttb.services.UserServices;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author LEGION
 */
public class RegisterController implements Initializable {

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtRePassword;
    @FXML
    private TextField txtEmail;
    @FXML
    private Label lblMessage;

    private final UserServices userServices = new UserServices();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblMessage.setText("");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String name = txtName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        String rePassword = txtRePassword.getText();
        String email = txtEmail.getText().trim();

        if (name.isEmpty()) {
            lblMessage.setText("Vui lòng nhập họ tên!");
            return;
        }

        if (username.isEmpty()) {
            lblMessage.setText("Vui lòng nhập tên đăng nhập!");
            return;
        }

        if (password.isEmpty()) {
            lblMessage.setText("Vui lòng nhập mật khẩu!");
            return;
        }

        if (rePassword.isEmpty()) {
            lblMessage.setText("Vui lòng nhập lại mật khẩu!");
            return;
        }

        if (email.isEmpty()) {
            lblMessage.setText("Vui lòng nhập email!");
            return;
        }

        // Regex kiểm tra mật khẩu mạnh
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!()_\\-{}\\[\\]:;\"'<>,.?/~`|\\\\]).{8,}$";
        if (!password.matches(passwordRegex)) {
            lblMessage.setText("Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.");
            return;
        }

        if (!password.equals(rePassword)) {
            lblMessage.setText("Mật khẩu không khớp!");
            return;
        }

        if (!isValidEmail(email)) {
            lblMessage.setText("Email không hợp lệ!");
            return;
        }

        try {
            User newUser = new User(name, email, username, password, "technician");
            boolean success = userServices.addUser(newUser);

            if (success) {
                lblMessage.setText("Đăng ký thành công!");
                clearForm();
            } else {
                lblMessage.setText("Đăng ký thất bại! Tên đăng nhập hoặc email có thể đã tồn tại.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblMessage.setText("Lỗi: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtName.clear();
        txtUsername.clear();
        txtPassword.clear();
        txtRePassword.clear();
        txtEmail.clear();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!()_\\-{}\\[\\]:;\"'<>,.?/~`|\\\\]).{8,}$";
        return password.matches(passwordRegex);
    }

}
