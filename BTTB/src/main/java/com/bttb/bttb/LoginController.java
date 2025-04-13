/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bttb.bttb;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

/**
 * FXML Controller class
 *
 * @author nhanh
 */
public class LoginController implements Initializable {

    @FXML
    private Button loginButton;

    @FXML
    public void hoverLoginButton() {
        loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10 20;");
    }

    @FXML
    public void unhoverLoginButton() {
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10 20;");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> roles = FXCollections.observableArrayList("Admin", "Technician");
        
    }

}
