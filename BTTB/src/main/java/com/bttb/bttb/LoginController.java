/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bttb.bttb;

import com.bttb.pojo.User;
import com.bttb.services.HashUtils;
import com.bttb.services.UserServices;
import com.bttb.services.Utils;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

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
    private UserServices us = new UserServices();

    @FXML
    private void login(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            errorLabel.setText("Vui lòng điền đầy đủ thông tin!");
            errorLabel.setVisible(true);
            return;
        }

        try {
            User user = us.getUserByUsernameAndRole(username, role);

            if (user != null && HashUtils.checkPassword(password, user.getPassword())) {
                // Đăng nhập thành công
                Utils.loadView((Stage) loginButton.getScene().getWindow(), "/com/bttb/bttb/dashboard.fxml");
            } else {
                // Sai username/password
                errorLabel.setText("Sai thông tin đăng nhập!");
                errorLabel.setVisible(true);
            }
        } catch (SQLException e) {
            errorLabel.setText("Lỗi kết nối cơ sở dữ liệu.");
            errorLabel.setVisible(true);
            e.printStackTrace();
        } catch (Exception e) {
            errorLabel.setText("Có lỗi xảy ra!");
            errorLabel.setVisible(true);
            e.printStackTrace();
        }
    }

}
