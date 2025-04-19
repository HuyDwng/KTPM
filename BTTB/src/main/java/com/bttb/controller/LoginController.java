/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bttb.controller;

import com.bttb.pojo.Users;
import com.bttb.services.HashUtils;
import com.bttb.services.UsersServices;
import com.bttb.services.Utils;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private void hoverLoginButton() {
        loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10 20;");
    }

    @FXML
    private void unhoverLoginButton() {
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10 20;");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roleComboBox.getItems().addAll("admin", "technician");
        roleComboBox.setEditable(false); // Không cho nhập tay
    }
    private UsersServices us = new UsersServices();

    @FXML
    private void login(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            showErrorWithTimeout("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        try {
            Users user = us.getUsersByUsernameAndRole(username, role);

            if (user != null && HashUtils.checkPassword(password, user.getPassword())) {
                // Đăng nhập thành công

                Users.currentUser = user; // 
                if ("technician".equals(user.getRole())) {
                    showErrorWithTimeout("Kỹ thuật viên chưa được hỗ trợ!");
                } else {
                    Utils.loadView((Stage) loginButton.getScene().getWindow(), "/com/bttb/bttb/dashboard.fxml");
                }

            } else {
                // Sai username/password
                errorLabel.setText("Sai thông tin đăng nhập!");
                errorLabel.setVisible(true);
            }
        } catch (SQLException e) {
            showErrorWithTimeout("Lỗi kết nối cơ sở dữ liệu.");

            e.printStackTrace();
        } catch (Exception e) {
            showErrorWithTimeout("Có lỗi xảy ra!");
            
            e.printStackTrace();
        }
    }

    private void showErrorWithTimeout(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(event -> errorLabel.setVisible(false));
        pause.play();
    }

}
